package org.recruit.base.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * 时间工具类
 * @author admin 赵志豪
 *
 */
public class DateUtil {

	// 第一次调用get将返回null  
	private static ThreadLocal<SimpleDateFormat> threadLocalDf = new ThreadLocal<SimpleDateFormat>(){
		protected SimpleDateFormat initialValue() {  
            return new SimpleDateFormat("yyyyMMdd");  
        }  
	};  
	private static String[] weeks = new String[]{"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
	private static String[] englishweek = new String[]{"Sun","Mon","Tues","Wed","Thur","Fri","Sat"};
	private static String[] monthAbb = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	private static Calendar calendar = Calendar.getInstance(Locale.CHINA);
	/** 格式化年月字符串 */
	public static String fmtYM = "yyyyMM";
	public static String fmtYMD = "yyyyMMdd";
	public static String fmtYYYY_MM_dd = "yyyy-MM-dd";
	public static String fmtYw = "yyyyww";
	public static String formatDate(Date d,String pattern){
		if(d == null){
			return "";
		}
		threadLocalDf.get().applyPattern(pattern);
		return threadLocalDf.get().format(d);
	}
	
	public static Integer fmtYMDate(Date d){
		if(d == null){
			return 0;
		}
		threadLocalDf.get().applyPattern(fmtYM);
		
		Integer num = Integer.parseInt(threadLocalDf.get().format(d));
		return num;
	}
	/**
	 * 得到年月日
	 * @param d
	 * @return
	 */
	public static Integer fmtYMDDate(Date d){
		if(d == null){
			return 0;
		}
		threadLocalDf.get().applyPattern(fmtYMD);
		Integer num = Integer.parseInt(threadLocalDf.get().format(d));
		return num;
	}

	public static Date parseYMDate(Integer date) {
		threadLocalDf.get().applyPattern(fmtYM);
		
		Date d = null;
		if(null == date){
			return d;
		}
		try {
			d = threadLocalDf.get().parse(date.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}   
	public static Date parseYMDDate(Integer date) {
		threadLocalDf.get().applyPattern(fmtYMD);
		Date d = null;
		if(null == date){
			return d;
		}
		try {
			d = threadLocalDf.get().parse(date.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}   
	/**
	 * 得到周
	 * @param week
	 * @return
	 */
	public static Date parseYwDate(Integer week) {
		threadLocalDf.get().applyPattern(fmtYw);
		Date d = null;
		if(null == week){
			return d;
		}
		try {
			d = threadLocalDf.get().parse(week.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}   
	
	public static Date parseDate(String date, String pattern) {
		threadLocalDf.get().applyPattern(pattern);
		Date d = null;
		try {
			d = threadLocalDf.get().parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return d;
	}
	
	/**
	 * 获取整点日期
	 * @param date
	 * @return
	 */
	public static Date getDayZeroTime(Date date){
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND,0);
		
		return calendar.getTime();
	}
	
	public static Date parseDate(String date, String pattern,Locale locale) {
		Date result = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,locale);
			result = simpleDateFormat.parse(date);
			return result;
		} catch (ParseException e) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd",locale);
				result = simpleDateFormat.parse(date);
				return result;
			} catch (ParseException e1) {
				e1.printStackTrace();
				return result;
			}
		}
		
	}
	
	/**
	 * 获取是周几 1表示周1 0表示周日
	 * @param d
	 * @return
	 */
	public static int getChineseWeekNum(Date d){
		calendar.setTime(d);
		
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		
		week = week - 1;	//默认周日为1
		
		return week;
	}
	
	/**
	 * 获取星期几
	 * @param num
	 * @return
	 */
	public static String getChineseWeekNameByNum(int num){
		return weeks[num];
	}
	/**
	 * 获取英文星期几
	 * @param num
	 * @return
	 */
	public static String getWeekNameByNum(int num){
		return englishweek[num];
	}
	public static String getWeekNameByDate(Date date){
		calendar.setTime(date);;
		return getWeekNameByNum(calendar.get(Calendar.DAY_OF_WEEK) - 1);
	}
	
	/**
	 * 获取年份
	 * @param now
	 * @return
	 */
	public static int getYear(Date now) {
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(now);
		int year = c.get(Calendar.YEAR);
		
		return year;
	}
	
	/**
	 * 获取月份
	 * @param now
	 * @return
	 */
	public static int getMonth(Date now) {
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(now);
		int month = c.get(Calendar.MONTH) + 1;
		
		return month;
	}
	/**
	 * 获取月份
	 * @param now
	 * @return
	 */
	public static int getDayOfMonth(Date now) {
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(now);
		int day = c.get(Calendar.DAY_OF_MONTH) ;
		return day;
	}
	

	/**
	 * 判断是否超过多少分钟
	 * 
	 * @param start
	 * @param end
	 * @param subMinute 相差的分钟
	 * @return boolean
	 */
	public static boolean judgmentDate(Date start, Date end, int subMinute) {
		long cha = end.getTime() - start.getTime();
		if (cha < 0) {
			return false;
		}
		double result = cha * 1.0 / (1000 * 60);
		if (result <= subMinute) {
			return true;
		}
		return false;
	}
	/**
	 * 获取
	 * @param now
	 * @return
	 */
	public static int getPrizeWeekByTime(Date now) {
		Date compareDate = getDayZeroTime(now);
		int week = 1;	//默认第一周
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(new Date());
		c.set(Calendar.MONTH,0);
		c.set(Calendar.DAY_OF_MONTH, 12);	//一月12日
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND,0);
		
	//	Date firstWeekBegin = c.getTime();
		c.set(Calendar.DAY_OF_MONTH, 18);	//一月18日
		Date firstWeekEnd = c.getTime();
	//	c.set(Calendar.DAY_OF_MONTH, 19);	//一月19日
	//	Date secondWeekBegin = c.getTime();
		c.set(Calendar.DAY_OF_MONTH, 25);	//一月25日
		Date secondWeekEnd = c.getTime();
	//	c.set(Calendar.DAY_OF_MONTH, 26);	//一月26日
	//	Date thirdWeekBegin = c.getTime();
		c.set(Calendar.MONDAY, 1);	//二月
		c.set(Calendar.DAY_OF_MONTH, 1);	//二月1日
		Date thirdWeekEnd = c.getTime();
	//	c.set(Calendar.DAY_OF_MONTH, 2);	//二月2日
	//	Date fourthWeekBegin = c.getTime();
		c.set(Calendar.DAY_OF_MONTH, 8);	//二月8日
		Date fourthWeekEnd = c.getTime();
	//	c.set(Calendar.DAY_OF_MONTH, 9);	//二月9日
	//	Date fifthWeekBegin = c.getTime();	
	//	c.set(Calendar.DAY_OF_MONTH, 15);	//二月15日
	//	Date fifthWeekEnd = c.getTime();
		
		
		if(!compareDate.after(firstWeekEnd)){
			week = 1;
		}else if(!compareDate.after(secondWeekEnd)){
			week = 2;
		}else if(!compareDate.after(thirdWeekEnd)){
			week = 3;
		}else if(!compareDate.after(fourthWeekEnd)){
			week = 4;
		}else{
			week = 5;
		}
		
		
		return week;
		
	}

	/**
	 * 获取上年同期月份
	 * @param predictionBeginMonth
	 * @return
	 */
	public static Integer getLastSameTermMonth(Integer month,String pattern) {
		Date date = parseDate(month.toString(),pattern);
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTime(date);
		cal.add(Calendar.YEAR, -1);
		
		Integer resultMonth = Integer.valueOf(formatDate(cal.getTime(), pattern));
		return resultMonth;
	}
	/**
	 * 获取下年同期月份
	 * @param predictionBeginMonth
	 * @return
	 */
	public static Integer getPreSameTermMonth(Integer month,String pattern) {
		Date date = parseDate(month.toString(),pattern);
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTime(date);
		cal.add(Calendar.YEAR, 1);
		
		Integer resultMonth = Integer.valueOf(formatDate(cal.getTime(), pattern));
		return resultMonth;
	}
	/**
	 * 得到相差的天数
	 * @param curDate
	 * @return
	 */
	public static int getSubDay(Integer curDate){
		return getSubDay(parseYMDDate(20160101), parseYMDDate(curDate));
	}
	public static int getSubWeek(Integer curWeek){
		return getSubWeek(parseYwDate(201601),parseYwDate(curWeek));
	}
	/**
	 * 增加月份
	 * @param maxValidDate
	 * @param i
	 * @return
	 */
	public static Date addMonth(Date date, int count) {
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTime(date);
		cal.add(Calendar.MONTH, count);
		
		return cal.getTime();
	}
	/**
	 * 增加天数
	 * @param date
	 * @param count
	 * @return
	 */
	public static Date addDay(Date date, int count) {
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, count);
		return calendar.getTime();
	}
	/**
	 * 增加天数
	 * @param dateStr
	 * @param count
	 * @return
	 */
	public static Date addDay(String dateStr,String patterm, int count) {
		Date date = parseDate(dateStr, patterm);
		return addDay(date, count);
	}
	
	/**
	 * 获取相差的天数
	 * @param diaryDate
	 * @param now
	 * @return
	 */
	public static int getSubDay(Date d1, Date d2) {
		int subDay = 0;
		
		Calendar c1 = Calendar.getInstance(Locale.CHINA);
		c1.setTime(d1);
		
		Calendar c2 = Calendar.getInstance(Locale.CHINA);
		c2.setTime(d2);
		
		if(c1.after(c2)){
			subDay = getDaysBetween(c2, c1);
		}else{
			subDay = getDaysBetween(c1, c2);
		}
		return subDay;
	}
	
	/**
	 * 获取相差的天数
	 * @param diaryDate
	 * @param now
	 * @return
	 */
	public static int getSubWeek(Date d1, Date d2) {
		int subWeek = 0;
		
		Calendar c1 = Calendar.getInstance(Locale.CHINA);
		c1.setTime(d1);
		
		Calendar c2 = Calendar.getInstance(Locale.CHINA);
		c2.setTime(d2);
		
		if(c1.after(c2)){
			subWeek = getWeekBetween(c2, c1);
		}else{
			subWeek = getWeekBetween(c1, c2);
		}
		return subWeek;
	}
	/**
	 * 获取两个日期之间的天数
	 * @param d1 开始日期
	 * @param d2 结束日期
	 * @return
	 */
	public static int getDaysBetween(java.util.Calendar d1,java.util.Calendar d2) {
		int days = d2.get(java.util.Calendar.DAY_OF_YEAR) - d1.get(java.util.Calendar.DAY_OF_YEAR);
		int y2 = d2.get(java.util.Calendar.YEAR);
		if (d1.get(java.util.Calendar.YEAR) != y2) {
			d1 = (java.util.Calendar) d1.clone();
			int i = 0;
			do {
				if(i > 100){
					//加载循环此处，防止出现死循环
					break;
				}
				days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
				d1.add(java.util.Calendar.YEAR, 1);
				i++;
			} while (d1.get(java.util.Calendar.YEAR) != y2);
		}
		return days;
	}
	/**
	 * 获取两个日期之间的天数
	 * @param d1 开始日期
	 * @param d2 结束日期
	 * @return
	 */
	public static int getWeekBetween(Calendar d1,Calendar d2) {
		int days = d2.get(Calendar.WEEK_OF_YEAR) - d1.get(Calendar.WEEK_OF_YEAR);
		int y2 = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			d1 = (Calendar) d1.clone();
			int i = 0;
			do {
				if(i > 100){
					//加载循环此处，防止出现死循环
					break;
				}
				days += d1.getActualMaximum(java.util.Calendar.WEEK_OF_YEAR);
				d1.add(java.util.Calendar.YEAR, 1);
				i++;
			} while (d1.get(java.util.Calendar.YEAR) != y2);
		}
		return days;
	}

