package com.haoge.recruit.reduce.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.RowBounds;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.haoge.recruit.base.page.PageResult;
import com.haoge.recruit.base.util.HttpUtil;
import com.haoge.recruit.reduce.dao.IZhaopinDao;
import com.haoge.recruit.reduce.dto.ZhaopinDto;
import com.haoge.recruit.reduce.entity.Zhaopin;
import com.haoge.recruit.reduce.service.IZhaopinService;
@Service("zhaopinServiceImpl")
@Transactional(value = "transactionManager")
public class ZhaopinServiceImpl implements IZhaopinService{

	@Resource(name = "zhaopinDaoImpl")
	private IZhaopinDao zhaopinDao;
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	
	private Log logger = LogFactory.getLog(getClass());

	/**
	 * 查询所有招聘信息表
	 */
	@Override
	public List<Zhaopin> listZhaopinAll(){
		return zhaopinDao.listZhaopinAll();
	}

	/**
	 * 根据条件查询招聘信息表
	 */
	@Override
	public List<Zhaopin> listZhaopinByCondition(ZhaopinDto condition){
		return zhaopinDao.listZhaopinByPage(condition,RowBounds.DEFAULT);
	}
	/**
	 * 查询总数
	 *@param condition 查询条件类
	 *@return 总条数
	 */
	@Override
	public int getZhaopinByPageCount(ZhaopinDto condition){
		return zhaopinDao.getZhaopinByPageCount(condition);
	}

	/**
	 * 分页查询
	 *@param pageResult 分页对象
	 *@param condition 查询条件类
	 */
	@Override
	public void listZhaopinByPage(PageResult<Zhaopin> pageResult,ZhaopinDto condition){
		int rows = zhaopinDao.getZhaopinByPageCount(condition);
		pageResult.setRows(rows);
		RowBounds rowBounds = new RowBounds(pageResult.getCurrentPageIndex(),pageResult.getPageSize());
		List<Zhaopin> list = zhaopinDao.listZhaopinByPage(condition,rowBounds);
		pageResult.setResult(list);
	}

	/**
	 * 根据ID查询
	 *@param id 主键
	 *@return 招聘信息表
	 */
	@Override
	public Zhaopin getZhaopinById(Integer id){
		return zhaopinDao.getZhaopinById(id);
	}

	/**
	 * 新增
	 *@param item 招聘信息表
	 */
	@Override
	public Zhaopin add(Zhaopin item){
		zhaopinDao.add(item);
		return item;
	}

	/**
	 * 批量新增
	 *@param List 
	 */
	@Override
	public void batchInsert(List<Zhaopin> arrayList){
		List<Zhaopin> resultList = new ArrayList<>();
		for (Zhaopin item : arrayList) {
			String key = item.getCity() + item.getCompanyName() + item.getWorke() + item.getMoneyStart() + item.getMoneyEnd() + item.getPublishTime();
			key = DigestUtils.md5Hex(key);
			if(!redisTemplate.hasKey(key)){
				resultList.add(item);
				redisTemplate.opsForValue().set(key,1);
			}
		}
		if(resultList.size() > 0){
			zhaopinDao.batchInsert(resultList);
		}
		
	}

	/**
	 * 修改
	 *@param item 招聘信息表
	 */
	@Override
	public int update(Zhaopin item){
		return zhaopinDao.update(item);
	}

	/**
	 * 删除
	 *@param item 招聘信息表
	 */
	@Override
	public void delByIds(List<Integer> ids){
		zhaopinDao.delByIds(ids);
	}

