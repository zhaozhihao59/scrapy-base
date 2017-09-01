package com.haoge.recruit.base.util;

import javax.annotation.Resource;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Component;

@Component
public class RedisCacheUtil {
	@Resource
	private RedisCacheManager cacheManager;
	
	/** 得到缓存
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public Object getCacheValue(String cacheName,String key){
		Cache cache = cacheManager.getCache(cacheName);
		ValueWrapper cacheValue = cache.get(key);
		if(cacheValue != null){
			return cacheValue.get();
		}else return null;
	}
	

	/**
	 * 放入缓存
	 * @param cacheName
	 * @param key
	 * @param obj
	 */
	public void putCacheValue(String cacheName,String key,Object obj){
		Cache cache = cacheManager.getCache(cacheName);
		cache.put(key, obj);
	}
	
	/** 清理缓存
	 * @param cacheName
	 * @param key
	 */
	public void cleanCache(String cacheName,String key){
		Cache cache = cacheManager.getCache(cacheName);
		if(key != null){
			cache.evict(key);
		}else{
			cache.clear();
		}
	}
	
	/** 清里缓存
	 * @param cacheName
	 */
	public void cleanCache(String cacheName){
		cleanCache(cacheName,null);
	}
}
