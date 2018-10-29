package com.demo.shirodemo.tool;

import org.apache.shiro.crypto.hash.Md5Hash;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class CryptographyUtil {

    /**
     * 加密时通过key->value映射
     * 解密反之
     * 0 -> j -> 3
     * 1 -> l -> 5
     * 2 -> n -> a
     * 3 -> m -> 6
     * 4 -> p -> 7
     * 5  q  2
     * 6  o  1
     * 7  r  9
     * 8  s  4
     * 9  v  f
     * a  t  e
     * b  z  8
     * c  u  0
     * d  x  b
     * e  w  d
     * f  y  c
     */
    private static char[] cha = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    private static char[] fir = {'j','l','n','m','p','q','o','r','s','v','t','z','u','x','w','y'};
    private static char[] sec = {'3','5','a','6','7','2','1','9','4','f','e','8','0','b','d','c'};


    private static String SALT = "TAKUMI";

    private static String EncodeSec(String str){
        for(int i=0;i<16;i++){
            str = str.replace(cha[i],fir[i]);
        }
        for(int i=0;i<16;i++){
            str = str.replace(fir[i],sec[i]);
        }
        return str;
    }

    private static String DecodeStr(String str){
        for(int i=0;i<16;i++){
            str = str.replace(sec[i],fir[i]);
        }
        for(int i=0;i<16;i++){
            str = str.replace(fir[i],cha[i]);
        }
        return str;
    }

    public static String md5(String str){

        return EncodeSec(new Md5Hash(str,CryptographyUtil.SALT).toString());
    }

    public static void main(String[] args) {
        String password = "12345678910";
        System.out.println(System.currentTimeMillis());
        System.out.println(md5(password));
        System.out.println(getUserToken("17608038113"));
        Scanner sc = new Scanner(System.in);
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");

        for (String i:list){
            System.out.println(i);
        }

        System.out.println(new Date());

        while(true){
            System.out.println("请输入要检验的字符串");
            password = sc.nextLine();
            System.out.println(password.matches("1\\d{10}"));
            System.out.println(password.matches("\\d{11}"));
        }

    }

    /**
     * 确认唯一性即可
     * @param phone
     * @return md5(时间戳)
     */
    public static String getUserToken(String phone){
        return EncodeSec(new Md5Hash(String.valueOf(System.currentTimeMillis()),phone).toString());
    }

}
