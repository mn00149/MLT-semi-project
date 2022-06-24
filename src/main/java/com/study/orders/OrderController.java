package com.study.orders;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.study.contents.ContentsDTO;
import com.study.contents.ContentsService;
import com.study.member.MemberDTO;
import com.study.member.MemberService;

@Controller
public class OrderController {

  private static final Logger log = LoggerFactory.getLogger(OrderController.class);
  
  @Autowired
  @Qualifier("com.study.contents.ContentsServiceImpl")
  private ContentsService cservice;
  @Autowired
  @Qualifier("com.study.member.MemberServiceImpl")
  private MemberService mservice;
  
  
  @GetMapping("/order/create/cart/{cno}/{qty}/{size}")
  public String create(
      @PathVariable String cno, //모두 ,로 연결된 문자열
      @PathVariable String qty, 
      @PathVariable String size,
      HttpSession session, Model model) {

    String id = (String)session.getAttribute("id");
    MemberDTO mdto = mservice.read(id);
    String[] no = cno.split(","); //"23,21" => "23", "21" 
    List<ContentsDTO> list = new ArrayList<ContentsDTO>();
    for(int i=0; i<no.length; i++) {
      int contentsno = Integer.parseInt(no[i]);
      ContentsDTO dto = cservice.detail(contentsno);
      list.add(dto);
    }
    
    model.addAttribute("list", list);//상품목록
    model.addAttribute("mdto", mdto);//회원정보(배송지등)
    model.addAttribute("qtys", qty);//수량들(orderdetail 테이블에 저장할데이터, 총금액만들것)
    model.addAttribute("size", size);//size들(orderdetail 테이블에 저장할데이터)
    model.addAttribute("str", "cart");//장바구니에서 주문을 한 것(상품상세보기 아니면 장바구니에서 주문한걸 표기)--어디서 받냐에따라 받는 데이터 달라짐
    model.addAttribute("cno", cno);//상품번호들
    
    return "/order/createForm";
  }
  @GetMapping("/order/create/order/{contentsno}/{qty}/{size}") //qty는 count..
  public String create(
      @PathVariable int contentsno, 
      @PathVariable int qty, 
      @PathVariable String size,
      HttpSession session, Model model) {
    
    //log.info("orderController(qty/size/contentsno): " + qty + "/" + size + "/" +contentsno);
    String id = (String)session.getAttribute("id");
    MemberDTO mdto = mservice.read(id);
    List<ContentsDTO> list = new ArrayList<ContentsDTO>();
    ContentsDTO dto = cservice.detail(contentsno);
    list.add(dto);
    
    model.addAttribute("list", list);//상품목록
    model.addAttribute("mdto", mdto);//회원정보(배송지등)
    model.addAttribute("qty", qty);//수량(orderdetail 테이블에 저장할데이터, 총금액만들것)
    model.addAttribute("size", size);//size(orderdetail 테이블에 저장할데이터)
    model.addAttribute("str", "order");//상품상세보기에서 주문을 한 것(상품상세보기 아니면 장바구니에서 주문한걸 표기)--어디서 받냐에따라 받는 데이터 달라짐
    model.addAttribute("contentsno", contentsno);//단품상품번호
      
    return "/order/createForm";
  }
}