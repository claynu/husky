package com.demo.shirodemo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class ResultForTech implements Serializable {
    @Id
    private String phone;

    private String product;//已接单类型

    private int status=1; //0  1

    private int amount=0;//已接单数量
    private String username;

}
