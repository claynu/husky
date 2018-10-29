package com.demo.shirodemo.service.impl;

import com.demo.shirodemo.dao.ResultForTechRepository;
import com.demo.shirodemo.dao.TechRepository;

import com.demo.shirodemo.entity.Result;

import com.demo.shirodemo.entity.table.*;
import com.demo.shirodemo.service.MerchantService;
import com.demo.shirodemo.service.OrderService;
import com.demo.shirodemo.service.RedisForTokenService;
import com.demo.shirodemo.service.TechService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TechServiceImpl implements TechService {

    @Autowired
    private RedisForTokenService tokenService;
    private Result result = new Result(false);
    @Autowired
    private TechRepository repository;
    @Autowired
    private OrderService orderService;

    @Autowired
    private ResultForTechRepository resultForTechRepository;

    @Autowired
    private MerchantService merchantService;



    @Transactional
    @Override
    public Result changeStatus(String token) {
        result = new Result(false);
        UserEntity userEntity = (UserEntity) SecurityUtils.getSubject().getPrincipal();
        TechForOrders forOrders =  repository.getOne(userEntity.getPhone());
        if (forOrders.getStatus()==1){
            forOrders.setStatus(0);
            result.setMsg("修改完成 已拒绝接受新订单");
        }else{
            forOrders.setStatus(1);
            result.setMsg("修改完成 已允许接受新订单");
        }
        result.setData(forOrders);
        result.setSuccess(true);
        return result;
    }


    @Override
    public Result getOneForOrder(OrdersEntity ordersEntity) {
        // done: 2018/9/21 获取所有可接单tech  根据 数量和类型 赋予一个匹配度 返回匹配度最高的一个  优先级 数量》软件名》版本
        List<TechForOrders> list_all = repository.findByStatusAndOrderByAmount();
        List<TechForOrders> list = new ArrayList<>();
        if (list_all.size()==0){
            result.setSuccess(false);
            result.setMsg("当前暂无技术人员接单，请稍后手动分配");
            result.setData(null);
            return result;
        }
        for (TechForOrders orders:list_all){
            log.error("list_all.size"+list_all.size());
            if (orders.getAmount()==list_all.get(0).getAmount()){
                //升序列表 和第一个相同就加入 第二级
                list.add(orders);
                continue;
            }
            else break;
        }
        if (list.size()==1){
            log.error("list_size可分配人数:"+list.get(0).getPhone());
            result.setData(list.get(0));
        }
        else{
            log.error("list_size可分配人数:"+list.size());
            result.setData(forTheOne(list,ordersEntity));
        }
        result.setSuccess(true);
        result.setMsg("已分配员工");
        return result;
      }
    
    public TechForOrders forTheOne( List<TechForOrders> list,OrdersEntity ordersEntity) {
        log.error("--------第二次匹配------------");
        //type == 1  时调用 所以 只存在软件  改用正则！！！
        String pattern = "(\\d+)(\\+)(\\d+||\\W+)([,;])";
        Matcher m = Pattern.compile(pattern).matcher(ordersEntity.getMerchandise());
        List<String> orderSoftId = new ArrayList<>();
        List<String> version = new ArrayList<>();
        while (m.find()){
            System.out.println("id :"+m.group(1));
            orderSoftId.add(m.group(1));
            System.out.println("version "+m.group(3));
            version.add(m.group(3));
        }
        log.error("order_soft_size"+orderSoftId.size());
        List<Integer> score_list = new ArrayList<>();
        for (int i = 0;i<list.size();i++){
            Matcher techMat = Pattern.compile(pattern).matcher(list.get(i).getProduct());
            int score = 0;
            while (techMat.find()){
                log.error("tech_id+version :"+techMat.group(1)+" + "+techMat.group(3));
                for (int a =0;a <orderSoftId.size();a++){
                    log.error("orderSoftId.get(a)"+orderSoftId.get(a));
                    if (orderSoftId.get(a).equals(techMat.group(1))){
                        score += 50;
                        log.error("软件相同+50 score = "+score );
                        if (version.get(a).equals(techMat.group(3))){
                            //版本相同
                            score += 20;
                            log.error("版本相同+20 score = "+score );
                        }
                    }
                }
            }
            score_list.add(score);
        }
        int[] max = {0,0};
       for (int  i=0;i<score_list.size();i++){
           int score = score_list.get(i);
           log.error("score:"+score);
            if (score>max[1]){
                max[1] = score;
                max[0] = i;
            }
       }
       return list.get(max[0]);
    }



    @Override
    public TechForOrders save(TechForOrders techForOrders) {
        return repository.save(techForOrders);
    }

    @Override
    public Result getOne(String phone) {
        result = new Result(false);
      try {
          result.setData(repository.getOne(phone));
          result.setSuccess(true);
          return result;
      }catch (Exception e){
          e.printStackTrace();
      }
      return result;
    }

    @Override
    public Result getByName(String name) {
        result = new Result(false);
        try {
            TechForOrders forOrders = repository.findByName(name);
            if (forOrders.getStatus()==1){
                result.setData(forOrders);
                result.setMsg("查询成功");
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
                result.setMsg("该技术人员当前暂无法接单");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public Result getAllByStatus() {
        result = new Result(false);
        try {
            result.setData(resultForTechRepository.findAllByStatus());
            result.setMsg("查询成功");
            result.setSuccess(true);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }

    @Transactional
    @Override
    public Result delOneRecord(OrdersEntity ordersEntity) {
        result = new Result(false);
        //根据分号分割匹配 比较之后 拼接
        try {
            System.out.println(ordersEntity.getOperation_phone());
            TechForOrders tech = repository.getOne(ordersEntity.getOperation_phone());
            String[] orders_array = tech.getProduct().split(";");
            if (orders_array.length>2) {
                String str = "";
                for (int i = 0; i < orders_array.length; i++) {
                    if (orders_array[i].equals(ordersEntity.getMerchandise())) {

                        for (int a = 0; (a > 0 && a < i) || (a > i && a < orders_array.length); a++) {
                            str += orders_array[a];
                        }
                    }
                }
                tech.setProduct(str);
                tech.setAmount(tech.getAmount() - 1);
                if (tech.getAmount() == 98) {
                    tech.setStatus(1);
                }

            }else {
                tech.setProduct("");
                tech.setStatus(1);
                tech.setAmount(0);
            }
            repository.save(tech);
            result.setData(tech);
            result.setSuccess(true);
            result.setMsg("操作成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
