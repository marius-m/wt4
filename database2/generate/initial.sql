CREATE TABLE `ticket` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `code_project` VARCHAR(50) DEFAULT '' NOT NULL,
  `code_number` VARCHAR(50) DEFAULT '' NOT NULL,
  `code` VARCHAR(50) DEFAULT '' NOT NULL,
  `description` TEXT DEFAULT '' NOT NULL,
  `parent_id` BIGINT NOT NULL DEFAULT -1,
  `remote_id` BIGINT NOT NULL DEFAULT -1,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  `is_dirty` TINYINT NOT NULL DEFAULT 0,
  `is_error` TINYINT NOT NULL DEFAULT 0,
  `error_message` TEXT NOT NULL DEFAULT '',
  `fetchTime` BIGINT NOT NULL DEFAULT 0,
  `URL` VARCHAR(1000) NOT NULL DEFAULT ''
);
CREATE TABLE `worklog` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `start` BIGINT NOT NULL DEFAULT 0,
  `end` BIGINT NOT NULL DEFAULT 0,
  `duration` BIGINT NOT NULL DEFAULT 0,
  `code` VARCHAR(50) DEFAULT '' NOT NULL,
  `comment` TEXT DEFAULT '' NOT NULL,
  `remote_id` BIGINT NOT NULL DEFAULT -1,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  `is_dirty` TINYINT NOT NULL DEFAULT 0,
  `is_error` TINYINT NOT NULL DEFAULT 0,
  `error_message` TEXT NOT NULL DEFAULT '',
  `fetchTime` BIGINT NOT NULL DEFAULT 0,
  `URL` VARCHAR(1000) NOT NULL DEFAULT ''
);
