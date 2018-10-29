package com.demo.shirodemo.rest;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.service.OrderService;
import com.demo.shirodemo.service.RedisForTokenService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/merchant")
public class MerchantController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisForTokenService redisService;
    private Result result;

    @RequiresRoles(value = {"root","merchant"},logical = Logical.OR)
    @ApiOperation(value = "查看所有第三方订单记录",notes = "front_admin and root are allowed")
    @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")
    @PostMapping("merchantFindAll")
    public Result merchantFindAll(
            @RequestParam(name = "token") String token){
        result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.findAllByType(2);
        }
        else return result;

    }
    @RequiresRoles(value = {"root","merchant"},logical = Logical.OR)
    @ApiOperation(value = "查看该状态的第三方订单记录 1未接单  2已接单  3等待交易  4 交易完成  -1 交易失败 -2申诉订单",notes = "merchant and root are allowed")
    @PostMapping("/merchantFindAllByStatus")
    public Result merchantFindAllByStatus(@RequestParam(name  ="status") int status,
                                  @RequestParam(name = "token") String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            if (status>=-2&&status<5&&status!=0)
                return orderService.findByTypeAndStatus(2,status,2);
            else return new Result(false,"未知状态码---please check it");

        }
        else return result;
    }


    @RequiresRoles(value = {"root","merchant"},logical = Logical.OR)
    @ApiOperation(value = "商家查看未接单订单记录",notes = " merchant and root are allowed")
    @PostMapping("/merchantFindAllNotReceive")
    public Result merchantFindAllNotReceive(@RequestParam(name = "token") String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.findByTypeAndStatus(2,0,2);
        }
        else return result;
    }
    @RequiresRoles(value = {"root","merchant"},logical = Logical.OR)
    @ApiOperation(value = "商家查看待发货订单记录",notes = " merchant and root are allowed")
    @PostMapping("/merchantFindAllNotShipments")
    public Result merchantFindAllNotShipments(@RequestParam(name = "token") String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.findByTypeAndStatus(2,1,2);
        }
        else return result;
    }
    @RequiresRoles(value = {"root","merchant"},logical = Logical.OR)
    @ApiOperation(value = "商家查看待收货订单记录",notes = " merchant and root are allowed")
    @PostMapping("/merchantFindAllReceive")
    public Result merchantFindAllReceive(@RequestParam(name = "token") String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.findByTypeAndStatus(2,2,2);
        }
        else return result;
    }
    @RequiresRoles(value = {"root","merchant"},logical = Logical.OR)
    @ApiOperation(value = "商家查看已完成订单记录",notes = " merchant and root are allowed")
    @PostMapping("/merchantFindAllWasDone")
    public Result merchantFindAllWasDone(@RequestParam(name = "token") String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.findByTypeAndStatus(2,3,2);
        }
        else return result;
    }
    @RequiresRoles(value = {"root","merchant"},logical = Logical.OR)
    @ApiOperation(value = "商家查看所有已放弃订单记录",notes = " merchant and root are allowed")
    @PostMapping("/merchantFindAllWasAbandon")
    public Result merchantFindAllWasAbandon(@RequestParam(name = "token") String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.findByTypeAndStatus(2,-1,2);
        }
        else return result;
    }

    @RequiresRoles(value = {"root","merchant"},logical = Logical.OR)
    @ApiOperation(value = "商家查看所有未处理申诉订单记录",notes = " merchant and root are allowed")
    @PostMapping("/merchantFindAllInDoubt")
    public Result merchantFindAllInDoubt(@RequestParam(name = "token") String token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.findByTypeAndStatus(2,-2,2);
        }
        else return result;
    }

    @RequiresRoles(value = {"merchant"},logical = Logical.OR)
    @ApiOperation(value = "商家发送取货通知",notes = "merchant")
    @PostMapping("/sendMessageToCustomer")
    public Result sendMessageToCustomer(@RequestParam(name = "id")int id,
                               @RequestParam(name = "token")String  token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.sendMessageToCustomer(id);
        }
        else return result;
    }

    @RequiresRoles(value = {"merchant","customer","root"},logical = Logical.OR)
    @ApiOperation(value = "确认完成交易 请使用OrderController内的该方法 只需要一方确认即可（工作室type=1，客户type=2，小程序type=1/2）",notes = "merchant,customer,root")
    @PostMapping("/completeOrder")
    public Result completeOrder(@RequestParam(name = "id")int id,
                                        @RequestParam(name = "token")String  token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.completeOrder(id);
        }
        else return result;
    }




}
