package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.table.SlideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SlideEntityRepository extends JpaRepository<SlideEntity,Integer> {
    @Query(value = "select u.path from SlideEntity u where u.type = ?1")
    List<String> getAllByType(int type);
}
