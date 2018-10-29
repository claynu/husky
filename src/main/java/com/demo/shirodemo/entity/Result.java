package com.demo.shirodemo.entity;

import java.io.Serializable;

public class Result<T> implements Serializable {

    public Result() {
    }

    private Boolean success;
    private String msg;
    private T data;


    public Result(Boolean success) {
        this.success = success;
    }

    public Result(Boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public Result(Boolean success, String msg, T data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
