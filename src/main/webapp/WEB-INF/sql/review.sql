   create table review(
   rnum int not null auto_increment primary key,//댓글의 seq
   content varchar(500) not null,//댓글 내용
   regdate date not null,//댓글 등록 날짜
   id varchar(10) not null,// 댓글 작성자
   contentsno int(7) not null,//댓글달릴 상품
   foreign key (contentsno) references contents(contentsno)
);

select * from review;
 
insert into reply(content, regdate, id, contentsno)
values('의견입니다.',sysdate(),'user1',34);
 
 
-- list(목록)
select rnum, content, regdate, id, contentsno
from review
where contentsno = 34
order by rnum DESC
limit 0, 3;
 
 
-- total(목록)
select count(*) from review
where contentsno = 34;
