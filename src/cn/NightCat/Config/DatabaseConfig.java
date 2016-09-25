package cn.NightCat.Config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import cn.NightCat.Exception.NCException;
import cn.NightCat.Util.LogUtil;
import cn.NightCat.Util.SQLUtil;

/*
	Create by Crazyist at 2015年8月14日 下午9:18:59 Filename:DatabaseConfig
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class DatabaseConfig {
	private static BasicDataSource dataSource;
	// 数据库账号
	private static String UserName = "";
	// 数据库密码
	private static String Password = "";
	// 驱动名
	private static String driverClassName = "";
	// 连接名
	private static String Url = "";
	// 初始化连接数量
	private static String initialSize = "";
	// 最小空闲连接
	private static String minIdle = "";
	// 最大空闲连接
	private static String maxIdle = "";
	// 最大超时等待时间
	private static String maxWait = "";
	// 最大连接
	private static String maxActive = "";
	// 是否在自动回收超时连接的时候打印连接的超时错误 
	private static boolean logAbandoned = false;
	// 是否自动回收超时连接
	private static boolean removeAbandoned = false;
	// 超时时间(以秒数为单位) 
	private static int removeAbandonedTimeout = 0;
	private static DatabaseConfig base = null;
	private DatabaseConfig() throws Exception
	{
		DatabaseConfig.driverClassName = NCConfig.getStringValue("jdbc.driverClassName", null);
		DatabaseConfig.Url = NCConfig.getStringValue("jdbc.url", null);
		DatabaseConfig.UserName = NCConfig.getStringValue("jdbc.username", null);
		DatabaseConfig.Password = NCConfig.getStringValue("jdbc.password", null);
		DatabaseConfig.initialSize = NCConfig.getStringValue("dataSource.initialSize", null);
		DatabaseConfig.minIdle = NCConfig.getStringValue("dataSource.minIdle", null);
		DatabaseConfig.maxIdle = NCConfig.getStringValue("dataSource.maxIdle", null);
		DatabaseConfig.maxWait = NCConfig.getStringValue("dataSource.maxWait", "");
		DatabaseConfig.maxActive = NCConfig.getStringValue("dataSource.maxActive", "");
		DatabaseConfig.logAbandoned = Boolean.parseBoolean(NCConfig.getStringValue("dataSource.logAbandoned", "false").trim());
		DatabaseConfig.removeAbandoned = Boolean.parseBoolean(NCConfig.getStringValue("dataSource.removeAbandoned", "false").trim());
		DatabaseConfig.removeAbandonedTimeout = Integer.parseInt(NCConfig.getStringValue("dataSource.removeAbandonedTimeout", "false").trim());
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName(DatabaseConfig.driverClassName);
		dataSource.setUrl(DatabaseConfig.Url);
		dataSource.setUsername(DatabaseConfig.UserName);
		dataSource.setPassword(DatabaseConfig.Password);
		if(null != DatabaseConfig.initialSize)
			dataSource.setInitialSize(Integer.parseInt(initialSize));
		if(null != DatabaseConfig.minIdle)
			dataSource.setMinIdle(Integer.parseInt(DatabaseConfig.minIdle));
		if(null != DatabaseConfig.maxIdle)
			dataSource.setMaxIdle(Integer.parseInt(DatabaseConfig.maxIdle));
		if(null != DatabaseConfig.maxWait)
			dataSource.setMaxWaitMillis(Long.parseLong(DatabaseConfig.maxWait));
		if(null != DatabaseConfig.maxActive)
			dataSource.setMaxTotal(Integer.parseInt(DatabaseConfig.maxActive));
		dataSource.setLogAbandoned(DatabaseConfig.logAbandoned);
		dataSource.setRemoveAbandonedOnBorrow(DatabaseConfig.removeAbandoned);
		dataSource.setRemoveAbandonedTimeout(DatabaseConfig.removeAbandonedTimeout);
		Connection conn = getConnection();
		if(null != conn){
			NCConfig.KeyMap = new HashMap<String,String>();
			ResultSet rs = SQLUtil.getResultSet(conn,"select * from `system_setting` where `key` like 'Platform_%'");
			while(rs.next())
				NCConfig.KeyMap.put(rs.getString("key"), rs.getString("value"));
			rs =SQLUtil.getResultSet(conn,"select * from NightCat_Group");
			while(rs.next()){
				NCConfig.User_Group.put(rs.getString("ID"), rs.getString("PowerFlag"));
				System.err.println(rs.getString("ID") +"   "+ rs.getString("PowerFlag"));
			}
			SQLUtil.safeClose(conn);
			LogUtil.getInstance().debug("Init Database is ok!");
		}
		else 
			throw new Exception("Init DataBase is error!");
	}
	
	/***
	 * 从数据库连接池获取一个连接
	 * @return 
	 */
	public Connection getConnection()
	{
		Connection conn = null;
		try {
			if(null == dataSource)
				return conn;
			conn = dataSource.getConnection();
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
		return conn;
	}
	
	/***
	 * 初始化数据库操作
	 */
	public static DatabaseConfig Init() throws Exception
	{
		if(null == base || null == dataSource)
			base = new DatabaseConfig();
		return base;
	}
	/**
	 * 关闭数据库连接池
	 */
	public static void CloseDatabase(){
		if(null == base || null == dataSource)
			return;
		try {
			dataSource.close();
			dataSource = null;
			base = null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			NCException.printStackTrace(e, false);
		}
	}
}
