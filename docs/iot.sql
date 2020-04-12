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
  `password` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `create_time` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT NULL COMMENT '修改时间',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态',
  `last_message_id` bigint(255) NULL DEFAULT 1 COMMENT '最后推送消息ID',
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
