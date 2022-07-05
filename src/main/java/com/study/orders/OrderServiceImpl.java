package com.study.orders;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
@Service("com.study.orders.OrderServiceImpl")
public class OrderServiceImpl implements OrderService {
  @Autowired
  private OrderMapper mapper;

  @Override
  public void create(OrdersDTO dto) throws Exception {
    // TODO Auto-generated method stub
    mapper.createOrder(dto);//dto안에 orderno가 들어온다
    int orderno = dto.getOrderno();
    System.out.println(orderno);
    
    List<OrderdetailDTO> list = dto.getList();
    
    for(int i=0; i<list.size(); i++) {
      OrderdetailDTO odto = list.get(i);
      odto.setOrderno(orderno);
      mapper.createDetail(odto);
    }
  }

}
