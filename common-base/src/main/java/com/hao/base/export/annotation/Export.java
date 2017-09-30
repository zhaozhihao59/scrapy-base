package com.hao.base.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * 
 * @author zhaozhihao
 * @createTime 2017年7月24日 下午3:13:07	
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Export {

	@AliasFor("value")
	String fileName() default "";
	@AliasFor("fileName")
	String value() default "";
	
}
