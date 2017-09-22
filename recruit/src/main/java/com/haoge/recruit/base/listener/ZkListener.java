package com.haoge.recruit.base.listener;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.leader.Participant;
import org.slf4j.LoggerFactory;


public class ZkListener extends LeaderSelectorListenerAdapter implements Closeable{

	
	private Log logger = LogFactory.getLog(getClass());
	
	private LeaderSelector leaderSelector;
	
	
	public ZkListener(CuratorFramework client,String path){
		leaderSelector = new LeaderSelector(client, path, this);
		leaderSelector.autoRequeue();
	}
	@Override
	public void takeLeadership(CuratorFramework client) throws Exception {
		System.out.println("aaa");
		TimeUnit.MINUTES.sleep(30);
		
	}
	public void start(){
		leaderSelector.start();
	}
	
	@Override
	public void close() throws IOException {
		leaderSelector.close();
	}
	
	public LeaderSelector getLeaderSelector(){
		return this.leaderSelector;
	}

}
