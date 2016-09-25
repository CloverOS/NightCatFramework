package cn.NightCat.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.NightCat.Config.NCConfig;
import cn.NightCat.Exception.NCException;

/*
	Create by Crazyist at 2015年10月28日 下午3:49:37 Filename:QXSms
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class QXSms {
	public static final String TAG = "QXSms.json";
	/**
	 * 发送验证码
	 * @param tel 手机号码 
	 * @param code 验证码
	 * @return >= 1 成功
	 */
 	public static String sendSmsCode(String tel,String code){
		String content = "";
		try {
			content = URLEncoder.encode("亲,您的手机验证码为:" + code + " 请注意查收！", "gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			NCException.printStackTrace(e,false);
		}
		if(null == content || content.equals("") || !NCConfig.Sms)
			return "-1";
		//post逻辑
		String respost;
		try {
			respost = HttpUtil.sendGet("http://117.25.149.152:8090/Api/SendSms?username=" + NCConfig.Sms_UserName +"&password="+NCConfig.Sms_Password + "&message=" + content + "&mobile=" + tel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			NCException.printStackTrace(e,false);
			respost = "-1";
		}
		return respost;
	}
 	/**
 	 * 发送支付通知
 	 * @param tel 手机号码
 	 * @param carcode 车牌号
 	 * @param name 姓名
 	 * @param money 金额
 	 * @param card 银行卡号
 	 * @param cardname 开户行
 	 * @return
 	 */
 	public static String sendOrderStatus(String tel,String carcode,String name,String money,String card,String cardname){
 		String content = "";
		try {
			content = URLEncoder.encode(name + ":您好!您的爱车["+carcode+"]保养即将完成,请您汇" + money +"元到我公司账户,卡号:"+card +",开户行:"+cardname, "gbk");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			NCException.printStackTrace(e,false);
		}
		if(null == content || content.equals("") || !NCConfig.Sms)
			return "-1";
		//post逻辑
		String respost;
		try {
			respost = HttpUtil.sendGet("http://117.25.149.152:8090/Api/SendSms?username=" + NCConfig.Sms_UserName +"&password="+NCConfig.Sms_Password + "&message=" + content + "&mobile=" + tel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			NCException.printStackTrace(e,false);
			respost = "-1";
		}
		return respost;
 	}
}
