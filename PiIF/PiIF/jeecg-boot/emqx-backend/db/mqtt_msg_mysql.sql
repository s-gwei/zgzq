/*
 Navicat Premium Data Transfer

 Source Server         : PilF
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 21/10/2020 10:09:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mqtt_msg
-- ----------------------------
DROP TABLE IF EXISTS `mqtt_msg`;
CREATE TABLE `mqtt_msg`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `from_client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `topic` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `from_username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `qos` tinyint(1) NOT NULL DEFAULT 0,
  `retain` tinyint(1) NULL DEFAULT NULL,
  `payload` json NULL,
  `ts` datetime(0) NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `topic_index`(`id`, `topic`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mqtt_msg
-- ----------------------------
INSERT INTO `mqtt_msg` VALUES (1, 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 'emqx', 0, 0, '{\"id\": \"2\", \"params\": {\"values\": [{\"t\": 1600756500035, \"aaa.Ramp1\": 95, \"bbb.Random1\": 47}, {\"t\": 1600756499042, \"aaa.Ramp1\": 63, \"bbb.Random1\": -9}, {\"t\": 1600756502036, \"aaa.Ramp1\": 91, \"bbb.Random1\": -12}, {\"t\": 1600756496041, \"aaa.Ramp1\": 35, \"bbb.Random1\": 34}, {\"t\": 1600756504041, \"aaa.Ramp1\": 87, \"bbb.Random1\": -16}, {\"t\": 1600756498042, \"aaa.Ramp1\": 99, \"bbb.Random1\": -1}, {\"t\": 1600756497037, \"aaa.Ramp1\": 67, \"bbb.Random1\": 9}, {\"t\": 1600756501036, \"aaa.Ramp1\": 59, \"bbb.Random1\": 7}, {\"t\": 1600756503039, \"aaa.Ramp1\": 55, \"bbb.Random1\": 37}, {\"t\": 1600756505039, \"aaa.Ramp1\": 51, \"bbb.Random1\": 60}], \"timestamp\": 1600756505311}, \"version\": \"1.0\"}', '2020-10-20 17:27:20');
INSERT INTO `mqtt_msg` VALUES (2, 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 'emqx', 0, 0, '{\"id\": \"3\", \"params\": {\"values\": [{\"t\": 1600756500035, \"aaa.Ramp1\": 95, \"bbb.Random1\": 47}, {\"t\": 1600756499042, \"aaa.Ramp1\": 63, \"bbb.Random1\": -9}, {\"t\": 1600756502036, \"aaa.Ramp1\": 91, \"bbb.Random1\": -12}, {\"t\": 1600756496041, \"aaa.Ramp1\": 35, \"bbb.Random1\": 34}, {\"t\": 1600756504041, \"aaa.Ramp1\": 87, \"bbb.Random1\": -16}, {\"t\": 1600756498042, \"aaa.Ramp1\": 99, \"bbb.Random1\": -1}, {\"t\": 1600756497037, \"aaa.Ramp1\": 67, \"bbb.Random1\": 9}, {\"t\": 1600756501036, \"aaa.Ramp1\": 59, \"bbb.Random1\": 7}, {\"t\": 1600756503039, \"aaa.Ramp1\": 55, \"bbb.Random1\": 37}, {\"t\": 1600756505039, \"aaa.Ramp1\": 51, \"bbb.Random1\": 60}], \"timestamp\": 1600756505311}, \"version\": \"1.0\"}', '2020-10-20 17:27:25');
INSERT INTO `mqtt_msg` VALUES (3, 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 'emqx', 0, 0, '{\"id\": \"4\", \"params\": {\"values\": [{\"t\": 1600756500035, \"aaa.Ramp1\": 95, \"bbb.Random1\": 47}, {\"t\": 1600756499042, \"aaa.Ramp1\": 63, \"bbb.Random1\": -9}, {\"t\": 1600756502036, \"aaa.Ramp1\": 91, \"bbb.Random1\": -12}, {\"t\": 1600756496041, \"aaa.Ramp1\": 35, \"bbb.Random1\": 34}, {\"t\": 1600756504041, \"aaa.Ramp1\": 87, \"bbb.Random1\": -16}, {\"t\": 1600756498042, \"aaa.Ramp1\": 99, \"bbb.Random1\": -1}, {\"t\": 1600756497037, \"aaa.Ramp1\": 67, \"bbb.Random1\": 9}, {\"t\": 1600756501036, \"aaa.Ramp1\": 59, \"bbb.Random1\": 7}, {\"t\": 1600756503039, \"aaa.Ramp1\": 55, \"bbb.Random1\": 37}, {\"t\": 1600756505039, \"aaa.Ramp1\": 51, \"bbb.Random1\": 60}], \"timestamp\": 1600756505311}, \"version\": \"1.0\"}', '2020-10-20 17:27:30');
INSERT INTO `mqtt_msg` VALUES (4, 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 'emqx', 0, 0, '{\"id\": \"5\", \"params\": {\"values\": [{\"t\": 1600756500035, \"aaa.Ramp1\": 95, \"bbb.Random1\": 47}, {\"t\": 1600756499042, \"aaa.Ramp1\": 63, \"bbb.Random1\": -9}, {\"t\": 1600756502036, \"aaa.Ramp1\": 91, \"bbb.Random1\": -12}, {\"t\": 1600756496041, \"aaa.Ramp1\": 35, \"bbb.Random1\": 34}, {\"t\": 1600756504041, \"aaa.Ramp1\": 87, \"bbb.Random1\": -16}, {\"t\": 1600756498042, \"aaa.Ramp1\": 99, \"bbb.Random1\": -1}, {\"t\": 1600756497037, \"aaa.Ramp1\": 67, \"bbb.Random1\": 9}, {\"t\": 1600756501036, \"aaa.Ramp1\": 59, \"bbb.Random1\": 7}, {\"t\": 1600756503039, \"aaa.Ramp1\": 55, \"bbb.Random1\": 37}, {\"t\": 1600756505039, \"aaa.Ramp1\": 51, \"bbb.Random1\": 60}], \"timestamp\": 1600756505311}, \"version\": \"1.0\"}', '2020-10-20 17:27:35');
INSERT INTO `mqtt_msg` VALUES (5, 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 'emqx', 0, 0, '{\"id\": \"6\", \"params\": {\"values\": [{\"t\": 1600756500035, \"aaa.Ramp1\": 95, \"bbb.Random1\": 47}, {\"t\": 1600756499042, \"aaa.Ramp1\": 63, \"bbb.Random1\": -9}, {\"t\": 1600756502036, \"aaa.Ramp1\": 91, \"bbb.Random1\": -12}, {\"t\": 1600756496041, \"aaa.Ramp1\": 35, \"bbb.Random1\": 34}, {\"t\": 1600756504041, \"aaa.Ramp1\": 87, \"bbb.Random1\": -16}, {\"t\": 1600756498042, \"aaa.Ramp1\": 99, \"bbb.Random1\": -1}, {\"t\": 1600756497037, \"aaa.Ramp1\": 67, \"bbb.Random1\": 9}, {\"t\": 1600756501036, \"aaa.Ramp1\": 59, \"bbb.Random1\": 7}, {\"t\": 1600756503039, \"aaa.Ramp1\": 55, \"bbb.Random1\": 37}, {\"t\": 1600756505039, \"aaa.Ramp1\": 51, \"bbb.Random1\": 60}], \"timestamp\": 1600756505311}, \"version\": \"1.0\"}', '2020-10-20 17:27:40');
INSERT INTO `mqtt_msg` VALUES (6, 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 'emqx', 0, 0, '{\"id\": \"7\", \"params\": {\"values\": [{\"t\": 1600756500035, \"aaa.Ramp1\": 95, \"bbb.Random1\": 47}, {\"t\": 1600756499042, \"aaa.Ramp1\": 63, \"bbb.Random1\": -9}, {\"t\": 1600756502036, \"aaa.Ramp1\": 91, \"bbb.Random1\": -12}, {\"t\": 1600756496041, \"aaa.Ramp1\": 35, \"bbb.Random1\": 34}, {\"t\": 1600756504041, \"aaa.Ramp1\": 87, \"bbb.Random1\": -16}, {\"t\": 1600756498042, \"aaa.Ramp1\": 99, \"bbb.Random1\": -1}, {\"t\": 1600756497037, \"aaa.Ramp1\": 67, \"bbb.Random1\": 9}, {\"t\": 1600756501036, \"aaa.Ramp1\": 59, \"bbb.Random1\": 7}, {\"t\": 1600756503039, \"aaa.Ramp1\": 55, \"bbb.Random1\": 37}, {\"t\": 1600756505039, \"aaa.Ramp1\": 51, \"bbb.Random1\": 60}], \"timestamp\": 1600756505311}, \"version\": \"1.0\"}', '2020-10-20 17:27:45');
INSERT INTO `mqtt_msg` VALUES (7, 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 'emqx', 0, 0, '{\"id\": \"8\", \"params\": {\"values\": [{\"t\": 1600756500035, \"aaa.Ramp1\": 95, \"bbb.Random1\": 47}, {\"t\": 1600756499042, \"aaa.Ramp1\": 63, \"bbb.Random1\": -9}, {\"t\": 1600756502036, \"aaa.Ramp1\": 91, \"bbb.Random1\": -12}, {\"t\": 1600756496041, \"aaa.Ramp1\": 35, \"bbb.Random1\": 34}, {\"t\": 1600756504041, \"aaa.Ramp1\": 87, \"bbb.Random1\": -16}, {\"t\": 1600756498042, \"aaa.Ramp1\": 99, \"bbb.Random1\": -1}, {\"t\": 1600756497037, \"aaa.Ramp1\": 67, \"bbb.Random1\": 9}, {\"t\": 1600756501036, \"aaa.Ramp1\": 59, \"bbb.Random1\": 7}, {\"t\": 1600756503039, \"aaa.Ramp1\": 55, \"bbb.Random1\": 37}, {\"t\": 1600756505039, \"aaa.Ramp1\": 51, \"bbb.Random1\": 60}], \"timestamp\": 1600756505311}, \"version\": \"1.0\"}', '2020-10-20 17:27:50');
INSERT INTO `mqtt_msg` VALUES (8, 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 'emqx', 0, 0, '{\"id\": \"9\", \"params\": {\"values\": [{\"t\": 1600756500035, \"aaa.Ramp1\": 95, \"bbb.Random1\": 47}, {\"t\": 1600756499042, \"aaa.Ramp1\": 63, \"bbb.Random1\": -9}, {\"t\": 1600756502036, \"aaa.Ramp1\": 91, \"bbb.Random1\": -12}, {\"t\": 1600756496041, \"aaa.Ramp1\": 35, \"bbb.Random1\": 34}, {\"t\": 1600756504041, \"aaa.Ramp1\": 87, \"bbb.Random1\": -16}, {\"t\": 1600756498042, \"aaa.Ramp1\": 99, \"bbb.Random1\": -1}, {\"t\": 1600756497037, \"aaa.Ramp1\": 67, \"bbb.Random1\": 9}, {\"t\": 1600756501036, \"aaa.Ramp1\": 59, \"bbb.Random1\": 7}, {\"t\": 1600756503039, \"aaa.Ramp1\": 55, \"bbb.Random1\": 37}, {\"t\": 1600756505039, \"aaa.Ramp1\": 51, \"bbb.Random1\": 60}], \"timestamp\": 1600756505311}, \"version\": \"1.0\"}', '2020-10-20 17:27:55');
INSERT INTO `mqtt_msg` VALUES (9, 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 'emqx', 0, 0, '{\"id\": \"10\", \"params\": {\"values\": [{\"t\": 1600756500035, \"aaa.Ramp1\": 95, \"bbb.Random1\": 47}, {\"t\": 1600756499042, \"aaa.Ramp1\": 63, \"bbb.Random1\": -9}, {\"t\": 1600756502036, \"aaa.Ramp1\": 91, \"bbb.Random1\": -12}, {\"t\": 1600756496041, \"aaa.Ramp1\": 35, \"bbb.Random1\": 34}, {\"t\": 1600756504041, \"aaa.Ramp1\": 87, \"bbb.Random1\": -16}, {\"t\": 1600756498042, \"aaa.Ramp1\": 99, \"bbb.Random1\": -1}, {\"t\": 1600756497037, \"aaa.Ramp1\": 67, \"bbb.Random1\": 9}, {\"t\": 1600756501036, \"aaa.Ramp1\": 59, \"bbb.Random1\": 7}, {\"t\": 1600756503039, \"aaa.Ramp1\": 55, \"bbb.Random1\": 37}, {\"t\": 1600756505039, \"aaa.Ramp1\": 51, \"bbb.Random1\": 60}], \"timestamp\": 1600756505311}, \"version\": \"1.0\"}', '2020-10-20 17:28:00');
INSERT INTO `mqtt_msg` VALUES (10, 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 'emqx', 0, 0, '{\"id\": \"11\", \"params\": {\"values\": [{\"t\": 1600756500035, \"aaa.Ramp1\": 95, \"bbb.Random1\": 47}, {\"t\": 1600756499042, \"aaa.Ramp1\": 63, \"bbb.Random1\": -9}, {\"t\": 1600756502036, \"aaa.Ramp1\": 91, \"bbb.Random1\": -12}, {\"t\": 1600756496041, \"aaa.Ramp1\": 35, \"bbb.Random1\": 34}, {\"t\": 1600756504041, \"aaa.Ramp1\": 87, \"bbb.Random1\": -16}, {\"t\": 1600756498042, \"aaa.Ramp1\": 99, \"bbb.Random1\": -1}, {\"t\": 1600756497037, \"aaa.Ramp1\": 67, \"bbb.Random1\": 9}, {\"t\": 1600756501036, \"aaa.Ramp1\": 59, \"bbb.Random1\": 7}, {\"t\": 1600756503039, \"aaa.Ramp1\": 55, \"bbb.Random1\": 37}, {\"t\": 1600756505039, \"aaa.Ramp1\": 51, \"bbb.Random1\": 60}], \"timestamp\": 1600756505311}, \"version\": \"1.0\"}', '2020-10-20 17:28:05');

SET FOREIGN_KEY_CHECKS = 1;
