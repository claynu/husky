package com.demo.shirodemo.rest;

import com.demo.shirodemo.dao.InstallationGuideRepository;
import com.demo.shirodemo.dao.SlideEntityRepository;
import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.ResultForGuide;
import com.demo.shirodemo.entity.table.InstallationGuide;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@Slf4j
@RequestMapping("/guide")
public class InstallationController {
    @Autowired
    private InstallationGuideRepository repository;
    @Autowired
    private SlideEntityRepository slideEntityRepository;


    @GetMapping("/getAll")
    public Result getAllGuide(){
        Result result = new Result(false);
        try {
            List<InstallationGuide> list = (repository.findAllOrderByType());
            List<ResultForGuide> list1 = new ArrayList<>(5);
            ResultForGuide guideByType = new ResultForGuide(1);
            for (InstallationGuide guide:list) {
                if (guideByType.getType() == guide.getType()) {
                    //如果guide type 和前者相同 直接添加
                    guideByType.getGuides().add(guide);
                } else {
                    //如果guide type 和前者不同 先添加到返回的列表 再修改
                    list1.add(guideByType);
                    guideByType = new ResultForGuide(guide.getType());
                    guideByType.getGuides().add(guide);
                }
            }
            list1.add(guideByType);
            result.setData(list1);
            result.setSuccess(true);
            result.setMsg("查询成功");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    @GetMapping("/getOneById")
    @ApiOperation(value = "根据id查询软件安装指南")
    @ApiImplicitParam(name = "id",required = true,paramType = "query",dataType = "int")
               public Result getOneById(@RequestParam(name = "id")int id){
        Result result = new Result(false);
        ReentrantLock lock = new ReentrantLock();
        try {
            lock.lock();
            InstallationGuide guide = (repository.getOne(id));
            guide.setNum(guide.getNum()+1);
            repository.save(guide);
            result.setData(guide);
            result.setSuccess(true);
            result.setMsg("查询成功");
        }catch (NullPointerException e){
            result.setSuccess(false);
            result.setMsg("不存在该数据");
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            result.setMsg("发生异常");
        }
        finally {
            lock.unlock();
            return result;
        }
    }


    @GetMapping("/getSlide")
    @ApiOperation(value = "获取轮播图")
    @ApiImplicitParam(name = "type",required = true,dataType = "int",paramType ="query")
    public Result getSlide(@RequestParam(name = "type")int type){
        Result result = new Result(false);
        try {
            result.setData(slideEntityRepository.getAllByType(type));
            result.setSuccess(true);
            result.setMsg("查询成功");
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            result.setMsg("发生异常");
        }finally {
            return result;
        }
    }
}
