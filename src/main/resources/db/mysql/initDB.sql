CREATE DATABASE IF NOT EXISTS Drakaris;

USE Drakaris;

CREATE TABLE IF NOT EXISTS `Users` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `login` varchar(64) NOT NULL,
  `passhash` varchar(64) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `login_UNIQUE` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Tweets` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `userID` bigint(20) unsigned NOT NULL,
  `text` text NOT NULL,
  `data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  CONSTRAINT `ID` FOREIGN KEY (`userID`) REFERENCES `Users` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Subscriptions` (
  `fromID` bigint(20) unsigned NOT NULL,
  `toID` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`toID`,`fromID`),
  CONSTRAINT `fromID` FOREIGN KEY (`fromID`) REFERENCES `Users` (`ID`),
  CONSTRAINT `toID` FOREIGN KEY (`toID`) REFERENCES `Users` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;