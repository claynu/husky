package com.demo.shirodemo.dao;


import com.demo.shirodemo.entity.table.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Integer> {
    @Query(value = "SELECT r FROM RoleEntity r WHERE r.id=?1")
    RoleEntity getOne(Integer s);


    @Query(value = "SELECT r.id FROM RoleEntity r WHERE r.roleName=?1")
    int findIdByName(String s);


}
