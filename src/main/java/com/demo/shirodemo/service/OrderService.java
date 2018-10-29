package com.demo.shirodemo.service;

import com.demo.shirodemo.entity.MerchandiseList;
import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.ResultForStudentOrder;
import com.demo.shirodemo.entity.table.OrdersEntity;
import org.springframework.stereotype.Service;

import java.util.List;


public interface OrderService {

    String merchandiseToString(List<MerchandiseList> lists);
    List<MerchandiseList> parseMerchandise(String MerchandiseString);
    //根据订单类型查询
    Result findAllByType(int type);
    Result findAll();
    //
    Result findAllByTypeAndCustPhone(int type, String phone,int i);


    Result findAllByCustomerPhoneWithList(String phone,int i);

    Result findAllByTechPhone(String phone);

    Result save(OrdersEntity ordersEntity);

    Result findByTypeAndStatus(int type,int status, int i );

    Result updateStatusById(int id, int status,String abandonReason);

    Result findOwnsOrders();
    Result abandonOrder(int id,String abandonReason);

    //软件用分配订单
    Result distributionOrder(int id);

    Result distributionOrderByName(int id,String name);

    //商家用 确认订单
    Result techCompleteOrder(int id);
    Result confirmOrder(int id);
    //商家发送取货通知 并修改状态
    Result sendMessageToCustomer(int id);
    Result completeOrder(int id);
    Result inDoubtOrder(int id);

    Result distributionDoubtOrder(int id);



}
