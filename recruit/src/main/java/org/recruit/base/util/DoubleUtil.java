package org.recruit.base.util;


import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DoubleUtil {
	
	/**
	 * 对double数据进行取精度.
	 * 
	 * @param value
	 *            double数据.
	 * @param scale
	 *            精度位数(保留的小数位数).
	 * @param roundingMode
	 *            精度取值方式. roundingMode 枚举
	 * @return 精度计算后的数据.
	 */
	public static double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(String.valueOf(value));
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}
	
	public static double round(String value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}
	
	/**
	 * 对double数据进行取精度.
	 * 
	 * @param value
	 *            double数据.
	 * @param scale
	 *            精度位数(保留的小数位数).
	 * @param roundingMode
	 *            精度取值方式. roundingMode 枚举
	 * @return 精度计算后的数据.
	 */
	public static Object roundLong(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(String.valueOf(value));
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		if (scale == 0) {
			return Math.round(d);
		}
		return d;
	}
	/**
	 * double 相加
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double sum(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.add(bd2).doubleValue();
	}
	
	public static double sum(double d1, double d2,double d3) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		BigDecimal bd3 = new BigDecimal(Double.toString(d3));
		double val = bd1.add(bd2).add(bd3).doubleValue();
		
		return val;
	}
	
	public static double sum(double d1, double d2,double d3,double d4) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		BigDecimal bd3 = new BigDecimal(Double.toString(d3));
		BigDecimal bd4 = new BigDecimal(Double.toString(d4));
		double val = bd1.add(bd2).add(bd3).add(bd4).doubleValue();
		
		return val;
	}

	/**
	 * double 相减
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double sub(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.subtract(bd2).doubleValue();
	}

	/**
	 * double 乘法
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double mul(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.multiply(bd2).doubleValue();
	}
	
	/**
	 * 乘法
	 * @param data
	 * @return
	 */
	public static Double mul(Double[] data) {
		BigDecimal total = new BigDecimal(1d);
		for(int i = 0;i < data.length;i++){
			BigDecimal bd = new BigDecimal(data[i]);
			total = total.multiply(bd);
		}
		
		return total.doubleValue();
	}

	/**
	 * double 除法
	 * 
	 * @param d1
	 * @param d2
	 * @param scale
	 *            四舍五入 小数点位数
	 * @return
	 */
	public static double div(double d1, double d2, int scale) {
		// 当然在此之前，你要判断分母是否为0，
		// 为0你可以根据实际需求做相应的处理
		if(d2 == 0){
			return 0;
		}
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 判断数据是否为零
	 * @param ds
	 * @return
	 */
	public static boolean judgeDataIsZero(Double[] ds) {
		boolean flag = true;
		for (int i = 0; i < ds.length; i++) {
			if (ds[i] != 0) {
				flag = false;
			}
		}
		return flag;
	}
	/**
	 * 判断数据是否为零
	 * @param ds
	 * @return
	 */
	public static boolean judgeDataIsZero(double[] ds) {
		boolean flag = true;
		for (int i = 0; i < ds.length; i++) {
			if (ds[i] != 0) {
				flag = false;
			}
		}
		return flag;
	}
	/**
	 * 计算平均值
	 * @param data
	 * @return
	 */
	public static Double countAvg(Double[] data){
		Double sum = 0d;
		for (int i = 0; i < data.length; i++) {
			sum += data[i];
		}
		return DoubleUtil.div(sum, data.length, 5);
	}
	/**
	 * 格式化千分位
	 * @param value
	 * @return
	 */
	public static String fmtMicrometer(Double value){
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(9);
		String s = df.format(value);
		if (s.indexOf(".") >=0) {
			StringBuffer sb = new StringBuffer();
			sb.append(s.substring(0, s.indexOf(".") + 1));
			
			char[] ch = s.substring(s.indexOf(".") + 1).toCharArray();
			StringBuffer temp = new StringBuffer();
			int j = 0;
			int k = 0;
			while(j < ch.length){
				k++;
				temp.append(ch[j]);
				if(k % 3 == 0 && j != ch.length - 1){
					k = 0;
					temp.append(",");
					continue;
				}
				j++;
			}
			sb.append(temp);
			return sb.toString();
		}else {
			return s;
		}
	}
	public static void main(String[] args) {
		Double a = -384750100.90981112;
		System.out.println(fmtMicrometer(a));
		
//		System.out.println(fmtMicrometer(a));
	}
}
