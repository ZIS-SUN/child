package com.kindergarten;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 幼儿园信息管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.kindergarten.mapper")
public class KindergartenApplication {

    public static void main(String[] args) {
        SpringApplication.run(KindergartenApplication.class, args);
        System.out.println("=================================");
        System.out.println("幼儿园信息管理系统启动成功！");
        System.out.println("接口文档地址: http://localhost:8080/api/doc.html");
        System.out.println("=================================");
    }
}
