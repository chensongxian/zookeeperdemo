package com.csx.zookeeper.base;

import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import org.apache.zookeeper.*;

import javax.crypto.spec.RC2ParameterSpec;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: Zookeeper base学习笔记
 * @Author: csx
 * @Date: 2018-02-27
 */
public class ZookeeperBase {
    /**zookeeper连接地址*/
    private static final String CONNECT_ADDR="192.168.159.131:2181,192.168.159.132:2181,192.168.159.133:2181";

    /**session超时时间*/
    private static final int SESSION_OUTTIME=2000;

    /** 信号量，阻塞程序执行，用于等待zookeeper连接成功，发送成功信号 */
    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);


    public static void main(String[] args) throws Exception {
        ZooKeeper zk=new ZooKeeper(CONNECT_ADDR, SESSION_OUTTIME, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                //获取事件状态
                Event.KeeperState keeperState = event.getState();
                Event.EventType eventType = event.getType();
                //如果是建立连接
                if(Event.KeeperState.SyncConnected == keeperState){
                    if(Event.EventType.None == eventType){
                        //如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
                        connectedSemaphore.countDown();
                        System.out.println("zk建立连接");
                    }
                }
            }
        });

        //进行阻塞
        connectedSemaphore.await();

        System.out.println("..");

        //创建父节点
		zk.create("/testRoot", "testRoot".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //创建子节点
		zk.create("/testRoot/children", "children data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //获取节点洗信息
//		byte[] data = zk.getData("/testRoot", false, null);
//		System.out.println(new String(data));
//		System.out.println(zk.getChildren("/testRoot", false));

        //修改节点的值
//		zk.setData("/testRoot", "modify data root".getBytes(), -1);
//		byte[] data = zk.getData("/testRoot", false, null);
//		System.out.println(new String(data));

        //判断节点是否存在
//		System.out.println(zk.exists("/testRoot/children", false));
        //删除节点
//		zk.delete("/testRoot/children", -1);
//		System.out.println(zk.exists("/testRoot/children", false));
        List<String> list=zk.getChildren("/testRoot",false,null);
        for(String path:list){
            System.out.println(path);
            String realPath="/testRoot/"+path;
            System.out.println(new String(zk.getData(realPath,false,null)));
        }
//
//        zk.delete("/testRoot", -1, new AsyncCallback.VoidCallback() {
//            @Override
//            public void processResult(int rc, String path, Object ctx) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(rc);
//                System.out.println(path);
//                System.out.println(ctx);
//            }
//        },"a");

        zk.close();
    }
}
