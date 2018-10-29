package com.demo.shirodemo.tool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtils {
    private final static String TOKEN = "";
/**
     *
     * @param list
     * @param n  每份的个数
     * @param <T>
     * @return
 */
public static <T> List<List<T>> subList(List<T> list, int n){

    if (n == 0){
        return null;
    }
    List<List<T>> result = new ArrayList<>();
    List<T> value;
    int remainder = list.size()%n;
    int num = list.size()/n;
    //遍历num次
    for (int i = 0;i<num;i++){
        value = list.subList(i*n,(i+1)*(n));
        result.add(value);
    }
    if (remainder>0){
        result.add(list.subList(list.size()-remainder,list.size()));
    }
    return result;

}

    public static void main(String[] args) {
        String s = "{\"access_token\":\"14_CSW36SRZBDj6KAq0yFLrmKi3LZhk8xJuCQwxf18_nZK0MEwY_uPpwVTDTDKwiygKf5yWkXsIoKfWZzK4pipKN7PD1z_1CU1iW1Ufzigss5CITPPkEklfgH8XwCPsX8RU2-i7Lg0B_EKkTU-GEOPdAGAOYZ\",\"expires_in\":7200}\n";
        String token = s.split("\"")[3];
        System.out.println(token);
        Arrays.asList(token).forEach((System.out::println));
    }

    public static Boolean checkSignature(String signature, String timestamp, String nonce){

        //与token 比较
        String[] arr = new String[] { TOKEN, timestamp, nonce };

        // 将token、timestamp、nonce三个参数进行字典排序
        Arrays.sort(arr);

        StringBuilder content = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }

        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");

            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = (digest.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        content = null;
        // 将sha1加密后的字符串可与signature对比
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;

    }
}
