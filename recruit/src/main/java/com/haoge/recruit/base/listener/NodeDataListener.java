package com.haoge.recruit.base.listener;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.haoge.recruit.reduce.service.IZhaopinService;

@Component
public class NodeDataListener implements NodeCacheListener{
	@Resource
	private ZkListener zkListener;
	@Resource
	private CuratorFramework curatorFramework;
	@Resource
	private IZhaopinService zhaopinService;
	
	@Override
	public void nodeChanged() throws Exception {
		byte[] data = curatorFramework.getData().forPath(zkListener.getLeaderSelector().getId());
		String jsonArrStr = null;
		if(data.length > 0){
			jsonArrStr = new String(data);
		}else{
			return;
		}
		List<Integer> arr = JSON.parseArray(jsonArrStr,Integer.class);
		for (Integer item : arr) {
			reduceCurIndex(item);
		}
		curatorFramework.setData().forPath(zkListener.getLeaderSelector().getId(), "".getBytes());
	}
	
	public void reduceCurIndex(int i) throws Exception{
		
		String tempUrl = "https://www.lagou.com/zhaopin/Java/" + i;
		String proxyIp = "";
//		if(list.size() > 0){
//			proxyIp = list.get(new Random().nextInt(list.size()));
//		}
		zhaopinService.loadNetData(tempUrl,proxyIp);
		
		String url = "http://www.zhipin.com/c100010000/h_100010000/?query=Java&page={0}&ka=page-{0}";
		tempUrl = MessageFormat.format(url, i);
//			String proxyIp = "";
//			if(list.size() > 0){
//				proxyIp = list.get(new Random().nextInt(list.size()));
//			}
		zhaopinService.loadNetData(tempUrl, proxyIp);
	}

}
