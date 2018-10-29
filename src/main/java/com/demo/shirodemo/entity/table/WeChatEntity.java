package com.demo.shirodemo.entity.table;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "weChat_entity")
@Getter
@Setter
public class WeChatEntity implements Serializable{

    @Id
    @Column(length = 11,nullable = false)
    private String phone;

    @Column(length = 20,nullable = false)
    private String username;

    @Column(length = 10,nullable = false,unique = true)
    private String Student_id; //学号

    @Column(nullable = true)
    private String PowerOnPassword; //开机密码

    @Column(nullable = false,unique = true)
    private String openId; //WeChat 的用户标识

    @Column(nullable = true)
    private String pc_brand;

    @Column(length = 18,nullable = true)
    private String idCardNo;//身份证号

    @Column(length = 18,nullable = true)
    private String sex;//身份证号

    public WeChatEntity() {
    }

    public WeChatEntity(String phone, String username, String powerOnPassword, String pc_brand, String idCardNo) {
        this.phone = phone;
        this.username = username;
        PowerOnPassword = powerOnPassword;
        this.pc_brand = pc_brand;
        this.idCardNo = idCardNo;
    }

    public WeChatEntity(String phone, String username, String student_id, String powerOnPassword, String openId, String pc_brand) {
        this.phone = phone;
        this.username = username;
        Student_id = student_id;
        this.pc_brand = pc_brand;
        PowerOnPassword = powerOnPassword;
        this.openId = openId;
    }

    public WeChatEntity(String phone, String username, String student_id, String powerOnPassword, String openId, String pc_brand, String idCardNo) {
        this.phone = phone;
        this.username = username;
        Student_id = student_id;
        PowerOnPassword = powerOnPassword;
        this.openId = openId;
        this.pc_brand = pc_brand;
        setIdCardNo(idCardNo);
    }

    @Override
    public String toString() {
        return "username:"+username+"        phone:"+phone+"   openid:"+openId;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
        if ((int)(idCardNo.charAt(16))%2==0){
            this.sex = "女";
        }
        else this.sex = "男";
    }
}
