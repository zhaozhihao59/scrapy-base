package com.haoge.recruit.base.listener;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class InitListener implements ApplicationListener<ContextRefreshedEvent>{

	@Value("${zk.leaderPath}")
	private String path;
	@Value("${zk.server}")
	private String server;
	@Value("${server.port}")
	private Integer port;
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		CuratorFramework client = CuratorFrameworkFactory.newClient(server, new ExponentialBackoffRetry(1000, 3));
		client.start();
		String name = "";
		try {
			name = InetAddress.getLocalHost().getHostAddress() + port;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ZkListener zkListener = new ZkListener(client, path,name);
		zkListener.start();
		
	}

}
