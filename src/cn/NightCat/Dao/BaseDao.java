package cn.NightCat.Dao;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.NightCat.Exception.NCException;
import cn.NightCat.Util.LogUtil;
import cn.NightCat.Util.SQLUtil;

/*
	Create by Crazyist at 2016年2月21日 下午2:41:30 Filename:BaseDao.java
	CopyRight © 2014-2016 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
/**
 * 数据库基类
 * @author 傅承灿
 *
 * @param <T> 数据基类 必须继承 BaseObject
 * @param <A> 操作基类 必须继承 BaseDao
 */
public abstract class BaseDao<T>{
	
	private Class<T> entityClass;
	private Map<String, Method> Methods = new HashMap<String,Method>();
	
	@SuppressWarnings("unchecked")
	protected BaseDao(){
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
		entityClass =  (Class<T>)params[0];
		Method[] methods = entityClass.getMethods();
		for (Method method : methods) {
			if(method.getName().startsWith("set"))
				Methods.put(method.getName(), method);
			
		}
	}
	
	/**
	 * 往数据库内插入一条记录
	 * @param obj 对象
	 * @return
	 		插入成功返回true 否则返回false
	 */
	public boolean insert(T obj){
		Map<String, Object> data = new HashMap<String,Object>();
		Method[] methods = obj.getClass().getMethods();
		String table = obj.getClass().getSimpleName().trim();
		for (Method method : methods) {
			String name = method.getName();
			if(!method.getDeclaringClass().getSimpleName().equals(obj.getClass().getSimpleName()))
				continue;
			if(!name.startsWith("get"))
				continue;
			try {
				data.put(name.replaceFirst("get", ""), method.invoke(obj));
			} catch (Exception e) {
				// TODO: handle exception
				LogUtil.getInstance().error("Cant invoke["+method.getName()+"] on ["+obj.getClass().getName()+"]");
			}
		}
		if(data.size() < 1)
			return false;
		if(SQLUtil.Insert(table, data) > 0)
			return true;
		else
			return false;
	}
	
	
	/**
	 * 获取指定对象
	 * @param where 条件
	 * @return
	 		自动生成一个对象
	 */
	public T get(String where){
		T obj = null;
		ResultSet rs = null;
		try {
			rs = SQLUtil.getResultSet("select * from `"+entityClass.getSimpleName()+"` where "+where+" limit 1");
			if(rs.next()){
				obj = entityClass.newInstance();
				ResultSetMetaData rsmd = rs.getMetaData();
				int num = rsmd.getColumnCount();
				for(int i = 1; i <= num; i++){
					String fieldName = rsmd.getColumnName(i);
					Method method = Methods.get("set" + fieldName);
					if(null == method)
						continue;
					Object value = rs.getObject(fieldName);
					if(null == value)
						continue;
					String paramType = method.getParameterTypes()[0].getSimpleName();
					if(paramType.equals("Integer") || paramType.equals("int"))
						method.invoke(obj, Integer.parseInt(value.toString()));
					else if(paramType.equals("String"))
						method.invoke(obj, value.toString());
					else if(paramType.equals("Double") || paramType.equals("double"))
						method.invoke(obj, Double.parseDouble(value.toString()));
					else if(paramType.equals("Boolean"))
						method.invoke(obj, Boolean.parseBoolean(value.toString()));
					else
						System.err.println("unknown Type [" + paramType +"]");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			NCException.printStackTrace(e, false);
		}finally {
			SQLUtil.safeClose(rs);
		}
		return obj;
	};
	
	
	public ArrayList<T> gets(String where){
		ArrayList<T> arrayList = new ArrayList<T>();
		ResultSet rs = null;
		try {
			rs = SQLUtil.getResultSet("select * from `"+entityClass.getSimpleName()+"` where "+where);
			while(rs.next()){
				T obj =  entityClass.newInstance();
				ResultSetMetaData rsmd = rs.getMetaData();
				int num = rsmd.getColumnCount();
				for(int i = 1; i <= num; i++){
					String fieldName = rsmd.getColumnName(i);
					Method method = Methods.get("set" + fieldName);
					if(null == method)
						continue;
					Object value = rs.getObject(fieldName);
					if(null == value)
						continue;
					String paramType = method.getParameterTypes()[0].getSimpleName();
					if(paramType.equals("Integer") || paramType.equals("int"))
						method.invoke(obj, Integer.parseInt(value.toString()));
					else if(paramType.equals("String"))
						method.invoke(obj, value.toString());
					else if(paramType.equals("Double") || paramType.equals("double"))
						method.invoke(obj, Double.parseDouble(value.toString()));
					else if(paramType.equals("Boolean"))
						method.invoke(obj, Boolean.parseBoolean(value.toString()));
					else
						System.err.println("unknown Type [" + paramType +"]");
				}
				arrayList.add(obj);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			NCException.printStackTrace(e, false);
		}finally {
			SQLUtil.safeClose(rs);
		}
		return arrayList;
	};
}
