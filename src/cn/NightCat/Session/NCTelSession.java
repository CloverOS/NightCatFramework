package cn.NightCat.Session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import cn.NightCat.Config.NCConfig;
import cn.NightCat.Exception.NCException;
import cn.NightCat.Util.LogUtil;

/*
	Create by Crazyist at 2015年8月16日 上午11:56:42 Filename:NCTelSession
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class NCTelSession {
	public static final String TAG = "NCTelSession.json";
	private static File TelSessionFile;
	private static Timer timer;
	private NCTelSession()
	{
		TelSessionFile = new File(NCConfig.PackagePath + "TelVerify");
		if(!TelSessionFile.exists())
			TelSessionFile.mkdirs();
		timer = new Timer();
		timer.scheduleAtFixedRate(task, 60000, 60000);
	}
	private TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				File[] files = TelSessionFile.listFiles();
				for (File file : files) {
					if(System.currentTimeMillis() - file.lastModified() > 1800000)
						file.delete();
				}
			} catch (Exception e) {
				// TODO: handle exception
				NCException.printStackTrace(e,false);
			}
		}
	};
	public static NCTelSession Init()
	{
		if(!NCConfig.OpenTelSession)
			return null;
		if(null == NCConfig.TelSession)
		{
			NCConfig.TelSession = new NCTelSession();
			LogUtil.getInstance().info("TelSession Init() is ok!");
		}
		return NCConfig.TelSession;
	}
	/***
	 * 发送短信验证码
	 * @param tel 手机号码
	 * @return 0表示已发送,该返回值 代表 剩下多少秒后才能继续发送
	 */
	public static int sendCode(String tel,String code)
	{
		File file = new File(TelSessionFile.getPath().replaceAll("%20", " ") + "/" + tel + ".verify");
		if(file.exists())
		{
			int time = (int) ((System.currentTimeMillis() - file.lastModified()) / 1000);
			if(time < NCConfig.TelSend)
				return NCConfig.TelSend - time;
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			fos.write(code.getBytes());
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			NCException.printStackTrace(e,false);
			return -1;
		}
		return 0;
	}
	/***
	 * 验证验证码是否正确
	 * @param tel 手机号码
	 * @param code 验证码
	 * @return -2验证时间小于1S -1 表示手机并未获取过验证码 0 验证码错误 1验证码正确
	 */
	public static int VerifyTel(String tel,String code)
	{
		try {
			File file = new File(TelSessionFile.getPath().replaceAll("%20", " ") + "/" + tel + ".verify");
			if(!file.exists())
				return -1;
			long last_Time = file.lastModified();
			if(System.currentTimeMillis() - last_Time < 1000)
				return -2;
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int buff = 0;
			while((buff = fis.read()) != -1)
			{
				bos.write(buff);
			}
			String codeString = bos.toString().trim();
			bos.close();
			fis.close();
			file.setLastModified(last_Time);
			if(codeString.equals(code.trim()))
				return 1;
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
			return -1;
		}
		return 0;
	}
	/**
	 * 删除验证码
	 * @param tel 手机号码
	 */
	public static boolean DelSmsCode(String tel){
		File file = new File(TelSessionFile.getPath().replaceAll("%20", " ") + "/" + tel + ".verify");
		if(!file.exists())
			return false;
		file.delete();
		return true;
	}
}
