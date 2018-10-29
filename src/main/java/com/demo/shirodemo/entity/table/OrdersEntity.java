package com.demo.shirodemo.entity.table;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "OrdersEntity",uniqueConstraints = {@UniqueConstraint(columnNames = {"customer_phone","post_time","merchandise","status"})})
public class OrdersEntity implements Serializable{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(length = 10)
    @NotNull
    private int id;
    @Column(length = 1,nullable = false)
    private int type;//订单类型 1 为软件  2 商品（数位板、数位屏）
    @Column(length = 11)
    @NotNull
    private String customer_phone;//用户id 设置user_role(customer) ->id 外键
    @Column(length = 20)
    private String operation_phone; //商家或技术人员dianhua  user_role(technicians)->name
    @Column
    @NotNull
    private String merchandise;//商品 id+versionId*num;  或者id 根据type
    @Column
    private String remark;//备注
    @Column
    @NotNull
    private int money;
    @Column
    private String false_describe; //当status == -1 时 添加失败描述
    @Column(length = 30)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T' HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd'T' HH:mm:ss",timezone = "GMT+8")
    private Timestamp post_time; //创建订单时间
    @Column(length = 30)
    private Date deal_time;//交易时间
    @Column(length = 2)
    private int  status=1;  //1 未接单   2 已接单（表示正在处理当前订单） 3等待交易 4交易成功  -1交易失败

    public OrdersEntity(int type, @NotNull String customer_phone, @NotNull String merchandise, @NotNull int money, Timestamp post_time, int status) {
        this.type = type;
        this.customer_phone = customer_phone;
        this.merchandise = merchandise;
        this.money = money;
        this.post_time = post_time;
        this.status = status;
    }

    public OrdersEntity(int type, @NotNull String customer_phone, @NotNull String merchandise, String remark, @NotNull int money, Timestamp post_time, int status) {
        this.type = type;
        this.customer_phone = customer_phone;
        this.merchandise = merchandise;
        this.remark = remark;
        this.money = money;
        this.post_time = post_time;
        this.status = status;
    }
}
