/**
*�û���
**/
create table mt_user (id integer primary key auto_increment ,mobile varchar(50),password varchar(255),nickname varchar(255));
/**
*���ѱ�
**/
create table mt_friend(id integer primary key auto_increment ,ownerId integer,userId integer,remarks varchar(255));

/**
**��Ϣ��
**/
create table mt_message(id integer primary key auto_increment ,fromId integer,toId integer,msgBody varchar(255),time Long,status integer default 0);


show tables;
