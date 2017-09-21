package com.haoge.recruit.reduce.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.haoge.recruit.reduce.dto.ZhaopinDto;
import com.haoge.recruit.reduce.entity.Zhaopin;
@Repository("zhaopinDaoImpl")
public interface IZhaopinDao{

	/**
	 * 查询所有招聘信息表
	 */
	List<Zhaopin> listZhaopinAll();

	/**
	 * 查询总数
	 *@param condition 查询条件类
	 *@return 总条数
	 */
	int getZhaopinByPageCount(@Param("condition") ZhaopinDto condition);

	/**
	 * 分页查询
	 *@param bounds RowBounds对象
	 *@param condition 查询条件类
	 *@return 总条数
	 */
	List<Zhaopin> listZhaopinByPage(@Param("condition") ZhaopinDto condition,RowBounds bounds);

	/**
	 * 根据ID查询
	 *@param id 主键
	 *@return 招聘信息表
	 */
	Zhaopin getZhaopinById(@Param("id") Integer id);

	/**
	 * 新增
	 *@param item 招聘信息表
	 */
	void add(Zhaopin item);

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

}
