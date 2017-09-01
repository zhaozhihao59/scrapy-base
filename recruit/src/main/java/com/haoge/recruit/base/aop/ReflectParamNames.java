package com.haoge.recruit.base.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

public class ReflectParamNames {
	private static Log log = LogFactory.getLog(ReflectParamNames.class);

	public static String[] getNames(String className,String methodName,Object[] obj) {
    	LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
     	Class<?> c = null;
		try {
			c = Class.forName(className);
			Method method = getMethod(c, methodName, obj);
			
			String[] tempArr = u.getParameterNames(method);
			String[] result = new String[tempArr.length];
			for (int i = 0; i < tempArr.length; i++) {
				result[i] = tempArr[i];
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}
    	
    	return null;
    }

	public static Method getMethod(Class<?> cc, String methodName, Object[] objs)
			throws NotFoundException, ClassNotFoundException, NoSuchMethodException, SecurityException {
		List<Method> list = new ArrayList<>();
		Method[] methods = cc.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName) && method.getParameterTypes().length == objs.length) {
				list.add(method);
			}
		}
		if (list.size() >= 2) {
			Method result = null;
			int max = Integer.MIN_VALUE;
			for (Method method : list) {
				Class<?>[] params = method.getParameterTypes();
				Class<?>[] customs = convertParams(true, objs);
				int temp = 0;
				for (int i = 0; i < customs.length; i++) {
					if (customs[i] != null && (customs[i].getName().equals(params[i].getName())
							|| params[i].isAssignableFrom(customs[i]) || isBaseType(customs[i], params[i]))) {
						temp++;
					}
				}
				if (max < temp) {
					max = temp;
					result = method;
				}
			}
			return result;
		} else {
			return list.get(0);
		}
	}

	public static Class<?>[] convertParams(boolean flag, Object... objs) throws ClassNotFoundException {
		Class<?>[] cc = new Class[objs.length];
		for (int i = 0; i < objs.length; i++) {
			if (objs[i] == null) {
				cc[i] = null;
				if (!flag) {
					return null;
				}
			} else {
				cc[i] = Class.forName(objs[i].getClass().getName());
			}
		}
		return cc;

	}
	
	private static boolean isBaseType(Class<?> customs, Class<?> params) {
		String className = params.getName();
		if(className.indexOf("class") < 0){
			String zz = customs.getTypeName().toLowerCase();
			String[] zzs = zz.split("\\.");
			if(zzs[zzs.length - 1] .equals(className)){
				return true;
			}
		}
		return false;
	}
}
