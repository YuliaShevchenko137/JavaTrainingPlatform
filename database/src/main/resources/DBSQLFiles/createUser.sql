--liquibase formatted sql

--changeset javal3:1
--real
grant all privileges to javal3 identified by javal3;

--for testing
grant all privileges to javal3test identified by javal3test;