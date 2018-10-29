package com.demo.shirodemo.entity.table;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "FinanceBakeUpEntity")
public class FinanceBakeUpEntity implements Serializable{

    @Id
    @Column
    @NotNull
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @Column
    private String describe_info;
    @Column(length = 80)
    private String Remarks;
    @Column(length = 3)
    private int type; //1软件 2小程序商家
    @Column
    private int money;
    @Column(length = 20)
    private String operate_name;
    @Column(length = 20)
    private String confirm_name;
    @Column(length = 30)
    private Date deal_time;

    public FinanceBakeUpEntity(FinanceEntity entity) {
        this.describe_info = entity.getDescribe_info();
        Remarks = entity.getRemarks();
        this.type = entity.getType();
        this.money = entity.getMoney();
        this.operate_name = entity.getOperate_name();
        this.confirm_name = entity.getConfirm_name();
        this.deal_time = entity.getDeal_time();
    }
}
