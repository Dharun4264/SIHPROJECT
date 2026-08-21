-- =====================================================================
-- Phase 1 seed data: static railway network foundation
-- Line modeled: Tiruchirappalli (TPJ) -- Karur (KRR) -- Erode Jn (ED)
--               -- Tiruppur (TUP) -- Coimbatore (CBE)
-- Explicit IDs are used for readability/traceability; sequences are
-- resynced at the end so subsequent API-created rows get correct ids.
--
-- This script runs on EVERY application startup (spring.sql.init.mode:
-- always), so it must be idempotent. The DELETEs below clear prior seed
-- rows (child tables first, to respect foreign keys) before re-inserting,
-- so restarts never collide on primary keys.
-- =====================================================================

-- ------------------------------------------------------------------
-- 0. Clear previous seed data (child -> parent order)
-- ------------------------------------------------------------------
DELETE FROM train_schedule;
DELETE FROM platform_compatible_train_type;
DELETE FROM platform;
DELETE FROM loop_line;
DELETE FROM track_section;
DELETE FROM train;
DELETE FROM station;

-- ------------------------------------------------------------------
-- 1. STATIONS (5)
-- ------------------------------------------------------------------
INSERT INTO station (id, code, name, latitude, longitude, station_type) VALUES
    (1, 'TPJ', 'Tiruchirappalli Junction', 10.8038, 78.6864, 'JUNCTION'),
    (2, 'KRR', 'Karur Junction',           10.9601, 78.0766, 'INTERMEDIATE'),
    (3, 'ED',  'Erode Junction',           11.3410, 77.7172, 'JUNCTION'),
    (4, 'TUP', 'Tiruppur',                 11.1085, 77.3411, 'INTERMEDIATE'),
    (5, 'CBE', 'Coimbatore Junction',      11.0018, 76.9629, 'TERMINAL');

-- ------------------------------------------------------------------
-- 2. TRACK SECTIONS (mix of single and double track)
-- ------------------------------------------------------------------
INSERT INTO track_section (id, from_station_id, to_station_id, section_type, length_km, max_speed_kmph) VALUES
    (1, 1, 2, 'SINGLE', 75.0, 100),  -- TPJ -> KRR (single line, needs crossing at KRR)
    (2, 2, 3, 'DOUBLE', 60.0, 110),  -- KRR -> ED  (double line)
    (3, 3, 4, 'SINGLE', 45.0, 100),  -- ED  -> TUP (single line, needs crossing at TUP)
    (4, 4, 5, 'DOUBLE', 50.0, 120);  -- TUP -> CBE (double line)

-- ------------------------------------------------------------------
-- 3. PLATFORMS (2-3 per station)
-- ------------------------------------------------------------------
INSERT INTO platform (id, station_id, platform_number, length_m) VALUES
    (1, 1, '1', 650.0), (2, 1, '2', 650.0), (3, 1, '3', 550.0),   -- TPJ (junction, 3)
    (4, 2, '1', 600.0), (5, 2, '2', 500.0),                        -- KRR (2)
    (6, 3, '1', 650.0), (7, 3, '2', 650.0), (8, 3, '3', 500.0),   -- ED  (junction, 3)
    (9, 4, '1', 550.0), (10, 4, '2', 500.0),                       -- TUP (2)
    (11, 5, '1', 650.0), (12, 5, '2', 650.0), (13, 5, '3', 600.0); -- CBE (terminal, 3)

-- Platform compatible train types (element collection table)
INSERT INTO platform_compatible_train_type (platform_id, train_type) VALUES
    (1, 'EXPRESS'), (1, 'PASSENGER'), (1, 'FREIGHT'),
    (2, 'EXPRESS'), (2, 'PASSENGER'),
    (3, 'PASSENGER'), (3, 'SUBURBAN'),
    (4, 'EXPRESS'), (4, 'PASSENGER'), (4, 'FREIGHT'),
    (5, 'PASSENGER'), (5, 'SUBURBAN'),
    (6, 'EXPRESS'), (6, 'PASSENGER'), (6, 'FREIGHT'),
    (7, 'EXPRESS'), (7, 'PASSENGER'),
    (8, 'PASSENGER'), (8, 'SUBURBAN'),
    (9, 'EXPRESS'), (9, 'PASSENGER'),
    (10, 'PASSENGER'), (10, 'SUBURBAN'),
    (11, 'EXPRESS'), (11, 'PASSENGER'), (11, 'FREIGHT'),
    (12, 'EXPRESS'), (12, 'PASSENGER'),
    (13, 'PASSENGER'), (13, 'SUBURBAN');

