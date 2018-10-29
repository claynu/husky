package com.demo.shirodemo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.service.RedisForTokenService;
import com.demo.shirodemo.service.RedisForUserService;
import com.demo.shirodemo.service.UserService;
import com.demo.shirodemo.tool.CryptographyUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;


@Slf4j
@Service
public class RedisForUserServiceImpl implements RedisForUserService {

    @Resource
    private RedisTemplate<String ,UserEntity> redisTemplate;
    @Autowired
    private RedisForTokenService redisForTokenService;
    @Autowired
    private UserService userService;
    private Result result ;


    /**
     * 查询
     * @param phone
     * @return
     */
    @Override
    public Result getUser(String phone) {
        result = new Result(false);
        String key = "user:"+phone;
        if (redisTemplate.hasKey(key)){
            log.info("从缓存获取成功");
            result.setMsg("从缓存获取成功");
            result.setSuccess(true);
            result.setData(redisTemplate.opsForValue().get(key));
            return result;
        }
        else {
            //从数据库获取值并存入redis
            result.setSuccess(false);
            UserEntity userEntity  = userService.findByPhone(phone);
            if (userEntity == null){
                result.setMsg("数据库不存在该用户");
                return result;
            }
            redisTemplate.opsForValue().set(key,userEntity);
            log.info("从数据库获取成功  user： "+userEntity.toString());
            result.setMsg("从数据库获取成功");
            result.setData(userEntity);
            return result;

        }
    }

    /**
     * 添加用户   注册
     * @param userEntity
     * @return
     */
    @Override
    public Result save(UserEntity userEntity) {
        result = new Result(false);
        String key = "user:"+userEntity.getPhone();
        userEntity.setPassword(CryptographyUtil.md5(userEntity.getPassword()));
        if (redisTemplate.hasKey(key)){
            log.info("已存在");
            result.setMsg("已存在");
            result.setSuccess(false);
            return result;
        }
        log.info(userEntity.toString());
        userService.addOneUser(userEntity);
        redisTemplate.opsForValue().set(key,userEntity);
        log.info("保存成功");
        result.setMsg("保存成功");
        result.setData(userEntity);
        result.setSuccess(true);
        return result;
    }


    /**
     * 使用场景 修改密码 用户名
     * @param userEntity
     * @return
     */
    @Override
    public Result updateUser(UserEntity userEntity) {
        result = new Result(false);
        String key = "user:"+userEntity.getPhone();
        result.setSuccess(false);
        if (redisTemplate.hasKey(key)){
            log.info("存在缓存中---修改中");
            userService.addOneUser(userEntity);
            redisTemplate.opsForValue().set(key,userEntity);
            log.info("修改完毕");
            result.setSuccess(true);
            result.setMsg("修改完成");
            result.setData(userEntity);
            return result;
        }else {
            //直接修改数据库 并加入redis缓存
                UserEntity userEntity1 = userService.findByPhone(userEntity.getPhone());
                if (userEntity1==null){
                    log.error("userEntity 不存在 ");
                    result.setMsg("用户不存在");
                    return result;
                }
                userService.addOneUser(userEntity);
                redisTemplate.opsForValue().set(key,userEntity);
                log.info("修改完成");
                result.setSuccess(true);
                result.setMsg("修改完成");
                result.setData(userEntity);
                return result;
        }
    }

    /**
     * 使用场景 删除用户时
     * @param phone
     * @return
     */
    @Override
    public Result deleteUser(String phone) {
        result = new Result(false);
        String key = "user:"+phone;
        result.setSuccess(false);
        if (redisTemplate.hasKey(key)){
            log.info("删除中...");
            if (userService.deleteUser(phone)){
                redisTemplate.delete(key);
                log.info("缓存和数据库删除完毕");
                result.setSuccess(true);
                result.setMsg("缓存和数据库删除完毕");
                result.setData(null);
                return result;
            }
            else
                result.setMsg("删除失败");
                 return result;
        }
        else{ //return userService.deleteUser(phone)
            if (userService.deleteUser(phone)){
                log.info("数据库删除完毕");
                result.setMsg("数据库删除完毕");
                return result;
            }
            else {
                log.info("不存在该数据");
                result.setMsg("不存在该数据");
                return result;
            }
        }
    }


}
