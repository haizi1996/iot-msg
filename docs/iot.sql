/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : localhost:3306
 Source Schema         : iot

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 12/04/2020 09:25:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '123456' COMMENT '密码',
  `create_time` timestamp(0) NOT NULL DEFAULT now() COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT now() COMMENT '修改时间',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态',
  `last_message_id` bigint(255) NOT NULL DEFAULT 1 COMMENT '最后推送消息ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'wangwu', '123456', '2019-10-28 21:06:43', '2019-10-28 21:06:43', 1, 1);
INSERT INTO `user` VALUES (100, 'zhangsan', '123456', '2019-10-28 21:06:43', '2019-10-28 21:06:43', 1, 0);

SET FOREIGN_KEY_CHECKS = 1;


CREATE DEFINER=`root`@`%` PROCEDURE `adduser`()
BEGIN

	SET @i = 2;
	WHILE @i<2000000 DO
	INSERT INTO `user` (username) VALUES ( CONCAT("user" , @i ));

	SET @i = @i + 1;
END WHILE;
END


CREATE TABLE `on_IMGroup` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '群名称',
  `avatar` varchar(256) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '群头像',
  `creator` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建者用户id',
  `type` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '群组类型，1-固定;2-临时群',
  `userCnt` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '成员人数',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '是否删除,0-正常，1-删除',
  `version` int(11) unsigned NOT NULL DEFAULT '1' COMMENT '群版本号',
  `lastChated` timestamp(0) NOT NULL DEFAULT now() COMMENT '最后聊天时间',
  `updated` timestamp(0) NOT NULL DEFAULT now() COMMENT '更新时间',
  `created`timestamp(0) NOT NULL DEFAULT now() COMMENT '创建时间',
  `flag` int(11) DEFAULT '0',
  `disable_send_msg` int(11) DEFAULT '0' COMMENT '禁言开关',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`(191)),
  KEY `idx_creator` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=172 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='IM群信息';

DROP TABLE IF EXISTS `on_IMGroupMember`;
CREATE TABLE `on_IMGroupMember` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `groupId` int(11) unsigned NOT NULL COMMENT '群Id',
  `userId` int(11) unsigned NOT NULL COMMENT '用户id',
  `status` tinyint(4) unsigned NOT NULL DEFAULT '1' COMMENT '是否退出群，0-正常，1-已退出',
  `remak` varchar(50) DEFAULT NULL,
  `created` timestamp(0) NOT NULL DEFAULT now() COMMENT '创建时间',
  `updated` timestamp(0) NOT NULL DEFAULT now() COMMENT '更新时间',
  `disable_send_msg` int(11) DEFAULT '0' COMMENT '是否禁言',
  `role` int(11) DEFAULT '0' COMMENT '是否管理员',
  `flag` int(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_groupId_userId_status` (`groupId`,`userId`,`status`),
  KEY `idx_userId_status_updated` (`userId`,`status`,`updated`),
  KEY `idx_groupId_updated` (`groupId`,`updated`)
) ENGINE=InnoDB AUTO_INCREMENT=541 DEFAULT CHARSET=utf8 COMMENT='用户和群的关系表';