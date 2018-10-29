package com.demo.shirodemo.rest;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.FinanceEntity;
import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.service.FinanceService;
import com.demo.shirodemo.service.RedisForTokenService;

import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;


@RestController
@RequestMapping("/finance")
public class FinanceController {
    @Autowired
    RedisForTokenService redisService;
    @Autowired
    FinanceService financeService;

    /**
     * 财务信息所有权限管理均在controller验证
     * service 只负责基本逻辑和数据库操作.
     *
     */



    @RequiresRoles(value = {"root","financial"},logical = Logical.OR)
    @ApiOperation(value = "添加账单记录",notes = "finance and root are allowed")
    @PostMapping("/addFinance")
    public Result save(@RequestParam(name = "describe_info")String describe_info,
                       @RequestParam(name = "type")int type,
                       @RequestParam(name = "money")int money,
                       @RequestParam(name = "operate_name")String operate_name,
                       @RequestParam(name = "token")String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
                try {
                        FinanceEntity financeEntity = new FinanceEntity(describe_info,type,money,operate_name,new Date(System.currentTimeMillis()));
                        result = financeService.save(financeEntity);
                        return result;
                    }
                catch (NullPointerException e){
                    return new Result(false,"保存失败");
                }
            }
            else {
                return result;
            }
    }
    @RequiresRoles(value = {"root","financial"},logical = Logical.OR)
    @ApiOperation(value = "查看所有账单记录",notes = "finance and root are allowed")
    @PostMapping("/findAll")
    public Result findAll(@RequestParam(name = "token")String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return financeService.findAll();
        }
        return result;
    }

    @RequiresRoles(value = {"root","financial"},logical = Logical.OR)
    @ApiOperation(value = "查看所有未确认账单记录",notes = "finance and root are allowed")
    @PostMapping("/findAllNotConfirm")
    public Result findAllNotConfirm(@RequestParam(name = "token")String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return financeService.findAllNotConfirm();
            }
        else {
            return result;
        }
    }
    @RequiresRoles(value = {"root","financial"},logical = Logical.OR)
    @ApiOperation(value = "查看所有已确认账单记录",notes = "finance and root are allowed")
    @PostMapping("/findAllConfirm")
    public Result findAllConfirm(@RequestParam(name = "token")String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            try {
                Subject subject = SecurityUtils.getSubject();
                if (subject.hasRole("financial_admin")||subject.hasRole("root")){
                    return null;
                }
                else
                    return new Result(false,"权限不足");
            }
            catch (NullPointerException e){
                e.printStackTrace();
                return new Result(false,"登录信息失效");
            }
        }
        else {
            return result;
        }
    }




    @RequiresRoles(value = {"root","financial"},logical = Logical.OR)
    @ApiOperation(value = "修改账单记录",notes = "finance and root are allowed")
    @PostMapping("/updateFinance")
    public Result updateFinance(@RequestParam(name = "id")int id,
                                @RequestParam(name = "type")int type,
                                @RequestParam(name = "describe_info",required = false,defaultValue = "")String describe_info,
                                @RequestParam(name = "updateReason")String remarks,
                                @RequestParam(name = "money",required = false,defaultValue = "0")int money,
                                @RequestParam(name = "operate_name",required = false,defaultValue = "")String operate_name,
                                @RequestParam(name = "token")String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
                    FinanceEntity financeEntity = financeService.findById(id);
                    financeEntity.setRemarks(remarks+"---金额变化:"+financeEntity.getMoney()+"-->"+money);
                    if (type!=financeEntity.getType()){
                        financeEntity.setRemarks(financeEntity.getRemarks()+"type :"+financeEntity.getType()+"-->"+type);
                        financeEntity.setType(type);
                    }
                    if (money>0){
                        financeEntity.setMoney(money);
                    }
                    if (!operate_name.isEmpty()){
                        financeEntity.setOperate_name(operate_name);
                    }
                    if (!describe_info.isEmpty()){
                        financeEntity.setDescribe_info(describe_info);
                    }
                    financeService.save(financeEntity);
                    result.setData(financeEntity);
                    result.setSuccess(true);
                    result.setMsg("保存成功");
                    return result;
                }
        return result;
        }

    @RequiresRoles(value = {"root","financial"},logical = Logical.OR)
    @ApiOperation(value = "确认账单",notes = "finance and root are allowed")
    @PostMapping("/confirm")
    public Result confirm(@RequestParam(name = "recordId")int id,
                          @RequestParam(name = "token")String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            UserEntity u = (UserEntity)SecurityUtils.getSubject().getPrincipal();
            FinanceEntity financeEntity = financeService.findById(id);
            financeEntity.setConfirm_name(u.getUsername());
            financeService.save(financeEntity);
            result.setMsg("记录确认成功");
            result.setSuccess(true);
            result.setData(financeEntity);
        }
        return result;
    }

}
