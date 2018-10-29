package com.demo.shirodemo.shiro;

import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.service.RedisForUserService;
import com.demo.shirodemo.service.RoleService;
import com.demo.shirodemo.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;

import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
public class Realm extends AuthorizingRealm {

    @Autowired
    RedisForUserService redisForUserService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    RoleService roleService;


    //验证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken)authenticationToken;
        String username = token.getUsername();
        //根据username 查询数据库获得user对象
        UserEntity userEntity = (UserEntity)redisForUserService.getUser(username).getData();
        if (userEntity == null){
            log.error("验证失败 userEntity is null");
            return null;
        }
        String password = userEntity.getPassword();
        try {
            log.info("UserEntity"+userEntity.toString());
            SimpleAuthenticationInfo info =  new SimpleAuthenticationInfo(userEntity,password,this.getClass().getName());
            log.info("验证成功 login"+info.toString());
            return info;
        }
        catch (Exception e){
            log.error("验证失败,密码错误");
            return null;
        }
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Subject subject = SecurityUtils.getSubject();
        UserEntity userEntity = (UserEntity) principalCollection.getPrimaryPrincipal();
        //PrincipalCollection 是根据SimpleAuthenticationInfo 第一个参数
        log.info("--------认证user:"+userEntity.getPhone());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        String role = roleService.getOne(userRoleService.getRoleIDByUserPhone(userEntity.getPhone())).getRoleName();
        info.addRole(role);
        log.info("--------添加权限:"+role);
        return info;
    }
    @Override
    public String getName() {
        return this.getClass().getName();
    }

}
