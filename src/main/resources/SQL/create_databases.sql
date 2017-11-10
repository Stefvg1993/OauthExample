-- Create syntax for TABLE 'app_role'
CREATE TABLE `app_role` (
  `id`        BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(255)        DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8;

-- Create syntax for TABLE 'app_user'
CREATE TABLE `app_user` (
  `id`                BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `first_name`        VARCHAR(255) NOT NULL,
  `last_name`         VARCHAR(255) NOT NULL,
  `password`          VARCHAR(255) NOT NULL,
  `username`          VARCHAR(255) NOT NULL,
  `email`             VARCHAR(255) NOT NULL DEFAULT '',
  `active`            TINYINT(1)   NOT NULL DEFAULT '0',
  `confirmation_code` VARCHAR(255)          DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 30
  DEFAULT CHARSET = utf8;

-- Create syntax for TABLE 'user_role'
CREATE TABLE `user_role` (
  `user_id` BIGINT(20) NOT NULL,
  `role_id` BIGINT(20) NOT NULL,
  KEY `FK859n2jvi8ivhui0rl0esws6o` (`user_id`),
  KEY `FKa68196081fvovjhkek5m97n3y` (`role_id`),
  CONSTRAINT `FK859n2jvi8ivhui0rl0esws6o` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FKa68196081fvovjhkek5m97n3y` FOREIGN KEY (`role_id`) REFERENCES `app_role` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;