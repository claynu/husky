package com.demo.shirodemo.service.impl;

import com.demo.shirodemo.dao.SoftwareRepository;
import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.SoftwareInfo;
import com.demo.shirodemo.entity.table.SoftwareEntity;
import com.demo.shirodemo.service.MerchantService;
import com.demo.shirodemo.service.RedisForMerchantService;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RedisForMerchantServiceImpl implements RedisForMerchantService {
    @Resource()
    private RedisTemplate<String , SoftwareInfo> softwareInfoRedisTemplate;
    @Autowired
    private SoftwareRepository softwareRepository;

    @Override
    public Result findSoftByIdAndVersion(int id, String version) {
        Result result = new Result(false);
        String key = "soft:"+id+version;
        if (softwareInfoRedisTemplate.hasKey(key)){
            result.setData(softwareInfoRedisTemplate.opsForValue().get(key));
            result.setSuccess(true);
            result.setMsg("从缓存获取成功");
            log.info("从缓存获取成功");
        }
        else {
            //如果不存在该key则从数据库获取数据 并存入redis
            try {
                SoftwareEntity softwareEntity = softwareRepository.getById(id);
                List<String> versions = new ArrayList<>();
                versions.add(version);
                SoftwareInfo info = new SoftwareInfo(softwareEntity,versions);
                softwareInfoRedisTemplate.opsForValue().set(key,info);
                result.setData(info);
                result.setSuccess(true);
                result.setMsg("从数据库获取成功");
                log.info("从数据库获取成功");
            }catch (Exception e){
                e.printStackTrace();
                result.setSuccess(false);
                result.setMsg("失败");
            }

        }
        return result;

    }
}
