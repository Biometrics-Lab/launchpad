-- Dev seed data — runs after Liquibase migrations.
-- Only tables that coexist safely with factory.cleanup() FK-delete ordering.
-- Leaf tables (assessment_metric, session_metric, rep_metric, template_metric, etc.)
-- are intentionally omitted: factory.cleanup() deletes their parents first, causing
-- FK violations if those leaves have rows that tests didn't create and clean up.

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

-- session1 (→ assessment)
INSERT INTO session1 (assessment_id, start_time) VALUES
    ((SELECT id FROM assessment WHERE player_id = (SELECT id FROM player WHERE name = 'Jake Thornton')),  '2024-01-15 09:00:00'),
    ((SELECT id FROM assessment WHERE player_id = (SELECT id FROM player WHERE name = 'Marcus Rivera')),  '2024-01-15 09:30:00'),
    ((SELECT id FROM assessment WHERE player_id = (SELECT id FROM player WHERE name = 'Caden Williams')), '2024-01-15 10:00:00');

-- rep (→ session1)
INSERT INTO rep (session1_id, start_time) VALUES
    (
        (SELECT s.id FROM session1 s JOIN assessment a ON a.id = s.assessment_id JOIN player p ON p.id = a.player_id WHERE p.name = 'Jake Thornton'),
        '2024-01-15 09:05:00'
    ),
    (
        (SELECT s.id FROM session1 s JOIN assessment a ON a.id = s.assessment_id JOIN player p ON p.id = a.player_id WHERE p.name = 'Marcus Rivera'),
        '2024-01-15 09:35:00'
    ),
    (
        (SELECT s.id FROM session1 s JOIN assessment a ON a.id = s.assessment_id JOIN player p ON p.id = a.player_id WHERE p.name = 'Caden Williams'),
        '2024-01-15 10:05:00'
    );

-- report (no FK constraints)
INSERT INTO report (name, ext_ref) VALUES
    ('Jake Thornton — January 2024',  'ext://reports/jake-thornton-jan-2024'),
    ('Marcus Rivera — January 2024',  'ext://reports/marcus-rivera-jan-2024'),
    ('Caden Williams — January 2024', 'ext://reports/caden-williams-jan-2024');
