package com.demo.shirodemo.entity.table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
//@Table(name = "merchandise_entity")
@Setter
@Entity
public class MerchandiseEntity implements Serializable{
    @Id
    @Column(length = 3)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 5)
    private String category;//种类 数位板  数位屏

    @Column(length = 5)
    private String type;//类型  s  m   l

    @Column(length = 20)
    private String merchandiseName;

    @Column(length = 40,nullable = false)
    private String image;

    @Column(length = 5,nullable = false)
    private int price;

    @Column(length = 5,nullable = false)
    private int promotion_price;

    @Column(length = 5,nullable = false)
    private int TMall_price;

    @Column(length = 5,nullable = false)
    private int JD_price;

    @Column(length = 4,nullable = false)
    private int profit; //利润

    @Column(length = 4,nullable = false)
    private int slideshowId; //轮播图ID

    @Column(length = 4,nullable = false)
    private int merchandiseImageInfoId; //详情图ID

    @Column(length = 100,nullable = false)
    private String present;  //赠品

    @Column(length = 4,nullable = false)
    private int sum=0; //销售总数  完成订单时加1

}
