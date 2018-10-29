package com.demo.shirodemo.entity.table;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "DigitalBoard")
public class DigitalBoard implements Serializable{
    @Id
    @Column(length = 3)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 40)
    private String merchandiseName;

    @Column(length = 80,nullable = false)
    private String image;

    @Column(length = 5,nullable = false)
    private int S_price;

    @Column(length = 5,nullable = false)
    private int S_promotion_price;

    @Column(length = 5,nullable = false)
    private int S_TMall_price;

    @Column(length = 5,nullable = false)
    private int S_JD_price;

    @Column(length = 5,nullable = false)
    private int M_price;

    @Column(length = 5,nullable = false)
    private int M_promotion_price;

    @Column(length = 5,nullable = false)
    private int M_TMall_price;

    @Column(length = 5,nullable = false)
    private int M_JD_price;

    @Column(length = 4,nullable = false)
    private int profit; //利润

    @Column(length = 300,nullable = false)
    private String slideshow; //轮播图ID

    @Column(length = 500,nullable = false)
    private String merchandiseImageInfo; //详情图

    @Column(length = 100,nullable = false)
    private String present;  //赠品

    @Column(length = 4,nullable = false)
    private int sum=0; //销售总数  完成订单时加1

    private String version;

}
