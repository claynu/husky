package com.demo.shirodemo.entity.table;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RoleEntity")
public class RoleEntity implements Serializable {


    @Id
    @NotNull
    @Column
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @Column(length = 20)

    private String roleName;



    /**
     * 总共五个角色
     * 1 超管         root
     * 2 前台管理员    front_admin
     * 3 财务管理      financial_admin
     * 4 技术人员      technicians
     * 5 顾客          customer
     * 6 商家          merchant
     */
}
