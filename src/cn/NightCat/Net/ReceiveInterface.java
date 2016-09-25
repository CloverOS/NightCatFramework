package cn.NightCat.Net;

/*
	Create by Crazyist at 2015年8月24日 下午4:24:57 Filename:ReceiveInterface.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public interface ReceiveInterface {
	/***
	 * 收到来自服务端数据
	 * @param ip 客户端地址
	 * @param port 客户端端口号
	 * @param msg 内容
	 * @return 处理后的返回值 如果不返回直接返回 空字符串或 NULL
	 */
	public String processMsg(String ip,String port,String msg);
	
	/**
	 * UDP当出现错误将会回调该函数
	 * @param errro_msg
	 */
	public void ServiceError(String errro_msg);

	/**
	 * 发送失败时调用
	 * @param ip 远程端口IP地址
	 * @param port 远程端口端口号
	 * @param data 发送的数据
	 */
	public void SendError(String ip,int port,byte[] data);
}

