package com.study.reply;

import java.util.List;
import java.util.Map;

public interface ReplyService {
  int create(ReplyDTO replyDTO);
  
  List<ReplyDTO> list(Map map);

  ReplyDTO read(int rnum);
 
  int update(ReplyDTO replyDTO); 
  
  int delete(int rnum);

  int total(int contentsno);
}
