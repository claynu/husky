package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.ResultForTech;
import com.demo.shirodemo.entity.table.TechForOrders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechRepository extends JpaRepository<TechForOrders,String> {

    @Query(value = "SELECT a.phone,a.amount,a.product,a.status from tech_for_orders a,user_role_entity b WHERE a.phone = b.phone AND a.`status` = 1 and b.role_id = 4 ORDER BY a.amount ",nativeQuery = true)
    List<TechForOrders> findByStatusAndOrderByAmount();

    @Query(value = "SELECT a.phone,a.amount,a.product,a.status,c.username from tech_for_orders a LEFT JOIN user_entity c on a.phone=c.phone left JOIN user_role_entity b on b.phone = a.phone  where c.username = ?1 ",nativeQuery = true)
    TechForOrders findByName(String name);



}
