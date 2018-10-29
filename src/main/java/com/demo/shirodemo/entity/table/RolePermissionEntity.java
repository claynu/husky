package com.demo.shirodemo.entity.table;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "RolePermissionEntity")
public class RolePermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @NotNull
    private int id;
    @NotNull
    @Column(length = 1)
    private int roleId;
    @Column(length = 1,nullable = false)
    private int permissionId;

    public RolePermissionEntity(@NotNull int roleId, @NotNull int permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public RolePermissionEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }
}