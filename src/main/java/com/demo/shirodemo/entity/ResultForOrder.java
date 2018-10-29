package com.demo.shirodemo.entity;

import com.demo.shirodemo.entity.table.OrdersEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "for_order")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class ResultForOrder implements Serializable {
    @Column
    @Id
    private int id;
    @Column
    private int type;//订单类型 1 为软件  2 商品（数位板、数位屏）
    @Column
    private String customer_phone;//用户id 设置user_role(customer) ->id 外键
    @Column
    private String operation_phone; //商家或技术人员姓名 user_role(technicians)->name
    @Column
    private String customer_name;//用户id 设置user_role(customer) ->id 外键
    @Column
    private String operation_name; //商家或技术人员姓名 user_role(technicians)->name
    @Column
    private String PowerOnPassword;
    @Column
    private String pc_brand;
    @Transient
    private List<MerchandiseList> merchandiselists;//商品 id+versionId*num;  或者id 根据type
    @Column
    private String remark;//备注
    @Column
    private int money;
    @Column
    @NotNull
    private String merchandise;
    @Column
    private String false_describe; //当status == -1 时 添加失败描述
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd'T' HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd'T' HH:mm:ss",timezone = "GMT+8")
    private Timestamp post_time; //创建订单时间
    @Column
    private Date deal_time;//交易时间
    @Column
    private int  status=1;  //1 未接单   2 已接单（表示正在处理当前订单）  3交易成功  -1交易失败


}
