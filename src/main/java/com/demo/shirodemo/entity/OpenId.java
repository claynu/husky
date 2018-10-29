package com.demo.shirodemo.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenId {
    private String openid;
    private String session_key;

    public OpenId() {
    }

    public OpenId(String openid, String session_key) {
        this.openid = openid;
        this.session_key = session_key;
    }

}
