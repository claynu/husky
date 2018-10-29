package com.demo.shirodemo.service.impl;

import com.demo.shirodemo.dao.DigitalBoardRepository;
import com.demo.shirodemo.dao.DigitalScreenRepository;
import com.demo.shirodemo.dao.SoftwareRepository;
import com.demo.shirodemo.dao.SoftwareVersionRepository;
import com.demo.shirodemo.entity.*;
import com.demo.shirodemo.entity.table.*;
import com.demo.shirodemo.service.MerchantService;
import com.demo.shirodemo.service.RedisForMerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private DigitalBoardRepository digitalBoardRepository;
    @Autowired
    private DigitalScreenRepository digitalScreenRepository;
    @Autowired
    private SoftwareRepository softwareRepository;
    @Autowired
    private SoftwareVersionRepository versionRepository;
    private Result result = new Result(false);

    @Autowired
    private RedisForMerchantService redisForMerchant;
    @Override
    public ResultForMerchant getAllInfo() {
        result = new Result(false);
        ResultForMerchant result = new ResultForMerchant();

            //数位板 数位屏
            //List<DigitalBoard> list_board =   digitalBoardRepository.findAll();
            List<MerchandiseList> lists_cate= new ArrayList<>();
            MerchandiseList<DigitalBoard> board = new MerchandiseList<>();
            board.setCategory("数位板");
            board.setList(digitalBoardRepository.findAll());
            MerchandiseList<DigitalScreen> screen = new MerchandiseList<>();
            screen.setCategory("数位屏");
            screen.setList(digitalScreenRepository.findAll());
            lists_cate.add(board);
            lists_cate.add(screen);

            result.setList_merchan(lists_cate);
            List<SoftList> list = new ArrayList<>(); //分类封装类
            List<SoftwareEntity> list_soft = softwareRepository.findAll(); //所有软件

            log.info("list_soft.size() = "+list_soft.size());
            for (SoftwareEntity s :list_soft) {
                List<String> versions = versionRepository.getVersionsBySoftwareId(s.getId());
                SoftwareInfo info = new SoftwareInfo(s, versions);
                //如果不存在该分类  添加分类   存在 直接添加
                Boolean isExist = false;
                for (SoftList soft:list){
                    if (soft.getCategory().equals(info.getCategory())){
                        isExist = true;
                        soft.getList_soft().add(info);
                    }
                }
                if (!isExist){ //如果不存在
                    SoftList softList = new SoftList();
                    softList.setCategory(info.getCategory());
                    List<SoftwareInfo> softLists = new ArrayList<>();
                    softLists.add(info);
                    softList.setList_soft(softLists);
                    list.add(softList);
                }
            result.setSoftList(list);
            }
            return result;
    }

    @Override
    public Result findBoardById(int id) {
        result = new Result(false);
        try {
            DigitalBoard board = digitalBoardRepository.getOne(id);
            result.setSuccess(true);
            result.setData(board);
            if (board.getMerchandiseName().isEmpty()){
                result.setSuccess(false);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
        }
        finally {
            return result;
        }
    }

    @Override
    public Result findScreenById(int id) {
        result = new Result(false);
        try {
            DigitalScreen screen = digitalScreenRepository.getOne(id);
            result.setSuccess(true);
            result.setData(screen);
            if (screen.getMerchandiseName().isEmpty()) {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        } finally {
            return result;
        }
    }
    @Override
    public Result findSoftById(int id) {
        result = new Result(false);
        try {
            SoftwareEntity softwareEntity = softwareRepository.getOne(id);
            List<String> version = versionRepository.getVersionsBySoftwareId(id);
            SoftwareInfo info = new SoftwareInfo(softwareEntity,version);
            result.setSuccess(true);
            result.setData(info);
            result.setMsg("查询成功");
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            result.setMsg("失败");
        }finally {
            return result;
        }
    }

    @Override
    public Result findSoftByIdAndVersion(int id, String version) {

        return redisForMerchant.findSoftByIdAndVersion(id,version);
    }
}
