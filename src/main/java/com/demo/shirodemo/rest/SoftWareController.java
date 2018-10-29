package com.demo.shirodemo.rest;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.service.RedisForTokenService;

import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



//@RestController
//@RequestMapping("/software")
public class SoftWareController {

    @Autowired
    RedisForTokenService redisService;




    @ApiOperation(value = "根据软件类型获取列表")
    @PostMapping("getAllByCategory")
    public Result getAllByCategory(@RequestParam(name = "category") String category,
                                   @RequestParam(name = "token") String token) {
        Result result = redisService.checkToken(token);
        if (result.getSuccess()) {
            return result;
        } else
            return result;
    }

    @ApiOperation(value = "获取所有软件列表")
    @PostMapping("getAll")
    public Result getAll(@RequestParam(name = "token") String token) {
        Result result = redisService.checkToken(token);
        if (result.getSuccess()) {
            return result;
        } else
            return result;
    }

    @RequiresRoles(value = "root")
    @ApiOperation(value = "添加软件")
    @PostMapping("addSoftware")  //TODO 使用excel
    public Result addSoftware(@RequestParam(name = "cate_id") int cate_id,
                              @RequestParam(name = "name") String name,
                              @RequestParam(name = "price") String price,
                              @RequestParam(name = "version",required = false) String version,
                              @RequestParam(name = "token") String token, MultipartFile file) {

        Result result = redisService.checkToken(token);
        if (result.getSuccess()) {
            Subject subject = SecurityUtils.getSubject();
            if (subject.hasRole("front_admin") || subject.hasRole("root"));
        }
                return result;
    }
    @RequiresRoles(value = "root")
    @ApiOperation(value = "添加软件版本")
    @PostMapping("addSoftwareVersion")
    public Result addSoftwareVersion(@RequestParam(name = "cate_id")int cate_id,
                                       @RequestParam(name = "child_id")int child_id,
                                       @RequestParam(name = "version")String version,
                                       @RequestParam(name = "token")String token){

        return null;
    }

    @ApiOperation(value = "添加软件分类")
    @PostMapping("addSoftwareType")
    public Result addSoftwareType(@RequestParam(name = "cate_name")String cate_name,
                                    @RequestParam(name = "token")String token){

        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            try {
                Subject subject = SecurityUtils.getSubject();
                if (subject.hasRole("front_admin") || subject.hasRole("root")) {
                    return result;
                }
                else {
                    result.setMsg("权限不足");
                    return result;
                }
            }catch (Exception e){
                result.setMsg("登录信息已失效");
                return result;
            }
        }
        else return result;
    }



}
