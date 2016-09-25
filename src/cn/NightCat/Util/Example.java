package cn.NightCat.Util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
	Create by Crazyist at 2015年12月4日 下午12:50:27 Filename:Example.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class Example {
	
	
	public static JSONObject getRequest(JSONObject body,boolean session){
		long now = System.currentTimeMillis();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Head", "10001");
		if(session)
			jsonObject.put("Session", MD5Util.MD5_Lower("123456"));
		if(null == body)
			body = new JSONObject();
		jsonObject.put("Body", body);
		jsonObject.put("Timestamp", now + "");
		jsonObject.put("Sign", Sign.getSign(body.toString(), Sign.getKey("42a1eae42a4ab13e6d75c8768324543b", now + "")));
		return jsonObject;
	}
	public static JSONObject getRequest(JSONArray body,boolean session){
		JSONObject jsonObject = new JSONObject();
		long now = System.currentTimeMillis();
		jsonObject.put("Head", "10001");
		if(session)
			jsonObject.put("Session", MD5Util.MD5(now+""));
		if(null == body)
			body = new JSONArray();
		jsonObject.put("Body", body);
		jsonObject.put("Timestamp", now + "");
		jsonObject.put("Sign", Sign.getSign(body.toString(), Sign.getKey("42a1eae42a4ab13e6d75c8768324543b", now +"")));
		return jsonObject;
	}
	public static JSONObject getResponse(JSONObject body,String msg){
		JSONObject jsonObject = new JSONObject();
		long now = System.currentTimeMillis();
		if(null == body)
			body = new JSONObject();
		jsonObject.put("Result", "10000");
		jsonObject.put("Body", body);
		jsonObject.put("Msg", msg);
		jsonObject.put("Timtestamp", now+"");
		jsonObject.put("Sign", Sign.getSign(body.toString(), Sign.getKey("42a1eae42a4ab13e6d75c8768324543b", now + "")));
		return jsonObject;
	}
	public static JSONObject getResponse(JSONArray body,String msg){
		JSONObject jsonObject = new JSONObject();
		long now = System.currentTimeMillis();
		if(null == body)
			body = new JSONArray();
		jsonObject.put("Result", "10000");
		jsonObject.put("Body", body);
		jsonObject.put("Msg", msg);
		jsonObject.put("Timtestamp", now + "");
		jsonObject.put("Sign", Sign.getSign(body.toString(), Sign.getKey("42a1eae42a4ab13e6d75c8768324543b", now + "")));
		return jsonObject;
	}
}
