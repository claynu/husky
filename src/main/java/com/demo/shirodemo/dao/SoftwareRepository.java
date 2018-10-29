package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.table.SoftwareEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoftwareRepository extends JpaRepository<SoftwareEntity,Integer> {

    @Query(value = "select u from SoftwareEntity u where u.category =?1")
    List<SoftwareEntity> getListByCategory(int category);

    @Query(value = "select u from SoftwareEntity u ")
    List<SoftwareEntity> findAll();

    @Query(value = "SELECT u from SoftwareEntity u where u.name = ?1")
    SoftwareEntity findByName(String name);

    @Query(value = "select u from SoftwareEntity u where u.id = ?1")
    SoftwareEntity getById(int id);
}