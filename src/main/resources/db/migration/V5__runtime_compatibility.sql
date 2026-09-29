-- Completa objetos requeridos por la versión actual sin recrear tablas ni borrar datos.
CREATE TABLE IF NOT EXISTS insurance_regimes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(30) NOT NULL UNIQUE,
  name VARCHAR(80) NOT NULL
);
-- MySQL 8.4 no admite IF NOT EXISTS para ADD COLUMN en todas las variantes.
-- Estas sentencias dinámicas hacen que la migración sea segura tanto para una
-- base nueva como para la base persistente que precede a este modelo.
SET @sql = (SELECT IF(COUNT(*) = 0,
  'ALTER TABLE eps_plans ADD COLUMN regime_id BIGINT NULL',
  'SELECT 1') FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'eps_plans' AND column_name = 'regime_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql = (SELECT IF(COUNT(*) = 0,
  'ALTER TABLE user_insurance_affiliations ADD COLUMN membership_number VARCHAR(80) NULL',
  'SELECT 1') FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'user_insurance_affiliations' AND column_name = 'membership_number');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql = (SELECT IF(COUNT(*) = 0,
  'ALTER TABLE user_insurance_affiliations ADD COLUMN is_current BOOLEAN NOT NULL DEFAULT TRUE',
  'SELECT 1') FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'user_insurance_affiliations' AND column_name = 'is_current');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
CREATE TABLE IF NOT EXISTS password_reset_tokens (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token_hash VARCHAR(64) NOT NULL UNIQUE,
  expires_at TIMESTAMP NOT NULL,
  used_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS reschedule_request_statuses (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(40) NOT NULL UNIQUE,
  name VARCHAR(80) NOT NULL,
  is_terminal BOOLEAN NOT NULL DEFAULT FALSE
);
INSERT IGNORE INTO reschedule_request_statuses(code,name,is_terminal) VALUES
 ('PENDING','Pendiente',FALSE),('APPROVED','Aprobada',TRUE),('REJECTED','Rechazada',TRUE),('CANCELLED','Cancelada por el usuario',TRUE);
CREATE TABLE IF NOT EXISTS reschedule_requests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  appointment_id BIGINT NOT NULL,
  requested_by_user_id BIGINT NOT NULL,
  requested_location_id BIGINT NOT NULL,
  status_id BIGINT NOT NULL,
  previous_start_at TIMESTAMP NOT NULL,
  previous_end_at TIMESTAMP NOT NULL,
  requested_start_at TIMESTAMP NOT NULL,
  requested_end_at TIMESTAMP NOT NULL,
  decision_reason VARCHAR(500),
  decided_by_user_id BIGINT,
  decided_at TIMESTAMP,
  patient_action_after_rejection VARCHAR(30)
);
CREATE TABLE IF NOT EXISTS reschedule_request_slots (
  request_id BIGINT NOT NULL,
  slot_id BIGINT NOT NULL,
  PRIMARY KEY(request_id,slot_id),
  UNIQUE(slot_id)
);
