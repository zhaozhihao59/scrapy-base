package com.hao.base.export.annotation;
/**
 * 
 * @author zhaozhihao
 * @createTime 2017年7月24日 下午3:12:23	
 * @version 1.0
 */
public enum DataFormatType {
	
	DEFUALT(""),
	DATE("date"),
	MULTI("multi");
	private final String value;
	DataFormatType(String value){
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	
}
