/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : mrp

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2024-05-28 22:09:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_bom
-- ----------------------------
DROP TABLE IF EXISTS `t_bom`;
CREATE TABLE `t_bom` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent` varchar(255) DEFAULT NULL,
  `comp` varchar(255) DEFAULT NULL,
  `Q_P` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_bom
-- ----------------------------
INSERT INTO `t_bom` VALUES ('9', 'X', 'B', '1');
INSERT INTO `t_bom` VALUES ('10', 'X', 'C', '2');
INSERT INTO `t_bom` VALUES ('11', 'C', 'D', '1');
INSERT INTO `t_bom` VALUES ('12', 'C', 'E', '2');
INSERT INTO `t_bom` VALUES ('13', 'Y', 'C', '1');
INSERT INTO `t_bom` VALUES ('14', 'Y', 'E', '1');

-- ----------------------------
-- Table structure for t_inventory
-- ----------------------------
DROP TABLE IF EXISTS `t_inventory`;
CREATE TABLE `t_inventory` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `P_No` varchar(255) DEFAULT NULL,
  `OH` varchar(255) DEFAULT NULL,
  `AL` varchar(255) DEFAULT NULL,
  `OO0` int(11) DEFAULT NULL,
  `OO1` int(11) DEFAULT NULL,
  `OO2` int(11) DEFAULT NULL,
  `OO3` int(11) DEFAULT NULL,
  `OO4` int(11) DEFAULT NULL,
  `OO5` int(11) DEFAULT NULL,
  `OO6` int(11) DEFAULT NULL,
  `OO7` int(11) DEFAULT NULL,
  `OO8` int(11) DEFAULT NULL,
  `OO9` int(11) DEFAULT NULL,
  `OO10` int(11) DEFAULT NULL,
  `OO11` int(11) DEFAULT NULL,
  `OO12` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_inventory
-- ----------------------------
INSERT INTO `t_inventory` VALUES ('7', 'X', '18', '10', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `t_inventory` VALUES ('9', 'Y', '16', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `t_inventory` VALUES ('10', 'C', '20', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `t_inventory` VALUES ('11', 'D', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `t_inventory` VALUES ('12', 'E', '30', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `t_inventory` VALUES ('14', 'B', '10', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');

-- ----------------------------
-- Table structure for t_itemmaster
-- ----------------------------
DROP TABLE IF EXISTS `t_itemmaster`;
CREATE TABLE `t_itemmaster` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `P_No` varchar(255) DEFAULT NULL,
  `LT` int(11) DEFAULT NULL,
  `ST` int(11) DEFAULT NULL,
  `SS` int(11) DEFAULT NULL,
  `LLC` int(11) DEFAULT NULL,
  `LSR` varchar(255) DEFAULT NULL,
  `LS` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_itemmaster
-- ----------------------------
INSERT INTO `t_itemmaster` VALUES ('7', 'X', '4', '0', '5', '0', 'LFL', '1');
INSERT INTO `t_itemmaster` VALUES ('8', 'Y', '2', '0', '6', '0', 'LFL', '1');
INSERT INTO `t_itemmaster` VALUES ('9', 'B', '3', '0', '0', '1', 'LFL', '1');
INSERT INTO `t_itemmaster` VALUES ('10', 'C', '2', '0', '0', '1', 'LFL', '1');
INSERT INTO `t_itemmaster` VALUES ('11', 'D', '1', '0', '0', '2', 'FOQ', '200');
INSERT INTO `t_itemmaster` VALUES ('12', 'E', '1', '0', '0', '2', 'FOQ', '500');

-- ----------------------------
-- Table structure for t_mrp
-- ----------------------------
DROP TABLE IF EXISTS `t_mrp`;
CREATE TABLE `t_mrp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `P_No` varchar(255) DEFAULT NULL,
  `LT` int(11) DEFAULT NULL,
  `ST` int(11) DEFAULT NULL,
  `SS` int(11) DEFAULT NULL,
  `OH` varchar(255) DEFAULT NULL,
  `LSR` varchar(255) DEFAULT NULL,
  `LS` int(11) DEFAULT NULL,
  `AL` varchar(255) DEFAULT NULL,
  `GR0` int(11) DEFAULT NULL,
  `GR1` int(11) DEFAULT NULL,
  `GR2` int(11) DEFAULT NULL,
  `GR3` int(11) DEFAULT NULL,
  `GR4` int(11) DEFAULT NULL,
  `GR5` int(11) DEFAULT NULL,
  `GR6` int(11) DEFAULT NULL,
  `GR7` int(11) DEFAULT NULL,
  `GR8` int(11) DEFAULT NULL,
  `GR9` int(11) DEFAULT NULL,
  `GR10` int(11) DEFAULT NULL,
  `GR11` int(11) DEFAULT NULL,
  `GR12` int(11) DEFAULT NULL,
  `SR0` int(11) DEFAULT NULL,
  `SR1` int(11) DEFAULT NULL,
  `SR2` int(11) DEFAULT NULL,
  `SR3` int(11) DEFAULT NULL,
  `SR4` int(11) DEFAULT NULL,
  `SR5` int(11) DEFAULT NULL,
  `SR6` int(11) DEFAULT NULL,
  `SR7` int(11) DEFAULT NULL,
  `SR8` int(11) DEFAULT NULL,
  `SR9` int(11) DEFAULT NULL,
  `SR10` int(11) DEFAULT NULL,
  `SR11` int(11) DEFAULT NULL,
  `SR12` int(11) DEFAULT NULL,
  `POH0` int(11) DEFAULT NULL,
  `POH1` int(11) DEFAULT NULL,
  `POH2` int(11) DEFAULT NULL,
  `POH3` int(11) DEFAULT NULL,
  `POH4` int(11) DEFAULT NULL,
  `POH5` int(11) DEFAULT NULL,
  `POH6` int(11) DEFAULT NULL,
  `POH7` int(11) DEFAULT NULL,
  `POH8` int(11) DEFAULT NULL,
  `POH9` int(11) DEFAULT NULL,
  `POH10` int(11) DEFAULT NULL,
  `POH11` int(11) DEFAULT NULL,
  `POH12` int(11) DEFAULT NULL,
  `PAB0` int(11) DEFAULT NULL,
  `PAB1` int(11) DEFAULT NULL,
  `PAB2` int(11) DEFAULT NULL,
  `PAB3` int(11) DEFAULT NULL,
  `PAB4` int(11) DEFAULT NULL,
  `PAB5` int(11) DEFAULT NULL,
  `PAB6` int(11) DEFAULT NULL,
  `PAB7` int(11) DEFAULT NULL,
  `PAB8` int(11) DEFAULT NULL,
  `PAB9` int(11) DEFAULT NULL,
  `PAB10` int(11) DEFAULT NULL,
  `PAB11` int(11) DEFAULT NULL,
  `PAB12` int(11) DEFAULT NULL,
  `NR0` int(11) DEFAULT NULL,
  `NR1` int(11) DEFAULT NULL,
  `NR2` int(11) DEFAULT NULL,
  `NR3` int(11) DEFAULT NULL,
  `NR4` int(11) DEFAULT NULL,
  `NR5` int(11) DEFAULT NULL,
  `NR6` int(11) DEFAULT NULL,
  `NR7` int(11) DEFAULT NULL,
  `NR8` int(11) DEFAULT NULL,
  `NR9` int(11) DEFAULT NULL,
  `NR10` int(11) DEFAULT NULL,
  `NR11` int(11) DEFAULT NULL,
  `NR12` int(11) DEFAULT NULL,
  `PORcpt0` int(11) DEFAULT NULL,
  `PORcpt1` int(11) DEFAULT NULL,
  `PORcpt2` int(11) DEFAULT NULL,
  `PORcpt3` int(11) DEFAULT NULL,
  `PORcpt4` int(11) DEFAULT NULL,
  `PORcpt5` int(11) DEFAULT NULL,
  `PORcpt6` int(11) DEFAULT NULL,
  `PORcpt7` int(11) DEFAULT NULL,
  `PORcpt8` int(11) DEFAULT NULL,
  `PORcpt9` int(11) DEFAULT NULL,
  `PORcpt10` int(11) DEFAULT NULL,
  `PORcpt11` int(11) DEFAULT NULL,
  `PORcpt12` int(11) DEFAULT NULL,
  `POR0` int(11) DEFAULT NULL,
  `POR1` int(11) DEFAULT NULL,
  `POR2` int(11) DEFAULT NULL,
  `POR3` int(11) DEFAULT NULL,
  `POR4` int(11) DEFAULT NULL,
  `POR5` int(11) DEFAULT NULL,
  `POR6` int(11) DEFAULT NULL,
  `POR7` int(11) DEFAULT NULL,
  `POR8` int(11) DEFAULT NULL,
  `POR9` int(11) DEFAULT NULL,
  `POR10` int(11) DEFAULT NULL,
  `POR11` int(11) DEFAULT NULL,
  `POR12` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_mrp
