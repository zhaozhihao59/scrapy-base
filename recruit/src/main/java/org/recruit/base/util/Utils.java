package org.recruit.base.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 工具集合包
 * @author admin 赵志豪
 *
 */
public class Utils {
	
	private static Log logger = LogFactory.getLog(Utils.class);
	
	public static void setField(Object source,String fieldName,Object val) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		 Field field = source.getClass().getDeclaredField(fieldName);
		 field.setAccessible(true);
		 field.set(source,val);
	}
	public static Object getField(Object source,String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
		Field field = source.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(source);
		
	}
	/**
	 * 深度复制
	 * @param src
	 * @return
	 */
	public Object deepCopy(Object src){
		Object dest = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();   
	        ObjectOutputStream out = new ObjectOutputStream(byteOut);
			
	        out.writeObject(src);   
	        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());   
	        ObjectInputStream in =new ObjectInputStream(byteIn);   
	        dest = in.readObject();   
			} catch (Exception e) {
				e.printStackTrace();
			}   
        return dest;    
	}
	
	public static String getJsonStr(HttpServletRequest request) throws IOException{
		ServletInputStream im = request.getInputStream();
		if(im != null){
			String reulst = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(im));
			StringBuffer sb = new StringBuffer();
			while((reulst = br.readLine()) != null){
				sb.append(reulst);
			}
			return sb.toString();
		}
		return null;
	}
	
	public static void removeKey(JSONObject obj,String... keys ){
		for (String key : keys) {
			obj.remove(key);
		}
	}
	
	public static void main(String[] args) {
	}
	
}