-- ------------------------------------------------------------------
-- 4. LOOP LINES (at least 2 stations - here: KRR and TUP, the two
--    intermediate stations that sit between single-track sections)
-- ------------------------------------------------------------------
INSERT INTO loop_line (id, station_id, loop_code, max_length_m) VALUES
    (1, 2, 'KRR-L1', 650.0),  -- Karur loop
    (2, 4, 'TUP-L1', 600.0),  -- Tiruppur loop
    (3, 3, 'ED-L1',  650.0);  -- Erode Jn also has a loop (junction, extra flexibility)

-- ------------------------------------------------------------------
-- 5. TRAINS (10)
-- ------------------------------------------------------------------
INSERT INTO train (id, train_number, name, type, priority, max_speed_kmph, length_m, origin_station_id, destination_station_id) VALUES
    (1,  '12671', 'Nellai Express',        'EXPRESS',    2, 110, 500.0, 1, 5),  -- TPJ -> CBE
    (2,  '12672', 'Nellai Express Return', 'EXPRESS',    2, 110, 500.0, 5, 1),  -- CBE -> TPJ
    (3,  '12675', 'Kovai Express',         'EXPRESS',    1, 120, 480.0, 5, 1),  -- CBE -> TPJ (top priority)
    (4,  '16609', 'West Coast Express',    'EXPRESS',    2, 110, 500.0, 1, 5),  -- TPJ -> CBE
    (5,  '56712', 'TPJ-CBE Passenger',     'PASSENGER',  5, 90,  400.0, 1, 5),  -- TPJ -> CBE
    (6,  '56713', 'CBE-TPJ Passenger',     'PASSENGER',  5, 90,  400.0, 5, 1),  -- CBE -> TPJ
    (7,  '07123', 'KRR-ED MEMU',           'SUBURBAN',   6, 80,  300.0, 2, 3),  -- KRR -> ED
    (8,  '60123', 'ED-CBE Passenger',      'PASSENGER',  5, 90,  380.0, 3, 5),  -- ED -> CBE
    (9,  '56714', 'TUP-KRR Passenger',     'PASSENGER',  5, 90,  350.0, 4, 2),  -- TUP -> KRR
    (10, 'TFR001', 'TPJ-CBE Goods',        'FREIGHT',    9, 60,  650.0, 1, 5);  -- TPJ -> CBE (lowest priority)

-- ------------------------------------------------------------------
-- 6. TRAIN SCHEDULES (ordered stops per train; arrival null at origin,
--    departure null at destination)
-- ------------------------------------------------------------------

-- Train 1: 12671 Nellai Express, TPJ -> CBE
INSERT INTO train_schedule (id, train_id, station_id, sequence_no, scheduled_arrival, scheduled_departure, planned_platform_id) VALUES
    (1, 1, 1, 1, NULL,      '06:00:00', 1),
    (2, 1, 2, 2, '07:25:00', '07:30:00', 4),
    (3, 1, 3, 3, '08:35:00', '08:40:00', 6),
    (4, 1, 4, 4, '09:25:00', '09:28:00', 9),
    (5, 1, 5, 5, '10:15:00', NULL,       11);

-- Train 2: 12672 Nellai Express Return, CBE -> TPJ
INSERT INTO train_schedule (id, train_id, station_id, sequence_no, scheduled_arrival, scheduled_departure, planned_platform_id) VALUES
    (6, 2, 5, 1, NULL,      '16:00:00', 12),
    (7, 2, 4, 2, '16:45:00', '16:48:00', 10),
    (8, 2, 3, 3, '17:33:00', '17:38:00', 7),
    (9, 2, 2, 4, '18:43:00', '18:48:00', 5),
    (10, 2, 1, 5, '20:10:00', NULL,      2);

