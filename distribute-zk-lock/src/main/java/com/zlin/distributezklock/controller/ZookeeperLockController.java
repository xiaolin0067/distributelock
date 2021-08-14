package com.zlin.distributezklock.controller;

import com.zlin.distributezklock.lock.ZkLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author zlin
 * @date 20210814
 */
@Slf4j
@RestController
public class ZookeeperLockController {

    @Autowired
    private CuratorFramework curatorFramework;

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

    @RequestMapping("zookeeperCuratorLock")
    public String zookeeperCuratorLock() {
        log.info("进入了方法");
        InterProcessMutex lock = null;
        try {
            lock = new InterProcessMutex(curatorFramework, "/order");
            if (lock.acquire(30, TimeUnit.SECONDS)) {
                log.info("进入了锁");
                Thread.sleep(15000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (lock!= null) {
                    lock.release();
                    log.info("释放了锁");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("方法执行完成");
        return "执行完成";
    }

}
