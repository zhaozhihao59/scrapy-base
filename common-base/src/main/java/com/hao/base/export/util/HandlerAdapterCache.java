package com.hao.base.export.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.support.RequestContextUtils;
/**
 * 
 * @author zhaozhihao
 * @createTime 2017年7月24日 下午3:13:41	
 * @version 1.0
 */
@Component
public class HandlerAdapterCache implements ApplicationContextAware, DisposableBean{
	private RequestMappingHandlerAdapter handlerAdapters;
	private boolean flag = false;
	private static ApplicationContext applicationContext = null;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		HandlerAdapterCache.applicationContext = applicationContext;
	}
	
	public void destroy() throws Exception {
		applicationContext = null;
	}
	
	/**
	 * 获取applicationContext
	 * 
	 * @return applicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 根据Bean名称获取实例
	 * 
	 * @param name
	 *            Bean注册名称
	 * 
	 * @return bean实例
	 * 
	 * @throws BeansException
	 */
	public static Object getBean(String name) throws BeansException {
		return applicationContext.getBean(name);
	}
	
	public void init(){
		if(!flag){
			Map<String, RequestMappingHandlerAdapter> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, RequestMappingHandlerAdapter.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerAdapters = new ArrayList<RequestMappingHandlerAdapter>(matchingBeans.values()).get(0);
			}
			flag = true;
		}
	}

	public RequestMappingHandlerAdapter getHandlerAdapters() {
		init();
		return handlerAdapters;
	}
	
	public ServletWebRequest createWebRequest(HttpServletRequest request,HttpServletResponse response){
		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		return webRequest;
	}
	
	public ModelAndViewContainer createMavContainer(HttpServletRequest request) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
		ModelAndViewContainer mavContainer = new ModelAndViewContainer();
		mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
		mavContainer.setIgnoreDefaultModelOnRedirect((boolean) CommonUtil.getFieldVal(handlerAdapters, "ignoreDefaultModelOnRedirect"));
		return mavContainer;
	}
	
	public ServletInvocableHandlerMethod createServletInvocableHandlerMethod(HttpServletRequest request,HttpServletResponse response,HandlerMethod handlerMethod) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{
		WebDataBinderFactory binderFactory = (WebDataBinderFactory) CommonUtil.invokeMethod(handlerAdapters, "getDataBinderFactory", handlerMethod);
		ServletInvocableHandlerMethod invocableMethod = (ServletInvocableHandlerMethod) CommonUtil.invokeMethod(handlerAdapters, "createInvocableHandlerMethod", handlerMethod) ;
		invocableMethod.setHandlerMethodArgumentResolvers((HandlerMethodArgumentResolverComposite) CommonUtil.getFieldVal(handlerAdapters, "argumentResolvers"));
		invocableMethod.setHandlerMethodReturnValueHandlers((HandlerMethodReturnValueHandlerComposite) CommonUtil.getFieldVal(handlerAdapters, "returnValueHandlers"));
		invocableMethod.setDataBinderFactory(binderFactory);
		invocableMethod.setParameterNameDiscoverer((ParameterNameDiscoverer) CommonUtil.getFieldVal(handlerAdapters, "parameterNameDiscoverer"));
		return invocableMethod;
	}

	
	
}
