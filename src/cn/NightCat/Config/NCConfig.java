package cn.NightCat.Config;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import cn.NightCat.Base.Action;
import cn.NightCat.Base.BaseConfig;
import cn.NightCat.Exception.NCException;
import cn.NightCat.Session.Interface_Session;
import cn.NightCat.Session.NCSession;
import cn.NightCat.Session.NCTelSession;
import cn.NightCat.Util.LogUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
	Create by Crazyist at 2015年8月14日 下午4:41:19 Filename:Config
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public final class NCConfig {
	private static ResourceBundle MY_Resource;
	private static DatabaseConfig data;
	private static boolean OpenData = true;
	
	/**
	 * 请求白名单
	 */
	public static JSONArray WhiteSites = new JSONArray();
	
	/** 游客用户组 */
	public static String GuestGroup = "";
	
	/** 当前服务器人数(针对FLAG_Statistics = true)有效 */
	public static int Count = 0;
	
	/** 开启服务端统计 */
	public static boolean FLAG_Statistics = true;
	/**
	 * HTML标签白名单
	 */
	public static Map<Tag, ArrayList<Attribute>> whites =null;
	
	/**
	 * 默认采用接口作者
	 */
	public static String Author = "";
	
	/**
	 * 默认采用访问方式
	 */
	public static String Method = "";
	
	/**
	 * 是否开启Action
	 */
	public static boolean OpenAction = true;
	
	/**
	 * 程序所在目录
	 */
	public static String PackagePath;
	
	public static String Web_Path;
	/**
	 * 服务器URL
	 */
	public static String URL = "";
	/**
	 * 包名
	 */
	public static String PackageName;
	/**
	 * 项目名
	 */
	public static String ProjectName;
	/**
	 * action 扩展名
	 */
	public static String Extension;
	/**
	 * 是否开启Sms
	 */
	public static boolean Sms = false;
	/**
	 * 短信通道帐号
	 */
	public static String Sms_UserName = "";
	/**
	 * 短信通道密码
	 */
	public static String Sms_Password = "";
	/**
	 * 短信通道密钥
	 */
	public static String Sms_Key = "";
	/**
	 * 项目配置
	 */
	public static String ProjectConfig = null;
	/**
	 * 项目配置类
	 */
	public static BaseConfig Config_ = null;
	/**
	 * 用户组
	 */
	public static Map<String, String> User_Group = new HashMap<String,String>();
	
	/**
	 * 系统邮箱
	 */
	public static String System_Email = null;
	/**
	 * 注册的Action,用检索权限ID
	 */
	public static Map<String, Integer> Map_Actions = new HashMap<String,Integer>();
	/**
	 * 系统注册的Action
	 */
	public static Action[] Actions = null;
	/**
	 * Action分组信息
	 */
	public static String[] Action_Groups = null;
	/**
	 * 平台编号及密钥
	 */
	public static Map<String, String> KeyMap;
	
	/**
	 * 是否开启Wiki
	 */
	public static boolean OpenWiki = true;
	/***
	 * 是否打开Session功能
	 */
	public static boolean OpenSession = true;

	/***
	 * 是否打开短信接口
	 */
	public static boolean OpenTelSession = true;
	/***
	 * 如未打开短信功能该值只会为空!
	 */
	public static NCTelSession TelSession;
	/***
	 * 验证码间隔发送时间
	 */
	public static int TelSend = 60;
	/***
	 * 图片验证码保持时间
	 */
	public static long ImageOverTime = 600000;
	
	/***
	 * 开启调试日记(不输出错误日记)
	 */
	public static boolean Log_Flag = true;
	
	/**
	 * 记录目前服务器Session文件信息
	 */
	public static Map<String, String> Session_Files = new HashMap<String,String>();
	
	/**
	 * 信任服务器列表
	 */
	public static JSONArray TrustServer = new JSONArray();
	
	
	
	// 请求协议头(为支持跨域请求)
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALL_HEADER = "Access-Control-Allow-Headers";
	public static final String FLAG = "NightCat_LQW";
    
    
	
	/***
	 * 获取字符串类型配置
	 * @param key 键名
	 * @param value 如果获取失败则返回该值!
	 * @return
	 */
	public static String getStringValue(String key,String value)
	{
		
		if(null == MY_Resource || !MY_Resource.containsKey(key))
			return value;
		try {
			return MY_Resource.getString(key).trim();
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
		return value;
	}
	/***
	 * 获取长整型类型配置
	 * @param key 键名
	 * @param value 如果获取失败则返回该值!
	 * @return
	 */
	public static Long getlongValue(String key,Long value)
	{
		
		if(null == MY_Resource || !MY_Resource.containsKey(key))
			return value;
		try {
			return Long.parseLong(MY_Resource.getString(key).trim());
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
		return value;
	}
	/***
	 * 获取布尔型类型配置
	 * @param key 键名
	 * @param value 如果获取失败则返回该值!
	 * @return
	 */
	public static boolean getBooleanValue(String key,Boolean value)
	{
		
		if(null == MY_Resource || !MY_Resource.containsKey(key))
			return value;
		try {
			return Boolean.parseBoolean(MY_Resource.getString(key).trim());
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
		return value;
	}
	/***
	 * 获取布尔型类型配置
	 * @param key 键名
	 * @param value 如果获取失败则返回该值!
	 * @return
	 */
	public static int getIntValue(String key,int value)
	{
		
		if(null == MY_Resource || !MY_Resource.containsKey(key))
			return value;
		try {
			return Integer.parseInt(MY_Resource.getString(key).trim());
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
		return value;
	}
	/**
	 * 初始化操作
	 */
	public static final void Init()
	{
		LogUtil.getInstance().debug("Begin Init this WebService API");
		NCConfig.PackagePath = NCConfig.class.getResource("//").getPath().replace("%20", " ");
		MY_Resource = ResourceBundle.getBundle("NightCat");
		try {
			InitKeyValue();
			if(NCConfig.OpenData)
				NCConfig.data = DatabaseConfig.Init();
			NCSession.getInstance();
			NCTelSession.Init();
			InitConfig();
			LogUtil.getInstance().debug("This WebService API init has been completed!");
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getInstance().error("Can't init this WebService API.Tomcat will be closed!");
			NCException.printStackTrace(e,false);
			System.exit(0);
		}
	}
	public static Interface_Session Inter_Session = new Interface_Session() {
		
		@Override
		public void SessionDestroy(String id, String Session, String uid) {
			// TODO Auto-generated method stub
			NCConfig.Config_.SessionDestroy(id, Session, uid);
			NCConfig.Session_Files.remove(Session);
			if(NCConfig.Log_Flag)
				LogUtil.getInstance().debug("Session["+Session+"] on User["+uid+"] at Head["+id+"] is Destroy");
		}
		
		@Override
		public void SessionCreate(String id, String Session, String uid) {
			// TODO Auto-generated method stub
			NCConfig.Config_.SessionCreate(id, Session, uid);
			if(NCConfig.Log_Flag)
				LogUtil.getInstance().debug("Session["+Session+"] on User["+uid+"] at Head["+id+"] is Create");
		}
	};
	/***
	 * 获取一个SQL连接池内的链接
	 * @return
	 */
	public static Connection getConnection()
	{
		if(!NCConfig.OpenData || null == data)
			return null;
		return data.getConnection();
	}
	/**
	 * 加载系统配置
	 * @throws Exception
	 */
	private static void InitKeyValue() throws Exception{
		// TODO Auto-generated method stub
		NCConfig.GuestGroup = getStringValue("Nightcat.GuestGroup", "");
		NCConfig.Author = getStringValue("Nightcat.Author", "傅承灿");
		NCConfig.Method = getStringValue("Nightcat.Method", "POST");
		NCConfig.OpenAction = getBooleanValue("Nightcat.OpenAction", true);
		NCConfig.FLAG_Statistics = getBooleanValue("Nightcat.Statistics", true);
		NCConfig.OpenWiki = getBooleanValue("Nightcat.Wiki", true);
		NCConfig.OpenSession = getBooleanValue("Nightcat.Session", true);
		NCConfig.OpenTelSession = getBooleanValue("Nightcat.Tel", true);
		NCConfig.TelSend = getIntValue("Nightcat.TelSend", 60);
		NCConfig.PackageName = NCConfig.getStringValue("Nightcat.PackageName", "cn.YMTeam.Action");
		NCConfig.ProjectName = NCConfig.getStringValue("Nightcat.ProjectName", "");
		NCConfig.ImageOverTime = NCConfig.getlongValue("Nightcat.ImageCodeOverTime", (long)10) * 60000;
		NCConfig.Extension = NCConfig.getStringValue("Nightcat.Extension", ".action");
		NCConfig.Sms = NCConfig.getBooleanValue("Nightcat.Sms", false);
		NCConfig.Sms_UserName = NCConfig.getStringValue("Nightcat.Sms.UserName", "");
		NCConfig.Sms_Password = NCConfig.getStringValue("Nightcat.Sms.Password", "");
		NCConfig.Sms_Key = NCConfig.getStringValue("Nightcat.Sms.Key", "");
		NCConfig.ProjectConfig = NCConfig.getStringValue("Nightcat.Config", null);
		NCConfig.URL = NCConfig.getStringValue("Nightcat.URL", "http://api2.ymteam.cn/");
		NCConfig.System_Email = NCConfig.getStringValue("Nightcat.SYSTEM_Email", null);
		try { 
			NCConfig.TrustServer = JSONArray.fromObject(NCConfig.getStringValue("Nightcat.TrustServer", "[]"));
			NCConfig.WhiteSites = JSONArray.fromObject(getStringValue("Nightcat.WhiteSites", "[]"));
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e, false);
			LogUtil.getInstance().error("Can't get TrustServer List!");
		}
	}
	/**
	 * 初始化项目Config
	 * @throws Exception
	 */
	private static void InitConfig() throws Exception {
		// TODO Auto-generated method stub
		NCConfig.Config_ = (BaseConfig)Class.forName(NCConfig.ProjectConfig).newInstance();
		whites = NCConfig.Config_.Init();
		if(whites == null)
			throw new Exception("html whites is null!");
		if(NCConfig.OpenAction){
			NCConfig.Action_Groups = NCConfig.Config_.ActionGroup();
			NCConfig.Actions = NCConfig.Config_.Actions();
			if(null == NCConfig.Action_Groups || null == NCConfig.Actions)
				throw new Exception("Action reg error!");
			for (int i = 0; i < Actions.length; i++) 
				NCConfig.Map_Actions.put(Actions[i].toString(), i);
		}else{
			NCConfig.Action_Groups = new String[]{ };
			NCConfig.Actions = new Action[]{ };
			NCConfig.Map_Actions = new HashMap<String,Integer>();
		}
	}
	/**
	 * 获取所有Action信息
	 * @return
	 */
	public static JSONArray getAllAction(){
		JSONArray result = new JSONArray();
		for (Action action : NCConfig.Actions) {
			JSONObject item = new JSONObject();
			item.put("Name", action.getActionName());
			item.put("Action", action.getAction());
			item.put("GroupID", action.getGroupID()+"");
			item.put("Url", action.getUrl());
			result.add(item);
		}
		return result;
	}
	/**
	 * 获取所有Action分组
	 * @return
	 */
	public static JSONArray getAllGroup(){
		JSONArray result = new JSONArray();
		for(int i = 0;i<NCConfig.Action_Groups.length ; i++){
			JSONObject item = new JSONObject();
			item.put("Name", NCConfig.Action_Groups[i]);
			item.put("ID", i + "");
			result.add(item);
		}
		return result;
	}
	

}
