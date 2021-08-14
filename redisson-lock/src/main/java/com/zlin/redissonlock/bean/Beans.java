package com.zlin.redissonlock.bean;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author zlin
 * @date 20210814
 */
@Component
public class Beans {

    @Bean
    public RedissonClient getRedissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.3.26:6379").setPassword("123456");
        return Redisson.create(config);
    }

}
