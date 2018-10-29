package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.table.FinanceBakeUpEntity;
import com.demo.shirodemo.entity.table.FinanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceBakeUpRepository extends JpaRepository<FinanceBakeUpEntity,Integer> {


    @Override
    @Query(value = "select f from FinanceBakeUpEntity f")
    List<FinanceBakeUpEntity> findAll();

    @Query(value = "select f from FinanceBakeUpEntity f where f.type =?1")
    List<FinanceBakeUpEntity> findAllByType(int type);

    @Query(value = "select f from FinanceBakeUpEntity f where f.id =?1")
    FinanceBakeUpEntity findById(int id);

}
