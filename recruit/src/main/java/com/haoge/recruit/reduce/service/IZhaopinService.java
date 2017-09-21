package com.haoge.recruit.reduce.service;

import java.util.List;

import com.haoge.recruit.base.page.PageResult;
import com.haoge.recruit.reduce.dto.ZhaopinDto;
import com.haoge.recruit.reduce.entity.Zhaopin;
public interface IZhaopinService{

	/**
	 * 查询所有招聘信息表
	 */
	List<Zhaopin> listZhaopinAll();

	/**
	 * 根据条件查询招聘信息表
	 */
	List<Zhaopin> listZhaopinByCondition(ZhaopinDto condition);

	/**
	 * 查询总数
	 *@param condition 查询条件类
	 *@return 总条数
	 */
	int getZhaopinByPageCount(ZhaopinDto condition);

	/**
	 * 分页查询
	 *@param pageResult 分页对象
	 *@param condition 查询条件类
	 *@return 总条数
	 */
	void listZhaopinByPage(PageResult<Zhaopin> pageResult,ZhaopinDto condition);

	/**
	 * 根据ID查询
	 *@param id 主键
	 *@return 招聘信息表
	 */
	Zhaopin getZhaopinById(Integer id);

	/**
	 * 新增
	 *@param item 招聘信息表
	 */
	Zhaopin add(Zhaopin item);

	/**
	 * 批量新增
	 *@param List 
	 */
	void batchInsert(List<Zhaopin> arrayList);

	/**
	 * 修改
	 *@param item 招聘信息表
	 */
	int update(Zhaopin item);

	/**
	 * 删除
	 *@param item 招聘信息表
	 */
	void delByIds(List<Integer> ids);

	void loadNetData(String url, String proxyIp) throws Exception;

}