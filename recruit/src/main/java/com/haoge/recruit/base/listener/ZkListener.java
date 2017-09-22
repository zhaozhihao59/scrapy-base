package com.haoge.recruit.base.listener;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.slf4j.LoggerFactory;

public class ZkListener extends LeaderSelectorListenerAdapter implements Closeable{

	private String name;
	
	public static CuratorFramework client;
	private Log logger = LogFactory.getLog(getClass());
	
	private LeaderSelector leaderSelector;
	
	private AtomicInteger leaderCount = new AtomicInteger();
	
	public ZkListener(CuratorFramework client,String path,String name){
		this.name = name;
		leaderSelector = new LeaderSelector(client, path, this);
		leaderSelector.autoRequeue();
	}
	@Override
	public void takeLeadership(CuratorFramework client) throws Exception {
		leaderCount.getAndIncrement();
		logger.info("cur leader is :" + this.name);
		
		ZkListener.client = client;
	}
	public void start(){
		leaderSelector.start();
	}
	
	@Override
	public void close() throws IOException {
		leaderSelector.close();
	}

}
