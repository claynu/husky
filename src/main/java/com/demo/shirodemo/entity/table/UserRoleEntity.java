package com.demo.shirodemo.entity.table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "UserRoleEntity")
@Setter
@Getter
@NoArgsConstructor
public class UserRoleEntity implements Serializable{
    @Id
    @Column(length = 11)
    private String phone;
    @Column(length = 1)
    @NotNull
    private int roleId;

    public UserRoleEntity(@NotNull String phone, @NotNull int roleId) {
        this.phone = phone;
        this.roleId = roleId;
    }
}
