package com.hao.base.export.interceptor;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import com.alibaba.fastjson.JSONObject;
import com.hao.base.export.annotation.Export;
import com.hao.base.export.util.CommonUtil;
import com.hao.base.export.util.HandlerAdapterCache;

/**
 * 导出拦截
 * @author zhaozhihao
 * @createTime 2017年7月27日 下午3:29:38	
 * @version 1.0
 */
@Component
public class ExportInterceptor  extends HandlerInterceptorAdapter {
	
	@Resource
	private HandlerAdapterCache handlerAdapterCache;

	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		if(handlerMethod.getMethod().isAnnotationPresent(Export.class)){
			JSONObject obj = CommonUtil.getJSONPOST(request);
			
			if(obj == null){
				String flag = request.getParameter("is_export");
				if(StringUtils.isNotBlank(flag) && Boolean.valueOf(flag)){
					/**
					 * columns: [{"name":"订单","field":"order"},{"name":"状态","field":"status"}]
					 */
					String columns = request.getParameter("columns");
					if(StringUtils.isBlank(columns)){
						throw new RuntimeException("导出的 columns 不能为空 (参数格式如下: columns: [{\"name\":\"订单\",\"field\":\"order\"},{\"name\":\"状态\",\"field\":\"status\"}])");
					}
					JSONObject paramsObj = new JSONObject();
					Object resultVal = invocableMethod(paramsObj, request, response, handlerMethod);
					Collection exportList = formatReturnValToCollection(resultVal);
					CommonUtil.convertCSV(getFileName(handlerMethod),exportList, columns, response);
					return false;
				}
			}else{
				String flag = obj.getString("is_export");
				if(StringUtils.isNotBlank(flag.toString()) && Boolean.valueOf(flag.toString())){
					/**
					 * columns: [{"name":"订单","field":"order"},{"name":"状态","field":"status"}]
					 */
					String columns = obj.getString("columns");
					if(StringUtils.isBlank(columns)){
						throw new RuntimeException("导出的 columns 不能为空 (参数格式如下: columns: [{\"name\":\"订单\",\"field\":\"order\"},{\"name\":\"状态\",\"field\":\"status\"}])");
					}
					Object resultVal = invocableMethod(obj, request, response, handlerMethod);
					Collection exportList = formatReturnValToCollection(resultVal);
					CommonUtil.convertCSV(getFileName(handlerMethod),exportList, columns, response);
					return false;
				}
			}
		}
		return	super.preHandle(request, response, handler);
	}
	private String getFileName(HandlerMethod handlerMethod) {
		Export export = handlerMethod.getMethod().getAnnotation(Export.class);
		String fileName = getFileName(export.fileName());
		
		if(StringUtils.isBlank(fileName)){
			fileName = getFileName(export.value());
		}
		if(StringUtils.isBlank(fileName)){
			fileName = handlerMethod.getMethod().getName() + ".csv";
		}
		return fileName;
	}
	private String getFileName(String fileName){
		if(fileName.lastIndexOf(".") != -1 && !fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase().equals("csv")){
			if(fileName.lastIndexOf(".") == fileName.length() - 1){
				fileName += "csv";
			}else{
				fileName += ".csv";
			}
		}
		return fileName;
	}
	
	public static void main(String[] args) {
		String fileName = "asdioj.";
		System.out.println(fileName.substring(fileName.lastIndexOf(".") + 1));
	}
	public Collection formatReturnValToCollection(Object resultVal) throws IllegalArgumentException, IllegalAccessException{
		Collection collection = convertCollection(resultVal);
		if(collection == null){
			 collection = getCollectionInResult(resultVal);
			 if(collection == null){
				 throw new RuntimeException("api 返回值中不存在需要导出的内容。请确认");
			 }
		}
		return collection;
	}
	
	private Collection getCollectionInResult(Object resultVal) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = resultVal.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
		 	Object val = field.get(resultVal);
		 	Collection tempCollection = convertCollection(val);
		 	if(tempCollection != null){
		 		return tempCollection;
		 	}
		 	if(isNotBaseType(val)){
		 		return getCollectionInResult(val);
		 	}
		}
		return null;
	}
	private boolean isNotBaseType(Object val) {
		if(val instanceof String){
			return false;
		}else if(val instanceof Integer){
			return false;
		}else if(val instanceof Long){
			return false;
		}else if(val instanceof Character){
			return false;
		}else if(val instanceof Short){
			return false;
		}else if(val instanceof Byte){
			return false;
		}else if(val instanceof Boolean){
			return false;
		}else if(val instanceof Date){
			return false;
		}else if(val instanceof Double){
			return false;
		}else if(val instanceof Float){
			return false;
		}
		return true;
	}
	private Collection convertCollection(Object obj) {
		if (obj instanceof Collection) {
			return (Collection) obj;
		}
		if (obj.getClass().isArray()) {
			Collection coll = new ArrayList();
			int length = Array.getLength(obj);
			for (int i = 0; i < length; i++) {
				Object val = Array.get(obj, i);
				coll.add(val);
			}
			return coll;
		}
		return null;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object invocableMethod(JSONObject params,HttpServletRequest request,HttpServletResponse response,HandlerMethod handlerMethod) throws Exception{
		Map paramsMap = request.getParameterMap(); 
		for (String key : params.keySet()) {
			if(!paramsMap.containsKey(key)){
				paramsMap.put(key, params.get(key));
			}
		}
		handlerAdapterCache.getHandlerAdapters();
		ServletWebRequest webRequest = handlerAdapterCache.createWebRequest(request, response);
		ModelAndViewContainer mavContainer = handlerAdapterCache.createMavContainer(request);
		ServletInvocableHandlerMethod invocableMethod = handlerAdapterCache.createServletInvocableHandlerMethod(request, response, handlerMethod);
		Object returnVal = invocableMethod.invokeForRequest(webRequest, mavContainer);
		return returnVal;
	}
	
}
