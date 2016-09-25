package cn.NightCat.Dao;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.NightCat.Exception.NCException;
import cn.NightCat.Util.LogUtil;

/*
	Create by Crazyist at 2016年2月21日 下午4:50:07 Filename:BaseObject.java
	CopyRight © 2014-2016 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public abstract class BaseObject {
	/** 当参数被改动后应该保存至该Data中 */
	protected Map<String, Object> data = new HashMap<String,Object>();
	
	public final Map<String, Object> getUpdate(){
		return this.data;
	}
	
	
	
	/**
	 * 修改变量
	 * @param key
	 * @param value
	 */
	public void change(String key,String value){
		key = key.toLowerCase();
		Method[] methods = this.getClass().getMethods();
		for (Method method : methods) {
			if(method.isAccessible())
				continue;
			try {
				if(method.getName().toLowerCase().equals("set"+key)){
					String Type_Name = method.getParameterTypes()[0].getSimpleName().toLowerCase();
					if(Type_Name.equals("string")){
						method.invoke(this, value);
					}else if(Type_Name.equals("int") || Type_Name.equals("integer")){
						method.invoke(this, Integer.parseInt(value));
					}else if(Type_Name.equals("double")){
						method.invoke(this, Double.parseDouble(value));
					}else if(Type_Name.equals("boolean") || Type_Name.equals("bool")){
						method.invoke(this, Boolean.parseBoolean(value));
					}else{
						LogUtil.getInstance().error("Unknown Type["+Type_Name+"] Field["+key+"]");
						continue;
					}
					this.data.put(key, value);
				}
			} catch (Exception e) {
				// TODO: handle exception
				NCException.printStackTrace(e, false);
			}
		}
	}
	
	/**
	 * 将此对象在数据库中删除
	 * @return
	 */
	public abstract boolean delete();
	
	/**
	 * 将此对象在数据库中保存
	 * @param dao 表操作类对象
	 * @return
	 */
	public abstract boolean save();
	
	
}