	@Override
//	@Async
	public void loadNetData(String url, String proxyIp) throws Exception {
		String result = HttpUtil.loadContentByGetMethod(url, proxyIp);
		Document doc = Jsoup.parse(result);
		if(url.indexOf("lagou.com") >= 0){
			logger.info("cur url is -> "+url);
			Elements ulDoc = doc.getElementsByClass("item_con_list");
			Thread.sleep(new Random().nextInt(6000)  + 6000);
			List<Zhaopin> batchList = new ArrayList<Zhaopin>();
		 	for (Element element : ulDoc) {
				Elements lisElement = element.getElementsByClass("con_list_item");
				for (Element li : lisElement) {
					Zhaopin item = new Zhaopin();
					String formatTimeStr = li.getElementsByClass("format-time").get(0).text();
					String regex = "\\d{2}:\\d{2}发布";
					String publishTimeStr = null;
					if(formatTimeStr.matches(regex)){
						LocalDateTime local = LocalDateTime.of(LocalDate.now(),LocalTime.parse(formatTimeStr.substring(0, formatTimeStr.indexOf("发布")),DateTimeFormatter.ofPattern("HH:mm")));
						publishTimeStr = local.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
					}else{
						continue;
					}
					item.setPublishTime(DateUtils.parseDate(publishTimeStr, "yyyy-MM-dd HH:mm:ss"));
					String dataSalary = li.attr("data-salary");
					item.setMoneyStart(reduceMoney(dataSalary.split("-")[0]));
					item.setMoneyEnd(reduceMoney(dataSalary.split("-")[1]));
					item.setCompanyName(li.attr("data-company"));
					item.setWorke(li.attr("data-positionname"));
					String address = li.getElementsByClass("add").get(0).text();
					if(address.indexOf("·") >= 0){
						item.setCity(address.substring(address.indexOf("[") + 1,address.indexOf("·")));
						item.setDistrict(address.substring(address.indexOf("·") + 1, address.indexOf("]")));
					}else{
						item.setCity(address.substring(address.indexOf("[") + 1, address.indexOf("]")));
						item.setDistrict("");
					}
					
					String industryStr = li.getElementsByClass("industry").get(0).text();
					item.setIndustry(industryStr.split("/")[0]);
					item.setCompanyType(industryStr.split("/")[1]);
					item.setAttractive(li.getElementsByClass("list_item_bot").get(0).text());
					String workAgeAndEducation = li.getElementsByClass("li_b_l").get(0).text();
					workAgeAndEducation = workAgeAndEducation.replace(dataSalary, "");
					String workAgeStr = workAgeAndEducation.split("/")[0];
					item.setWorkeAge(workAgeStr.substring(workAgeStr.indexOf("经验")+ 2));
					String educationStr = workAgeAndEducation.split("/")[1];
					item.setEducation(educationStr);;
					batchList.add(item);
				}
			}
		 	if(batchList.size()> 0){
		 		batchInsert(batchList);
		 	}
		 	
		}else if(url.indexOf("zhipin.com") >= 0){
			logger.info("cur url is -> "+url);
			List<Zhaopin> batchList = new ArrayList<>();
			Elements ulDoc = doc.getElementsByClass("job-list").get(0).getElementsByTag("ul");
			Thread.sleep(new Random().nextInt(6000)  + 6000);
		 	for (Element element : ulDoc) {
				Elements lisElement = element.getElementsByTag("li");
				for (Element li : lisElement) {
					Zhaopin item = new Zhaopin();
					String jobTimeStr = li.getElementsByClass("time").get(0).text();
					String regex = "发布于\\d{2}:\\d{2}";
					String publishTimeStr = "";
					if(jobTimeStr.matches(regex)){
						publishTimeStr = jobTimeStr.substring(jobTimeStr.indexOf("于") + 1);
						LocalDateTime local = LocalDateTime.of(LocalDate.now(), LocalTime.parse(publishTimeStr,DateTimeFormatter.ofPattern("HH:mm")));
						publishTimeStr = local.format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
					}else{
						continue;
					}
					item.setPublishTime(DateUtils.parseDate(publishTimeStr, "yyyy:MM:dd HH:mm:ss"));
					String workeAndSalary = li.getElementsByClass("info-primary").get(0).getElementsByClass("name").get(0).text();
					String[] workeAndSalaryArr = workeAndSalary.split(" ");
					if(workeAndSalaryArr.length > 2){
						String worke = "";
						for (int i = 0; i < workeAndSalaryArr.length - 1; i++) {
							worke += workeAndSalaryArr[i];
						}
						item.setWorke(worke);
						String salary = workeAndSalaryArr[workeAndSalaryArr.length - 1];
						item.setMoneyStart(reduceMoney(salary.split("-")[0]));
						item.setMoneyEnd(reduceMoney(salary.split("-")[1]));
					}else{
						item.setWorke(workeAndSalaryArr[0]);
						String salary = workeAndSalaryArr[1];
						item.setMoneyStart(reduceMoney(salary.split("-")[0]));
						item.setMoneyEnd(reduceMoney(salary.split("-")[1]));
					}
					String cityAndWorkAgeAndEducation = li.getElementsByClass("info-primary").get(0).getElementsByTag("p").get(0).html();
					String regexReplace = "<em class=\"vline\"></em>";
					cityAndWorkAgeAndEducation = cityAndWorkAgeAndEducation.replaceAll(regexReplace, " ");
					item.setCity(cityAndWorkAgeAndEducation.split(" ")[0]);
					item.setWorkeAge(cityAndWorkAgeAndEducation.split(" ")[1]);
					item.setEducation(cityAndWorkAgeAndEducation.split(" ")[2]);
					
					String companyName = li.getElementsByClass("company-text").get(0).getElementsByClass("name").get(0).text();
					item.setCompanyName(companyName);
					String industryAndCompanyTypeAndcompanyScale = li.getElementsByClass("company-text").get(0).getElementsByTag("p").get(0).html();
					industryAndCompanyTypeAndcompanyScale = industryAndCompanyTypeAndcompanyScale.replaceAll(regexReplace, " ");
					String[] industryAndCompanyTypeAndcompanyScaleArr = industryAndCompanyTypeAndcompanyScale.split(" ");
					if(industryAndCompanyTypeAndcompanyScaleArr.length == 3){
						item.setIndustry(industryAndCompanyTypeAndcompanyScaleArr[0]);
						item.setCompanyType(industryAndCompanyTypeAndcompanyScaleArr[1]);				
						item.setCompanyScale(industryAndCompanyTypeAndcompanyScaleArr[2]);
					}else{
						item.setIndustry(industryAndCompanyTypeAndcompanyScaleArr[0]);
						String scaleRegex = "\\d.*人";
						Pattern p = Pattern.compile(scaleRegex);
						Matcher m = p.matcher(industryAndCompanyTypeAndcompanyScaleArr[1]);
						if(m.find()){
							item.setCompanyScale(industryAndCompanyTypeAndcompanyScaleArr[1]);
							item.setCompanyType("");
						}else{
							item.setCompanyScale("");
							item.setCompanyType(industryAndCompanyTypeAndcompanyScaleArr[1]);
						}
					}
					item.setDistrict("");
					List<String> xiyinli = new ArrayList<>();
					Elements spans = li.getElementsByClass("job-tags").get(0).getElementsByTag("span");
					for (Element span : spans) {
						xiyinli.add(span.text());
					}
					item.setAttractive(StringUtils.join(xiyinli," "));
					batchList.add(item);
				}
			}
		 	
		 	if(batchList.size()>0){
		 		batchInsert(batchList);
		 	}
		 	
		}
		
	}
	
