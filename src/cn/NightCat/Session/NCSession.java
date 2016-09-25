package cn.NightCat.Session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.NightCat.Config.NCConfig;
import cn.NightCat.Exception.NCException;
import cn.NightCat.Util.LogUtil;
import cn.NightCat.Util.MD5Util;
import net.sf.json.JSONObject;
/*
	Create by Crazyist at 2015年8月15日 下午9:22:34 Filename:NCSession
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public final class NCSession {
	public static final String TAG = "NCSession.json";
	private static File SessionFile;
	private Timer timer;
	private static NCSession Session;
	private NCSession(){
		SessionFile = new File(NCConfig.PackagePath + "Session/");
		if(!SessionFile.exists())
			SessionFile.mkdirs();
		timer = new Timer();
		timer.schedule(task, 60000, 60000);
		try {
			File[] files = SessionFile.listFiles();
			for (File file : files) {
				String[] param = file.getName().replace(".session", "").split("_");
				if(param.length == 4)
					NCConfig.Session_Files.put(param[1], file.getPath().replace("%20", " "));
				else{
					file.delete();
					LogUtil.getInstance().debug("Session文件已被删除["+file.getPath()+"]");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
	}
	
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			checkSessionFile();
		}
	};
	private synchronized void checkSessionFile(){
		try {
			File[] files = SessionFile.listFiles();
			for (File file : files) {
				String[] param = file.getName().replace(".session", "").split("_");
				if(param.length != 4){
					file.delete();
					LogUtil.getInstance().debug("Session文件已被删除["+file.getPath()+"]");
					continue;
				}
				long overtime = Long.parseLong(param[3]);
				if((file.lastModified() + overtime) < System.currentTimeMillis())
				{
					file.delete();
					NCConfig.Session_Files.remove(param[1]);
					if(param.length == 4 && !param[0].equals("Guest"))
						NCConfig.Inter_Session.SessionDestroy(param[0], param[1],param[2]);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
	}
	
	/**
	 * 获取一个Session,注意本类为单例
	 * @return
	 */
	public static NCSession getInstance(){
		if(!NCConfig.OpenSession)
			return null;
		if(null == NCSession.Session)
		{
			LogUtil.getInstance().info("Session Init() is ok!");
			NCSession.Session = new NCSession();
		}
		return NCSession.Session;
	}
	/***
	 * 获取Session数据
	 * @param session 分配的Session号码
	 * @return
	 */
	public JSONObject getSession(String id,String session)
	{
		if(!NCConfig.Session_Files.containsKey(session))
			return null;
		JSONObject jsonObject = null;
		try {
			String fileName = NCConfig.Session_Files.get(session);
			File file = new File(fileName);
			
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int buff = 0;
			while((buff = fis.read()) != -1)
			{
				bos.write(buff);
			}
			file.setLastModified(System.currentTimeMillis());
			jsonObject = JSONObject.fromObject(bos.toString());
			bos.close();
			fis.close();
			return jsonObject;
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
		return jsonObject;
	}

	/**
	 * 重新设置Session内容
	 * @param id 平台ID
 	 * @param session session编号
	 * @param data 数据
	 */
	public void setSession(String id,String session,JSONObject data)
	{
		if(!NCConfig.Session_Files.containsKey(session))
			return;
		try {
			FileWriter writer = new FileWriter(NCConfig.Session_Files.get(session), false);
			writer.write(data.toString());
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
	}
		
	/***
	 * 创建游客 Session文件
	 * @return
	 */
	public synchronized String AddSession()
	{
		JSONObject body = new JSONObject();
		int keep = 10 * 60000;
		try {
			String session = MD5Util.MD5_Lower(MD5Util.getRandomString(32));
			while(NCConfig.Session_Files.containsKey(session))
				session = MD5Util.MD5(MD5Util.getRandomString(10));
			File file = new File(SessionFile.getPath().replace("%20", " ") + "/Guest_" + session + "_" + UUID.randomUUID().toString() + "_" + keep + ".session");
			NCConfig.Session_Files.put(session, file.getPath().replace("%20", " "));
			FileOutputStream fos = new FileOutputStream(file);
			body.put("Session", session);
			fos.write(body.toString().getBytes());
			fos.close();
			return session;
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,true);
			return null;
		}
	}
	
	/***
	 * 创建Session文件
	 * @param id ID编号
	 * @param body Session内容
	 * @param uid 建议用户ID
	 * @param groupid 用户组ID
	 * @param keep 保持时间(单位分钟)
	 * @return
	 */
	public synchronized String AddSession(String id,JSONObject body,String uid,String groupid,long keep)
	{
		
		if(null == body)
			return null;
		keep = keep * 60000;
		DeleteSessionByUID(id, uid);
		try {
			String session = MD5Util.MD5_Lower(MD5Util.getRandomString(32));
			while(NCConfig.Session_Files.containsKey(session))
				session = MD5Util.MD5(MD5Util.getRandomString(10));
			File file = new File(SessionFile.getPath().replace("%20", " ") + "/" + id + "_" + session + "_" + uid + "_" + keep + ".session");
			NCConfig.Session_Files.put(session, file.getPath().replace("%20", " "));
			FileOutputStream fos = new FileOutputStream(file);
			if(null != groupid)
				body.put("Power_Str", NCConfig.User_Group.get(groupid));
			body.put("Session", session);
			fos.write(body.toString().getBytes());
			fos.close();
			NCConfig.Inter_Session.SessionCreate(id, session, uid);
			return session;
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,true);
			return null;
		}
	}

	/***
	 * 删除Session文件
	 * @param id ID编号
	 * @param session 
	 */
	public void DeleteSession(String id,String session)
	{
		if(!NCConfig.Session_Files.containsKey(session))
			return;
		try {
			String fileName = NCConfig.Session_Files.get(session);
			File file = new File(fileName);
			String[] param = file.getName().replace(".session", "").split("_");
			file.delete();
			if(param.length == 4)
				NCConfig.Inter_Session.SessionDestroy(param[0], param[1],param[2]);
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
	}
	/***
	 * 删除所有平台指定UID 的 Session文件
	 * @param uid 
	 */
	public void DeleteSessionByUID(String uid)
	{
		try {
			File[] files = SessionFile.listFiles();
			for (File file : files) {
				if(file.getName().indexOf("_" + uid + "_") == -1)
					continue;
				String[] param = file.getName().replace(".session", "").split("_");
				file.delete();
				if(param.length == 4)
					NCConfig.Inter_Session.SessionDestroy(param[0], param[1],param[2]);
			}
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
	}
	/***
	 * 根据UID删除Session
	 * @param id ID编号
	 * @param uid UID编号
	 */
	public void DeleteSessionByUID(String id,String uid)
	{
		try {
			File[] files = SessionFile.listFiles();
			Pattern p = Pattern.compile("(?<=" + id + "\\_\\S{32}\\_)(" + uid + ")(?=\\_)");
			for (File file : files) {
				Matcher m = p.matcher(file.getPath().replaceAll("%20", " "));
				if(!m.find())
					continue;
				String[] param = file.getName().replace(".session", "").split("_");
				file.delete();
				if(param.length == 4)
					NCConfig.Inter_Session.SessionDestroy(param[0], param[1],param[2]);
			}
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e,false);
		}
	}
	/***
	 * Session 是否存在
	 * @param id 
	 * @param session session编码
	 * @return True:存在 False:不存在
	 */
	public boolean ExistsSession(String id,String session)
	{
		JSONObject jsonObject = getSession(id, session);
		if(jsonObject.size() == 0)
			return false;
		else
			return true;
	}
}
