package cn.NightCat.Session;

/*
	Create by Crazyist at 2015年9月24日 下午7:21:00 Filename:Interface_Session
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public interface Interface_Session {
	
	/**
	 * Sesison销毁时调用
	 * @param id 平台 Guest 为游客
	 * @param Session 
	 * @param uid
	 */
	public void SessionDestroy(String id,String Session,String uid);
	public void SessionCreate(String id,String Session,String uid);
}
