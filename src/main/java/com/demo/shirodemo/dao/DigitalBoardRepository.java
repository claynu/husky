package com.demo.shirodemo.dao;


import com.demo.shirodemo.entity.table.DigitalBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DigitalBoardRepository extends JpaRepository<DigitalBoard,Integer> {
//    @Query(value = "select * from digital_board  where id = ?1",nativeQuery = true)
//    DigitalBoard getOne(int id);

    @Query(value = "select u from DigitalBoard u  where u.id = ?1")
    DigitalBoard getOne(int id);

    @Override
    @Query(value = "select * from digital_board order by m_price",nativeQuery = true)
    List<DigitalBoard> findAll();
}
