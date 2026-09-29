-- Completa columnas que existían con otro nombre en instalaciones anteriores.
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE refresh_tokens ADD COLUMN token_id VARCHAR(64) NULL UNIQUE', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'refresh_tokens' AND column_name = 'token_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE specialties ADD COLUMN appointment_duration_minutes INT NOT NULL DEFAULT 30', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'specialties' AND column_name = 'appointment_duration_minutes');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE specialties ADD COLUMN requires_admin_approval BOOLEAN NOT NULL DEFAULT TRUE', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'specialties' AND column_name = 'requires_admin_approval');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE appointments ADD COLUMN insurance_affiliation_id BIGINT NULL', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'appointments' AND column_name = 'insurance_affiliation_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE appointments ADD COLUMN created_by_user_id BIGINT NULL', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'appointments' AND column_name = 'created_by_user_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE appointments ADD COLUMN approved_by_user_id BIGINT NULL', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'appointments' AND column_name = 'approved_by_user_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE appointments ADD COLUMN approved_at TIMESTAMP NULL', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'appointments' AND column_name = 'approved_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE reschedule_request_statuses ADD COLUMN is_terminal BOOLEAN NOT NULL DEFAULT FALSE', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'reschedule_request_statuses' AND column_name = 'is_terminal');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
