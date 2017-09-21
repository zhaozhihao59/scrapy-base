package com.haoge.recruit.base.aop;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Aspect
@Component
@Order(1)
public class InterceptReduceAOP {
	
	private Log logger = LogFactory.getLog(InterceptReduceAOP.class);
	@Resource
	private DBUseTimeServiceImpl dbUseTimeServiceImpl;
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	@Pointcut(value = "@annotation(reduce)")
	private void getPointcut(MyReduce reduce){
		
	}
	public static void main(String[] args) {
		System.out.println(DigestUtils.md5DigestAsHex("zhaozhihao".getBytes()));;
	}
	
	@Around("getPointcut(reduce)")
	public Object preProcessQueryPattern(ProceedingJoinPoint point,MyReduce reduce) throws Throwable{
        String targetName = point.getTarget().getClass().getName();
        String simpleName = point.getTarget().getClass().getSimpleName();
        String methodName = point.getSignature().getName();
        Class<?> targetClass = reduce.targetClass();
        Object[] arguments = point.getArgs();
        
        String key = null;
////        没传key
//        if (intercept.curDate().length() > 0) {
//        	key = "'"+simpleName+"."+methodName + ".'+" +  intercept.curDate();
//        }else{
            key = reduce.key();
          
//        }
         
        String[] paramNames = ParamNameMap.get(key);
        if (paramNames == null){
//          反射得到形参名称
            paramNames = ReflectParamNames.getNames(targetName, methodName,arguments);
            ParamNameMap.put(key, paramNames);
        }
        
         
        if(reduce.key().length() > 0){
//          spring EL 表达式
        	int startFix = reduce.key().indexOf("#");
        	if(startFix >= 0){
        		key = SpelParser.getKey(key, "", paramNames, arguments);
        	}
        }
        
//        Object object = redisCacheUtil.getCacheValue(reduce.cacheName(),reduce.cacheName() + key);
//        if(object != null){
//        	Integer v = Integer.valueOf(object.toString());
//        	if(v == 0){
//        		return null;
//        	}else {
//        		Object z = arguments[0];
//        		Utils.setField(z,"id",v.longValue());
//        		return z;
//        	}
//        }
        
        long start = System.currentTimeMillis();
        Object target = point.proceed();
        long end = System.currentTimeMillis();
        dbUseTimeServiceImpl.submit(reduce.cacheName(), end - start);
        return target;
	}
}
