package com.demo.shirodemo.entity.table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table
public class TechForOrders implements Serializable{
    @Id
    @Column(length = 11)
    @NotNull
    private String phone;

    @Column(length = 400)
    private String product="";//已接单类型

    @Column(length = 1)
    @NotNull
    private int status=1; //0  1

    @NotNull
    @Column(length = 1)
    private int amount=0;//已接单数量

    public TechForOrders() {
    }


    public TechForOrders(@NotNull String phone, @NotNull int status) {
        this.phone = phone;
        this.status = status;
    }

    public TechForOrders(@NotNull String phone, @NotNull String product, @NotNull int status, @NotNull int amount) {
        this.phone = phone;
        this.product = product;
        this.status = status;
        this.amount = amount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "PHONE:"+phone+"product:"+product+"amount"+amount;
    }
}
