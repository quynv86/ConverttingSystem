-- phpMyAdmin SQL Dump
-- version 4.1.11
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 30, 2015 at 09:04 AM
-- Server version: 5.6.26
-- PHP Version: 5.5.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `wowza_analytics`
--

-- --------------------------------------------------------

--
-- Table structure for table `auth_perm`
--

CREATE TABLE IF NOT EXISTS `auth_perm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `auth_role`
--

CREATE TABLE IF NOT EXISTS `auth_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `auth_role`
--

INSERT INTO `auth_role` (`id`, `name`) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_MANAGE_THREADS');

-- --------------------------------------------------------

--
-- Table structure for table `auth_role_perm`
--

CREATE TABLE IF NOT EXISTS `auth_role_perm` (
  `role_id` int(11) NOT NULL,
  `perm_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`,`perm_id`),
  KEY `FK_RP_PERM_idx` (`perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `auth_user`
--

CREATE TABLE IF NOT EXISTS `auth_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(45) DEFAULT NULL,
  `first_name` varchar(15) DEFAULT NULL,
  `middle_name` varchar(15) DEFAULT NULL,
  `last_name` varchar(15) DEFAULT NULL,
  `full_name` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `gender` varchar(1) DEFAULT '0',
  `salt` varchar(45) DEFAULT NULL,
  `password` varchar(128) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `is_verified` tinyint(4) NOT NULL DEFAULT '1',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `user_type` tinyint(4) DEFAULT '1' COMMENT '0 -  system user; 1 - ; 2- affiliate; 4-sale staff',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`user_name`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `auth_user`
--

INSERT INTO `auth_user` (`id`, `user_name`, `first_name`, `middle_name`, `last_name`, `full_name`, `email`, `gender`, `salt`, `password`, `created_date`, `modified_date`, `is_verified`, `status`, `user_type`) VALUES
(1, 'admin', 'Admin', NULL, NULL, NULL, 'admin@yo.com', '1', '5876695f8e4e1811', 'IEkbnAKxLSX3d74SDOH5wBdgOu4=', '2014-07-03 22:21:33', NULL, 1, 1, 1),
(3, 'user', 'User', NULL, NULL, NULL, 'user@yo.com', '1', '5876695f8e4e1811', 'IEkbnAKxLSX3d74SDOH5wBdgOu4=', '2014-07-03 22:21:33', NULL, 1, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `auth_usermeta`
--

CREATE TABLE IF NOT EXISTS `auth_usermeta` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `meta_key` varchar(255) DEFAULT NULL,
  `meta_value` longtext,
  PRIMARY KEY (`id`),
  KEY `FK_UM_USER_idx` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `auth_user_role`
--

CREATE TABLE IF NOT EXISTS `auth_user_role` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK_UR_ROLE_idx` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `auth_user_role`
--

INSERT INTO `auth_user_role` (`user_id`, `role_id`) VALUES
(1, 1),
(1, 2),
(3, 2),
(1, 3);

-- --------------------------------------------------------

--
-- Table structure for table `publish_stream_log`
--

CREATE TABLE IF NOT EXISTS `publish_stream_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `datetime` datetime DEFAULT NULL,
  `stream_name` varchar(255) DEFAULT NULL,
  `action` varchar(45) DEFAULT NULL,
  `wowza_ip` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_INDEX` (`datetime`,`stream_name`,`action`,`wowza_ip`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `publish_stream_log`
--

INSERT INTO `publish_stream_log` (`id`, `datetime`, `stream_name`, `action`, `wowza_ip`) VALUES
(1, '2015-10-07 16:30:04', 'orglive_channel8_360p', 'Publish ', '123.30.50.68'),
(2, '2015-10-07 16:30:20', 'channel8_360p', 'Publish ', '123.30.50.68'),
(3, '2015-10-07 16:30:51', 'channel8_360p', 'UnPublish ', '123.30.50.68');

-- --------------------------------------------------------

--
-- Table structure for table `session_log`
--

CREATE TABLE IF NOT EXISTS `session_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `session_id` varchar(45) DEFAULT NULL,
  `user_id` varchar(45) DEFAULT NULL,
  `client_ip` varchar(45) DEFAULT NULL,
  `client_ip_api` varchar(45) DEFAULT NULL,
  `server` varchar(256) DEFAULT NULL,
  `application` varchar(45) DEFAULT NULL,
  `stream` varchar(45) DEFAULT NULL,
  `action` varchar(45) DEFAULT NULL,
  `result` tinyint(1) DEFAULT NULL,
  `hash` varchar(256) DEFAULT NULL,
  `reason` varchar(256) DEFAULT NULL,
  `query_uri` varchar(256) DEFAULT NULL,
  `referrer` varchar(1024) DEFAULT NULL,
  `user_agent` text,
  `wowza_ip` varchar(45) DEFAULT NULL,
  `client_app` varchar(128) DEFAULT NULL,
  `category` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `channel` varchar(45) DEFAULT NULL,
  `link_created_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `KEY_UNIQUE` (`created_date`,`session_id`,`user_id`,`wowza_ip`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=149046 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
