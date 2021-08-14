package com.zlin.distributezklock.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author zlin
 * @date 20210814
 */
@Slf4j
public class ZkLock implements AutoCloseable, Watcher {

    private final ZooKeeper zooKeeper;

    private String zNode;

    public ZkLock() throws IOException {
        this.zooKeeper = new ZooKeeper("192.168.3.26:2181", 100000, this);
    }

    public boolean getLock(String businessCode) {
        String rootNode = "/" + businessCode;
        try {
            // 创建业务根节点
            Stat state = zooKeeper.exists(rootNode, false);
            if (state == null) {
                zooKeeper.create(
                        rootNode,
                        businessCode.getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }
            // 创建瞬时节点（/{businessCode}/{businessCode}_0000001）
            zNode = zooKeeper.create(
                    rootNode + rootNode + "_",
                    businessCode.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            // 获取锁
            List<String> childrenList = zooKeeper.getChildren(rootNode, false);
            Collections.sort(childrenList);
            log.info("businessCode: {}, childrenList: {}", businessCode, childrenList);
            String firstNode = childrenList.get(0);
            if (zNode.endsWith(firstNode)) {
                return true;
            }
            // 未获取到锁，监听前一个节点消失
            String previousNode = firstNode;
            for (String node : childrenList) {
                if (zNode.endsWith(node)) {
                    zooKeeper.exists(rootNode + "/" + previousNode, true);
                    break;
                }else {
                    previousNode = node;
                }
            }
            // 等待前一个节点消失
            synchronized (this) {
                wait();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        zooKeeper.delete(zNode, -1);
        zooKeeper.close();
        log.info("已经释放了zookeeper锁, zNode: {}", zNode);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        // 监听到前一个节点消失，唤醒线程
        if (Event.EventType.NodeDeleted.equals(watchedEvent.getType())) {
            synchronized (this) {
                notify();
            }
        }
    }
}
