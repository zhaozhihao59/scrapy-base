package com.haoge.recruit.base.page;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 分页类
 * @creator     zhangqiang
 * @create-time Oct 7, 2011   5:42:50 PM
 */
public class PageResult<T> implements Serializable{
	private static final long serialVersionUID = -4133717165046854344L;
	
	
	private String orderBy = "";
	private String sort = "asc";

	// 当前页数据
	private List<T> result = new ArrayList<T>();
	// 首页
	private int homePage = 1;
	// 第一页
	private int fristPage = 1;
	// 当前页
	private int currentPage = 1;
	// 上一页
	@SuppressWarnings("unused")
	private int prePage = 1;
	// 下一页
	@SuppressWarnings("unused")
	private int nextPage;
	// 最后一页
	@SuppressWarnings("unused")
	private int lastPage;
	/** 页的条数 */
	private int pageSize = 100;
	/** 总条数 */
	private int rows;
	/** 总页数 */
	private int allPages;
	//下一个拉取的OpenId
	private String nextOpenId;
	//最后更新时间 精确到分钟
	private String lastDate;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		if(currentPage == 0){
			currentPage = 1;
		}
		this.currentPage = currentPage;
	}

	public int getCurrentPageIndex() {
		return (this.getCurrentPage() - 1) * this.getPageSize() < 0 ? 0 : (this
				.getCurrentPage() - 1)
				* this.getPageSize();
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public int getHomePage() {
		return homePage;
	}

	public int getFristPage() {
		return fristPage;
	}

	public int getLastPage() {
		return this.getAllPages();
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	// 得到总页数
	public int getAllPages() {
		if(this.allPages > 0){
			return this.allPages;
		}
		int mod = this.rows % this.pageSize;
		if (mod == 0) {
			return this.rows / this.pageSize;
		}
		return this.rows / this.pageSize + 1;
	}

	// 得到上一页
	public int getPrePage() {
		if (this.currentPage > 1)
			return this.currentPage - 1;
		return this.fristPage;
	}

	// 得到下一页
	public int getNextPage() {
		if (this.currentPage >= this.getLastPage())
			return this.getLastPage();
		return this.currentPage + 1;
	}

	public PageResult(int pageSize) {
		super();
		this.pageSize = pageSize;
	}

	public PageResult() {
		super();
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public void setHomePage(int homePage) {
		this.homePage = homePage;
	}

	public void setFristPage(int fristPage) {
		this.fristPage = fristPage;
	}

	public void setPrePage(int prePage) {
		this.prePage = prePage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	public void setAllPages(int allPages) {
		this.allPages = allPages;
	}
	
	public String getNextOpenId() {
		return nextOpenId;
	}

	public void setNextOpenId(String nextOpenId) {
		this.nextOpenId = nextOpenId;
	}

	public String getLastDate() {
		return lastDate;
	}

	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
	
	/**
	 * GOOGLE 分页
	 * 
	 * @param curpage
	 *            当前页
	 * @param showNum
	 *            页面显示的页数
	 * @param pages
	 *            实际页数
	 * @return
	 */
	public Integer[] getPageBar() {
		int showNum = 8;

		Integer startIndex = 1;
		Integer endIndex = getAllPages();
		if (getAllPages() > showNum) {
			startIndex = getCurrentPage() - 3;
			if (startIndex < 1) {
				startIndex = 1;
			}
			endIndex = startIndex + showNum - 1;
			if (endIndex > getAllPages()) {
				startIndex = startIndex - endIndex + getAllPages();
				endIndex = getAllPages();
			}
		}
		return new Integer[] { startIndex, endIndex };
	}
}
