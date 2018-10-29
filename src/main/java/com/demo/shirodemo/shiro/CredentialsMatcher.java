package com.demo.shirodemo.shiro;

import com.demo.shirodemo.entity.table.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

@Slf4j
public class CredentialsMatcher extends SimpleCredentialsMatcher {

    public CredentialsMatcher() {
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken utoken=(UsernamePasswordToken) token;
        return equals(utoken.getPassword(),info.getCredentials().toString());
    }
}
