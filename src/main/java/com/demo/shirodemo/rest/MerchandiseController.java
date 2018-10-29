package com.demo.shirodemo.rest;


import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.service.MerchantService;
import com.demo.shirodemo.service.RedisForTokenService;
import com.demo.shirodemo.tool.FileUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Slf4j
public class MerchandiseController {
    @Autowired
    private RedisForTokenService tokenService;
    @Autowired
    private MerchantService service;
    private Result result;


    @ResponseBody
    @RequiresRoles(value = {"root"})
    @PostMapping("/addMerchandiseByExcel")
    public Result addMerchandiseByExcel( MultipartFile file )throws Exception{

        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            return new Result(false,"上传文件类型不允许，请先下载模板");
        log.info(file.getContentType());
        Workbook wb = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = wb.getSheetAt(0);
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++)
        {
            Row row = sheet.getRow(i);//获取索引为i的行，以0开始
//            String name= row.getCell(0).getStringCellValue();//获取第i行的索引为0的单元格数据
//            int age = (int)row.getCell(1).getNumericCellValue();
//            list.add("age: "+age +" name:"+name);
//            log.info("age: "+age +"    name:"+name);

            /**
             * 图片处理
             *
             * */
        }
        try
        {
            wb.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new Result(true,"上传成功");
    }

    @ResponseBody
    @RequiresRoles(value = {"root"})
    @ApiOperation(value = "获取添加商品模板  TODO redis缓存",notes = "only root can do this")
    @GetMapping("/downLoadFile")
    public void downLoadFile(HttpServletResponse response)throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String path = ResourceUtils.getURL("classpath:").getPath();
        File filePath = new File(path, "static/Excel/");
        String fileName = "MerchandiseDemo.xlsx";
        File file = new File(filePath.getPath() + '/', fileName);
        InputStream in = (new FileInputStream(file));
        int len = 0;
        byte[] b = new byte[1024];
        while ((len = in.read(b, 0, b.length)) != -1) {
            baos.write(b, 0, len);
        }
        response.setContentType("application/octet-stream");

        OutputStream os = response.getOutputStream();
        response.setContentLength((int)file.length());
        while ((len = in.read(b)) > 0) {
            os.write(b, 0, len);
        }
        os.close();

    }


    @ResponseBody
    @ApiOperation(value = "获取所有商品/软件  TODO redis缓存",notes = "all can do this")
    @GetMapping("/getAllMerchant")

    public Result getAllMerchant(){
        result = new Result(false);
        try {
            result.setData(service.getAllInfo());
        }catch (Exception e){
            e.printStackTrace();
        }
        result.setSuccess(true);
        result.setMsg("success");
        return result;
    }


    @ApiOperation(value = "获取商品/软件图片  TODO redis缓存",notes = "all can do this")
    @GetMapping("/image")
    public ModelAndView image(@RequestParam String path){
        log.error(path);
        ModelAndView modelAndView = new ModelAndView("redirect:http://routany.cn:81/"+path);
        return modelAndView;
    }


    @ResponseBody
    @ApiOperation(value = "根据id查询商品 TODO redis缓存",notes = "only root can do this")
    @GetMapping("/getMerchantByIdAndType")
    @ApiImplicitParams({@ApiImplicitParam(name = "id",value = "id值",required = true,paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "type",value = "1软件2数位屏3数位板",required = true,paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "token",value = "令牌",paramType = "query",dataType = "String",defaultValue = "123456")})
    public Result getMerchantByIdAndType(
            @RequestParam("id")int id,
            @RequestParam("type")int type,
            @RequestParam("token")String token){
        result = tokenService.checkToken(token);
        //Done 测试
        if (result.getSuccess()) {
            switch (type){
                case 3:
                    result =(service.findBoardById(id));
                    break;
                case 2:
                    result = (service.findScreenById(id));
                    break;
                case 1:
                    result = (service.findSoftById(id));
                    break;
            }
            result.setSuccess(true);
            result.setMsg("success");
        }
        return result;
    }





}
