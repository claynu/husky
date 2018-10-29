package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.ResultForOrder;
import com.demo.shirodemo.entity.table.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ForOrdersRepository extends JpaRepository<ResultForOrder,Integer> {

    @Query(value = "select * from for_order where customer_phone = ?2 and type = ?1",nativeQuery = true)
    List<ResultForOrder> findAllByTypeAndPhone(int type, String phone);

    @Query(value = "select * from for_order where customer_phone = ?1",nativeQuery = true)
    List<ResultForOrder>  findAllByCustomerPhone(String phone);

    @Query(value = "select * from for_order where type = ?1",nativeQuery = true)
    List<ResultForOrder> findAllByType(int type);

    @Query(value = "select * from for_order where status = ?2 and type = ?1 order by status",nativeQuery = true)
    List<ResultForOrder> findByTypeAndStatus(int type,int status);

    @Query(value = "select * from for_order where operation_phone = ?2 and type = ?1",nativeQuery = true)
    List<ResultForOrder> findAllByTechAndType(int type,String phone);



}
