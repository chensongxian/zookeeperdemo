package com.csx.zookeeper.base;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author csx
 * @Package com.csx.zookeeper.base
 * @Description: 创建一个基本的zookeeper会话
 * @date 2018/3/30 0030
 */
public class ZooKeeper_Constructor_Usage_Simple implements Watcher{
    /**zookeeper连接地址*/
    private static final String CONNECT_ADDR="192.168.159.131:2181,192.168.159.132:2181,192.168.159.133:2181";

    /**session超时时间*/
    private static final int SESSION_OUTTIME=2000;

    private static CountDownLatch connectedSemaphore=new CountDownLatch(1);
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper=new ZooKeeper(CONNECT_ADDR
                    , SESSION_OUTTIME
                    , new ZooKeeper_Constructor_Usage_Simple());
            System.out.println(zooKeeper.getState());
            connectedSemaphore.await();

            System.out.println("zookeeper session established.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event:"+event);
        if(Event.KeeperState.SyncConnected==event.getState()){
            connectedSemaphore.countDown();
        }
    }
}
