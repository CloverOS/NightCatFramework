package cn.NightCat.Net;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import cn.NightCat.Exception.NCException;
import cn.NightCat.Util.MD5Util;

/*
	Create by Crazyist at 2015年8月24日 下午8:51:25 Filename:DataQueue.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
/**
 * 用于UDP数据发送
 * @author HexCode
 *
 */
public class DataPacket {
	private byte[] buff;
	private String sendIP = "";
	private int sendPort = 0;
	private boolean Retry = false;
	private int Recount = 0;
	private long SendTime = 0;
	public final String sendKey = MD5Util.getRandomString(30);
	/**
	 * 获取当前发送次数
	 * @return 发送次数
	 */
	public int getRecount() {
		return Recount;
	}
	/**
	 * 构造一个用于发送的数据包
	 * @param buff 将发送的字节数组
	 * @param sendip 远程IP地址
	 * @param sendport 远程端口号
	 * @param retry 失败是否尝试重新发送
	 */
	public DataPacket(byte[] buff,String sendip,int sendport,boolean retry){
		this.buff = buff;
		this.sendIP = sendip;
		this.sendPort = sendport;
		this.Retry = retry;
	}
	/**
	 * 获取发送字节数组
	 * @return 将发送的字节数组
	 */
	public byte[] getBuff() {
		return buff;
	}
	/**
	 * 获取远程IP
	 * @return 远程IP
	 */
	public String getSendIP() {
		return sendIP;
	}
	/**
	 * 获取远程端口号
	 * @return 远程端口号
	 */
	public int getSendPort() {
		return sendPort;
	}
	/**
	 * 发送失败是否尝试重新发送
	 * @return True 失败将尝试重新发送
	 */
	public boolean isRetry() {
		return Retry;
	}
	/***
	 * 设置发送的字节数组
	 * @param buff 字节数组
	 */
	public void setBuff(byte[] buff) {
		this.buff = buff;
	}
	/**
	 * 设置远程IP
	 * @param sendIP IP地址
	 */
	public void setSendIP(String sendIP) {
		this.sendIP = sendIP;
	}
	/**
	 * 设置远程端口号
	 * @param sendPort 远程端口号
	 */
	public void setSendPort(int sendPort) {
		this.sendPort = sendPort;
	}
	/**
	 * 获取上一次发送时间
	 * @return 上次发送时间可以用 System.currentTimeMillis() 获取时间与该时间进行比较
	 */
	public long getSendTime(){
		return SendTime;
	}
	/**
	 * 获取发送数据包
	 * @return 数据包 如果创建失败将会返回 null
	 * @throws UnknownHostException 如果IP地址错误或者端口号错误将会抛出该异常
	 */
	public DatagramPacket getSendPack() throws UnknownHostException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DatagramPacket temp_Pack = null;
		try {
			if(Retry)
				bos.write((sendKey + "\n").getBytes());
			bos.write(buff);
			byte[] temp_buff = bos.toByteArray();
			bos.close();
			temp_Pack = new DatagramPacket(temp_buff, temp_buff.length, InetAddress.getByName(sendIP), sendPort);
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
		SendTime = System.currentTimeMillis();
		Recount++;
		return temp_Pack;
	}
}
