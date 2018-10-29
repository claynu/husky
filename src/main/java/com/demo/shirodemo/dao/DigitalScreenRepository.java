package com.demo.shirodemo.dao;


import com.demo.shirodemo.entity.table.DigitalBoard;
import com.demo.shirodemo.entity.table.DigitalScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DigitalScreenRepository extends JpaRepository<DigitalScreen,Integer> {
    @Query(value = "select * from digital_screen  where id = ?1",nativeQuery = true)
    DigitalScreen getOne(int id);

    @Override
    @Query(value = "select * from digital_screen order by price",nativeQuery = true)
    List<DigitalScreen> findAll();
}
