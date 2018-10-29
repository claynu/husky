package com.demo.shirodemo.rest;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.UserEntity;

import com.demo.shirodemo.service.RedisForTokenService;
import com.demo.shirodemo.service.TechService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/tech")
@RestController
public class TechController {

        @Autowired
        private RedisForTokenService redisService;
        @Autowired
        private TechService techService;
        private Result result;
    //
    @RequiresRoles("tech")
    @ApiOperation(value = "修改接单状态",notes = "only tech can do this")
    @PostMapping("/changeStatus") //技术人员用
    public Result changeStatus(@RequestParam(name = "token")String  token) {
        result = redisService.checkToken(token);
        if (result.getSuccess()) {
            return techService.changeStatus(token);
        }
        return result;
    }

    @RequiresRoles("tech")
    @GetMapping("/getSelf") //技术人员用
    @ApiOperation(value = "查看个人接单信息",notes = "only tech can do this")
    public Result getAll(@RequestParam(name = "token")String  token) {
        result = redisService.checkToken(token);
        if (result.getSuccess()) {
            try {
                Subject subject = SecurityUtils.getSubject();
                UserEntity userEntity = (UserEntity) subject.getPrincipal();
                return techService.getOne(userEntity.getPhone());
            } catch (NullPointerException e) {
                return new Result(false, "登录信息已失效---请重新登录");
            }
        }
        return result;
    }

}
