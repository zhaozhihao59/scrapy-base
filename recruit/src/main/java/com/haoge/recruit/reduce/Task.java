package com.haoge.recruit.reduce;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.haoge.recruit.reduce.service.IZhaopinService;

@Component
public class Task {

	public static CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
	@Resource
	private IZhaopinService zhaopinService;
	
//	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Scheduled(cron = "0 0/1 * * * ?")
	public void startTask() throws Exception{
		
//		String url = "https://www.lagou.com/zhaopin/Java/";
//		for (int i = 1; i <= 30; i++) {
//			String tempUrl = url + i;
//			String proxyIp = "";
//			if(list.size() > 0){
//				proxyIp = list.get(new Random().nextInt(list.size()));
//			}
//			zhaopinService.loadNetData(tempUrl,proxyIp);
//		}
		
		String url = "http://www.zhipin.com/c100010000/h_100010000/?query=Java&page={0}&ka=page-{0}";
		for (int i = 1; i <= 30; i++) {
			String tempUrl = MessageFormat.format(url, i);
			String proxyIp = "";
			if(list.size() > 0){
				proxyIp = list.get(new Random().nextInt(list.size()));
			}
			zhaopinService.loadNetData(tempUrl, proxyIp);
		}
		
//		logger.info("http://www.zhipin.com/c101020100-p100101/?page=[1&]&ka=page-[1&]");
		
	}
	public static void main(String[] args) {
		String url = "http://www.zhipin.com/c101020100-p100101/?page={0}&ka=page-{0}";
		
		System.out.println();
	}
	
	
}
