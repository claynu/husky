package com.demo.shirodemo.rest;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.service.MessageService;
import com.demo.shirodemo.service.RedisForTokenService;
import com.demo.shirodemo.service.UserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;


@RestController
@RequestMapping("/sendMessage")
public class MessageController {
    @Autowired
    MessageService messageService;
    @Autowired
    RedisForTokenService redisService;
    @Autowired
    UserService userService;

    Result result;
    @PostMapping("/send2Customer")
    public Result send2Customer(@RequestParam(name = "customerPhone") String customerPhone,
                                @RequestParam(name = "token") String token){
        result = redisService.checkToken(token);
        result.setSuccess(true);
        if (result.getSuccess()){
            Subject subject = SecurityUtils.getSubject();
            if (subject.hasRole("root")||subject.hasRole("front_admin")){
                UserEntity userEntity = (UserEntity)subject.getPrincipal();
                try {
                    UserEntity customer = userService.findByPhone(customerPhone);
                    String customerName = customer.getUsername();
                    result =  messageService.send2Customer(customerName,userEntity.getPhone(),customerPhone);
                }catch (NullPointerException e){
                    e.printStackTrace();
                    result.setMsg("不存在该电话号码的用户");
                }
                catch (EntityNotFoundException e){
                    e.printStackTrace();
                    result.setMsg("不存在该电话号码的用户");
                }catch (Exception e){
                    e.printStackTrace();
                    result.setMsg("短信发送失败");
                }

            }
        }
        return result;
    }

    @PostMapping("/send2Tech")
    public Result send2Tech(@RequestParam(name = "techName") String techName,
                            @RequestParam(name = "techPhone") String techPhone,
                            @RequestParam(name = "token") String token){
        result = redisService.checkToken(token);
        result.setSuccess(true);
        if (result.getSuccess()) {
            return messageService.send2Tech(techName, techPhone);
        }
        return result;
    }
    @PostMapping("/send2Merchant")
    public Result send2Merchant(@RequestParam(name = "merchantName") String merchantName,
                            @RequestParam(name = "merchantPhone") String merchantPhone,
                            @RequestParam(name = "token") String token){
        result = redisService.checkToken(token);
        result.setSuccess(true);
        if (result.getSuccess()) {
            return messageService.send2Merchant(merchantName, merchantPhone);
        }
        return result;
    }

    @PostMapping("/merchantSend2Customer")
    public Result merchantSend2Customer(@RequestParam(name = "name") String name,
                                @RequestParam(name = "merchantPhone") String merchantPhone,
                                @RequestParam(name = "customerPhone") String customerPhone,
                                @RequestParam(name = "token") String token) {
        result = redisService.checkToken(token);
        result.setSuccess(true);
        if (result.getSuccess()) {
            return messageService.merchantSend2Customer(name, merchantPhone, customerPhone);
        }
        return result;
    }
}
