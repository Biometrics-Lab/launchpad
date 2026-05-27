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
INSERT INTO measurement (name)
SELECT 'rpm'     WHERE NOT EXISTS (SELECT 1 FROM measurement WHERE name = 'rpm');
INSERT INTO measurement (name)
SELECT 'watts'   WHERE NOT EXISTS (SELECT 1 FROM measurement WHERE name = 'watts');
INSERT INTO measurement (name)
SELECT '%'       WHERE NOT EXISTS (SELECT 1 FROM measurement WHERE name = '%');

-- metric (→ measurement)
INSERT INTO metric (name, measurement_id, negate)
SELECT 'Bat Speed',               (SELECT id FROM measurement WHERE name = 'mph'),     false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'Bat Speed');
INSERT INTO metric (name, measurement_id, negate)
SELECT 'Peak Hand Speed',         (SELECT id FROM measurement WHERE name = 'mph'),     false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'Peak Hand Speed');
INSERT INTO metric (name, measurement_id, negate)
SELECT 'Attack Angle',            (SELECT id FROM measurement WHERE name = 'degrees'), false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'Attack Angle');
INSERT INTO metric (name, measurement_id, negate)
SELECT 'Vertical Bat Angle',      (SELECT id FROM measurement WHERE name = 'degrees'), false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'Vertical Bat Angle');
INSERT INTO metric (name, measurement_id, negate)
SELECT 'Time to Contact',         (SELECT id FROM measurement WHERE name = 'ms'),      false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'Time to Contact');
INSERT INTO metric (name, measurement_id, negate)
SELECT 'Rotational Acceleration', (SELECT id FROM measurement WHERE name = 'rpm'),     false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'Rotational Acceleration');
INSERT INTO metric (name, measurement_id, negate)
SELECT 'Power',                   (SELECT id FROM measurement WHERE name = 'watts'),   false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'Power');
INSERT INTO metric (name, measurement_id, negate)
SELECT 'On Plane Efficiency',     (SELECT id FROM measurement WHERE name = '%'),       false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'On Plane Efficiency');
INSERT INTO metric (name, measurement_id, negate)
SELECT 'Early Connection',        (SELECT id FROM measurement WHERE name = '%'),       false WHERE NOT EXISTS (SELECT 1 FROM metric WHERE name = 'Early Connection');

-- conditional_metric (→ condition, metric)
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'Bat Speed',               (SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'Bat Speed')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'Bat Speed');
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'Peak Hand Speed',         (SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'Peak Hand Speed')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'Peak Hand Speed');
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'Attack Angle',            (SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'Attack Angle')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'Attack Angle');
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'Vertical Bat Angle',      (SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'Vertical Bat Angle')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'Vertical Bat Angle');
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'Time to Contact',         (SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'Time to Contact')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'Time to Contact');
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'Rotational Acceleration', (SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'Rotational Acceleration')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'Rotational Acceleration');
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'Power',                   (SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'Power')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'Power');
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'On Plane Efficiency',     (SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'On Plane Efficiency')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'On Plane Efficiency');
INSERT INTO conditional_metric (name, condition_id, metric_id)
SELECT 'Early Connection',        (SELECT id FROM condition WHERE name = 'Hitting from T'), (SELECT id FROM metric WHERE name = 'Early Connection')
WHERE NOT EXISTS (SELECT 1 FROM conditional_metric WHERE name = 'Early Connection');

