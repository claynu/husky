package com.demo.shirodemo.entity;

import com.demo.shirodemo.entity.table.MerchandiseEntity;
import com.demo.shirodemo.entity.table.SoftwareEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor

public class ResultForMerchant implements Serializable{
    private List<MerchandiseList> list_merchan;
    private List<SoftList> softList;
}
