-- Dev seed data — runs after Liquibase migrations.

-- organisation (no FKs)
INSERT INTO organisation (name) VALUES
    ('Elite Baseball Academy'),
    ('Metro Youth Sports'),
    ('Riverside Athletic Club');

-- team (→ organisation, sport_dictionary)
INSERT INTO team (name, organisation_id, sport, description) VALUES
    ('14U Blue',   (SELECT id FROM organisation WHERE name = 'Elite Baseball Academy'), 'Baseball', 'Competitive 14U travel team'),
    ('12U Gold',   (SELECT id FROM organisation WHERE name = 'Elite Baseball Academy'), 'Baseball', 'Competitive 12U travel team'),
    ('13U Select', (SELECT id FROM organisation WHERE name = 'Metro Youth Sports'),     'Baseball', 'Select 13U program');

-- player (→ team)
INSERT INTO player (name, graduation_year, team_id, dob) VALUES
    ('Jake Thornton',  2028, (SELECT id FROM team WHERE name = '14U Blue'),  '2010-03-15'),
    ('Marcus Rivera',  2028, (SELECT id FROM team WHERE name = '14U Blue'),  '2010-07-22'),
    ('Caden Williams', 2029, (SELECT id FROM team WHERE name = '12U Gold'),  '2011-11-04');

-- user (→ user_roles_dictionary)
INSERT INTO "user" (name, role) VALUES
    ('Coach Davis', 'Coach'),
    ('Coach Patel', 'Coach'),
    ('Admin User',  'Admin');

-- measurement (no FKs)
INSERT INTO measurement (name)
SELECT 'mph'     WHERE NOT EXISTS (SELECT 1 FROM measurement WHERE name = 'mph');
INSERT INTO measurement (name)
SELECT 'degrees' WHERE NOT EXISTS (SELECT 1 FROM measurement WHERE name = 'degrees');
INSERT INTO measurement (name)
SELECT 'ms'      WHERE NOT EXISTS (SELECT 1 FROM measurement WHERE name = 'ms');

-- metric (→ measurement)
INSERT INTO metric (name, measurement_id, negate)
SELECT 'Bat Speed',     (SELECT id FROM measurement WHERE name = 'mph'),     false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'Bat Speed');
INSERT INTO metric (name, measurement_id, negate)
SELECT 'Attack Angle',  (SELECT id FROM measurement WHERE name = 'degrees'), false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'Attack Angle');
INSERT INTO metric (name, measurement_id, negate)
SELECT 'Time to Impact',(SELECT id FROM measurement WHERE name = 'ms'),      false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'Time to Impact');

-- integration (no FKs)
INSERT INTO integration (name)
SELECT 'Blast Motion' WHERE NOT EXISTS (SELECT 1 FROM integration WHERE name = 'Blast Motion');

-- conditional_metric (→ condition, metric)
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'Bat Speed',     (SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'Bat Speed')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'Bat Speed'     AND metric_id = (SELECT id FROM metric WHERE name = 'Bat Speed'));
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'Attack Angle',  (SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'Attack Angle')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'Attack Angle'  AND metric_id = (SELECT id FROM metric WHERE name = 'Attack Angle'));
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'Time to Impact',(SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'Time to Impact')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'Time to Impact' AND metric_id = (SELECT id FROM metric WHERE name = 'Time to Impact'));

-- data_source (→ integration, metric)
INSERT INTO data_source (name, type, integration_id, metric_id, content)
SELECT 'Blast Bat Speed',     'JSON_CONFIG', (SELECT id FROM integration WHERE name = 'Blast Motion'), (SELECT id FROM metric WHERE name = 'Bat Speed'),      '{"path": "$.batSpeed"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE integration_id = (SELECT id FROM integration WHERE name = 'Blast Motion') AND metric_id = (SELECT id FROM metric WHERE name = 'Bat Speed'));
INSERT INTO data_source (name, type, integration_id, metric_id, content)
SELECT 'Blast Attack Angle',  'JSON_CONFIG', (SELECT id FROM integration WHERE name = 'Blast Motion'), (SELECT id FROM metric WHERE name = 'Attack Angle'),   '{"path": "$.attackAngle"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE integration_id = (SELECT id FROM integration WHERE name = 'Blast Motion') AND metric_id = (SELECT id FROM metric WHERE name = 'Attack Angle'));
INSERT INTO data_source (name, type, integration_id, metric_id, content)
SELECT 'Blast Time to Impact','JSON_CONFIG', (SELECT id FROM integration WHERE name = 'Blast Motion'), (SELECT id FROM metric WHERE name = 'Time to Impact'), '{"path": "$.timeToContact"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE integration_id = (SELECT id FROM integration WHERE name = 'Blast Motion') AND metric_id = (SELECT id FROM metric WHERE name = 'Time to Impact'));

