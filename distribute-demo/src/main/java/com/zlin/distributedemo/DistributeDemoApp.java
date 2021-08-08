package com.zlin.distributedemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zlin
 * @date 20210808
 */
@SpringBootApplication
@MapperScan("com.zlin.distributedemo.dao")
public class DistributeDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(DistributeDemoApp.class, args);
    }

}