	/**
	 * 获取今年1月1号
	 * @param today
	 * @return
	 */
	public static Date getCurYearFirstDay(Date today) {
		
		calendar.setTime(today);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		return calendar.getTime();
	}
	/**
	 * 得到上一个月份的
	 * @param endMonth
	 */
	public static Integer getLastTimeMonth(Integer month) {
		calendar.setTime(parseYMDate(month));
		calendar.add(Calendar.MONTH, -1);
		return fmtYMDate(calendar.getTime());
	}
	/**
	 * 得到下一个月份的
	 * @param endMonth
	 */
	public static Integer getNextTimeMonth(Integer endMonth) {
		calendar.setTime(parseYMDate(endMonth));
		calendar.add(Calendar.MONTH, 1);
		return fmtYMDate(calendar.getTime());
	}
	/**
	 * 得到当前周
	 * @param date
	 * @return
	 */
	public static Integer getWeek(Date date){
		calendar.setTime(date);
		String result = "";
		Integer weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
		if(weekNumber < 10){
			result = "0" + weekNumber;
		}else{
			result = weekNumber.toString();
		}
		return Integer.valueOf(calendar.get(Calendar.YEAR) + result);
	}
	public static Date addWeek(Date date,Integer count){
		calendar.setTime(date);
		calendar.add(Calendar.WEEK_OF_YEAR, count);
		return calendar.getTime();
	}
	/**
	 * 得到日期格式化字符串
	 * @param date
	 * @return
	 */
	public static String getFormatDateToStr(Integer date){
		Date dateValue = parseYMDate(date);
		calendar.setTime(dateValue);
		return "'"+monthAbb[calendar.get(Calendar.MONTH)] + "-"+ calendar.get(Calendar.YEAR)+"'";
	}
	/**
	 * 获取当前月的最后一天
	 * @param month
	 * @return
	 */
	public static Integer fmtYMDDate(Integer month) {
		if(month == null){
			return 0;
		}
		calendar.setTime(parseYMDate(month));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		threadLocalDf.get().applyPattern(fmtYMD);
		Integer num = Integer.parseInt(threadLocalDf.get().format(calendar.getTime()));
		return num;
	}
	/**
	 * 获取当前月的最后一天
	 * @param month
	 * @return
	 */
	public static Integer getLastDayOfWeek(Date week) {
		if(week == null){
			return 0;
		}
		calendar.setTime(week);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
		threadLocalDf.get().applyPattern(fmtYMD);
		Integer num = Integer.parseInt(threadLocalDf.get().format(calendar.getTime()));
		return num;
	}
	/**
	 * 得到第一天
	 * @param date yyyyMMdd
	 * @return
	 */
	public static Integer getFirstDayOfChose(Integer date,int select){
		if(date == null){
			return 0;
		}
		calendar.setTime(parseYMDDate(date));
		calendar.set(select, 1);
		threadLocalDf.get().applyPattern(fmtYMD);
		Integer num = Integer.parseInt(threadLocalDf.get().format(calendar.getTime()));
		return num;
	}
	
	public static void main(String[] args) {
		System.out.println(Math.sqrt(0));
	}
}
