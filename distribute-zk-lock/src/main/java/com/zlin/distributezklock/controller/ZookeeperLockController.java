package com.zlin.distributezklock.controller;

import com.zlin.distributezklock.lock.ZkLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zlin
 * @date 20210814
 */
@Slf4j
@RestController
public class ZookeeperLockController {

    @RequestMapping("zookeeperLock")
    public String zookeeperLock() {
        log.info("进入了方法");
        try (ZkLock zkLock = new ZkLock()) {
            if (zkLock.getLock("testZookeeperNode")) {
                log.info("进入了锁");
                Thread.sleep(15000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("方法执行完成");
        return "执行完成";
    }

}
