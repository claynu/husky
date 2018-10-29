package com.demo.shirodemo.service.impl;

import com.demo.shirodemo.dao.FinanceRepository;
import com.demo.shirodemo.entity.MerchandiseList;
import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.ResultForOrder;
import com.demo.shirodemo.entity.table.DigitalBoard;
import com.demo.shirodemo.entity.table.DigitalScreen;
import com.demo.shirodemo.entity.table.FinanceEntity;
import com.demo.shirodemo.entity.table.OrdersEntity;
import com.demo.shirodemo.service.FinanceService;

import com.demo.shirodemo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private FinanceRepository financeRepository;
    @Autowired
    private OrderService orderService;


    @Override
    public Result save(FinanceEntity financeEntity) {
        Result result = new Result(false);
        try {
            return new Result(true,"保存成功",financeRepository.save(financeEntity));
        }catch (Exception e)
        {
            return new Result(false,"保存失败",financeRepository.save(financeEntity));
        }
    }

    @Override
    public FinanceEntity findById(int id) {
        return financeRepository.findById(id);
    }

    @Override
    public Result findAll() {
        Result result = new Result(false);
        try {
            System.out.println("-----查询中----");
            List<FinanceEntity> list = financeRepository.findAll();
            result.setData(list);
            System.out.println("-----done----");
            result.setMsg("查询成功");
            result.setSuccess(true);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"查询失败");
        }
    }

    @Override
    public Result findByOperateName(String operate_name) {
        try {
            return new Result(true,"查询成功",financeRepository.findAllByOperate_name(operate_name));
        }catch (Exception e){
            return new Result(false,"查询失败",financeRepository.findAllByOperate_name(operate_name));
        }
    }

    @Override
    public Result findAllNotConfirm() {
        Result result = new Result(false);
        try {
            System.out.println("-----查询中----");
            List<FinanceEntity> list = financeRepository.findAllNotConfirm();
            result.setData(list);
            System.out.println("-----done----");
            result.setMsg("查询成功");
            result.setSuccess(true);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"查询失败");
        }
    }

    @Override
    public Result findAllConfirm() {
        Result result = new Result(false);
        try {
            System.out.println("-----查询中----");
            List<FinanceEntity> list = financeRepository.findAllConfirm();
            result.setData(list);
            System.out.println("-----done----");
            result.setMsg("查询成功");
            result.setSuccess(true);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"查询失败");
        }
    }

    @Override
    public Result findAllByType(int type) {
        Result result = new Result(false);
        try {
            System.out.println("-----查询中----");
            List<FinanceEntity> list = financeRepository.findAllByType(type);
            result.setData(list);
            System.out.println("-----done----");
            result.setMsg("查询成功");
            result.setSuccess(true);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"查询失败");
        }
    }

    @Override
    public int getProfit(OrdersEntity ordersEntity) {
        //订单type ==2 时 添加财务信息时计算提成
        int total =0;
        List<MerchandiseList> merchandiseLists = orderService.parseMerchandise(ordersEntity.getMerchandise());
        for (MerchandiseList merchandiseList:merchandiseLists){
            if (merchandiseList.getCategory().equals("数位屏")){
                List<DigitalScreen> digitalScreens = merchandiseList.getList();
                for (DigitalScreen digitalScreen:digitalScreens){
                    total += digitalScreen.getProfit()*digitalScreen.getSum();
                }
            }if (merchandiseList.getCategory().equals("数位板")){
                List<DigitalBoard> digitalBoards = merchandiseList.getList();
                for (DigitalBoard digitalBoard:digitalBoards){
                    total += digitalBoard.getProfit()*digitalBoard.getSum();
                }
            }
        }
        return total;
    }

    @Override
    public List<FinanceEntity> findAllByDescribe_infoContaining(String info) {
        return financeRepository.findAllByDescribe_infoContaining(info);
    }
}
