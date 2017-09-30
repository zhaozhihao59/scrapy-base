package com.hao.base.export.filter;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestParamWrapper extends HttpServletRequestWrapper{
private Map<String, String[]> params;
	
	public RequestParamWrapper(HttpServletRequest request,Map<String, String[]> newParam) {
		super(request);
		this.params = newParam;
		
	}
	
	@Override
	public String[] getParameterValues(String name) {
		String[] result = null;  
        
        Object v = params.get(name);  
        if (v == null) {  
            result =  null;  
        } else if (v instanceof String[]) {  
            result =  (String[]) v;  
        } else if (v instanceof String) {  
            result =  new String[] { (String) v };  
        } else {  
            result =  new String[] { v.toString() };  
        }  
          
        return result;  
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return new Vector<String>(params.keySet()).elements();  
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getParameterMap() {
		// TODO Auto-generated method stub
		return params;
	}

	@Override
	public String getParameter(String name) {
		String result = "";  
        
        Object v = params.get(name);  
        if (v == null) {  
            result = null;  
        } else if (v instanceof String[]) {  
            String[] strArr = (String[]) v;  
            if (strArr.length > 0) {  
                result =  strArr[0];  
            } else {  
                result = null;  
            }  
        } else if (v instanceof String) {  
            result = (String) v;  
        } else {  
            result =  v.toString();  
        }  
          
        return result;  
	}

}
