package cn.NightCat.Base;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

/*
	Create by Crazyist at 2015年10月9日 下午9:48:52 Filename:BaseConfig
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public abstract class BaseConfig {
	public static final String TAG = "BaseConfig.json";
	/**
	 * 配置基类,如果项目有配置配置。必须继承该类并重写Init。 该类将会在应用启动时被调用
	 * 并且该类必须返回白名单HTML标签不允许返回NULL 否则将会初始化失败。
	 */
	public abstract Map<Tag, ArrayList<Attribute>> Init();
	
	
	/**
	 * 注册Action分类
	 * @return 
	 */
	public abstract String[] ActionGroup();
	/**
	 * 注册Action
	 * @return
	 */
	public abstract Action[] Actions();
	/**
	 * Session被销毁时调用
	 * @param id 平台ID
	 * @param Session 密钥
	 * @param uid 用户ID
	 */
	public abstract void SessionDestroy(String id, String Session, String uid);
	/**
	 * Session被创建时调用
	 * @param id 平台ID
	 * @param Session 密钥
	 * @param uid 用户ID
	 */
	public abstract void SessionCreate(String id, String Session, String uid);
	
	/**
	 * 项目即将销毁
	 */
	public void ServiceDestroyed(){ }
}
