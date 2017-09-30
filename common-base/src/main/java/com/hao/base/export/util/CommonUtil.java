package com.hao.base.export.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hao.base.export.annotation.ExportFieldDataFormat;

/**
 * 
 * @author zhaozhihao
 * @createTime 2017年7月24日 下午3:13:33	
 * @version 1.0
 */
public class CommonUtil {
	public static Map<String, String> dateFormatMap = new HashMap<>();
	
	static{
		dateFormatMap.put("pattern", "yyyy-MM-dd HH:mm:ss");
		dateFormatMap.put("class", "org.apache.commons.lang3.time.DateFormatUtils");
		dateFormatMap.put("method", "format");
	}
	
	public static Object getFieldValByMethod(Object source,String field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method[] methods = source.getClass().getMethods();
		Method resultMethod = null;
		String tempName = "get" + field;
		String temp1Name = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
		for (Method method : methods) {
			String methodName = method.getName();
			if(temp1Name.equals(methodName) || tempName.equals(methodName)){
				resultMethod = method;
				break;
			}
		}
		
		return resultMethod.invoke(source,null);
	}
	
	
	/**
	 * 导出csv
	 * @param fileName csv文件名
	 * @param collection 导出的集合
	 * @param columnsJson 列表名 如：[{"name":"订单","field":"order"},{"name":"状态","field":"status"}]
	 * @param formatJson 需要格式化的字段 {"status":{"1":"有效","0":"无效"},"delete":{"1":"删除","2":"不删除"}}
	 * @param response
	 * @throws IOException 
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static void convertCSV(String fileName, Collection collection, String columnsJson,HttpServletResponse response) throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		// 设定输出文件头
		response.setHeader("Content-disposition","attachment; filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
		response.setContentType("application/csv;charset=utf-8");
		response.getOutputStream().write(new   byte []{( byte ) 0xEF ,( byte ) 0xBB ,( byte ) 0xBF });
		BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "utf-8"),2048);
		JSONArray columnArr = JSON.parseArray(columnsJson);
		
		outStream.write(createCsvName(columnArr));
		outStream.newLine();
		Optional optional = collection.stream().findFirst();
		if(!optional.isPresent()){
			outStream.flush();
			return;
		}
		String[] params = convertSpElParams(optional.get());
		JSONObject formatMap = convertFormatMap(optional.get());
		for (Object item : collection) {
			outStream.write(createCSVVal(item,formatMap,columnArr,params));
			outStream.newLine();
		}
		outStream.flush();
	}
	
	private static JSONObject convertFormatMap(Object unitObj) {
		JSONObject resultObj = new JSONObject();
		Class<?> superClass = unitObj.getClass().getSuperclass();
		Field[] superFields = superClass.getDeclaredFields();
		convertClassFormat(resultObj, superFields);
	 	Field[] fields = unitObj.getClass().getDeclaredFields();
	 	convertClassFormat(resultObj, fields);
		return resultObj;
	}
	
	public static Map<String, String> convertIntState(String[] strs, int index) {
		Map<String, String> resultMap = new HashMap<>();
		for (int i = 0; i < strs.length; i++) {
			resultMap.put((i + index)+ "",strs[i]);
		}
		return resultMap;
	}
	
	
	private static void convertClassFormat(JSONObject resultObj,Field[] fields){
		for (Field field : fields) {
			if(field.isAnnotationPresent(ExportFieldDataFormat.class)){
				ExportFieldDataFormat annotation = field.getAnnotation(ExportFieldDataFormat.class);
				JSONObject valObj = new JSONObject();
				if(annotation.formatType().getValue().equals("date")){
					valObj.put("formatType", "date");
					valObj.put("pattern", annotation.datePattern());
				}else if(annotation.formatType().getValue().equals("multi")){
					valObj.put("formatType", "multi");
					Map<String, String> map = convertIntState(annotation.value(),annotation.startIndex()) ;
					for (String keyKal : map.keySet()) {
						valObj.put(keyKal, map.get(keyKal));
					}
				}else if(annotation.formatType().getValue().equals("")){
					Map<String, String> map = convertIntState(annotation.value(),annotation.startIndex()) ;
					for (String keyKal : map.keySet()) {
						valObj.put(keyKal, map.get(keyKal));
					}
				}
				resultObj.put(field.getName(), valObj);
			}
		}
	}

	private static String[] convertSpElParams(Object obj) {
		
		Field[] fields = obj.getClass().getDeclaredFields();
		String[] result = new String[fields.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = fields[i].getName();
		}
		return result;
	}
	
	private static Object[] convertSpElObject(Object obj) throws IllegalArgumentException, IllegalAccessException{
		Field[] fields = obj.getClass().getDeclaredFields();
		Object[] objs = new Object[fields.length];
		for (int i = 0; i < objs.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			objs[i] = field.get(obj);
		}
		return objs;
	}

	public static String createCsvName(JSONArray title) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : title) {
			JSONObject map = (JSONObject) obj;
			sb.append(",").append(map.get("name"));
		}
		sb.deleteCharAt(0);
		return sb.toString();
	}
	@SuppressWarnings("unchecked")
	public static String getFormatFieldVal(JSONObject fieldMap,Object val) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Object typeName = fieldMap.get("formatType");
		if(typeName != null && "date".equals(typeName)){
			Object pattern = dateFormatMap.get("pattern");
			if(fieldMap.containsKey("pattern")){
				pattern = fieldMap.get("pattern");
			}
			Object[] args = new Object[2];
			args[0] = val;
			args[1] = pattern;
			Object resultVal = ReflectParam.getRefrenceVal(dateFormatMap.get("class"), dateFormatMap.get("method"), args);
			return resultVal.toString();
		}else if(typeName != null && "multi".equals(typeName)){
			Collection result = convertCollection(val);
			if(result != null){
				List tempList = new ArrayList<>();
				for (Object temp : result) {
					if(fieldMap.containsKey(temp.toString())){
						tempList.add(fieldMap.get(temp.toString()));
					}
				}
				return StringUtils.join(tempList,";");
			}else{
				return "";
			}
		}else if(fieldMap.containsKey(val.toString())){
			return fieldMap.get(val.toString()).toString();
		}
		return "";
	} 

	public static String createCSVVal(Object item, JSONObject formatMap, JSONArray columnArr, String[] params) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException{
		StringBuilder sb = new StringBuilder();
		for (Object obj : columnArr) {
			JSONObject map = (JSONObject) obj;
			String field = map.getString("field");
			if(field != null && field.indexOf("#") >= 0){
				Object[] objs = convertSpElObject(item);
				String reduceStr = SpelParser.getKey(field, params, objs).toString();
				Tokenizer tokenizer = new Tokenizer(reduceStr);
				List<Token> list = tokenizer.tokens;
				for (int i = 0; i < list.size(); i++) {
					Token token = list.get(i);
					if(token.getKind().equals(TokenKind.HASH)){
						Token nextToken = list.get(i + 1);
						String val = getCurFieldVal(formatMap, nextToken.data, item);
						reduceStr = reduceStr.replaceAll("#" + nextToken.data, val);
						i++;
					}
				}
			 	sb.append(",").append(reduceStr.replaceAll(",", ";"));
			 	continue;
			}else if(field == null){
					
				sb.append(",").append(item.toString().replaceAll(",", ";"));
				continue;
			}
			String resultStr = getCurFieldVal(formatMap,field,item);
			sb.append(",").append(resultStr.replaceAll(",", ";"));
			
		}
		sb.deleteCharAt(0);
		return sb.toString();
	}
	
	public static String getCurFieldVal(JSONObject formatMap,String field,Object item) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException{
		JSONObject fieldMap =  (JSONObject) formatMap.get(field);
		Object val = CommonUtil.getFieldValByMethod(item, field);
		if(fieldMap != null && val != null){
			return getFormatFieldVal(fieldMap, val);
		}else if(fieldMap == null && (val instanceof Date || val instanceof Long)){
			JSONObject tempFieldMap = new JSONObject();
			tempFieldMap.put("formatType", "date");
			if(val instanceof Long && Long.valueOf(val.toString()) >= 1469008517266l){
				return getFormatFieldVal(fieldMap, val);
			}else if(val instanceof Date){
				return getFormatFieldVal(fieldMap, val);
			}else{
				return val!=null?val.toString():"";
			}
		}else{
			if(val != null){
				return val.toString();
			}else{
				return "";
			}
		}
	}
	
	
	
	public static Collection convertCollection(Object obj) {
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

	public static JSONObject getJSONPOST(HttpServletRequest request) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));  
        String line = null;  
        StringBuilder sb = new StringBuilder();  
        while ((line = br.readLine()) != null) {  
            sb.append(line);  
        }  
		return JSON.parseObject(sb.toString());
	}
	
	public static Object getFieldVal(Object source,String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
		Field field = source.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(source);
	}
	
	public static Object invokeMethod(Object source,String methodName,Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Class<?>[] paramsType = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			paramsType[i]  = args[i].getClass();
		}
		Method method = source.getClass().getDeclaredMethod(methodName, paramsType);
		method.setAccessible(true);
		return method.invoke(source, args);
	}
	public static void main(String[] args) {
	}

	
}
