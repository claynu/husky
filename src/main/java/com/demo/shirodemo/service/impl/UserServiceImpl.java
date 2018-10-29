package com.demo.shirodemo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.demo.shirodemo.dao.UserEntityRepository;
import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.ResultForLogin;
import com.demo.shirodemo.entity.table.TechForOrders;
import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.entity.table.UserRoleEntity;
import com.demo.shirodemo.service.*;
import com.demo.shirodemo.tool.CryptographyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserEntityRepository repository;
    @Autowired
    private RedisForUserService redisForUserService;
    @Autowired
    private RedisForTokenService tokenService;
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    TechService techService;
    @Autowired
    private RoleService roleService;
    private Result result;


    @Override
    public UserEntity findByPhone(String phone) {
        return repository.getOne(phone);
    }

    @Override
    public void addOneUser(UserEntity userEntity) {
        repository.save(userEntity);
    }

    @Override
    public Boolean deleteUser(String phone) {
        try {
            repository.deleteById(phone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public Result register(UserEntity u, UserRoleEntity ur) {
        result = new Result(false);
        result = redisForUserService.save(u);
        if (result.getSuccess()) {
            userRoleService.save(ur);
        }
        return result;
    }


    @Override
    public ResultForLogin login(String phone, String password) {
        ResultForLogin result = new ResultForLogin(false);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken utoken = new UsernamePasswordToken(phone, CryptographyUtil.md5(password));
        try {
            subject.login(utoken);
            result.setMsg("登录成功");
            result.setSuccess(true);
            result.setRoleId(userRoleService.getRoleIDByUserPhone(phone));
        }catch (Exception e){
            result.setMsg("登录失败，请检查您的信息");
            result.setData(null);
        }
        UserEntity userEntity = (UserEntity) subject.getPrincipal();
        if (!(userEntity == null)) {
            result.setMsg("welcome " + userEntity.getUsername());
            /**
             * 添加一个token key为phone value(每次登录产生的token要不同)对key md5(时间戳,phone)
             *
             */
            String userToken = tokenService.addUserToken(userEntity.getPhone());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", userToken);
            result.setData(jsonObject);
            return result;
        }
        log.info("userEntity:"+userEntity);
        return result;

    }

    @Override
    public Result findAll() {
        result = new Result(false);
       try {
           List<UserEntity> list = repository.findAll();
           result = new Result(true,"查询成功",list);
           return result;
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }
    }

    @Override
    public List<UserEntity> findAllByNameLike(String username) {
        return repository.findAllByUsernameContaining(username);
    }

    @Override
    public Result resetPassword(String phone) {

      return changePassword(phone,"123456");
    }

    @Override
    public Result changePassword(String phone, String password) {
        result = new Result(false);
        UserEntity userEntity = findByPhone(phone);
        if (userEntity == null){
            result.setMsg("不存在该用户");
            return result;
        }
        userEntity.setPassword(CryptographyUtil.md5(password));
        return redisForUserService.updateUser(userEntity);
    }

    @Override
    public Result getAllByRole(String role) {
        return getAllByRoleId(roleService.findIdByName(role));
    }

    @Override
    public Result getAllByRoleId(int roleId) {
       try {
           List<UserEntity> list = repository.getAllUserByRoleId(roleId);
           return new Result(true,"查询成功",list);
       }catch (Exception e){
           e.printStackTrace();
           return new Result(false,"查询失败");
       }
    }

    @Override
    @Transactional
    public Result addInsider(UserEntity userEntity, int roleId) {
        result = new Result(false);
        try {
            UserRoleEntity ur = new UserRoleEntity(userEntity.getPhone(),roleId);
            if (userEntity.getPassword().isEmpty()||userEntity.getPassword().length()>=6) {
                result = redisForUserService.save(userEntity);
                if (result.getSuccess()) {
                    userRoleService.save(ur);
                    if (roleId == 4) {
                        techService.save(new TechForOrders(userEntity.getPhone(), 1));
                    }
                    result.setSuccess(true);
                    result.setData(userEntity);
                    result.setMsg("添加成功");
                    return result;
                } else {
                    return result;
                }
            }
            else {
                result.setMsg("密码不能低于6位");
                return result;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}