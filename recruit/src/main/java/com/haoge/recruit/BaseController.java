package com.haoge.recruit;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.haoge.recruit.reduce.dto.ZhaopinDto;
import com.haoge.recruit.reduce.service.IZhaopinService;

@RequestMapping
@Controller
public class BaseController {

	protected Log logger = LogFactory.getLog(getClass());
	@Resource
	private IZhaopinService zhaopinService;
	public static final String STATUS_PARAMETER_NAME = "status";// 操作状态参数名称
	public static final String MESSAGE_PARAMETER_NAME = "message";// 操作消息参数名称
	public static final String TOKEN_PARAMETER_NAME = "token";	//操作token参数名称
	
	// 操作状态（警告、错误、成功）
	public enum Status {
		warn, error, success
	}
	
	@RequestMapping(value = "/test",method = RequestMethod.POST)
	public String testForm(String name,@RequestParam("arr") String[] arr){
		System.out.println(JSON.toJSONString(arr));
		return "ok";
	}
	
	@RequestMapping(value = "/reloadRedis",method = RequestMethod.POST)
	@ResponseBody
	public String reloadRedis(){
		ZhaopinDto condition = new ZhaopinDto();
		int count = zhaopinService.getZhaopinByPageCount(condition);
		
		return "ok";
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
