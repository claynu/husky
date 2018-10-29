package com.demo.shirodemo.service;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.FinanceEntity;
import com.demo.shirodemo.entity.table.OrdersEntity;

import java.util.List;


/**
 * 增
 * 删
 * 查
 * 改
 */
public interface FinanceService {


    Result save(FinanceEntity financeEntity);

    Result findAllNotConfirm();
    Result findAllConfirm();
    Result findAll();
    Result findByOperateName(String operate_name);
    FinanceEntity findById(int id);
    Result findAllByType(int type);

    int getProfit(OrdersEntity ordersEntity);
    List<FinanceEntity> findAllByDescribe_infoContaining(String info);
}
