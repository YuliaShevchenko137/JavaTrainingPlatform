--liquibase formatted sql

--changeset javal3:6

insert into OBJECT_TYPES values(12,'Answer',11);
insert into OBJECT_TYPES values(13,'Course',11);
insert into OBJECT_TYPES values(11,'DBObject',10);
insert into OBJECT_TYPES values(10,'EntityImpl',null);
insert into OBJECT_TYPES values(14,'Evaluating',10);
insert into OBJECT_TYPES values(15,'Material',11);
insert into OBJECT_TYPES values(16,'ProgramClass',17);
insert into OBJECT_TYPES values(17,'Question',11);
insert into OBJECT_TYPES values(18,'Role',10);
insert into OBJECT_TYPES values(19,'State',10);
insert into OBJECT_TYPES values(20,'Task',11);
insert into OBJECT_TYPES values(21,'Test',17);
insert into OBJECT_TYPES values(22,'TestType',10);
insert into OBJECT_TYPES values(23,'Thema',11);
insert into OBJECT_TYPES values(24,'User',10);
