package com.demo.shirodemo.rest;


import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.ResultForLogin;
import com.demo.shirodemo.entity.table.TechForOrders;
import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.entity.table.UserRoleEntity;
import com.demo.shirodemo.service.*;

import com.demo.shirodemo.tool.CryptographyUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;


/**
 * controller执行基本的逻辑 正则匹配
 * 函数的具体实现在service 实现
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    RedisForUserService redisService;
    @Autowired
    RedisForTokenService tokenService;
    @Autowired
    UserService userService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    TechService techService;


    @ApiOperation(value = "顾客注册",notes = "only customer can do this")
    @PostMapping("/register")
    @ApiImplicitParams({@ApiImplicitParam(name = "name",value = "姓名",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "phone",value = "电话号码",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "password",value = "密码",paramType = "query",dataType = "String",defaultValue = "123456")})


    public Result addCustomer(@RequestParam(name = "name") String name,
                              @RequestParam(name = "phone") String phone,
                              @RequestParam(name = "password",defaultValue = "123456",required = false) String password){
        UserEntity userEntity = new UserEntity(phone,password,name);
        UserRoleEntity ur = new UserRoleEntity(phone,5);
        if (userEntity.getPassword().length()>=6){
            if (userEntity.getPhone().matches("1[3,5,6,7,8,9]\\d{9}")){
                return userService.register(userEntity,ur);
            }
            else {
                System.out.println(userEntity.getPhone().length());
                return new Result(false,"请输入正确的电话号码");
            }
        }
        else {
            return new Result(false,"密码不能低于6位"+userEntity.getPassword().length());
        }
    }


    /**
     * 管理员用的接口
     * @param
     * @return
     */
    //整合到一起
    @RequiresRoles("root")
    @ApiOperation(value = "添加系统成员(财务3 前台2 技术人员4 小程序商家6)",notes = "only root can do this")
    @PostMapping("/addUser")
    @ApiImplicitParams({@ApiImplicitParam(name = "name",value = "姓名",required = true,paramType = "query",dataType = "String"),
                        @ApiImplicitParam(name = "phone",value = "电话号码",required = true,paramType = "query",dataType = "String"),
                        @ApiImplicitParam(name = "password",value = "密码",required = false,paramType = "query",dataType = "String",defaultValue = "123456"),
                        @ApiImplicitParam(name = "roleId",value = "角色id",required = true,paramType = "query",dataType = "int"),
                        @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    public Result addUser(@RequestParam(name = "name") String name,
                          @RequestParam(name = "phone") String phone,
                          @RequestParam(name = "password",defaultValue = "123456",required = false) String password,
                          @RequestParam(name = "roleId",defaultValue ="4") int roleId,
                          @RequestParam(name = "token")String token){
       if (phone.matches("\\d{11}")) {
           UserEntity userEntity = new UserEntity(phone, password, name);
           return userService.addInsider(userEntity, roleId);
       }
       else return new Result(false,"密码不符合规则");
    }

    /**
     * 账号匹配11位数字
     * @param phone,password
     * @return
     */
    @ApiOperation(value = "登录")
    @PostMapping("login")
    @ApiImplicitParams({@ApiImplicitParam(name = "phone",value = "电话号码",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "password",value = "密码",required = true,paramType = "query",dataType = "String")})
    public ResultForLogin login(@RequestParam(name = "phone") String phone,
                                @RequestParam(name = "password") String password) {

        if (phone.matches("1[3,5,6,7,8,9]\\d{9}")){
            return userService.login(phone,password);
        }
        else
            return new ResultForLogin(false,"账号不符合规则",0);
    }


    @ApiOperation(value = "修改本用户密码",notes = "all role")
    @ApiImplicitParams({@ApiImplicitParam(name = "password",value = "密码",required = true,paramType = "query",dataType = "String"),
                        @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    @PutMapping("/changePassword")
    public Result changePassword(@RequestParam(name = "password") String password,
                                 @RequestParam(name = "token")String token
    ){
            if (password.length() >= 6) {
                Result result = tokenService.checkToken(token);
                if (result.getSuccess()) {
                    UserEntity userEntity = (UserEntity)SecurityUtils.getSubject().getPrincipal();
                    result =  userService.changePassword(userEntity.getPhone(),password);
                    log.info("-----"+userEntity.getPhone()+"password:"+password);
                    if (result.getSuccess()){
                        logout(token);
                        return result;
                    }
                }
                return new Result(false,"登录信息失效");
            } else {
                return new Result(false, "密码不能低于6位");
            }
        }



    @ApiOperation(value = "判断登录状态")
    @GetMapping("/isLogin")
    @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")
    public Boolean isLogin(@RequestParam("token")String token){
        return tokenService.checkToken(token).getSuccess();

    }

    @ApiOperation(value = "注销")
    @PutMapping("/logout")
    @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")
    public Result logout(@RequestParam("token")String token) {
        Result result = tokenService.checkToken(token);
        if (result.getSuccess()) {
            SecurityUtils.getSubject().logout();
            return new Result(true,"注销成功");
        }
        else
            return new Result(false,"您还未登录呢");

    }

    @ApiOperation(value = "获取所有用户",notes = "only root can do this")
    @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")
    @GetMapping("/getAllUser")
    public Result getAllUser(@RequestParam("token")String token){
        Result result = tokenService.checkToken(token);
        if (result.getSuccess()) {
           return userService.findAll();
       }
       else return new Result(false,"登录信息失效");
    }

    @RequiresRoles(value = {"root"})
    @ApiOperation(value = "根据姓名模糊查询  TODO redis缓存",notes = "only root can do this")
    @GetMapping("/findByName")
    @ApiImplicitParams({@ApiImplicitParam(name = "username",value = "模糊查询的姓名",required = true,paramType = "query",dataType = "String"),
                        @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    public Result findByNameLike(@RequestParam("username")String name,
                                  @RequestParam("token")String token) {
        Result result = tokenService.checkToken(token);
        if (result.getSuccess()) {
            result.setData(userService.findAllByNameLike(name));
        }
            return result;
    }

    @RequiresRoles("root")
    @ApiOperation(value = "重置用户密码",notes = "only root can do this")
    @PutMapping("resetPassword")
    @ApiImplicitParams({@ApiImplicitParam(name = "userPhone",value = "用户电话号码",required = true,paramType = "query",dataType = "String"),
                        @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    public Result resetUserPassword(@RequestParam("userPhone")String phone,
                                    @RequestParam("token")String token){
        Result result = new Result(false);
        if (phone.matches("1[3,5,6,7,8,9]\\d{9}")){
            result = tokenService.checkToken(token);
            if (result.getSuccess()) {
                return userService.resetPassword(phone);
            }
            else{
                result.setMsg("令牌已失效--请重新登录");
                return result;
            }
        }
        else{
            result.setMsg("账号不匹配");
            return result;
        }
    }

    /*
    @PostMapping(path = "/demo1")//接收json
    public void demo1(@RequestBody Map<String, String> person) {
    System.out.println(person.get("name"));
}
     */

    @ApiOperation(value = "修改用户名",notes = "all role")
    @PutMapping("modifyUsername")
    @ApiImplicitParams({@ApiImplicitParam(name = "newUsername",value = "新用户名",required = true,paramType = "query",dataType = "String"),
                        @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    public Result modifyUsername(@RequestParam("newUsername")String username,
                                 @RequestParam("token")String token) {

        Result result = tokenService.checkToken(token);
        if (result.getSuccess()) {
            UserEntity userEntity = (UserEntity)SecurityUtils.getSubject().getPrincipal();
            userEntity.setUsername(username);
            return redisService.updateUser(userEntity);
        } else {
            return new Result(false,"登录信息失效");
        }
    }
    @RequiresRoles("root")
    @ApiOperation(value = "根据角色查询用户",notes = "only root can do this")
    @GetMapping("getAllByRoleName")
    @ApiImplicitParams({@ApiImplicitParam(name = "roleName",value = "角色名",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    public Result getAllByRoleName(@RequestParam("roleName")String roleName,
                                 @RequestParam("token")String token){
        Result result = tokenService.checkToken(token);
        if (result.getSuccess()) {
            return userService.getAllByRole(roleName);
        }
    return result;
    }

    @RequiresRoles("root")
    @ApiOperation(value = "根据角色id查询用户",notes = "only root can do this")
    @GetMapping("getAllByRoleId")
    @ApiImplicitParams({@ApiImplicitParam(name = "roleId",value = "角色Id",required = true,paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    public Result getAllByRoleId(@RequestParam("roleId")int roleId,
                                 @RequestParam("token")String token){
        Result result = tokenService.checkToken(token);
        if (result.getSuccess()) {
            return userService.getAllByRoleId(roleId);
        }
        return result;
    }


}


