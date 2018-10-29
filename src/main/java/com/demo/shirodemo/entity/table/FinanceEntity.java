package com.demo.shirodemo.entity.table;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "FinanceEntity")
public class FinanceEntity implements Serializable{

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

    public FinanceEntity(String describe_info, int type, int money, String operate_name, Date deal_time) {
        this.describe_info = describe_info;
        this.type = type;
        this.money = money;
        this.operate_name = operate_name;
        this.deal_time = deal_time;
    }
}
