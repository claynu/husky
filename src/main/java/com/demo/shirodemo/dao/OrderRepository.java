package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.table.OrdersEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderRepository  extends JpaRepository<OrdersEntity,Integer> {

    @Query(value = "select o from OrdersEntity o where o.customer_phone = ?2 and o.type = ?1")
    List<OrdersEntity> findAllByTypeAndPhone(int type,String phone);

    @Query(value = "select o from OrdersEntity o where o.customer_phone = ?1")
    List<OrdersEntity>  findAllByCustomerPhone(String phone);

    @Query(value = "select o from OrdersEntity o where o.type = ?1")
    List<OrdersEntity> findAllByType(int type);

    OrdersEntity save(OrdersEntity ordersEntity);

    @Query(value = "select o from OrdersEntity o where o.id = ?1")
    OrdersEntity findById(int id);

    @Query(value = "select o from OrdersEntity o where o.type = ?1 and o.status = ?2 order by o.status")
    List<OrdersEntity> findByTypeAndStatus(int type,int status);

    @Query(value = "select o from OrdersEntity o where o.type = ?1 and o.operation_phone = ?2")
    List<OrdersEntity> findAllByTechAndType(int type,String phone);


}
