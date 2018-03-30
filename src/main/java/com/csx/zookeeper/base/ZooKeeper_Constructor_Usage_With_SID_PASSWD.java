package com.csx.zookeeper.base;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author csx
 * @Package com.csx.zookeeper.base
 * @Description: 复用sessionId和sessionPasswd，维持之前对话的有效性
 * @date 2018/3/30 0030
 */
public class ZooKeeper_Constructor_Usage_With_SID_PASSWD implements Watcher {
    /**zookeeper连接地址*/
    private static final String CONNECT_ADDR="192.168.159.131:2181,192.168.159.132:2181,192.168.159.133:2181";

    /**session超时时间*/
    private static final int SESSION_OUTTIME=2000;

    private static CountDownLatch connectedSemaphore=new CountDownLatch(1);

    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper=new ZooKeeper(CONNECT_ADDR
                    ,SESSION_OUTTIME
                    ,new ZooKeeper_Constructor_Usage_With_SID_PASSWD());
            connectedSemaphore.await();
            long sessionId = zooKeeper.getSessionId();
            byte[] sessionPasswd = zooKeeper.getSessionPasswd();

            //错误的sessionId和sessionPasswd
            zooKeeper=new ZooKeeper(CONNECT_ADDR
                    ,SESSION_OUTTIME
                    ,new ZooKeeper_Constructor_Usage_With_SID_PASSWD()
                    ,111
                    ,"test".getBytes());

            //正确的sessionId和sessionPasswd
            zooKeeper=new ZooKeeper(CONNECT_ADDR
                    ,SESSION_OUTTIME
                    ,new ZooKeeper_Constructor_Usage_With_SID_PASSWD()
                    ,sessionId
                    ,sessionPasswd);

            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void process(WatchedEvent event) {
        System.out.println("Received watched event:"+event);
        if(Event.KeeperState.SyncConnected==event.getState()){
            connectedSemaphore.countDown();
        }
    }
}
