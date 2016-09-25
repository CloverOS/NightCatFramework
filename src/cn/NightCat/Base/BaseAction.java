package cn.NightCat.Base;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletResponse;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import cn.NightCat.Base.Param.ParamType;
import cn.NightCat.Config.NCConfig;
import cn.NightCat.Exception.NCException;
import cn.NightCat.Session.NCSession;
import cn.NightCat.Util.Example;
import cn.NightCat.Util.LogUtil;
import cn.NightCat.Util.SQLUtil;
import cn.NightCat.Util.Sign;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/*
	Create by Crazyist at 2015年8月14日 下午4:41:38 Filename:BaseAction
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public abstract class BaseAction {
	
	public Charset charset = null;
	
	public static final String TAG = "BaseAction.json";
	/** 数据库连接 */
	protected Connection conn = null;
	/** 命令执行工具 */
	protected Statement statement = null;
	/** 请求者平台秘钥 */
	private String key = "";
	/**  请求者登录信息(没登录则为NULL) */
	protected JSONObject Json_UserInfo = null;
	/** 请求者平台编号 */
	protected String Head = "";
	/** 请求者Session */
	protected String Session = null; 
	/**
	 * 如果需要使用 非自动 提交 数据库连接请重写该方法,并返回 true
	 * @return 返回 true 则需要Action自动创建数据库连接(非自动提交)
	 */
	protected boolean NeedCreateSQL(){ return false; }
	/**
	 * 设置该接口是否需要权限
	 * @return true 需要权限
	 */
	public boolean NeedPower(){ return false; }

	/**
	 * 次方法切勿调用,由系统自动调用
	 * @param action
	 * @param response
	 * @param remoteIP
	 * @param remotePort
	 * @param data
	 * @throws Exception
	 */
	public static void disposeaAction(Charset charset,String action,HttpServletResponse response,String remoteIP,String remotePort,String data) throws Exception
	{
		long FirstTime = 0;
		long ProcessTime = 0;
		if(NCConfig.FLAG_Statistics){
			FirstTime = System.currentTimeMillis();
			NCConfig.Count ++;
		}
		BaseAction processAction = null;
		String result = null;
		try {
			JSONObject jsonObject = null;
			processAction = (BaseAction)Class.forName(action).newInstance();
			if(null == processAction.getParams() || null == processAction.getReturn())
				throw new NCException(1016, "系统出错!");
			if(null == charset)
				charset = Charset.defaultCharset();
			processAction.charset = charset;
			try {
				jsonObject = JSONObject.fromObject(data);
			} catch (Exception e) {
				// TODO: handle exception
				throw new NCException(1007, "系统出错!");
			}
			processAction.verifySign(jsonObject);
			if(processAction.NeedPower()){
				if(!jsonObject.has("Session"))
					throw new NCException(1002, "系统出错!");
				processAction.Session = jsonObject.getString("Session");
				processAction.Json_UserInfo = NCSession.getInstance().getSession(jsonObject.getString("Head"), processAction.Session);
				if(null == processAction.Json_UserInfo)
					throw new NCException(1002, "系统出错!");
				// 判断是否有权限
				String str_power = processAction.Json_UserInfo.getString("Power_Str");
				if(str_power.charAt(NCConfig.Map_Actions.get(action)) != '1')
					throw new NCException(1004, "无权访问!");
				// 访问限制控制
				if(processAction.Json_UserInfo.has(action)){
					long last_time = Long.parseLong(processAction.Json_UserInfo.getString(action));
					long now_time = System.currentTimeMillis();
					long interval = now_time - last_time;
					if(interval < processAction.Limit())
						throw new NCException(1005, "访问受限制!");
				}
				processAction.Json_UserInfo.put(action, System.currentTimeMillis() + "");
				NCSession.getInstance().setSession(processAction.Head, processAction.Session, processAction.Json_UserInfo);
			}
			// 2.0.1.4 Body协议需进行Base64处理
//			JSONObject body = jsonObject.getString("Body"); // 2.0.1.3
			String str_body = jsonObject.getString("Body");
			try {
				str_body = new String(Base64.decode(str_body), charset);
			} catch (Exception e) {
				// TODO: handle exception
				throw new NCException(1012, "系统错误!");
			}
			JSONObject body = JSONObject.fromObject(str_body);
			
			for (Param param : processAction.getParams()) {
				String value = null;
				if(body.has(param.getName()))
					value = body.getString(param.getName());
				if(null == value && !param.getMust())
					continue;
				if(param.getType() == ParamType.Type_JSONObject || param.getType() == ParamType.Type_JSONArray)
					body.put(param.getName(), Param.Check(param, param.getType(), param.getParams(), value));
				else
					body.put(param.getName(), Param.Check(param, param.getType(), value));
			}
			if(processAction.NeedCreateSQL()){
				processAction.conn = NCConfig.getConnection();
				if(processAction.conn == null)
					throw new NCException(1006, "服务器繁忙,请稍后重试!");
				processAction.statement = processAction.conn.createStatement();
				processAction.conn.setAutoCommit(false);
				if(processAction.statement == null)
					throw new NCException(1006, "服务器繁忙,请稍后重试!");
			}
			result = processAction.processData(remoteIP, remotePort, body);
			if(processAction != null && processAction.conn != null)
				SQLUtil.safeClose(processAction.conn);
			if(null == result || result.equals(""))
				throw new NCException(1009, "系统出错!");
		} catch(NCException e){
			result = processAction.CreateSignData(e.getResult(), null, e.getError_msg() ,null);
		} catch (JSONException e) {
			// TODO: handle exception
			result = processAction.CreateSignData(1008, null, "系统错误!", e.getMessage());
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(data,e,true);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("Result", "-1");
			jsonObject.put("Body", new JSONObject());
			jsonObject.put("Msg", "System Error");
			jsonObject.put("Error_Msg", e.getMessage());
			result = jsonObject.toString();
		}
		if(NCConfig.FLAG_Statistics){
			ProcessTime = System.currentTimeMillis() - FirstTime;
			response.setHeader("Process-Time", ProcessTime +"");
			NCConfig.Count--;
		}
		response.getWriter().print(result);
		if(processAction != null && processAction.ReturnLogs())
			LogUtil.getInstance().info("User["+remoteIP+":"+remotePort+"] ProcessTime[" + ProcessTime + "] Oline["+NCConfig.Count+"] visit[" + action + "] Charset["+charset.toString()+"] \n\t receive Data:"+data+"\n\t result Data:"+ result);
	}
	/**
	 * 获取请请求参数列表
	 * @return 请求参数数组
	 */
	public abstract Param[] getParams();
	/**
	 * 获取返回参数列表
	 * @return 返回参数数组
	 */
	public abstract Param[] getReturn();
	
	/***
	 * 数据报文进行签名
	 * @param result 返回代码
	 * @param body 数据内容
	 * @param msg 信息
	 * @param error_msg 错误信息
	 * @return 签名后的JSON数据报
	 */
	protected final String CreateSignData(int result,JSONObject body,String msg,String error_msg)
	{
		String Timestamp = System.currentTimeMillis() + "";
		if(null == body)
			body = new JSONObject();
		if(null == key)
			key = "";
		key = Sign.getKey(key, Timestamp);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Result", result+"");
		jsonObject.put("Body", Base64.encode(body.toString().getBytes(charset)));
		if(null != msg && !"".equals(msg))
			jsonObject.put("Msg", msg);
		if(null != error_msg && !"".equals(error_msg))
			jsonObject.put("Error_Msg", error_msg.replace("\"", ""));
		jsonObject.put("Timestamp", Timestamp);
		jsonObject.put("Sign", Sign.getSign(jsonObject.getString("Body"), key));
		return jsonObject.toString();
	}
	
	private void verifySign(JSONObject data) throws NCException
	{
		if(!data.has("Head"))
			throw new NCException(1017, "系统出错!");
		else if(!data.has("Body"))
			throw new NCException(1017, "系统出错!");
		else if(!data.has("Timestamp"))
			throw new NCException(1017, "系统出错!");
		else if(!data.getString("Timestamp").matches("^[0-9]{13}$"))
			throw new NCException(1018, "系统出错!");
		else if(!data.has("Sign"))
			throw new NCException(1019, "系统出错!");
		Head = data.getString("Head");
		key = NCConfig.KeyMap.get("Platform_" + Head + "");
		// 控制访问时间
//		long Timestamp = data.getLong("Timestamp");
//		if(System.currentTimeMillis() - Timestamp >= 600000)
//			throw new NCException(1017, "系统出错!");
		if(null == key || "".equals(key))
			throw new NCException(1020, "系统出错!");
		if(Sign.VerifySign(data.getString("Body"), key, data.getString("Timestamp"),data.getString("Sign")))
			return;
		throw new NCException(1001, "系统出错!");
	}
	
	/**
	 * 接口访问限制(两次访问必须间隔 多少ms 默认 1s 需要权限接口才能使用限制)
	 * @return
	 */
	public int Limit() {
		return 1000;
	}

	/**
	 * Action 接收器
	 * @param remoteIP 客户端IP地址
	 * @param remotePort 客户端端口
	 * @param data 接收到的数据
	 * @return
	 * @throws Exception
	 */
	protected abstract String processData(String remoteIP, String remotePort, JSONObject data) throws Exception;
	
	/***
	 * 接口名称
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * 接口访问方式
	 * @return
	 */
	public String getMethod(){ return NCConfig.Method; };
	
	/***
	 * 接口说明
	 * @return
	 */
	public abstract String getDesc();
	/***
	 * 接口作者
	 * @return
	 */
	public String getAuthor(){ return NCConfig.Author; };
	
	
	/***
	 * 请求示例
	 * @return
	 */
	public JSONObject getRequestExample(){
		JSONObject body = new JSONObject();
		for (Param param : getParams()){
			if(param.getType() == ParamType.Type_JSONArray || param.getType() == ParamType.Type_JSONObject){
				body.put(param.getName(), CreateData(param));
			}else{
				body.put(param.getName(), param.getExample());
			}
		}
		if(NeedPower())
			return Example.getRequest(body, true);
		else
			return Example.getRequest(body, false);
	};
	
	/***
	 * 返回示例
	 * @return
	 */
	public JSONObject getResponseExample(){
		JSONObject body = new JSONObject();
		for (Param param : getReturn()){
			if(param.getType() == ParamType.Type_JSONArray || param.getType() == ParamType.Type_JSONObject){
				if(param.getExample() != null)
					body.put(param.getName(), param.getExample());
				else
					body.put(param.getName(), CreateData(param));
			}else{
				body.put(param.getName(), param.getExample());
			}
		}
		return Example.getResponse(body, null);
	};
	
	private String CreateData(Param p){
		Object result = null;
		Param[] params = p.getParams();
		
		JSONObject temp = new JSONObject();
		if(params.length > 1)
			for (Param param : params)
				temp.put(param.getName(), param.getExample());
		if(p.getType() == ParamType.Type_JSONObject)
			result = temp;
		else{
			result = new JSONArray();
			if(params.length > 1)
				((JSONArray)result).add(temp);
			else
				((JSONArray)result).add(params[0].getExample());
		}
		return result.toString();
	}
	/**
	 * 如果True表示该Action输出日记
	 * @return
	 */
	protected boolean ReturnLogs(){
		return true;
	}
	
	/***
	 * 最后修改时间
	 * @return
	 */
	public final String getLastTime(){
		SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		URL url = this.getClass().getResource("");
		if(null != url)
		{
			String filename = getClass().getName();
			filename = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
			File file = new File(url.getPath().replace("%20", " ")+ filename + ".class");
			if(!file.exists())
				return "获取时间失败!["+file.getPath()+"]";
			return sm.format(new java.util.Date(file.lastModified()));
		}else{
			return "获取时间失败!";
		}
	}
}
