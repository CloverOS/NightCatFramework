package cn.NightCat.Base;

import cn.NightCat.Config.NCConfig;

/*
	Create by Crazyist at 2015年11月30日 上午12:21:46 Filename:Action.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class Action {
	private String ActionName = "";
	private String Url = "";
	private int GroupID = -1;
	private String Name = "";
	public Action(String name,String action,int groupid){
		this.ActionName = name;
		this.GroupID = groupid;
		this.Url = NCConfig.URL + NCConfig.PackageName.replace(".", "/")  + "/"+ action + NCConfig.Extension;
		this.Name = NCConfig.PackageName + "." + action;
	}
	public Action(String name,String action){
		this(name,action, -1);
	}
	/**
	 * 获取Action名称
	 * @return action名称
	 */
	public String getActionName(){
		return this.ActionName;
	}
	/**
	 * 获取Action 所属组ID
	 * @return 组ID
	 */
	public int getGroupID(){
		return this.GroupID;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.Name;
	}
	/**
	 * 获取Action的Url
	 * @return
	 */
	public String getUrl(){
		return this.Url;
	}
	public String getAction() {
		return this.Name;
	}
}
