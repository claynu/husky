package com.demo.shirodemo.entity.table;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "user_entity")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserEntity implements Serializable{

    @Column(length = 11,nullable = false)
    @Id
    private String phone;
    @Column(length = 36,nullable = false)
    private String password;
    @Column(length = 20,nullable = false)
    private String username;

    public UserEntity(String phone, String password, String username) {
        this.phone = phone;
        this.password = password;
        this.username = username;
    }

    @Override
    public String toString() {
        return "username: "+username+"           phone:"+phone+"          password:"+password;
    }
}