-- assessment_template (→ sport_dictionary)
-- condition_id intentionally NULL: factory.cleanup() deletes conditions before templates,
-- so a non-null condition_id would cause an FK violation.
INSERT INTO assessment_template (name, sport, description) VALUES
    ('Blast Hitting T',       'Baseball', 'Blast Motion assessment — hitting from tee'),
    ('Blast Live AB',         'Baseball', 'Blast Motion assessment — live at-bat'),
    ('Blast Pitching Machine','Baseball', 'Blast Motion assessment — pitching machine at 60ft');

-- assessment (→ player, sport_dictionary, assessment_template)
-- condition_id intentionally NULL for the same reason as assessment_template above.
INSERT INTO assessment (player_id, sport, template_id) VALUES
    (
        (SELECT id FROM player WHERE name = 'Jake Thornton'),
        'Baseball',
        (SELECT id FROM assessment_template WHERE name = 'Blast Hitting T')
    ),
    (
        (SELECT id FROM player WHERE name = 'Marcus Rivera'),
        'Baseball',
        (SELECT id FROM assessment_template WHERE name = 'Blast Hitting T')
    ),
    (
        (SELECT id FROM player WHERE name = 'Caden Williams'),
        'Baseball',
        (SELECT id FROM assessment_template WHERE name = 'Blast Hitting T')
    );

-- session (→ assessment)
INSERT INTO session (assessment_id, start_time) VALUES
    ((SELECT id FROM assessment WHERE player_id = (SELECT id FROM player WHERE name = 'Jake Thornton')),  '2024-01-15 09:00:00'),
    ((SELECT id FROM assessment WHERE player_id = (SELECT id FROM player WHERE name = 'Marcus Rivera')),  '2024-01-15 09:30:00'),
    ((SELECT id FROM assessment WHERE player_id = (SELECT id FROM player WHERE name = 'Caden Williams')), '2024-01-15 10:00:00');

-- rep (→ session)
INSERT INTO rep (session_id, start_time) VALUES
    (
        (SELECT s.id FROM session s JOIN assessment a ON a.id = s.assessment_id JOIN player p ON p.id = a.player_id WHERE p.name = 'Jake Thornton'),
        '2024-01-15 09:05:00'
    ),
    (
        (SELECT s.id FROM session s JOIN assessment a ON a.id = s.assessment_id JOIN player p ON p.id = a.player_id WHERE p.name = 'Marcus Rivera'),
        '2024-01-15 09:35:00'
    ),
    (
        (SELECT s.id FROM session s JOIN assessment a ON a.id = s.assessment_id JOIN player p ON p.id = a.player_id WHERE p.name = 'Caden Williams'),
        '2024-01-15 10:05:00'
    );

-- template_metric (→ assessment_template, conditional_metric, data_source)
INSERT INTO template_metric (template_id, conditional_metric_id, data_source_id, description)
SELECT
    (SELECT id FROM assessment_template WHERE name = 'Blast Hitting T'),
    cm.id,
    ds.id,
    cm.name
FROM conditional_metric cm
JOIN metric m ON m.id = cm.metric_id
JOIN data_source ds ON ds.metric_id = m.id
JOIN integration i ON i.id = ds.integration_id
WHERE i.name = 'Blast Motion'
  AND cm.condition_id = (SELECT id FROM condition WHERE name = 'Hitting from T');

-- assessment_metric (→ assessment, conditional_metric, data_source)
INSERT INTO assessment_metric (assessment_id, conditional_metric_id, data_source_id, min_value, max_value, avg_value)
SELECT
    a.id,
    cm.id,
    ds.id,
    CASE cm.name WHEN 'Bat Speed' THEN 58 WHEN 'Attack Angle' THEN 8  WHEN 'Time to Impact' THEN 145 END,
    CASE cm.name WHEN 'Bat Speed' THEN 75 WHEN 'Attack Angle' THEN 22 WHEN 'Time to Impact' THEN 195 END,
    CASE cm.name WHEN 'Bat Speed' THEN 67 WHEN 'Attack Angle' THEN 15 WHEN 'Time to Impact' THEN 170 END
