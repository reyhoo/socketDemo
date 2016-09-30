/**insert into mt_user(mobile,password,nickname)values('15129237121','911004','minitalk_15129237121');
insert into mt_user(mobile,password,nickname)values('13032162523','911004','minitalk_13032162523');
insert into mt_user(mobile,password,nickname)values('13474274790','911004','minitalk_13474274790');
insert into mt_user(mobile,password,nickname)values('15191079112','911004','minitalk_15191079112');


desc mt_friend;

select * from mt_user;


delete from mt_friend;
insert into mt_friend(ownerId,userId,remarks)values(1,2,'rph');
insert into mt_friend(ownerId,userId,remarks)values(1,3,'f');
insert into mt_friend(ownerId,userId,remarks)values(1,11,'m');

select * from mt_friend;

select f.id as fid,f.ownerId,f.userId,f.remarks,u.*
from 
mt_friend f right join mt_user u
on f.userId=u.id where f.ownerId=1;
**/
update mt_user set password = '96e79218965eb72c92a549dd5a330112' where mobile='15129237121';
update mt_user set password = 'e3ceb5881a0a1fdaad01296d7554868d' where mobile='15129237121';

