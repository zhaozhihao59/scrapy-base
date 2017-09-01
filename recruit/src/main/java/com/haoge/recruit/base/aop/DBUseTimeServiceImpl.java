package com.haoge.recruit.base.aop;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;
@Component
public class DBUseTimeServiceImpl implements PublicMetrics{
	
	private final ConcurrentHashMap<String, Metric<?>> writer = new ConcurrentHashMap<>();  
	private final ConcurrentHashMap<String, String> names = new ConcurrentHashMap<String, String>();
	private final ConcurrentHashMap<String, Double> vals = new ConcurrentHashMap<>();

	public void submit(String metricName, double value) {
		this.writer.put(metricName, (new Metric<Double>(wrap(metricName),wrap( metricName,value))));
	}

	private String wrap(String metricName) {
		String cached = this.names.get(metricName);
		if (cached != null) {
			return cached;
		}
		if (metricName.startsWith("customer")) {
			return metricName;
		}
		String name = "customer." + metricName;
		this.names.put(metricName, name);
		return name;
	}
	
	private Double wrap(String metriName,Double values){
		Double val = vals.get(metriName);
		if(val != null){
			val = val + values;
			vals.put(metriName, val);
			return val;
		}else{
			vals.put(metriName, values);
			return values;
		}
	}

	@Override
	public Collection<Metric<?>> metrics() {
		return writer.values();
	}
}
