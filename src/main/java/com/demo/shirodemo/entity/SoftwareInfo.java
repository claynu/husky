package com.demo.shirodemo.entity;

import com.demo.shirodemo.entity.table.SoftwareEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SoftwareInfo implements Serializable{

    private int id;//id

    private String category;//类别

    private String image;//logo图片地址


    private String name;//软件名

    private String price;//价格


    private String remark;//备注

    private List<String> versions;

    public SoftwareInfo(SoftwareEntity s, List<String> version ) {
        this.id = s.getId();
        this.category = s.getCategory();
        this.image = s.getImage();
        this.name = s.getName();
        this.price = s.getPrice();
        this.remark = s.getRemark();
        this.versions = version;
    }
}
