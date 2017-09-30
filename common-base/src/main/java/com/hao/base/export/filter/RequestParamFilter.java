package com.hao.base.export.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
public class RequestParamFilter implements Filter{
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Map<String,String[]> m = new HashMap<String,String[]>(request.getParameterMap());    
        request = new RequestParamWrapper((HttpServletRequest)request, m);    
            
        chain.doFilter(request, response);   
	}

	@Override
	public void destroy() {
		
	}

}
