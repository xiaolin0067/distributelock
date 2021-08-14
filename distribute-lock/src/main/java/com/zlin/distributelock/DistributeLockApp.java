package com.zlin.distributelock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zlin
 * @date 20210810
 */
@EnableScheduling
@SpringBootApplication
@MapperScan("com.zlin.distributelock.dao")
public class DistributeLockApp {

    public static void main(String[] args) {
        SpringApplication.run(DistributeLockApp.class, args);
    }
}
