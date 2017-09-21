package com.haoge.recruit.reduce.dto;

import java.io.Serializable;
import java.util.Date;

import com.haoge.recruit.base.dto.BaseDto;
import com.haoge.recruit.reduce.entity.Zhaopin;
public class ZhaopinDto extends BaseDto<Zhaopin> implements Serializable {
	private static final long serialVersionUID = 7904053207325003853L;

	/** id */
	private Integer id;
	/** 公司名 */
	private String companyName;
	/** 单位元 */
	private Integer moneyStart;
	/** 单位元 */
	private Integer moneyEnd;
	/** 学历 */
	private String education;
	/** 发布时间 */
	private Date publishTime;
	/** 岗位名称 */
	private String worke;
	/** 城市 */
	private String city;
	/** 区 */
	private String district;
	/** 工龄 */
	private String workeAge;
	/** 公司类型 */
	private String companyType;
	/** 公司介绍 */
	private String industry;
	/** 吸引力 */
	private String attractive;

	/** id */
	public Integer getId(){
		return this.id;
	}

	/** id */
	public void setId(Integer id){
		this.id = id;
	}

	/** 公司名 */
	public String getCompanyName(){
		return this.companyName;
	}

	/** 公司名 */
	public void setCompanyName(String companyName){
		this.companyName = companyName;
	}

	/** 单位元 */
	public Integer getMoneyStart(){
		return this.moneyStart;
	}

	/** 单位元 */
	public void setMoneyStart(Integer moneyStart){
		this.moneyStart = moneyStart;
	}

	/** 单位元 */
	public Integer getMoneyEnd(){
		return this.moneyEnd;
	}

	/** 单位元 */
	public void setMoneyEnd(Integer moneyEnd){
		this.moneyEnd = moneyEnd;
	}

	/** 学历 */
	public String getEducation(){
		return this.education;
	}

	/** 学历 */
	public void setEducation(String education){
		this.education = education;
	}

	/** 发布时间 */
	public Date getPublishTime(){
		return this.publishTime;
	}

	/** 发布时间 */
	public void setPublishTime(Date publishTime){
		this.publishTime = publishTime;
	}

	/** 岗位名称 */
	public String getWorke(){
		return this.worke;
	}

	/** 岗位名称 */
	public void setWorke(String worke){
		this.worke = worke;
	}

	/** 城市 */
	public String getCity(){
		return this.city;
	}

	/** 城市 */
	public void setCity(String city){
		this.city = city;
	}

	/** 区 */
	public String getDistrict(){
		return this.district;
	}

	/** 区 */
	public void setDistrict(String district){
		this.district = district;
	}

	/** 工龄 */
	public String getWorkeAge(){
		return this.workeAge;
	}

	/** 工龄 */
	public void setWorkeAge(String workeAge){
		this.workeAge = workeAge;
	}

	/** 公司类型 */
	public String getCompanyType(){
		return this.companyType;
	}

	/** 公司类型 */
	public void setCompanyType(String companyType){
		this.companyType = companyType;
	}

	/** 公司介绍 */
	public String getIndustry(){
		return this.industry;
	}

	/** 公司介绍 */
	public void setIndustry(String industry){
		this.industry = industry;
	}

	/** 吸引力 */
	public String getAttractive(){
		return this.attractive;
	}

	/** 吸引力 */
	public void setAttractive(String attractive){
		this.attractive = attractive;
	}

}