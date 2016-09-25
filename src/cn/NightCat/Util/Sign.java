package cn.NightCat.Util;

import java.security.MessageDigest;

import cn.NightCat.Exception.NCException;

/*
	Create by Crazyist at 2015年8月9日 下午6:08:34 Filename:Sign
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class Sign {
	public static final String TAG = "Sign.json";
	private Sign(){ }
	/***
	 * 对数据进行签名处理
	 * @param body 要签名的数据
	 * @param key 平台分配的Key
	 * @return 签名后的数据
	 */
	public static String getSign(String body,String key)
	{
		char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = (body+"YMTeam"+key).getBytes("utf-8");
//            for (int i = 0; i < btInput.length; i++) 
//				btInput[i] = (byte)(btInput[i] ^ key.charAt(i % 32) >> i % 8 + i % 256);
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
        	NCException.printStackTrace(e,false);
            return null;
        }
	}
	/***
	 * 判断签名是否正确
	 * @param body 签名内容
	 * @param key 平台分配的key
	 * @param sign 签名值
	 * @return
	 */
	public static boolean VerifySign(String body,String key,String timestamp,String sign)
	{
		String key_1 = getSign(body, getKey(key, timestamp));
		String key_2 = sign;
		if(key_1.equalsIgnoreCase(key_2))
			return true;
		else{
			LogUtil.getInstance().error("Sign error["+key_1+":"+key_2+"]["+key+"]["+timestamp+":"+body+"]");
			return false;
		}
	}
	/**
	 * 获取签名key
	 * @param key 平台分配的key
	 * @param timestamp 时间戳 yyyyMMddHHmmssSSS
	 * @return 签名key
	 */
	public static String getKey(String key,String timestamp){
		return getSign(key, timestamp);
	}
}
