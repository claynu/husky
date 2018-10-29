package com.demo.shirodemo.rest;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.service.RedisForUserService;
import com.demo.shirodemo.tool.CryptographyUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class TestController {
    @Autowired
    RedisForUserService redisForUserService;



    @ApiOperation(value = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone",value = "电话号码",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "password",value = "密码",dataType = "String",required = true,paramType = "query")
    })
    @PostMapping("/login")
    public String login(@RequestParam(name = "phone")String phone,
                        @RequestParam(name = "password")String password){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(phone,CryptographyUtil.md5(password));
        try {
            subject.login(token);
            return "success";

        }
        catch (Exception e){
            e.printStackTrace();
            return "false";
        }
    }

    @RequiresRoles(value = "root")
    @ApiOperation(value = "添加用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username",value = "用户名",paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "phone",value = "电话号码",paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "password",value = "密码",paramType = "query",required = true,dataType = "String")
    })
    @PostMapping("/addUser")
    public String addUser(@RequestParam(name = "username")String username,
                          @RequestParam(name = "phone")String phone,
                          @RequestParam(name = "password")String password){
        UserEntity userEntity = new UserEntity(phone,password,username);
        redisForUserService.save(userEntity);
        return userEntity.toString();
    }

    @GetMapping("/test")
    public String test(){
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole("root")){
            log.info("角色：root");
            return "root";
        }
        return "false";
    }

    @DeleteMapping("/deleteUser")
    public Result deleteUser(
            @RequestParam(name = "phone")String phone){
        return redisForUserService.deleteUser(phone);

    }
    @PutMapping("/updateUser")
    public Result  updateUser(
            @RequestParam(name = "username")String username,
            @RequestParam(name = "phone")String phone,
            @RequestParam(name = "password")String password){
        UserEntity userEntity = new UserEntity(phone,password,username);
        return redisForUserService.updateUser(userEntity);
    }

}
