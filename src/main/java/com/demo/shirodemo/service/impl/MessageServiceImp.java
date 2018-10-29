package com.demo.shirodemo.service.impl;

import com.alibaba.fastjson.JSONException;
import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.service.MessageService;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MessageServiceImp implements MessageService {

    public Result sendMseeage(String a, String b, String c,int i){
        Result result = new Result(false);
        // 短信应用SDK AppID
        int appid = 1400134641; // 1400开头
        // 短信应用SDK AppKey
        String appkey = "3329cbf14a1acf758ff0b86d2714fe6b";
        // 需要发送短信的手机号码
        String phoneNumbers = c;
     ;
        // 短信模板ID，需要在短信应用中申请
        int templateId = 183650 ; //
        String smsSign = "二哈Plus";

        try {
            SmsSingleSender sSender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result1 ;
            if (i==2){
                templateId = 183569;
                String[] params = {a};
                result1 = sSender.sendWithParam("86", phoneNumbers,
                        templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            }else if (i==1){
                templateId = 183650;
                String[] params = {a,b};
                result1 = sSender.sendWithParam("86", phoneNumbers,
                        templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            }else if(i==3){
                templateId = 199918;
                String[] params = {a,b};
                result1 = sSender.sendWithParam("86", phoneNumbers,
                        templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            }else{
                templateId = 199919;
                String[] params = {a};
                result1 = sSender.sendWithParam("86", phoneNumbers,
                        templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            }
            System.out.println(result1);
            if (result1.result==0){
                result.setData(result1);
                result.setSuccess(true);
                result.setMsg("发送成功");
            }else
                result.setSuccess(false);
                result.setMsg(result1.errMsg);
                result.setData(result1);

        } catch (HTTPException e) {
            // HTTP响应码错误
            result.setMsg("HTTP响应码错误");
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            result.setMsg("json解析错误");
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            result.setMsg("网络IO错误");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result send2Customer(String name, String frontPhone, String customerPhone) {
        return sendMseeage(name,frontPhone,customerPhone,1);
    }

    @Override
    public Result send2Tech(String techName, String techPhone) {
        return sendMseeage(techName,null,techPhone,2);
    }


    //再加一个参数判断
    @Override
    public Result merchantSend2Customer(String name, String customerPhone,String merchantPhone) {
        return sendMseeage(name,customerPhone,merchantPhone,3);
    }

    @Override
    public Result send2Merchant(String merchantName, String merchantPhone) {
        return sendMseeage(merchantName,null,merchantPhone,4);
    }
}
