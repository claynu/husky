package com.demo.shirodemo.rest;

import com.alibaba.fastjson.JSONObject;
import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.ResultForLogin;
import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.entity.table.UserRoleEntity;
import com.demo.shirodemo.entity.table.WeChatEntity;
import com.demo.shirodemo.schedule.WeChatToken;
import com.demo.shirodemo.service.RedisForTokenService;
import com.demo.shirodemo.service.UserRoleService;
import com.demo.shirodemo.service.UserService;
import com.demo.shirodemo.service.WeChatService;
import com.demo.shirodemo.tool.ListUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/weChat")
public class WeChatController {
    @Autowired
    UserService userService;
    @Autowired
    WeChatService weChatService;
    @Autowired
    RedisForTokenService tokenService;

    @Autowired
    UserRoleService userRoleService;
    @ApiOperation(value = "微信顾客登录")
    @PostMapping("/login")
    public ResultForLogin login(@RequestParam(name = "code",required = true) String code){
        ResultForLogin result = new ResultForLogin();
        String openId = weChatService.getOpenId(code);
        String weChatToken = getWechatToken();
        if (openId == null){
            result.setSuccess(false);
            result.setMsg("用户暂未注册");
            return result;
        }
        else {
            //根据openid 查询是否存在  不存在则注册
            // 查询phone  再查询 user 登录 授权
           WeChatEntity weChatEntity = weChatService.findByOpenId(openId);
            if (weChatEntity!=null){
                UserEntity userEntity = userService.findByPhone(weChatEntity.getPhone());
                UsernamePasswordToken token = new UsernamePasswordToken(userEntity.getPhone(), userEntity.getPassword());
                SecurityUtils.getSubject().login(token);
                String userToken = tokenService.addUserToken(userEntity.getPhone());
                result.setRoleId(userRoleService.getRoleIDByUserPhone(userEntity.getPhone()));
                result.setMsg("welcome "+userEntity.getUsername());
                JSONObject jsonObject = new JSONObject();
                result.setSuccess(true);
                jsonObject.put("openId",openId);
                jsonObject.put("weChatToken",weChatToken);
                jsonObject.put("token", userToken);
                result.setData(jsonObject);
                return result;
            }
            else {
                log.info("weChat");
                result.setMsg("该账号未注册");
                result.setData(weChatEntity);
                result.setSuccess(false);
                return result;
            }
        }

    }





    @ApiOperation(value = "微信顾客注册")
    @PostMapping("/register")
    public Result register(@RequestParam(name = "student_id") String student_id,
                           @RequestParam(name = "name") String name,
                           @RequestParam(name = "phone") String phone,
                           @RequestParam(name = "pc_brand") String pc_brand,
                           @RequestParam(name = "code") String code,
                           @RequestParam(name = "powerOnPassword", defaultValue = "", required = false) String powerOnPassword) {
        Result result = new Result(false);
        UserEntity userEntity = new UserEntity(phone,"123456",name);
        try {
            String openid = weChatService.getOpenId(code);
            if(weChatService.isExist(openid)){
                result.setMsg("您的微信账户已存在");
                return result;
            }
            if (openid==null){
                 result.setSuccess(false);
                 result.setMsg("未获得openid");
                 return result;
            }
            WeChatEntity weChatEntity = new WeChatEntity(phone,name,student_id,powerOnPassword,openid,pc_brand);
            result = weChatService.addOne(weChatEntity);
            if (result.getSuccess()){
                if (userService.findByPhone(weChatEntity.getPhone())==null){
                    //如果不存在即为客户
                    result = userService.register(userEntity,new UserRoleEntity(userEntity.getPhone(),5));
                    log.info("客户注册"+userEntity.getPhone());
                }
                else {
                    int roleId = userRoleService.getRoleIDByUserPhone(userEntity.getPhone());
                    result = userService.register(userEntity,new UserRoleEntity(userEntity.getPhone(),roleId));
                    log.info("商家注册"+userEntity.getPhone());
                }
            }
            return result;
        }catch (Exception e){
            return new Result(false,"注册失败");
        }
    }

    @PostMapping("/getOpenId")
    public String getOpenId(@RequestParam(name = "code",required = false) String code){
        return weChatService.getOpenId(code);
    }
    @PostMapping("/getWeChatToken")
    public String getWechatToken(){
        if (WeChatToken.getWeChatToken()==null){
            return weChatService.getWechatToken();
        }else {
            return WeChatToken.getWeChatToken();}
        }
    @ApiOperation(value = "修改本用户基本信息",notes = "all role")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "姓名",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "pc_brand",value = "电脑",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "powerOnPassword",value = "开机密码",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "idCard_no",value = "身份证号码",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    @PutMapping("/updateUserInfo")
    public Result updateUserInfo(@RequestParam(name = "name") String name,
                                 @RequestParam(name = "pc_brand") String pc_brand,
                                 @RequestParam(name = "powerOnPassword", defaultValue = "") String powerOnPassword,
                                 @RequestParam(name = "idCard_no") String idCardNo,
                                 @RequestParam(name = "token") String token)
    {
        Result result = tokenService.checkToken(token);
            if (result.getSuccess()){
                UserEntity userEntity = (UserEntity)SecurityUtils.getSubject().getPrincipal();
                String phone = userEntity.getPhone();
                WeChatEntity weChatEntity = weChatService.findByPhone(phone);
                if(idCardNo.matches("\\d{17}(\\d||X||x)")&&idCardNo.length()==18){
                    weChatEntity.setIdCardNo(idCardNo);
                }else {
                    result.setSuccess(false);
                    result.setMsg("请输入正确的身份证号码");
                    return result;
                }

                weChatEntity.setPc_brand(pc_brand);
                weChatEntity.setPowerOnPassword(powerOnPassword);
                weChatEntity.setUsername(name);
                result = weChatService.UpdateOne(weChatEntity);
                return result;
            }
            else return result;
    }


    @GetMapping("/getMyInfo")
    @ApiOperation(value = "获取本用户基本信息",notes = "all role")
    public Result getMyInfo(@RequestParam(name = "token",required = true) String token){
        Result result = tokenService.checkToken(token);
        if (result.getSuccess()){
            UserEntity userEntity = (UserEntity)SecurityUtils.getSubject().getPrincipal();
            String phone = userEntity.getPhone();
            WeChatEntity weChatEntity = weChatService.findByPhone(phone);
            if (weChatEntity==null){
                result.setSuccess(false);
                result.setMsg("您还未在微信小程序注册");
            }
            result.setData(weChatEntity);
        }
        return result;
    }


    @PostMapping("/sendWeChatMessage")
    @ApiOperation(value = "发送消息推送",notes = "all role")
    public Boolean sendWeChatMessage(@RequestBody JSONObject json) {
        log.error(json.toString());
        return weChatService.sendWeChatMessage(json);
    }

    @GetMapping("/sendWeChatMessage")
    @ApiOperation(value = "处理微信服务器的信息",notes = "all role")
    public String getWeChatMessage(@RequestParam(name = "signature")String signature,
                                    @RequestParam(name = "timestamp")String timestamp,
                                    @RequestParam(name = "nonce")String nonce,
                                    @RequestParam(name = "echostr")String echostr) {
        log.info(signature+ "  timestamp:" +timestamp);
        log.info(echostr);
        if (ListUtils.checkSignature(signature, timestamp, nonce)) {
            return echostr;

        }else
            return null;
    }
}