-- data_source — content matches Integration.BLAST_MOTION_API_DEMO.id + BatSensorMetric enum constant name
INSERT INTO data_source (name, type, metric_id, content)
SELECT 'Blast Bat Speed',               'BAT_SENSOR', (SELECT id FROM metric WHERE name = 'Bat Speed'),               '{"integrationId":"blast-motion-api-demo","metric":"BAT_SPEED"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE name = 'Blast Bat Speed');
INSERT INTO data_source (name, type, metric_id, content)
SELECT 'Blast Peak Hand Speed',         'BAT_SENSOR', (SELECT id FROM metric WHERE name = 'Peak Hand Speed'),         '{"integrationId":"blast-motion-api-demo","metric":"PEAK_HAND_SPEED"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE name = 'Blast Peak Hand Speed');
INSERT INTO data_source (name, type, metric_id, content)
SELECT 'Blast Attack Angle',            'BAT_SENSOR', (SELECT id FROM metric WHERE name = 'Attack Angle'),            '{"integrationId":"blast-motion-api-demo","metric":"ATTACK_ANGLE"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE name = 'Blast Attack Angle');
INSERT INTO data_source (name, type, metric_id, content)
SELECT 'Blast Vertical Bat Angle',      'BAT_SENSOR', (SELECT id FROM metric WHERE name = 'Vertical Bat Angle'),      '{"integrationId":"blast-motion-api-demo","metric":"VERTICAL_BAT_ANGLE"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE name = 'Blast Vertical Bat Angle');
INSERT INTO data_source (name, type, metric_id, content)
SELECT 'Blast Time to Contact',         'BAT_SENSOR', (SELECT id FROM metric WHERE name = 'Time to Contact'),         '{"integrationId":"blast-motion-api-demo","metric":"TIME_TO_CONTACT"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE name = 'Blast Time to Contact');
INSERT INTO data_source (name, type, metric_id, content)
SELECT 'Blast Rotational Acceleration', 'BAT_SENSOR', (SELECT id FROM metric WHERE name = 'Rotational Acceleration'), '{"integrationId":"blast-motion-api-demo","metric":"ROTATIONAL_ACCELERATION"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE name = 'Blast Rotational Acceleration');
INSERT INTO data_source (name, type, metric_id, content)
SELECT 'Blast Power',                   'BAT_SENSOR', (SELECT id FROM metric WHERE name = 'Power'),                   '{"integrationId":"blast-motion-api-demo","metric":"POWER"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE name = 'Blast Power');
INSERT INTO data_source (name, type, metric_id, content)
SELECT 'Blast On Plane Efficiency',     'BAT_SENSOR', (SELECT id FROM metric WHERE name = 'On Plane Efficiency'),     '{"integrationId":"blast-motion-api-demo","metric":"ON_PLANE_EFFICIENCY"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE name = 'Blast On Plane Efficiency');
INSERT INTO data_source (name, type, metric_id, content)
SELECT 'Blast Early Connection',        'BAT_SENSOR', (SELECT id FROM metric WHERE name = 'Early Connection'),        '{"integrationId":"blast-motion-api-demo","metric":"EARLY_CONNECTION"}'
WHERE NOT EXISTS (SELECT 1 FROM data_source WHERE name = 'Blast Early Connection');

-- assessment_template (→ sport_dictionary)
INSERT INTO assessment_template (name, sport, description) VALUES
    ('Blast Hitting T',       'Baseball', 'Blast Motion assessment — hitting from tee'),
    ('Blast Live AB',         'Baseball', 'Blast Motion assessment — live at-bat'),
    ('Blast Pitching Machine','Baseball', 'Blast Motion assessment — pitching machine at 60ft');

-- assessment (→ player, sport_dictionary, assessment_template)
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
WHERE ds.name LIKE 'Blast %'
  AND cm.condition_id = (SELECT id FROM condition WHERE name = 'Hitting from T');

-- assessment_metric (→ assessment, conditional_metric, data_source)
INSERT INTO assessment_metric (assessment_id, conditional_metric_id, data_source_id)
SELECT
    a.id,
    cm.id,
    ds.id
FROM assessment a
CROSS JOIN conditional_metric cm
JOIN metric m ON m.id = cm.metric_id
JOIN data_source ds ON ds.metric_id = m.id
WHERE ds.name LIKE 'Blast %'
  AND cm.condition_id = (SELECT id FROM condition WHERE name = 'Hitting from T');

-- session for Blast Motion testing — status NULL = ready to start
-- Jake Thornton: clean session, no historical reps (use this to test start/stop)
INSERT INTO session (assessment_id, start_time) VALUES
    (
        (SELECT id FROM assessment WHERE player_id = (SELECT id FROM player WHERE name = 'Jake Thornton')),
        NOW()
    );

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
        WHEN '14U' THEN CASE cm.name WHEN 'Bat Speed' THEN 68 WHEN 'Attack Angle' THEN 15 WHEN 'Time to Contact' THEN 160 ELSE 50 END
        WHEN '13U' THEN CASE cm.name WHEN 'Bat Speed' THEN 62 WHEN 'Attack Angle' THEN 13 WHEN 'Time to Contact' THEN 170 ELSE 45 END
        WHEN '12U' THEN CASE cm.name WHEN 'Bat Speed' THEN 56 WHEN 'Attack Angle' THEN 12 WHEN 'Time to Contact' THEN 180 ELSE 40 END
    END
FROM model mo
CROSS JOIN conditional_metric cm
WHERE cm.condition_id = (SELECT id FROM condition WHERE name = 'Hitting from T');

-- report (no FK constraints)
INSERT INTO report (name, ext_ref) VALUES
    ('Jake Thornton — January 2024',  'ext://reports/jake-thornton-jan-2024'),
    ('Marcus Rivera — January 2024',  'ext://reports/marcus-rivera-jan-2024'),
    ('Caden Williams — January 2024', 'ext://reports/caden-williams-jan-2024');
