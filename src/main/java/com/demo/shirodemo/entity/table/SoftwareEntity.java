package com.demo.shirodemo.entity.table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;

@Entity()
@Table(name = "soft_ware_entity")
@Getter
@Setter
@NoArgsConstructor

public class SoftwareEntity implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 4)
    private int id;//id

    @Column(length = 50)
    private String category;//类别

    @Column(length = 100)
    private String image;//logo图片地址

    @Column(length = 40)
    private String name;//软件名

    @Column(length = 3)
    private String price;//价格

    @Column(length = 50)
    private String remark;//备注

    public SoftwareEntity(String category, String image, String name, String price, String remark) {
        this.category = category;
        this.image = image;
        this.name = name;
        this.price = price;
        this.remark = remark;
    }
}
