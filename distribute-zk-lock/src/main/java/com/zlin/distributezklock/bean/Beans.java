package com.zlin.distributezklock.bean;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author zlin
 * @date 20210814
 */
@Component
public class Beans {

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework getCuratorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        return CuratorFrameworkFactory.newClient("192.168.3.26:2181", retryPolicy);
    }

}
