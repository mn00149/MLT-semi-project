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
        String cno = request.getParameter("cno");//???????????????
        String qty = request.getParameter("qtys");//?????????
        String size = request.getParameter("size");//????????????
        
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
        service.create(dto);//??????
        if(str.equals("cart"))
          cartservice.deleteAll(id); //???????????? ?????????
        
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
      model.addAttribute("list", list); //????????????
      model.addAttribute("mdto", mdto); //????????????
      model.addAttribute("qtys", qty); //?????????(orderdetail ???????????? ??????, ?????????)
      model.addAttribute("size", size); //size???(orderdetail ???????????? ??????)
      model.addAttribute("str", "cart"); //?????????????????? ????????? ??????
      model.addAttribute("cno", cno); //???????????????
        
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
      
      model.addAttribute("list", list); //????????????
      model.addAttribute("mdto", mdto); //????????????
      model.addAttribute("qty", qty); //??????(orderdetail ???????????? ??????, ?????????)
      model.addAttribute("size", size); //size(orderdetail ???????????? ??????)
      model.addAttribute("str", "order"); //???????????????????????? ????????? ??????
      model.addAttribute("contentsno", contentsno); //??????????????????
        
    return "/order/create";
  }
  
  
  
}
