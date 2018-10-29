package com.demo.shirodemo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResultForLogin<T> {
    private Boolean success;
    private String msg;
    private int roleId=0;
    private T data;

    public ResultForLogin(Boolean success) {
        this.success = success;
    }

    public ResultForLogin(Boolean success, String msg, int roleId) {
        this.success = success;
        this.msg = msg;
        this.roleId = roleId;
    }

    public ResultForLogin(Boolean success, String msg, int roleId, T data) {
        this.success = success;
        this.msg = msg;
        this.roleId = roleId;
        this.data = data;
    }
}
