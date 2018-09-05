CREATE TABLE IF NOT EXISTS `user` (
    `s_id` char(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL PRIMARY KEY,
    `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `game_queue` (
    `s_id` char(32) NOT NULL,
    `move` int(11) DEFAULT NULL,
    `chat` varchar(128) DEFAULT NULL,
    INDEX(`s_id`),
    FOREIGN KEY (`s_id`) REFERENCES `user` (`s_id`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `room` (
    `room_id` int(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `s_id` char(32) COLLATE utf8_bin NOT NULL UNIQUE,
    `expiration_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_matched` tinyint(1) DEFAULT '0',
    `s_id2` char(32) COLLATE utf8_bin DEFAULT NULL,
    `flag` tinyint(1) NOT NULL DEFAULT '0',
    FOREIGN KEY (`s_id`) REFERENCES `user` (`s_id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`s_id2`) REFERENCES `user` (`s_id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
