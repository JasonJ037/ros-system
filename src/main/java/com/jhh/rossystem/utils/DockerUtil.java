package com.jhh.rossystem.utils;

import java.util.UUID;

public class DockerUtil {

    //取名字的不需要了，因为用户不用看到名字
    public static String generateName() {
        return "docker-" + UUID.randomUUID().toString().replace("-", "").substring(0,15);
    }

    public static void main(String[] args) {
        System.out.println(generateName());
    }

}
