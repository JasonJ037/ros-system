package com.jhh.rossystem.utils;

import java.util.UUID;

public class DockerUtil {

    public static String generateName(String username) {
        return username + "-docker-" + UUID.randomUUID().toString().replace("-", "").substring(0,15);
    }

    public static void main(String[] args) {
        String username = "john"; // 假设操作者的用户名为"john"
        System.out.println(generateName(username));
    }

}