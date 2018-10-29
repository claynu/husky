package com.demo.shirodemo.dao;


import com.demo.shirodemo.entity.table.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity,Integer> {
    @Query(value = "SELECT rp.permissionId from RolePermissionEntity  rp where rp.roleId = ?1")
    int getPermissionIdByRoleId(Integer i);



}
