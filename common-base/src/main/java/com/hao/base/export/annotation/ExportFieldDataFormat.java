package com.hao.base.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DateFormat;

/**
 * 
 * @author zhaozhihao
 * @createTime 2017年7月24日 下午3:13:13	
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExportFieldDataFormat {

	
	DataFormatType formatType() default DataFormatType.DEFUALT;
	
	String datePattern() default "yyyy-MM-dd HH:mm:ss";
	
	String[] value() default "";
	
	int startIndex() default 0;
}
