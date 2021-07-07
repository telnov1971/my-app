CREATE TABLE `usr` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`activation_code` VARCHAR(255) NULL COLLATE 'utf8_general_ci',
	`active` BIT(1) NOT NULL,
	`email` VARCHAR(255) NULL COLLATE 'utf8_general_ci',
	`password` VARCHAR(255) NULL COLLATE 'utf8_general_ci',
	`username` VARCHAR(255) NULL COLLATE 'utf8_general_ci',
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=4
;