-- Train 3: 12675 Kovai Express (fastest, top priority), CBE -> TPJ
INSERT INTO train_schedule (id, train_id, station_id, sequence_no, scheduled_arrival, scheduled_departure, planned_platform_id) VALUES
    (11, 3, 5, 1, NULL,      '05:30:00', 11),
    (12, 3, 4, 2, '06:08:00', '06:10:00', 9),
    (13, 3, 3, 3, '06:50:00', '06:53:00', 6),
    (14, 3, 2, 4, '07:53:00', '07:55:00', 4),
    (15, 3, 1, 5, '09:10:00', NULL,       1);

-- Train 4: 16609 West Coast Express, TPJ -> CBE
INSERT INTO train_schedule (id, train_id, station_id, sequence_no, scheduled_arrival, scheduled_departure, planned_platform_id) VALUES
    (16, 4, 1, 1, NULL,      '11:00:00', 2),
    (17, 4, 2, 2, '12:25:00', '12:30:00', 5),
    (18, 4, 3, 3, '13:35:00', '13:40:00', 7),
    (19, 4, 4, 4, '14:25:00', '14:28:00', 9),
    (20, 4, 5, 5, '15:15:00', NULL,       12);

-- Train 5: 56712 TPJ-CBE Passenger (slower, more halts)
INSERT INTO train_schedule (id, train_id, station_id, sequence_no, scheduled_arrival, scheduled_departure, planned_platform_id) VALUES
    (21, 5, 1, 1, NULL,      '06:30:00', 3),
    (22, 5, 2, 2, '08:15:00', '08:25:00', 4),
    (23, 5, 3, 3, '09:45:00', '09:55:00', 8),
    (24, 5, 4, 4, '10:50:00', '10:55:00', 10),
    (25, 5, 5, 5, '11:55:00', NULL,       13);

-- Train 6: 56713 CBE-TPJ Passenger
INSERT INTO train_schedule (id, train_id, station_id, sequence_no, scheduled_arrival, scheduled_departure, planned_platform_id) VALUES
    (26, 6, 5, 1, NULL,      '13:00:00', 13),
    (27, 6, 4, 2, '13:58:00', '14:03:00', 10),
    (28, 6, 3, 3, '14:58:00', '15:08:00', 8),
    (29, 6, 2, 4, '16:28:00', '16:38:00', 4),
    (30, 6, 1, 5, '18:20:00', NULL,       3);

-- Train 7: 07123 KRR-ED MEMU (short suburban hop)
INSERT INTO train_schedule (id, train_id, station_id, sequence_no, scheduled_arrival, scheduled_departure, planned_platform_id) VALUES
    (31, 7, 2, 1, NULL,      '07:00:00', 5),
    (32, 7, 3, 2, '08:05:00', NULL,       8);

-- Train 8: 60123 ED-CBE Passenger
INSERT INTO train_schedule (id, train_id, station_id, sequence_no, scheduled_arrival, scheduled_departure, planned_platform_id) VALUES
    (33, 8, 3, 1, NULL,      '09:00:00', 7),
    (34, 8, 4, 2, '09:50:00', '09:55:00', 9),
    (35, 8, 5, 3, '10:55:00', NULL,       11);

-- Train 9: 56714 TUP-KRR Passenger (reverse short hop)
INSERT INTO train_schedule (id, train_id, station_id, sequence_no, scheduled_arrival, scheduled_departure, planned_platform_id) VALUES
    (36, 9, 4, 1, NULL,      '15:00:00', 10),
    (37, 9, 3, 2, '15:45:00', '15:50:00', 6),
    (38, 9, 2, 3, '16:55:00', NULL,       5);

-- Train 10: TFR001 Freight Goods, TPJ -> CBE (slow, lowest priority,
-- expected to be held at loop lines for crossings/overtakes)
INSERT INTO train_schedule (id, train_id, station_id, sequence_no, scheduled_arrival, scheduled_departure, planned_platform_id) VALUES
    (39, 10, 1, 1, NULL,      '04:00:00', 1),
    (40, 10, 2, 2, '06:10:00', '06:40:00', NULL),  -- held at Karur loop (no platform, uses loop line)
    (41, 10, 3, 3, '08:10:00', '08:30:00', 6),
    (42, 10, 4, 4, '09:50:00', '10:20:00', NULL),  -- held at Tiruppur loop
    (43, 10, 5, 5, '11:40:00', NULL,       11);

-- MySQL AUTO_INCREMENT resync lives in data-mysql.sql (mysql profile only).
