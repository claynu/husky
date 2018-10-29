package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.table.FinanceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceRepository extends JpaRepository<FinanceEntity,Integer> {

    @Query(value = "select f from FinanceEntity f where f.operate_name=?1")
    List<FinanceEntity> findAllByOperate_name(String operate_name);


    @Query(value = "select f from FinanceEntity f where f.confirm_name is null ")
    List<FinanceEntity> findAllNotConfirm();

    @Query(value = "select f from FinanceEntity f where f.confirm_name is not null ")
    List<FinanceEntity> findAllConfirm();

    @Query(value = "select f from FinanceEntity f where f.type =?1")
    List<FinanceEntity> findAllByType(int type);

    @Query(value = "select f from FinanceEntity f where f.id =?1")
    FinanceEntity findById(int id);

    @Query(value = "select f from FinanceEntity f where f.describe_info like %?1%")
    List<FinanceEntity> findAllByDescribe_infoContaining(String describe_info);
}
