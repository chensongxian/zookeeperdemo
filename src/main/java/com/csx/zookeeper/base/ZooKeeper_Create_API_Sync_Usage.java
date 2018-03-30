package com.csx.zookeeper.base;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author csx
 * @Package com.csx.zookeeper.base
 * @Description: TODO
 * @date 2018/3/30 0030
 */
public class ZooKeeper_Create_API_Sync_Usage implements Watcher{
    /**zookeeper连接地址*/
    private static final String CONNECT_ADDR="192.168.159.131:2181,192.168.159.132:2181,192.168.159.133:2181";

    /**session超时时间*/
    private static final int SESSION_OUTTIME=2000;

    private static CountDownLatch connectedSemaphore=new CountDownLatch(1);


    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper=new ZooKeeper(CONNECT_ADDR
                                ,SESSION_OUTTIME
                                ,new ZooKeeper_Create_API_ASync_Usage());
            connectedSemaphore.await();

            String path1 = zooKeeper.create("/zk-test-ephemeral-",
                    "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL);
            System.out.println("Success create znode: " + path1);

            String path2 = zooKeeper.create("/zk-test-ephemeral-",
                    "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("Success create znode: " + path2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if(Event.KeeperState.SyncConnected==event.getState()){
            connectedSemaphore.countDown();
        }
    }
}
