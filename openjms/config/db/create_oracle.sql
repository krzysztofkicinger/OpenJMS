drop table system_data;
create table system_data (
  id                     NUMBER(3) not null,
  version                varchar2(20) not null,
  creationDate           date not null
);
create unique index system_data_pk on system_data(id);

drop table seeds;
create table seeds (
  name                   varchar2(20) not null,
  seed                   NUMBER(28) not null
);
create unique index seeds_pk on seeds(name);

drop table destinations;
create table destinations (
  name                   varchar2(255) not null,
  isQueue                NUMBER(3) not null,
  destinationId          NUMBER(28) not null
);
create unique index destinations_pk on destinations(name);

drop table messages;
create table messages (
  messageId             varchar2(64) not null,
  destinationId         NUMBER(28) not null,
  priority              NUMBER(3),
  createTime            NUMBER(28) not null,
  expiryTime            NUMBER(28),
  processed             NUMBER(3),
  messageBlob           LONG raw not null
);
create index messages_pk on messages(messageId);

DROP TABLE message_handles;
CREATE TABLE message_handles (
   messageId            varchar2(64) NOT NULL,
   destinationId        NUMBER(28) NOT NULL,
   consumerId           NUMBER(28) NOT NULL,
   priority             NUMBER(3),
   acceptedTime         NUMBER(28),
   sequenceNumber       NUMBER(28),
   expiryTime           NUMBER(28),
   delivered            NUMBER(3)
);
CREATE INDEX message_handles_pk ON message_handles(destinationId, consumerId, messageId);

drop table consumers;
create table consumers (
  name                 varchar2(255) not null,
  destinationId        NUMBER(28) not null,
  consumerId           NUMBER(28) not NULL,
  created              NUMBER(28) NOT NULL
);
create unique index consumers_pk on consumers(name, destinationId);

drop table users;
create table users (
  username             varchar2(50) not null,
  password             varchar2(50) not null
);
create unique index users_pk on users(username);