FROM assessment a
CROSS JOIN conditional_metric cm
JOIN metric m ON m.id = cm.metric_id
JOIN data_source ds ON ds.metric_id = m.id
JOIN integration i ON i.id = ds.integration_id
WHERE i.name = 'Blast Motion'
  AND cm.condition_id = (SELECT id FROM condition WHERE name = 'Hitting from T');

-- session_metric (→ session, conditional_metric)
INSERT INTO session_metric (session_id, conditional_metric_id, min_value, max_value, avg_value)
SELECT
    s.id,
    cm.id,
    CASE cm.name WHEN 'Bat Speed' THEN 60 WHEN 'Attack Angle' THEN 10 WHEN 'Time to Impact' THEN 150 END,
    CASE cm.name WHEN 'Bat Speed' THEN 73 WHEN 'Attack Angle' THEN 20 WHEN 'Time to Impact' THEN 190 END,
    CASE cm.name WHEN 'Bat Speed' THEN 66 WHEN 'Attack Angle' THEN 15 WHEN 'Time to Impact' THEN 168 END
FROM session s
CROSS JOIN conditional_metric cm
WHERE cm.condition_id = (SELECT id FROM condition WHERE name = 'Hitting from T');

-- rep_metric (→ rep, conditional_metric)
INSERT INTO rep_metric (rep_id, conditional_metric_id, value)
SELECT
    r.id,
    cm.id,
    CASE cm.name WHEN 'Bat Speed' THEN 70 WHEN 'Attack Angle' THEN 16 WHEN 'Time to Impact' THEN 165 END
FROM rep r
CROSS JOIN conditional_metric cm
WHERE cm.condition_id = (SELECT id FROM condition WHERE name = 'Hitting from T');

-- assessment_resource (→ assessment, resource_type_dictionary)
INSERT INTO assessment_resource (assessment_id, type, url)
SELECT id, 'Video', 'https://storage.example.com/assessments/' || id || '/video.mp4'
FROM assessment;

-- session_resource (→ session, resource_type_dictionary)
INSERT INTO session_resource (session_id, type, url)
SELECT id, 'Video', 'https://storage.example.com/sessions/' || id || '/video.mp4'
FROM session;

-- rep_resource (→ rep, resource_type_dictionary)
INSERT INTO rep_resource (rep_id, type, url)
SELECT id, 'Video', 'https://storage.example.com/reps/' || id || '/video.mp4'
FROM rep;

-- model (→ sport_dictionary, age_group_dictionary)
INSERT INTO model (sport, age_group, description) VALUES
    ('Baseball', '14U', '14U Baseball benchmark model'),
    ('Baseball', '13U', '13U Baseball benchmark model'),
    ('Baseball', '12U', '12U Baseball benchmark model');

-- model_metric (→ model, conditional_metric)
INSERT INTO model_metric (model_id, conditional_metric_id, value)
SELECT
    mo.id,
    cm.id,
    CASE mo.age_group
        WHEN '14U' THEN CASE cm.name WHEN 'Bat Speed' THEN 68 WHEN 'Attack Angle' THEN 15 WHEN 'Time to Impact' THEN 160 END
        WHEN '13U' THEN CASE cm.name WHEN 'Bat Speed' THEN 62 WHEN 'Attack Angle' THEN 13 WHEN 'Time to Impact' THEN 170 END
        WHEN '12U' THEN CASE cm.name WHEN 'Bat Speed' THEN 56 WHEN 'Attack Angle' THEN 12 WHEN 'Time to Impact' THEN 180 END
    END
FROM model mo
CROSS JOIN conditional_metric cm
WHERE cm.condition_id = (SELECT id FROM condition WHERE name = 'Hitting from T');

-- report (no FK constraints)
INSERT INTO report (name, ext_ref) VALUES
    ('Jake Thornton — January 2024',  'ext://reports/jake-thornton-jan-2024'),
    ('Marcus Rivera — January 2024',  'ext://reports/marcus-rivera-jan-2024'),
    ('Caden Williams — January 2024', 'ext://reports/caden-williams-jan-2024');
