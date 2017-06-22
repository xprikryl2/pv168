-- my-test-data.sql

INSERT INTO Person (NAME, SURNAME, EMAIL) VALUES ('Johnny','Cash', 'johnnycash@rocks.it');

INSERT INTO Person (NAME, SURNAME, EMAIL) VALUES ('Joshua','Cash', 'joshuacash@rocks.it');

INSERT INTO Person (NAME, SURNAME, EMAIL) VALUES ('John','Smith', 'johnsmith@gmail.com');

INSERT INTO Dragon (name, element, speed, born) VALUES ('Grigori','FIRE',150,'1450-10-10');

INSERT INTO Dragon (name, element, speed, born) VALUES ('Icey','WATER',100,'1450-05-01');

INSERT INTO Reservation (timeFrom, timeTo,borrower,dragon,moneyPaid,pricePerHour) VALUES ('1990-05-01 10:00:00','1990-05-10 10:00:00',2,1,10,10);

INSERT INTO Reservation (timeFrom, timeTo,borrower,dragon,moneyPaid,pricePerHour) VALUES ('1990-04-01 10:00:00','1990-04-10 10:00:00',2,1,10,10);

INSERT INTO Reservation (timeFrom, timeTo,borrower,dragon,moneyPaid,pricePerHour) VALUES ('1990-10-10 10:00:00','1990-11-11 10:00:00',2,1,200,10);

INSERT INTO Reservation (timeFrom, timeTo,borrower,dragon,moneyPaid,pricePerHour) VALUES ('1991-06-01 10:00:00','1991-06-10 10:00:00',1,2,0,20);

INSERT INTO Reservation (timeFrom, timeTo,borrower,dragon,moneyPaid,pricePerHour) VALUES ('1991-08-10 10:00:00','1991-08-12 10:00:00',2,2,100,20);
