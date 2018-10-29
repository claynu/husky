package com.demo.shirodemo.rest;


import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.ResultForStudentOrder;
import com.demo.shirodemo.entity.table.OrdersEntity;
import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.entity.table.WeChatEntity;
import com.demo.shirodemo.service.*;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;


@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private RedisForTokenService redisService;
    @Autowired
    private UserService userService;
    @Autowired
    private WeChatService weChatService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TechService techService;

    private Result result;

    @RequiresRoles(value = {"root","front_admin"},logical = Logical.OR)
    @ApiOperation(value = "根据电话查询该用户订单记录",notes = "front_admin and root are allowed")
    @ApiImplicitParams({@ApiImplicitParam(name = "type",value = "1软件2 数位屏/数位板",required = true,paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "phone",value = "电话号码",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    @PostMapping("/findByPhoneAndType")
    public Result findByPhoneAndType(
            @RequestParam(name = "type") int type,
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "token") String token){
        result = (redisService.checkToken(token));
        if (result.getSuccess()){
            return orderService.findAllByTypeAndCustPhone(type,phone,2);
        }
        else return result;

    }

    @RequiresRoles(value = {"root","front_admin"},logical = Logical.OR)
    @ApiOperation(value = "根据电话查询该用户订单记录",notes = "front_admin and root are allowed")
    @ApiImplicitParams({@ApiImplicitParam(name = "type",value = "1软件2 数位屏/数位板",required = true,paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "phone",value = "电话号码",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    @PostMapping("/findByPhoneAndTypeOneByOne")
    public Result findByPhoneAndTypeOneByOne(
            @RequestParam(name = "type") int type,
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "token") String token){
        result = (redisService.checkToken(token));
        if (result.getSuccess()){
            return orderService.findAllByTypeAndCustPhone(type,phone,1);
        }
        else return result;

    }

    @RequiresRoles(value = {"root"},logical = Logical.OR)
    @ApiOperation(value = "根据学号查询该用户订单记录",notes = "front_admin and root are allowed")
    @ApiImplicitParams({@ApiImplicitParam(name = "type",value = "1软件2数位屏/数位板",required = true,paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "studentId",value = "学号",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    @PostMapping("/findAllByUserStudentId")
    public Result findAllByUserStudentId(@RequestParam(name = "type") int type,
                                         @RequestParam(name = "studentId") String studentId,
                                     @RequestParam(name = "token") String token){
        result = redisService.checkToken(token);
        if (result.getSuccess()){
            try {
                String phone = weChatService.findByStudentId(studentId).getPhone();
                return orderService.findAllByTypeAndCustPhone(type,phone,2);
            }catch (Exception e){
                result.setSuccess(false);
                result.setMsg("学号不存在");
            }
        }
        return result;
    }

    @RequiresRoles(value = {"root"},logical = Logical.OR)
    @ApiOperation(value = "根据学号查询该用户订单记录",notes = "front_admin and root are allowed")
    @ApiImplicitParams({@ApiImplicitParam(name = "type",value = "1软件2数位屏/数位板",required = true,paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "studentId",value = "学号",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")})
    @PostMapping("/findAllByUserStudentIdOneByOne")
    public Result findAllByUserStudentIdOneByOne(@RequestParam(name = "type") int type,
                                         @RequestParam(name = "studentId") String studentId,
                                         @RequestParam(name = "token") String token){
        result = redisService.checkToken(token);
        if (result.getSuccess()){
            try {
                String phone = weChatService.findByStudentId(studentId).getPhone();
                return orderService.findAllByTypeAndCustPhone(type,phone,1);
            }catch (Exception e){
                result.setSuccess(false);
                result.setMsg("学号不存在");
            }
        }
        return result;
    }



    @ApiOperation(value = "查看该技术人员订单记录",notes = "front_admin and root are allowed")
    @PostMapping("/findAllByTechPhone")
    public Result findAllByTechPhone(@RequestParam(name = "tech_phone") String phone,
                                    @RequestParam(name = "token") String token){
        Result result =redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.findAllByTechPhone(phone);
        }
        else return result;
    }

    @RequiresRoles(value = {"root"},logical = Logical.OR)
    @ApiOperation(value = "查看该状态的软件订单记录 1未接单  2已接单  3等待交易  4 交易完成  -1 交易失败 -2申诉订单",notes = "front_admin and root are allowed")
    @PostMapping("/findByStatus")
    public Result findAllByStatus(@RequestParam(name  ="status") int status,
                                  @RequestParam(name = "token") String token){
        Result result = (redisService.checkToken(token));
        if (result.getSuccess()){
            if (status>=-2&&status<5&&status!=0)
                return orderService.findByTypeAndStatus(1,status,2);
            else return new Result(false,"未知状态码---please check it");

        }
        else return result;
    }

    @RequiresRoles(value = {"root"},logical = Logical.OR)
    @ApiOperation(value = "查看该状态的软件订单记录 1未接单  2已接单  3等待交易  4 交易完成  -1 交易失败 -2申诉订单",notes = "front_admin and root are allowed")
    @PostMapping("/findAllByStatusOneByOne")
    public Result findAllByStatusOneByOne(@RequestParam(name  ="status") int status,
                                  @RequestParam(name = "token") String token){
        Result result = (redisService.checkToken(token));
        if (result.getSuccess()){
            if (status>=-2&&status<5&&status!=0)
                return orderService.findByTypeAndStatus(1,status,1);
            else return new Result(false,"未知状态码---please check it");

        }
        else return result;
    }

    @RequiresRoles(value = {"root"},logical = Logical.OR)
    @ApiOperation(value = "查看所有订单记录",notes = "front_admin and root are allowed")
    @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "String")
    @PostMapping("/findAll")
    public Result findAll(@RequestParam(name = "token")String token){
        result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.findAll();
        }
        else return result;

    }

    @RequiresRoles("customer")
    @ApiOperation(value = "添加订单记录",notes = "merchandise有固定格式注意标点符号 (type.id+version*num,id+version*num;type.id+version*num,id+version*num;)" +
            " 软件没有num 数位屏没有version type:1软件 2数位板 3数位屏    eg:1.2+2012,2+2014;2.1+s*1; 软件id2 版本2012 2014 各1个 数位板id 1 s型号一个")
    @ApiImplicitParams({@ApiImplicitParam(name = "type",value = "1软件 2(数位板、数位屏)",required = true,paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "remarks",value = "备注",required = false,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "merchandise",value = "商品/软件",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "money",value = "价格",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "token",value = "令牌",required = true,paramType = "query",dataType = "Strinet")})
    @PostMapping("/addOrder")
    public Result save(@RequestParam(name = "type")int type,
                       @RequestParam(name = "remarks",required = false)String remarks,
                        @RequestParam(name = "merchandise")String merchandise,
                       @RequestParam(name = "money")int money,
                       @RequestParam(name = "token")String token
                       ){
        result = redisService.checkToken(token);
        if (result.getSuccess()){
            try {
                Subject subject = SecurityUtils.getSubject();
                UserEntity userEntity = (UserEntity)subject.getPrincipal();
                OrdersEntity ordersEntity = new OrdersEntity(type,userEntity.getPhone(),merchandise,remarks,money,new Timestamp(System.currentTimeMillis()),1);
                return orderService.save(ordersEntity);
            }
            catch (NullPointerException e){
                return new Result(false,"请先登录再操作");
            }
            catch (Exception e){
                return new Result(false,"操作异常");
            }
        }
        else return result;
    }

    @ApiOperation(value = "查看本人所有的订单记录",notes = "customer only")
    @PostMapping("/getOwnOrdersForCustomer")
    public Result findOwnsOrders(@RequestParam(name = "token")String  token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.findOwnsOrders();
        }
        else return result;
    }


    // done: 2018/9/21 修改订单  分配订单  短信  技术人员的订单查询

    @RequiresRoles(value = {"customer","tech"},logical = Logical.OR)
    @ApiOperation(value = "客户/技术人员放弃订单",notes = "customer  tech ")
    @PostMapping("/AbandonOrder")
    public Result AbandonOrder(@RequestParam(name = "id")int id,
                                       @RequestParam(name = "abandonReason")String  abandonReason,
                                       @RequestParam(name = "token")String  token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.abandonOrder(id,abandonReason);
        }
        else return result;
    }

    @RequiresRoles(value = {"root","front_admin"},logical = Logical.OR)
    @ApiOperation(value = "手动分配订单",notes = "\"root\",\"front_admin\"")
    @PostMapping("/distributionOrder")
    public Result distributionOrder(@RequestParam(name = "id")int id,
                                    @RequestParam(name = "token")String  token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.distributionOrder(id);
        }
        else return result;
    }

    @RequiresRoles(value = {"root","front_admin"},logical = Logical.OR)
    @ApiOperation(value = "分配订单给指定技术人员",notes = "\"root\",\"front_admin\"")
    @PostMapping("/distributionOrderByTechName")
    public Result distributionOrderByName(@RequestParam(name = "id")int id,
                                    @RequestParam(name = "techName")String techName,
                                    @RequestParam(name = "token")String  token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.distributionOrderByName(id,techName);
        }
        else return result;
    }



    @RequiresRoles(value = {"merchant","root"},logical = Logical.OR)
    @ApiOperation(value = "重分配疑问订单",notes = "merchant,root")
    @PostMapping("/distributionDoubtOrder")
    public Result distributionDoubtOrder(@RequestParam(name = "id")int id,
                               @RequestParam(name = "token")String  token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.distributionDoubtOrder(id);
        }
        else return result;
    }
    @RequiresRoles(value = {"merchant","root","customer"},logical = Logical.OR)
    @ApiOperation(value = "申诉订单（存在交易问题）",notes = "merchant,root,customer")
    @PostMapping("/inDoubtOrder")
    public Result inDoubtOrder(@RequestParam(name = "id")int id,
                                         @RequestParam(name = "token")String  token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.inDoubtOrder(id);
        }
        else return result;
    }

    @RequiresRoles(value = {"merchant","customer","root"},logical = Logical.OR)
    @ApiOperation(value = "确认完成交易 只需要一方确认即可（工作室type=1，客户type=2，小程序type=1/2）",notes = "{\"merchant\",\"customer\",\"root\"}")
    @PostMapping("/completeOrder")
    public Result completeOrder(@RequestParam(name = "id")int id,
                                @RequestParam(name = "token")String  token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.completeOrder(id);
        }
        else return result;
    }

    @RequiresRoles(value = {"tech"},logical = Logical.OR)
    @ApiOperation(value = "技术人员确认完成订单",notes = "tech only")
    @PostMapping("/techCompleteOrder")
    public Result techCompleteOrder(@RequestParam(name = "id")int id,
                                    @RequestParam(name = "token")String  token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return orderService.techCompleteOrder(id);
        }
        else return result;
    }


    @RequiresRoles(value = {"root","front_admin"},logical = Logical.OR)
    @ApiOperation(value = "查询当前所有可接单技术人员",notes = "root or front_admin")
    @PostMapping("/findAllTech")
    public Result findAllTech(@RequestParam(name = "token")String  token){
        Result result = redisService.checkToken(token);
        if (result.getSuccess()){
            return techService.getAllByStatus();
        }
        else return result;
    }

}
