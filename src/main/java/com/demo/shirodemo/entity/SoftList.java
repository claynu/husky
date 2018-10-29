package com.demo.shirodemo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class SoftList implements Serializable{
    private String category;
    private List<SoftwareInfo> list_soft;
}
