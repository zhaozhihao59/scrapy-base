package org.recruit.base.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;


public class HttpUtil {

	private static Log logger = LogFactory.getLog(HttpUtil.class);
	private static ThreadLocal<HttpClient>  threadClients = new ThreadLocal<HttpClient>(){
		@Override
		protected HttpClient initialValue() {
			return HttpClients.createDefault();
		}
	}; 
	
	/**
	 * Get方式获取数据
	 * @param url
	 * @return
	 */
	public static String loadContentByGetMethod(String url,List<Header> headers) throws Exception{
		String result = null;
//		HttpClient httpClient = HttpClients.createDefault();
		/* 1 生成 HttpClinet 对象并设置参数 */
		// 设置 Http 连接超时为10秒
//		httpClient.
//		.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
		/* 2 生成 GetMethod 对象并设置参数 */
		
		HttpGet getMethod = new HttpGet(url);
		try {
//			getMethod.setURI(URI.create(url));
			for (Header header : headers) {
				getMethod.addHeader(header);
			}
			
			// 设置 get 请求超时为 10 秒
			RequestConfig requestConfig = RequestConfig.custom().build();
			getMethod.setConfig(requestConfig);
			
//			getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 10000);
			// 设置请求重试处理，用的是默认的重试处理：请求三次
//			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(3,true));
			/* 3 执行 HTTP GET 请求 */
			 HttpResponse statusResponse = threadClients.get().execute(getMethod);
			/* 4 判断访问的状态码 */
			if (statusResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				getMethod.releaseConnection();
				logger.debug("GET请求失败: " + statusResponse.getStatusLine());
				return null;
			}
			// 读取 HTTP 响应内容，这里简单打印网页内容
			// 读取为字节数组
			result = EntityUtils.toString(statusResponse.getEntity());
		} catch (Exception e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			logger.error("读取GET接口数据时发生异常："+e.getMessage(),e);
			throw new Exception(e);
		} finally {
			/* 6 .释放连接 */
			getMethod.releaseConnection();
		}
		logger.debug("读取数据为："+result);
		
		return result;
	}
	
	/**
	 * 发送JSON数据，POST方式
	 * @param data
	 */
	public static String postJSONData(String url,JSONObject data) throws Exception{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		if(data != null){
			String inputParam = data.toJSONString();
			logger.debug("POST接口发送内容："+inputParam);
			httpPost.setEntity(new StringEntity(inputParam, "UTF-8"));	
		}
		httpPost.addHeader("Content-Type", "application/json");
		CloseableHttpResponse response = null;
		
		String result = null;
		try{
			response = httpclient.execute(httpPost);
			result = EntityUtils.toString(response.getEntity(), "UTF-8");
			logger.debug("发送POST接口数据返回内容："+result);
		}catch(Exception ex){
			logger.debug("调用POST接口数据时发生异常："+ex.getMessage(),ex);
			throw new Exception(ex.getMessage());
		}finally{
			if(null != response){
				try{
					response.close();
				}catch(Exception e){}
			}
		}
		
		return result;
		
	}
	
	/**
	 * 发送POST请求参数
	 * @param url
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public static String postData(String url,Map<String,String> paramMap) throws Exception{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String result = null;
		try {
			// 构造请求
			HttpPost httpPost = new HttpPost(url);
			// 封装参数
			List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
			if(null != paramMap){
				for(Entry<String,String> item : paramMap.entrySet()){
					nvps.add(new BasicNameValuePair(item.getKey(),item.getValue()));
				}
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps,"utf-8"));
			// 发起请求
			response = httpClient.execute(httpPost);
			// 获取响应数据
			result = EntityUtils.toString(response.getEntity(),Charset.forName("UTF-8"));
//			logger.debug("接收到的POST响应内容：" + result);
		} catch (Exception ex) {
			String msg = "POST请求发送失败:" + ex.getMessage();
			logger.error(msg, ex);
		} finally {
			try {
				response.close();
				httpClient.close();
			} catch (Exception e) {}
		}
		
		return result;
	}
	public static List<Header> headerList = new ArrayList<>();
	static{
		headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"));
		headerList.add(new BasicHeader("Connection", "keep-alive"));
		headerList.add(new BasicHeader("authorization", "oauth c3cef7c66a1843f8b3a9e6a1e3160e20"));
	}
	public static void main(String[] args) throws Exception {
		List<String> list = new ArrayList<>();
		list.add("miao-ying");
		list.add("ku-rong-53-87");
		list.add("dao-sen-sen");
		list.add("hua-zhuo-53");
		list.add("yin-jiao-shou-32");
		list.add("ling-hu-fu-gui");
		list.add("cai-tong");
		list.add("libiubiu");
		list.add("xu-zhi-ting-21");
		list.add("liu-xiao-liu-9-24");
		list.add("ikaros_kirishima");
		list.add("i-li-si-hua-mao-zi");
		list.add("i-li-si-hua-mao-zi");
		for (int i = 0; i < 20; i++) {
			String url = "https://www.zhihu.com/api/v4/members/"+list.get(new Random().nextInt(list.size()))+"?include=locations%2Cemployments%2Cgender%2Ceducations%2Cbusiness%2Cvoteup_count%2Cthanked_Count%2Cfollower_count%2Cfollowing_count%2Ccover_url%2Cfollowing_topic_count%2Cfollowing_question_count%2Cfollowing_favlists_count%2Cfollowing_columns_count%2Cavatar_hue%2Canswer_count%2Carticles_count%2Cpins_count%2Cquestion_count%2Ccolumns_count%2Ccommercial_question_count%2Cfavorite_count%2Cfavorited_count%2Clogs_count%2Cmarked_answers_count%2Cmarked_answers_text%2Cmessage_thread_token%2Caccount_status%2Cis_active%2Cis_bind_phone%2Cis_force_renamed%2Cis_bind_sina%2Cis_privacy_protected%2Csina_weibo_url%2Csina_weibo_name%2Cshow_sina_weibo%2Cis_blocking%2Cis_blocked%2Cis_following%2Cis_followed%2Cmutual_followees_count%2Cvote_to_count%2Cvote_from_count%2Cthank_to_count%2Cthank_from_count%2Cthanked_count%2Cdescription%2Chosted_live_count%2Cparticipated_live_count%2Callow_message%2Cindustry_category%2Corg_name%2Corg_homepage%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics";
			
			long start = System.currentTimeMillis();
			System.out.println(loadContentByGetMethod(url, headerList));
			long end = System.currentTimeMillis();
			System.out.println((end-start));
		}
		
	}
}
