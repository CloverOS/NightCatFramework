package cn.NightCat.Net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.NightCat.Exception.NCException;
import cn.NightCat.Util.LogUtil;

/*
	Create by Crazyist at 2015年8月24日 下午2:21:40 Filename:UDPHelper.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class UDPHelper {
	// 用于接收或发送数据的Socket
	private DatagramSocket socket;
	// 服务运行状态
	private boolean Run_Flag = false;
	// 当前服务端发送状态 true 处于正在发送
	private boolean Send_Flag = false;
	// 接收处理线程
	private Thread UDP_thread;
	// 服务器开启状态
	private boolean Run = false;
	// 处理UDP错误或接收接口
	private ReceiveInterface receive = null;
	// 消息队列
	private ArrayList<DataPacket> SendDatas = null;
	// 监听消息队列时钟
	private Timer sendTimer = null;
	// 最大接收字节数 默认为接收 4K数据
	private int MaxReceiveBuffSize = 1024 * 4;
	
	private UDPHelper(String port,String ip) throws Exception
	{
		socket = new DatagramSocket(Integer.parseInt(port), InetAddress.getByName(ip));
		UDP_thread = new Thread(udpRunnable);
		SendDatas = new ArrayList<DataPacket>();
		sendTimer = new Timer();
	}
	private UDPHelper() throws Exception
	{
		socket = new DatagramSocket(1314, InetAddress.getByName("0.0.0.0"));
		UDP_thread = new Thread(udpRunnable);
		SendDatas = new ArrayList<DataPacket>();
		sendTimer = new Timer();
	}
	private TimerTask timerTask = new TimerTask() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 如果当前队列未启动。 且有未发送的数据将会启动发送队列
			if(!Send_Flag && SendDatas.size() != 0)
				StartSendQueue();
		}
	};
	// 发送队列
	private synchronized void StartSendQueue(){
		Send_Flag = true;
		if(SendDatas.size() == 0 || null == socket)
			return;
		for (int i = 0; i < SendDatas.size(); i++) {
			try {
				DataPacket sendPack = SendDatas.get(i);
				if(sendPack.isRetry() && ((System.currentTimeMillis() - sendPack.getSendTime()) < 2000))
					continue;
				SendDatas.remove(i);
				i--;
				if(sendPack.isRetry() && sendPack.getRecount() > 3){
						LogUtil.getInstance().error("Send to ["+sendPack.getSendIP()+":"+sendPack.getSendPort()+"] Data:"+ new String(sendPack.getBuff()));
					continue;
				}
				socket.send(sendPack.getSendPack());
				if(sendPack.isRetry() && sendPack.getRecount() <=3)
					SendDatas.add(sendPack);
			} catch (Exception e) {
				// TODO: handle exception
				NCException.printStackTrace(e,false);
				
			}
		}
		Send_Flag = false;
	}
	/***
	 * 初始化一个UDPhelper（默认监听本机所有IP地址端口为1314）
	 * @return UDPHelper实例 如果初始化失败将返回NULL
	 */
	public static UDPHelper Init()
	{
		UDPHelper udp = null;
		try {
			udp = new UDPHelper();
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
		return udp;
	}
	/***
	 * 初始化一个UDPhelper
	 * @param port 端口
	 * @param ip 绑定 0.0.0.0 代表本机所有IP地址
	 * @return UDPHelper实例 如果初始化失败将返回NULL
	 */
	public static UDPHelper Init(String port,String ip)
	{
		UDPHelper udp = null;
		try {
			udp = new UDPHelper(port, ip);
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
		return udp;
	}
	private class DealWithProcess extends Thread{
		private DatagramPacket packet;
		public DealWithProcess(DatagramPacket packet) {
			// TODO Auto-generated constructor stub
			this.packet = packet;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!Run){
				Run_Flag = false;
				LogUtil.getInstance().debug("UDP Service is stopping!");
				return;
			}
			try {
				String returnStr = "";
				String ip = "";
				int port = 0;
				String revStr = "";
				int revLen = 0;
				revLen = packet.getLength();
				revStr = new String(packet.getData(), 0, revLen, "utf-8").trim();
				String[] str_data = revStr.split("\n");
				if(str_data.length > 1){
					revStr = str_data[1];
					delPack(str_data[0]);
				}
				ip = packet.getAddress().getHostAddress();
				port = packet.getPort();
				if(receive != null)
					returnStr = receive.processMsg(ip, port + "", revStr);
				if(null != returnStr && !returnStr.equals(""))
					sendMessage(ip, port, returnStr.getBytes(), false);
			} catch (Exception e) {
				// TODO: handle exception
				NCException.printStackTrace(e,false);
				if(!Run){
					Run_Flag = false;
					LogUtil.getInstance().debug("UDP Service is stopping!");
					return;
				}
				if(null != receive)
					receive.ServiceError("UDP Service error,Error Message:" + e.getMessage());
			}
		}
		
	}
	private Runnable udpRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Run_Flag = true;
			while(Run)
			{
				byte[] buff = new byte[MaxReceiveBuffSize];
				DatagramPacket revPacket = new DatagramPacket(buff, buff.length);
				try {
					socket.receive(revPacket);
					// 收到数据启动线程处理
					new DealWithProcess(revPacket)
						.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if(!Run){
						Run_Flag = false;
						LogUtil.getInstance().debug("UDP Service is stopping!");
						return;
					}
					NCException.printStackTrace(e,false);
					if(null != receive)
						receive.ServiceError("UDP Service error,Error Message:" + e.getMessage());
				}
			}
			Run_Flag = false;
		}
	};

	/***
	 * 获取当前UDP运行状态
	 * @return True 运行中 False 已停止运行
	 */
	public boolean getRunFlag()
	{
		return this.Run_Flag;
	}

	/**
	 * 设置UDP回调
	 * @param receive
	 */
	public void setReceive(ReceiveInterface receive)
	{
		this.receive = receive;
	}
	
	/**
	 * 启动UDP Service
	 */
	public void startService()
	{
		if(Run)
			return;
		if(socket == null || Run_Flag)
			return;
		Run = true;
		UDP_thread.start();
		sendTimer.schedule(timerTask, 0, 500);
	}
	
	/**
	 * 发送数据
	 * @param ip 远程ip地址(可以是域名)
	 * @param port 远程端口号
	 * @param data 发送的数据 
	 * @param retry true:客户没回复将会启动重发机制 false:不启用重发机制
	 * @return 如果发送成功 返回True,否则返回False(不管有没有发送成功只要发出去都会返回True)
	 * @throws UnknownHostException 
	 */
	public boolean sendMessage(String ip,int port,byte[] data,boolean retry){
		if(!Run_Flag || null == socket)
			return false;
		SendDatas.add(new DataPacket(data, ip, port, retry));
		return true;
	}
	/**
	 * 关闭服务
	 */
	public void stopService(){
		if(!Run)
			return;
		Run = false;
		try {
			if(null != socket && !socket.isClosed())
				socket.close();
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
		sendTimer.cancel();
	}

	// 删除队列中客户端已回复的队列消息
	private synchronized void delPack(String flag){
		for (int i = 0; i < SendDatas.size(); i++) {
			if(SendDatas.get(i).sendKey.equals(flag)){
				SendDatas.remove(i);
				i--;
			}
		}
	}
}
