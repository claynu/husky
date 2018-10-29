package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.table.InstallationGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstallationGuideRepository extends JpaRepository<InstallationGuide,Integer> {

    @Query(value = "select u from InstallationGuide u where u.id = ?1")
    InstallationGuide getOne(int id);

    @Query(value = "select i from InstallationGuide i order by type ")
    List<InstallationGuide> findAllOrderByType();

}
