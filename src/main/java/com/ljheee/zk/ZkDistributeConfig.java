package com.ljheee.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lijianhua04 on 2018/8/7.
 */
public class ZkDistributeConfig implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private static ZooKeeper zk = null;

    private static Stat stat = new Stat();


    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        // zookeeper配晋数据存放路径
        String path = "/username"; // zhuge

        //连接zookeeper并且注册一个戥认的监听器
        zk = new ZooKeeper("192.168.0.60:2181", 5000, new ZkDistributeConfig());

        //等待zk连接成功的通知
        connectedSemaphore.await();

        //获取path@录节点的配S数据，并注册對认的监听器
        System.out.println(new String(zk.getData(path, true, stat)));

    }


    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) { // zk连接成功通知事件
            if (Event.EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            } else if (event.getType() == Event.EventType.NodeDataChanged) {// zk目录节点数据变化通知事件
                try {
                    System.out.println("配置已修改，新值为：" + new String(zk.getData(event.getPath(), true, stat)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
