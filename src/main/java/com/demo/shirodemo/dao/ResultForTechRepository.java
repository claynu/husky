package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.ResultForTech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultForTechRepository extends JpaRepository<ResultForTech,Integer> {
    @Query(value = "SELECT a.phone,a.amount,a.product,a.status,c.username from tech_for_orders a LEFT JOIN user_entity c on a.phone=c.phone left JOIN user_role_entity b on b.phone = a.phone order by a.amount",nativeQuery = true)
    List<ResultForTech> findAllByStatus();
}
