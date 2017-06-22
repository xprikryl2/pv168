-- schema-javadb.sql
-- DDL commands for JavaDB/Derby
CREATE TABLE person (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name  VARCHAR(255),
  surname VARCHAR(255),
  email VARCHAR(255)
);

CREATE TABLE dragon (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255),
    element VARCHAR(42),
    speed INT,
    born DATE
);

CREATE TABLE reservation (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    timeFrom TIMESTAMP NOT NULL,
    timeTo TIMESTAMP,
    borrower BIGINT NOT NULL references person,
    dragon BIGINT NOT NULL references dragon,
    moneyPaid DECIMAL(19, 2) NOT NULL,
    pricePerHour DECIMAL(19, 2) NOT NULL
);

