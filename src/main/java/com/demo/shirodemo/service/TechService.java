package com.demo.shirodemo.service;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.OrdersEntity;
import com.demo.shirodemo.entity.table.TechForOrders;
import com.demo.shirodemo.entity.table.UserEntity;
import org.springframework.transaction.annotation.Transactional;

public interface TechService {

    Result changeStatus(String token);

    Result getOneForOrder(OrdersEntity ordersEntity);

    TechForOrders save(TechForOrders techForOrders);

    Result getOne(String phone);

    Result getByName(String name);

    Result getAllByStatus();


    Result delOneRecord(OrdersEntity ordersEntity);
}
