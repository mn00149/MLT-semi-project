package com.study.orders;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.study.cart.CartService;
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
  
  @Autowired
  @Qualifier("com.study.orders.OrderServiceImpl")
  private OrderService service;

  @Autowired
  @Qualifier("com.study.cart.CartServiceImpl")
  private CartService cartservice;
  
  @PostMapping("/order/create/{str}")
  public String create(
      @PathVariable String str,
      int tot, String payment, String reqtext,
      HttpServletRequest request, HttpSession session 
      ) {    
      String id = (String)session.getAttribute("id");
      String mname = (String)session.getAttribute("mname");
    
      OrdersDTO dto = new OrdersDTO();
      dto.setId(id);
      dto.setMname(mname);
      dto.setTotal(tot);
      dto.setPayment(payment);
      dto.setReqtext(reqtext);
      
      List<OrderdetailDTO> list = new ArrayList<OrderdetailDTO>();
      
      if(str.equals("cart")) {
        String cno = request.getParameter("cno");//상품번호들
        String qty = request.getParameter("qtys");//수량들
        String size = request.getParameter("size");//사이즈들
        
        String[] no = cno.split(",");
        for(int i=0; i<no.length; i++) {
          int contentsno = Integer.parseInt(no[i]);
          ContentsDTO cdto = cservice.detail(contentsno);
          OrderdetailDTO ddto = new OrderdetailDTO();
          ddto.setContentsno(contentsno);
          ddto.setPname(cdto.getPname());
          ddto.setQuantity(Integer.parseInt(qty.split(",")[i]));
          ddto.setSize(size.split(",")[i]);
          list.add(ddto);
        }
        
      }else if(str.equals("order")) {
        int contentsno = Integer.parseInt(request.getParameter("contentsno"));
        ContentsDTO cdto = cservice.detail(contentsno);
        OrderdetailDTO ddto = new OrderdetailDTO();
        ddto.setContentsno(contentsno);
        ddto.setPname(cdto.getPname());
        ddto.setQuantity(Integer.parseInt(request.getParameter("qty")));
        ddto.setSize(request.getParameter("size"));
        list.add(ddto);
      }
      
      dto.setList(list);
      
      try {
        service.create(dto);//주문
        if(str.equals("cart"))
          cartservice.deleteAll(id); //장바구니 비우기
        
        return "redirect:/member/mypage";
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return "error";
      }
  }
  
  @GetMapping("/order/create/cart/{cno}/{qty}/{size}")
  public String create(
      @PathVariable String cno,
      @PathVariable String qty,
      @PathVariable String size,
      HttpSession session,Model model ) {    
      //log.info("orderController(qty/size/contentsno):"+qty+"/"+size+"/"+contentsno);
      
      String id = (String)session.getAttribute("id");
      MemberDTO mdto = mservice.read(id);
      String[] no = cno.split(","); //"23,21" => "23", "21"
      List<ContentsDTO> list = new ArrayList<ContentsDTO>(); 
      for(int i=0; i<no.length; i++) {
        int contentsno = Integer.parseInt(no[i]);
        ContentsDTO dto = cservice.detail(contentsno);
        list.add(dto);
      }
      model.addAttribute("list", list); //상품목록
      model.addAttribute("mdto", mdto); //회원정보
      model.addAttribute("qtys", qty); //수량들(orderdetail 테이블에 저장, 총금액)
      model.addAttribute("size", size); //size들(orderdetail 테이블에 저장)
      model.addAttribute("str", "cart"); //장바구니에서 주문을 한것
      model.addAttribute("cno", cno); //상품번호들
        
    return "/order/create";
  }
  @GetMapping("/order/create/order/{contentsno}/{qty}/{size}")
  public String create(
      @PathVariable int contentsno,
      @PathVariable int qty,
      @PathVariable String size,
      HttpSession session,Model model ) {    
      //log.info("orderController(qty/size/contentsno):"+qty+"/"+size+"/"+contentsno);
      
      String id = (String)session.getAttribute("id");
      MemberDTO mdto = mservice.read(id);
      List<ContentsDTO> list = new ArrayList<ContentsDTO>(); 
      ContentsDTO dto = cservice.detail(contentsno);
      list.add(dto);
      
      model.addAttribute("list", list); //상품목록
      model.addAttribute("mdto", mdto); //회원정보
      model.addAttribute("qty", qty); //수량(orderdetail 테이블에 저장, 총금액)
      model.addAttribute("size", size); //size(orderdetail 테이블에 저장)
      model.addAttribute("str", "order"); //상품상세보기에서 주문을 한것
      model.addAttribute("contentsno", contentsno); //단품상품번호
        
    return "/order/create";
  }
  
  
  
}