-- ----------------------------
INSERT INTO `t_mrp` VALUES ('97', 'B', '3', '0', '0', '10', 'LFL', '1', '0', '0', '0', '0', '0', '100', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '-1', '10', '10', '10', '-90', '0', '0', '0', '0', '0', '0', '0', '0', '-1', '10', '10', '10', '0', '0', '0', '0', '0', '0', '0', '0', '0', '-1', '0', '0', '0', '90', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '90', '0', '0', '0', '0', '0', '0', '0', '0', '0', '90', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `t_mrp` VALUES ('98', 'C', '2', '0', '0', '20', 'LFL', '1', '0', '0', '0', '0', '0', '200', '0', '190', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '-1', '20', '20', '20', '-180', '0', '-190', '0', '0', '0', '0', '0', '0', '-1', '20', '20', '20', '0', '0', '0', '0', '0', '0', '0', '0', '0', '-1', '0', '0', '0', '180', '0', '190', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '180', '0', '190', '0', '0', '0', '0', '0', '0', '0', '0', '180', '0', '190', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `t_mrp` VALUES ('99', 'D', '1', '0', '0', '0', 'FOQ', '200', '0', '0', '0', '180', '0', '190', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '-1', '0', '-180', '20', '-170', '30', '30', '30', '30', '30', '30', '30', '30', '-1', '0', '20', '20', '30', '30', '30', '30', '30', '30', '30', '30', '30', '-1', '0', '180', '0', '170', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '200', '0', '200', '0', '0', '0', '0', '0', '0', '0', '0', '0', '200', '0', '200', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `t_mrp` VALUES ('100', 'E', '1', '0', '0', '30', 'FOQ', '500', '0', '0', '0', '360', '0', '380', '0', '190', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '-1', '30', '-330', '170', '-210', '290', '100', '100', '100', '100', '100', '100', '100', '-1', '30', '170', '170', '290', '290', '100', '100', '100', '100', '100', '100', '100', '-1', '0', '330', '0', '210', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '500', '0', '500', '0', '0', '0', '0', '0', '0', '0', '0', '0', '500', '0', '500', '0', '0', '0', '0', '0', '0', '0', '0', '0');

-- ----------------------------
-- Table structure for t_tpop
-- ----------------------------
DROP TABLE IF EXISTS `t_tpop`;
CREATE TABLE `t_tpop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `P_No` varchar(255) DEFAULT NULL,
  `LT` int(11) DEFAULT NULL,
  `ST` int(11) DEFAULT NULL,
  `SS` int(11) DEFAULT NULL,
  `OH` varchar(255) DEFAULT NULL,
  `LSR` varchar(255) DEFAULT NULL,
  `LS` int(11) DEFAULT NULL,
  `AL` varchar(255) DEFAULT NULL,
  `GR0` int(11) DEFAULT NULL,
  `GR1` int(11) DEFAULT NULL,
  `GR2` int(11) DEFAULT NULL,
  `GR3` int(11) DEFAULT NULL,
  `GR4` int(11) DEFAULT NULL,
  `GR5` int(11) DEFAULT NULL,
  `GR6` int(11) DEFAULT NULL,
  `GR7` int(11) DEFAULT NULL,
  `GR8` int(11) DEFAULT NULL,
  `GR9` int(11) DEFAULT NULL,
  `GR10` int(11) DEFAULT NULL,
  `GR11` int(11) DEFAULT NULL,
  `GR12` int(11) DEFAULT NULL,
  `SR0` int(11) DEFAULT NULL,
  `SR1` int(11) DEFAULT NULL,
  `SR2` int(11) DEFAULT NULL,
  `SR3` int(11) DEFAULT NULL,
  `SR4` int(11) DEFAULT NULL,
  `SR5` int(11) DEFAULT NULL,
  `SR6` int(11) DEFAULT NULL,
  `SR7` int(11) DEFAULT NULL,
  `SR8` int(11) DEFAULT NULL,
  `SR9` int(11) DEFAULT NULL,
  `SR10` int(11) DEFAULT NULL,
  `SR11` int(11) DEFAULT NULL,
  `SR12` int(11) DEFAULT NULL,
  `POH0` int(11) DEFAULT NULL,
  `POH1` int(11) DEFAULT NULL,
  `POH2` int(11) DEFAULT NULL,
  `POH3` int(11) DEFAULT NULL,
  `POH4` int(11) DEFAULT NULL,
  `POH5` int(11) DEFAULT NULL,
  `POH6` int(11) DEFAULT NULL,
  `POH7` int(11) DEFAULT NULL,
  `POH8` int(11) DEFAULT NULL,
  `POH9` int(11) DEFAULT NULL,
  `POH10` int(11) DEFAULT NULL,
  `POH11` int(11) DEFAULT NULL,
  `POH12` int(11) DEFAULT NULL,
  `PAB0` int(11) DEFAULT NULL,
  `PAB1` int(11) DEFAULT NULL,
  `PAB2` int(11) DEFAULT NULL,
  `PAB3` int(11) DEFAULT NULL,
  `PAB4` int(11) DEFAULT NULL,
  `PAB5` int(11) DEFAULT NULL,
  `PAB6` int(11) DEFAULT NULL,
  `PAB7` int(11) DEFAULT NULL,
  `PAB8` int(11) DEFAULT NULL,
  `PAB9` int(11) DEFAULT NULL,
  `PAB10` int(11) DEFAULT NULL,
  `PAB11` int(11) DEFAULT NULL,
  `PAB12` int(11) DEFAULT NULL,
  `NR0` int(11) DEFAULT NULL,
  `NR1` int(11) DEFAULT NULL,
  `NR2` int(11) DEFAULT NULL,
  `NR3` int(11) DEFAULT NULL,
  `NR4` int(11) DEFAULT NULL,
  `NR5` int(11) DEFAULT NULL,
  `NR6` int(11) DEFAULT NULL,
  `NR7` int(11) DEFAULT NULL,
  `NR8` int(11) DEFAULT NULL,
  `NR9` int(11) DEFAULT NULL,
  `NR10` int(11) DEFAULT NULL,
  `NR11` int(11) DEFAULT NULL,
  `NR12` int(11) DEFAULT NULL,
  `PORcpt0` int(11) DEFAULT NULL,
  `PORcpt1` int(11) DEFAULT NULL,
  `PORcpt2` int(11) DEFAULT NULL,
  `PORcpt3` int(11) DEFAULT NULL,
  `PORcpt4` int(11) DEFAULT NULL,
  `PORcpt5` int(11) DEFAULT NULL,
  `PORcpt6` int(11) DEFAULT NULL,
  `PORcpt7` int(11) DEFAULT NULL,
  `PORcpt8` int(11) DEFAULT NULL,
  `PORcpt9` int(11) DEFAULT NULL,
  `PORcpt10` int(11) DEFAULT NULL,
  `PORcpt11` int(11) DEFAULT NULL,
  `PORcpt12` int(11) DEFAULT NULL,
  `POR0` int(11) DEFAULT NULL,
  `POR1` int(11) DEFAULT NULL,
  `POR2` int(11) DEFAULT NULL,
  `POR3` int(11) DEFAULT NULL,
  `POR4` int(11) DEFAULT NULL,
  `POR5` int(11) DEFAULT NULL,
  `POR6` int(11) DEFAULT NULL,
  `POR7` int(11) DEFAULT NULL,
  `POR8` int(11) DEFAULT NULL,
  `POR9` int(11) DEFAULT NULL,
  `POR10` int(11) DEFAULT NULL,
  `POR11` int(11) DEFAULT NULL,
  `POR12` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_tpop
-- ----------------------------
INSERT INTO `t_tpop` VALUES ('1', 'X', '4', '0', '5', '18', 'LFL', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '103', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '3', '3', '3', '3', '3', '3', '3', '3', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '100', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '100', '0', '0', '0', '0', '0', '0', '0', '0', '100', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `t_tpop` VALUES ('2', 'Y', '2', '0', '6', '16', 'LFL', '1', '0', '0', '0', '0', '0', '0', '0', '0', '200', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '10', '10', '10', '10', '10', '10', '10', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '190', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '190', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '190', '0', '0', '0', '0', '0', '0');
