package org.recruit.base.conf;

import javax.sql.DataSource;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

/**
 * myBatis扫描接口
 * @author haoge
 *
 */
@Configuration
//TODO 注意，由于MapperScannerConfigurer执行的比较早，所以必须有下面的注解
@AutoConfigureAfter(JavaConf.class)
public class MyBatisMapperScannerConfig {
	
	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer(){
		MapperScannerConfigurer configurer = new MapperScannerConfigurer();
		configurer.setAnnotationClass(Repository.class);
		configurer.setBasePackage("com.hao");
		configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
		return configurer;
	}
}
