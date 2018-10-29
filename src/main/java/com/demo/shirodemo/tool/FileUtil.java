package com.demo.shirodemo.tool;

import com.demo.shirodemo.entity.MerchandiseList;
import com.demo.shirodemo.entity.SoftwareInfo;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.KeyStore;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        System.out.println(targetFile);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }

    public static HttpServletResponse downloadFile(String filePath, String fileName,HttpServletResponse response) throws Exception {
        String path = ResourceUtils.getURL("classpath:").getPath();
        File file = new File(filePath+ '/', fileName);
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
        InputStream in = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            in = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(in);
            wb.write(response.getOutputStream());
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return response;
    }
    public static void main(String[] args) {
        try {
            String s = "1.2+2012,3+通用版,2+2012;";
            long start;
            long end;

            StringTokenizer stringTokenizer = new StringTokenizer(s,";");
             while(stringTokenizer.hasMoreElements()){
                 start=System.currentTimeMillis();
                 StringTokenizer merchandises = new StringTokenizer(stringTokenizer.nextToken(),".");
                 switch (merchandises.nextToken()){
                     case "1" :
                         StringTokenizer merchandise = new StringTokenizer(merchandises.nextToken(),",");

                         MerchandiseList<SoftwareInfo> soft = new MerchandiseList<>();
                         List<SoftwareInfo> infos = new ArrayList<>();
                         soft.setCategory("软件");
                         //遍历merchandise
                         while (merchandise.hasMoreElements()){
                             StringTokenizer mer = new StringTokenizer(merchandise.nextToken(),"+");
                             String softId = mer.nextToken();
                             String softVersion = mer.nextToken();
                             //oftwareInfo info = (SoftwareInfo) merchantService.findSoftByIdAndVersion(Integer.parseInt(softId), softVersion).getData();
                             //infos.add(info);
                             System.out.println(softId+"version"+softVersion);
                         }
                         soft.setList(infos);
                         end = System.currentTimeMillis();
                         System.out.println(end-start);

                         //merchandiseLists.add(soft);
                         break;
                 }
             }


//            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//            System.out.println(timestamp);
//
//            String idCardNo = "51052219970921925X";
//            System.out.println( idCardNo.matches("\\d{17}(\\d||X)"));;



        } catch (Exception e) {
            e.printStackTrace();
        }
        String s = "1.2+2012,3+通用版,2+2012;";
        String[] list = s.split(";");
        List<MerchandiseList> merchandiseLists = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            long start=System.currentTimeMillis();
            String[] split1 = list[i].split("\\.");
            String type = split1[0]; //类型
            String[] merchandise = split1[1].split(",");
            switch (type) {
                case "1":

                    MerchandiseList<SoftwareInfo> soft = new MerchandiseList<>();
                    List<SoftwareInfo> infos = new ArrayList<>();
                    soft.setCategory("软件");
                    //遍历merchandise
                    for (int j = 0; j < merchandise.length; j++) {
                        String softId = merchandise[j].split("\\+")[0];
                        String softVersion = merchandise[j].split("\\+")[1];
                        System.out.println(softId+"version"+softVersion);
                    }
                    soft.setList(infos);
                    merchandiseLists.add(soft);

                    long end=System.currentTimeMillis();
                    System.out.println(end-start);
                    break;
            }
}}
}