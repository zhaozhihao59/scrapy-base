package org.recruit.base.dto;

import java.io.Serializable;
import java.util.Date;

import org.recruit.base.page.PageResult;

public abstract class BaseDto<T> implements Serializable{

	private static final long serialVersionUID = 1L;
	private Date startTime;
	private Date endTime;
	
	private PageResult<T> pageResult = new PageResult<>();

	public PageResult<T> getPageResult() {
		return pageResult;
	}

	public void setPageResult(PageResult<T> pageResult) {
		this.pageResult = pageResult;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
