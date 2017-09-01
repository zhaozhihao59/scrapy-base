package org.recruit;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class BaseController {

	protected Log logger = LogFactory.getLog(getClass());

	public static final String STATUS_PARAMETER_NAME = "status";// 操作状态参数名称
	public static final String MESSAGE_PARAMETER_NAME = "message";// 操作消息参数名称
	public static final String TOKEN_PARAMETER_NAME = "token";	//操作token参数名称
	
	// 操作状态（警告、错误、成功）
	public enum Status {
		warn, error, success
	}
	

	
	/**
	 * 获取网站地址
	 * @param request
	 * @return
	 */
	protected String getBaseUrl(HttpServletRequest request){
		String path = request.getContextPath();
		StringBuffer basePath = new StringBuffer();
		basePath.append(request.getScheme());
		basePath.append("://");
		basePath.append(request.getServerName());
		int port = request.getServerPort();
		if(port != 80){
			basePath.append(":").append(port);
		}
		basePath.append(path).append("/");
		return basePath.toString();
	}
		
	/**
	 * 获取IP地址
	 * @param request
	 * @return
	 */
	protected String getIpAddr(HttpServletRequest request){
		String ip = request.getHeader("X-Real-IP");
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		} else {
			return ip;
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		} else {
			int index = ip.indexOf(',');
			if (index != -1) {
				ip = ip.substring(0, index);
			}
		}
		return ip;
	}
	
}
