package com.haoge.recruit.base.aop;

import java.util.concurrent.ConcurrentHashMap;

public class ParamNameMap {

	 private static ConcurrentHashMap<String, String[]> map = new ConcurrentHashMap<String, String[]>();
	 
	    public static void put(String key, String[] paramNames) {
	        map.putIfAbsent(key, paramNames);
	    }
	 
	    public static String[] get(String key) {
	        return map.get(key);
	    }
}
