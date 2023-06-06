package com.jhh.rossystem.utils;

import java.util.Base64;


/**
 * 加密类
 */
public class Base64Util {

    public static String encrypt(String password) {
        // 使用基本编码
        String encryptPassword = null;
        try {
            encryptPassword = Base64.getEncoder().encodeToString(password.getBytes("utf-8"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return encryptPassword;
    }

    public static String dEncrypt(String base64encodedString) {
        // 使用基本编码
        String password = null;
        try {
            byte[] base64decodedBytes = Base64.getDecoder().decode(base64encodedString);
            password = new String(base64decodedBytes, "utf-8");
        } catch (Exception e){
            e.printStackTrace();
        }
        return password;
    }



        public static void main(String[] args) {
        try
        {
            String base64encodedString = encrypt("123456");
            System.out.println("Base64 编码字符串(基本):" + base64encodedString);
            // 解码
            String password = dEncrypt(base64encodedString);
            System.out.println("Base64 解码字符串(基本):" + password);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

}