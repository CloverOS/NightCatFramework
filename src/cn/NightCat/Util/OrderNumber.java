package cn.NightCat.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
	Create by Crazyist at 2015年12月13日 下午10:48:00 Filename:OrderNumber.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
/**
 * 理论上1毫秒可生产13^10个不重复订单编号(根据访问者访问顺序生成)
 * @author fuchengcan
 */
public class OrderNumber {
	private static OrderNumber o = null;
	private String nowTime = "";
	private int int_Count = 0;
	private String str_Count = "0";
	private SimpleDateFormat sm = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private OrderNumber(){
		
	}
	/**
	 * 获取一个OrderNumber单例
	 * @return
	 */
	public static OrderNumber getInstace(){
		if(null == o)
			o = new OrderNumber();
		return o;
	}
	/**
	 * 按访问顺序获取一个30位订单编号 理论上 1毫秒可以生成 13^10 个不重复订单
	 * @return
	 */
	public synchronized String getOrderNumber(){
		Date now = new Date();
		String temp_order = sm.format(now);
		if(temp_order.equals(nowTime))
			int_Count++;
		else{
			int_Count = 0;
			nowTime = temp_order;
		}
		str_Count = int_Count + "";
		temp_order += "0000000000000".substring(0, 13 - str_Count.length()) + str_Count;
		return temp_order;
	}
}
