package cn.NightCat.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import cn.NightCat.Config.NCConfig;
import cn.NightCat.Exception.NCException;

public class CNSms {
 /**
  	* 向指定 URL 发送POST方法的请求
  * @param url
  *            发送请求的 URL
  * @param param
  *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
  * @return 所代表远程资源的响应结果
  */
	synchronized private static String sendPost(String url, String param) {
		
	     PrintWriter out = null;
	     BufferedReader in = null;
	     String result = "";
	     try {
	         URL realUrl = new URL(url);
	         // 打开和URL之间的连接
	         URLConnection conn = realUrl.openConnection();
	         // 设置通用的请求属性
	         conn.setRequestProperty("accept", "*/*");
	         conn.setRequestProperty("connection", "Keep-Alive");
	         conn.setRequestProperty("user-agent",
	                 "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	         // 发送POST请求必须设置如下两行
	         conn.setDoOutput(true);
	         conn.setDoInput(true);
	         // 获取URLConnection对象对应的输出流
	         out = new PrintWriter(conn.getOutputStream());
	         // 发送请求参数
	         out.print(param);
	         // flush输出流的缓冲
	         out.flush();
	         // 定义BufferedReader输入流来读取URL的响应
	         in = new BufferedReader(
	                 new InputStreamReader(conn.getInputStream()));
	         String line;
	         while ((line = in.readLine()) != null) {
	             result += line;
	         }
	     } catch (Exception e) {
	    	 NCException.printStackTrace(e,false);
	     }
	     //使用finally块来关闭输出流、输入流
	     finally{
	         try{
	             if(out!=null){
	                 out.close();
	             }
	             if(in!=null){
	                 in.close();
	             }
	         }
	         catch(IOException ex){
	        	 NCException.printStackTrace(ex,false);
	         }
	     }	
     	return result;
 	}
 	private static long GetGMTTime(){
 		return System.currentTimeMillis() / 1000 -  28800;
 	}
	/**
	 * 发送验证码
	 * @param tel 手机号码 
	 * @param code 验证码
	 * @return -1:发送失败 1:成功 2:参数错误 3:密钥验证失败 4:账号或密码错误 5:服务器内部认证失败 6:余额不足 7:内容不符合格式 8:频率超限 9:接口超时 10:后缀签名长度超限
	 */
 	public static String sendSms(String tel,String msg){
		String content = "";
		try {
			content = URLEncoder.encode(msg, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			NCException.printStackTrace(e,false);
		}
		if(null == content || content.equals("") || !NCConfig.Sms)
			return "-1";
		long time = GetGMTTime();
		String authkey = MD5Util.MD5_Lower(NCConfig.Sms_UserName+time+ MD5Util.MD5_Lower(NCConfig.Sms_Password)+NCConfig.Sms_Key);//生成authkey
		String para = "username="+NCConfig.Sms_UserName+"&time="+time+"&content="+content+"&mobile="+tel+"&authkey="+authkey;//生成参数
		//post逻辑
		String respost = sendPost("http://sms.edmcn.cn/api/cm/trigger_mobile.php", para);
		LogUtil.getInstance().debug("发送短信至["+tel+"] 内容["+msg+"] 返回结果["+respost+"]");
		return respost;
	}

}