	public static Integer reduceMoney(String str){
		str = str.toUpperCase();
		if(str.indexOf("K") >= 0){
			Integer money = Integer.valueOf( str.substring(0, str.indexOf("K")));
			return money * 1000;
		}else if(str.indexOf("W") >= 0){
			return Integer.valueOf( str.substring(0, str.indexOf("W"))) * 10000;
		}
		return 0;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
//		File file = new File("C:\\Users\\win 10\\Desktop\\boss页面.html");
		Document doc = Jsoup.connect("http://www.zhipin.com/c101020100-p100101/?page=5&ka=page-5").get();
		
		Elements ulDoc = doc.getElementsByClass("job-list").get(0).getElementsByTag("ul");
//		Thread.sleep(new Random().nextInt(6000)  + 6000);
	 	for (Element element : ulDoc) {
			Elements lisElement = element.getElementsByTag("li");
			for (Element li : lisElement) {
				Zhaopin item = new Zhaopin();
				String jobTimeStr = li.getElementsByClass("time").get(0).text();
				String regex = "发布于\\d{2}:\\d{2}";
				String publishTimeStr = "";
				if(jobTimeStr.matches(regex)){
					publishTimeStr = jobTimeStr.substring(jobTimeStr.indexOf("于") + 1);
					LocalDateTime local = LocalDateTime.of(LocalDate.now(), LocalTime.parse(publishTimeStr,DateTimeFormatter.ofPattern("HH:mm")));
					publishTimeStr = local.format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
				}else{
					continue;
				}
				item.setPublishTime(DateUtils.parseDate(publishTimeStr, "yyyy:MM:dd HH:mm:ss"));
				String workeAndSalary = li.getElementsByClass("info-primary").get(0).getElementsByClass("name").get(0).text();
				String[] workeAndSalaryArr = workeAndSalary.split(" ");
				if(workeAndSalaryArr.length > 2){
					String worke = "";
					for (int i = 0; i < workeAndSalaryArr.length - 1; i++) {
						worke += workeAndSalaryArr[i];
					}
					item.setWorke(worke);
					String salary = workeAndSalaryArr[workeAndSalaryArr.length - 1];
					item.setMoneyStart(reduceMoney(salary.split("-")[0]));
					item.setMoneyEnd(reduceMoney(salary.split("-")[1]));
				}else{
					item.setWorke(workeAndSalaryArr[0]);
					String salary = workeAndSalaryArr[1];
					item.setMoneyStart(reduceMoney(salary.split("-")[0]));
					item.setMoneyEnd(reduceMoney(salary.split("-")[1]));
				}
				
				
				String cityAndWorkAgeAndEducation = li.getElementsByClass("info-primary").get(0).getElementsByTag("p").get(0).html();
				String regexReplace = "<em class=\"vline\"></em>";
				cityAndWorkAgeAndEducation = cityAndWorkAgeAndEducation.replaceAll(regexReplace, " ");
				item.setCity(cityAndWorkAgeAndEducation.split(" ")[0]);
				item.setWorkeAge(cityAndWorkAgeAndEducation.split(" ")[1]);
				item.setEducation(cityAndWorkAgeAndEducation.split(" ")[2]);
				
				String companyName = li.getElementsByClass("company-text").get(0).getElementsByClass("name").get(0).text();
				item.setCompanyName(companyName);
				String industryAndCompanyTypeAndcompanyScale = li.getElementsByClass("company-text").get(0).getElementsByTag("p").get(0).html();
				industryAndCompanyTypeAndcompanyScale = industryAndCompanyTypeAndcompanyScale.replaceAll(regexReplace, " ");
				String[] industryAndCompanyTypeAndcompanyScaleArr = industryAndCompanyTypeAndcompanyScale.split(" ");
				if(industryAndCompanyTypeAndcompanyScaleArr.length == 3){
					item.setIndustry(industryAndCompanyTypeAndcompanyScaleArr[0]);
					item.setCompanyType(industryAndCompanyTypeAndcompanyScaleArr[1]);				
					item.setCompanyScale(industryAndCompanyTypeAndcompanyScaleArr[2]);
				}else{
					item.setIndustry(industryAndCompanyTypeAndcompanyScaleArr[0]);
					String scaleRegex = "\\d.*人";
					Pattern p = Pattern.compile(scaleRegex);
					Matcher m = p.matcher(industryAndCompanyTypeAndcompanyScaleArr[1]);
					if(m.find()){
						item.setCompanyScale(industryAndCompanyTypeAndcompanyScaleArr[1]);
						item.setCompanyType("");
					}else{
						item.setCompanyScale("");
						item.setCompanyType(industryAndCompanyTypeAndcompanyScaleArr[1]);
					}
				}
				item.setDistrict("");
				List<String> xiyinli = new ArrayList<>();
				Elements spans = li.getElementsByClass("job-tags").get(0).getElementsByTag("span");
				for (Element span : spans) {
					xiyinli.add(span.text());
				}
				item.setAttractive(StringUtils.join(xiyinli," "));
				System.out.println(JSON.toJSONString(item));
			}
		}
		
	}

	@Override
	public void reloadRedis(List<Zhaopin> result) {
		for (Zhaopin item : result) {
			String key = item.getCity() + item.getCompanyName() + item.getWorke() + item.getMoneyStart() + item.getMoneyEnd() + item.getPublishTime();
			key = DigestUtils.md5Hex(key);
			if(!redisTemplate.hasKey(key)){
				redisTemplate.opsForValue().set(key,1);
			}
		}
	}

}