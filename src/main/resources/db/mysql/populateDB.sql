
INSERT INTO `Drakaris`.`Users` (`ID`, `login`,`passhash`) VALUES (1, "Jon Snow", "123");
INSERT INTO `Drakaris`.`Users` (`ID`, `login`,`passhash`) VALUES (2, "Eddard Stark", "234");
INSERT INTO `Drakaris`.`Users` (`ID`, `login`,`passhash`) VALUES (3, "Jaime Lannister", "567");
INSERT INTO `Drakaris`.`Users` (`ID`, `login`,`passhash`) VALUES (4, "Ygritte", "634");
INSERT INTO `Drakaris`.`Users` (`ID`, `login`,`passhash`) VALUES (5, "Tirion Lannister", "634");
INSERT INTO `Drakaris`.`Users` (`ID`, `login`,`passhash`) VALUES (6, "Arya Stark", "634");
INSERT INTO `Drakaris`.`Users` (`ID`, `login`,`passhash`) VALUES (7, "Cersei Lannister", "211");
INSERT INTO `Drakaris`.`Users` (`ID`, `login`,`passhash`) VALUES (8, "Daenerys Targaryen", "111");

INSERT INTO `Drakaris`.`Tweets` (`ID`,`userID`,`text`,`data`) VALUES
						(1, 4, 'You know nothing, Jon Snow', '2013-08-05 18:19:03');
INSERT INTO `Drakaris`.`Tweets` (`ID`,`userID`,`text`,`data`) VALUES
						(2, 2, 'Winter is coming', '2010-06-19 20:10:07');
INSERT INTO `Drakaris`.`Tweets` (`ID`,`userID`,`text`,`data`) VALUES
						(3, 8, 'Drakaris', '2012-12-12 14:55:11');
INSERT INTO `Drakaris`.`Tweets` (`ID`,`userID`,`text`,`data`) VALUES
						(4, 6, 'Valar Morghulis', '2014-03-29 18:00:15');

INSERT INTO `Drakaris`.`Subscriptions` (`fromID`, `toID`) VALUES (1, 4);
INSERT INTO `Drakaris`.`Subscriptions` (`fromID`, `toID`) VALUES (3, 7);
INSERT INTO `Drakaris`.`Subscriptions` (`fromID`, `toID`) VALUES (2, 1);
INSERT INTO `Drakaris`.`Subscriptions` (`fromID`, `toID`) VALUES (2, 6);
INSERT INTO `Drakaris`.`Subscriptions` (`fromID`, `toID`) VALUES (5, 3);
INSERT INTO `Drakaris`.`Subscriptions` (`fromID`, `toID`) VALUES (5, 7);