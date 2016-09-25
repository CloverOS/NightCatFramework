package cn.NightCat.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.NightCat.Config.NCConfig;
import cn.NightCat.Exception.NCException;

public class SQLUtil {

  	/**
     * 获取一个 Statement
     * 该 Statement 已经设置数据集 可以滚动,可以更新
     * @return 如果获取失败将返回 null,调用时记得检查返回值
     */
    public static Statement getStatement()
    {
        Connection conn = NCConfig.getConnection();
        if (conn == null)
        {
            return null;
        }
        try
        {
            return conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        // 设置数据集可以滚动,可以更新
        } catch (SQLException ex)
        {
        	NCException.printStackTrace(ex,false);
        }
        return null;
    }

    /**
     * 获取一个 Statement
     * 该 Statement 已经设置数据集 可以滚动,可以更新
     * @param conn 数据库连接
     * @return 如果获取失败将返回 null,调用时记得检查返回值
     */
    public static Statement getStatement(Connection conn)
    {
        if (conn == null)
        {
            return null;
        }
        try
        {

            return conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        // 设置数据集可以滚动,可以更新
        } catch (SQLException ex)
        {
        	NCException.printStackTrace(ex,false);
            return null;
        }
    }

    /**
     * 获取一个带参数的 PreparedStatement
     * 该 PreparedStatement 已经设置数据集 可以滚动,可以更新
     * @param cmdText 需要 ? 参数的 SQL 语句
     * @param cmdParams SQL 语句的参数表
     * @return 如果获取失败将返回 null,调用时记得检查返回值
     */
    public static PreparedStatement getPreparedStatement(String cmdText, Object... cmdParams)
    {
        Connection conn = NCConfig.getConnection();;
        if (conn == null)
        {
            return null;
        }

        PreparedStatement pstmt = null;
        try
        {
            pstmt = conn.prepareStatement(cmdText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int i = 1;
            for (Object item : cmdParams)
            {
                pstmt.setObject(i, item);
                i++;
            }
        } catch (SQLException e)
        {
        	NCException.printStackTrace(cmdText,e,false);
        }
        return pstmt;
    }

    /**
     *  获取一个带参数的 PreparedStatement
     * 该 PreparedStatement 已经设置数据集 可以滚动,可以更新
     * @param conn 数据库连接
     * @param cmdText 需要 ? 参数的 SQL 语句
     * @param cmdParams SQL 语句的参数表
     * @return 如果获取失败将返回 null,调用时记得检查返回值
     */
    public static PreparedStatement getPreparedStatement(Connection conn, String cmdText, Object... cmdParams)
    {
        if (conn == null)
        {
            return null;
        }

        PreparedStatement pstmt = null;
        try
        {
            pstmt = conn.prepareStatement(cmdText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int i = 1;
            for (Object item : cmdParams)
            {
                pstmt.setObject(i, item);
                i++;
            }
        } catch (SQLException e)
        {
        	NCException.printStackTrace(cmdText,e,false);
        }
        return pstmt;
    }

    /**
     * 执行 SQL 语句,返回结果为整型
     * 主要用于执行非查询语句
     * @param cmdText SQL 语句
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
    public static int ExecSql(String cmdText)
    {
        Statement stmt = getStatement();
        if (stmt == null)
        {
            return -2;
        }
        int i;
        try
        {
            i = stmt.executeUpdate(cmdText);
        } catch (SQLException ex)
        {
        	NCException.printStackTrace(cmdText,ex,false);
            i = -1;
        }finally
        {
        	safeClose(stmt);
        }
        return i;
    }
    
    /**
     * 执行 SQL 语句,返回结果为整型
     * 主要用于执行非查询语句
     * @param cmdText SQL 语句
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
    public static int ExecSql(Statement stmt,String cmdText)
    {
        if (stmt == null)
        {
            return -2;
        }
        int i;
        try
        {
            i = stmt.executeUpdate(cmdText);
        } catch (SQLException ex)
        {
        	NCException.printStackTrace(cmdText,ex,false);
            i = -1;
        }
        return i;
    }

    /**
     * 返回一个 ResultSet
     * @param cmdText SQL 语句
     * @return
     */
    public static ResultSet getResultSet(String cmdText)
    {
        Statement stmt = getStatement();
        if (stmt == null)
        {
            return null;
        }
        try
        {
            return stmt.executeQuery(cmdText);
        } catch (SQLException ex)
        {
        	NCException.printStackTrace(cmdText,ex,false);
        }
        return null;
    }

    /**
     * 返回一个 ResultSet
     * @param conn
     * @param cmdText SQL 语句
     * @return
     */
    public static ResultSet getResultSet(Connection conn, String cmdText)
    {
        Statement stmt = getStatement(conn);
        if (stmt == null)
        {
            return null;
        }
        try
        {
            return stmt.executeQuery(cmdText);
        } catch (SQLException ex)
        {
        	NCException.printStackTrace(cmdText,ex,false);
        }
        return null;
    }

    /**
     * 返回一个 ResultSet
     * @param cmdText 需要 ? 参数的 SQL 语句
     * @param cmdParams SQL 语句的参数表
     * @return
     */
    public static ResultSet getResultSet(String cmdText, Object... cmdParams)
    {
        PreparedStatement pstmt = getPreparedStatement(cmdText, cmdParams);
        if (pstmt == null)
        {
            return null;
        }
        try
        {
            return pstmt.executeQuery();
        } catch (SQLException ex)
        {
        	NCException.printStackTrace(cmdText,ex,false);
        }
        return null;
    }

    /**
     * 返回一个 ResultSet
     * @param conn 数据库连接
     * @param cmdText 需要 ? 参数的 SQL 语句
     * @param cmdParams SQL 语句的参数表
     * @return
     */
    public static ResultSet getResultSet(Connection conn, String cmdText, Object... cmdParams)
    {
        PreparedStatement pstmt = getPreparedStatement(conn, cmdText, cmdParams);
        if (pstmt == null)
        {
            return null;
        }
        try
        {
            return pstmt.executeQuery();
        } catch (SQLException ex)
        {
        	NCException.printStackTrace(cmdText,ex,false);
        }
        return null;
    }  
    /**
     * 返回结果集的第一行的一列的值,其他忽略
     * @param cmdText SQL 语句
     * @return
     */
    public static String ExecScalar(String cmdText)
    {
        ResultSet rs = getResultSet(cmdText);
        Object obj = buildScalar(rs);
        if (null == obj)
        	return "";
        return obj.toString();
    }
    /**
     * 返回结果集的第一行的一列的值,其他忽略
     * @param conn 连接
     * @param cmdText SQL 语句
     * @return
     */
    public static String ExecScalar(Connection conn,String cmdText)
    {
        ResultSet rs = getResultSet(conn, cmdText);
        Object obj = buildScalar(rs);
        if (null == obj)
        	return "";
        return obj.toString();
    }
    public static Object buildScalar(ResultSet rs)
    {
        if (rs == null)
        {
            return null;
        }
        Object obj = null;
        try
        {
            if (rs.next())
            {
                obj = rs.getObject(1);
            }
        } catch (SQLException ex)
        {
        	NCException.printStackTrace(ex,false);
        }
        safeClose(rs);
        return obj;
    }
    
    /***
     * 判断是否存在数据
     * @param cmdtext
     * @return true 已存在 false 不存在
     */
    public static boolean ExistsRow(String cmdtext)
    {
    	String result = ExecScalar("select exists("+cmdtext+")");
    	if(result.trim().equals("1"))
    		return true;
    	else
    		return false;
    }

    /***
     * 向数据库内插入一条数据
     * @param table 表名
     * @param columnnames 列名数据组
     * @param values 值数组
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
    public static int Insert(String table,String[] columnnames,Object[] values)
    {
    	String columns = "";
    	String valuess = "";
    	if(columnnames.length < 1)
    		return -1;
    	for (int i = 0; i < columnnames.length; i++) {
    		if(null == columnnames[i] || null == values[i])
    			continue;
			columns += "`"+columnnames[i]+"`,";
			String classtype = values[i].getClass().getName();
			if(classtype.equals("java.lang.String"))
			{
				if(values[i].toString().equals("NULL"))
					valuess += ""+values[i].toString()+",";
				else
					valuess += "'"+values[i].toString()+"',";
			}else {
				valuess += ""+values[i].toString()+",";
			}
		}
    	columns = columns.substring(0, columns.length() - 1);
    	valuess = valuess.substring(0,valuess.length() - 1);
    	String cmd = "insert into `" + table + "`(" + columns + ") values(" + valuess + ")";
    	System.err.println(cmd);
    	return SQLUtil.ExecSql(cmd);
    }
    
    
    
    /***
     * 向数据库内插入一条数据
     * @param stmt 
     * @param table 表名
     * @param columnnames 列名数据组
     * @param values 值数组
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
    public static int Insert(Statement stmt,String table,String[] columnnames,Object[] values)
    {
    	String columns = "";
    	String valuess = "";
    	if(columnnames.length < 1)
    		return -1;
    	for (int i = 0; i < columnnames.length; i++) {
    		if(null == columnnames[i] || null == values[i])
    			continue;
			columns += "`"+columnnames[i]+"`,";
			String classtype = values[i].getClass().getName();
			if(classtype.equals("java.lang.String"))
			{
				if(values[i].toString().equals("NULL"))
					valuess += ""+values[i].toString()+",";
				else
					valuess += "'"+values[i].toString()+"',";
			}else {
				valuess += ""+values[i].toString()+",";
			}
		}
    	columns = columns.substring(0, columns.length() - 1);
    	valuess = valuess.substring(0,valuess.length() - 1);
    	String cmd = "insert into `" + table + "`(" + columns + ") values(" + valuess + ")";
    	System.err.println(cmd);
    	return SQLUtil.ExecSql(stmt, cmd);
    }
    
    /***
     * 像数据库内插入一条数据库 
     * @param table 表名
     * @param data map数据,键名为列名
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
    public static int Insert(String table,Map<String, Object> data)
    {
    	ArrayList<String> columns = new ArrayList<String>();
    	ArrayList<Object> values = new ArrayList<Object>();
    	
    	Iterator<Map.Entry<String, Object>> iter = data.entrySet().iterator();
    	while (iter.hasNext()) {
    		Map.Entry<String, Object> entry = iter.next();
    		columns.add(entry.getKey());
    		values.add(entry.getValue());
		}
    	String[] column_s = new String[columns.size()];
    	Object[] values_s = new Object[columns.size()];
    	for (int i = 0; i < columns.size(); i++) {
			column_s[i] = columns.get(i);
			values_s[i] = values.get(i);
		}
    	return Insert(table, column_s, values_s);
    }
    
    /***
     * 像数据库内插入一条数据库 
     * @param stmt
     * @param table 表名
     * @param data map数据,键名为列名
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
    public static int Insert(Statement stmt,String table,Map<String, Object> data)
    {
    	ArrayList<String> columns = new ArrayList<String>();
    	ArrayList<Object> values = new ArrayList<Object>();
    	
    	Iterator<Map.Entry<String, Object>> iter = data.entrySet().iterator();
    	while (iter.hasNext()) {
    		Map.Entry<String, Object> entry = iter.next();
    		columns.add(entry.getKey());
    		values.add(entry.getValue());
		}
    	String[] column_s = new String[columns.size()];
    	Object[] values_s = new Object[columns.size()];
    	for (int i = 0; i < columns.size(); i++) {
			column_s[i] = columns.get(i);
			values_s[i] = values.get(i);
		}
    	return Insert(stmt, table, column_s, values_s);
    }

    /***
     * 像数据库内更新数据
     * @param table 表名
     * @param columnnames 列名
     * @param values 值
     * @param where 条件
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
    public static int Update(String table,String[] columnnames,Object[] values,String where)
    {
    	String columns = "";
    	String valuess = "";
    	if(columnnames.length < 1)
    		return -1;
    	for (int i = 0; i < columnnames.length; i++) {
    		if(null == columnnames[i] || null == values[i])
    			continue;
			String classtype = values[i].getClass().getName();
			if(classtype.equals("java.lang.String"))
			{
				if(values[i].toString().equals("NULL"))
					valuess = "`" + columnnames[i] + "`="+values[i].toString();
				else
					valuess = "`" + columnnames[i] + "`='"+values[i].toString()+"'";
			}else {
				valuess = "`" + columnnames[i] + "`=" + values[i].toString();
			}
			columns += valuess + ",";
		}
    	columns = columns.substring(0, columns.length() - 1);
    	String cmd = "";
    	cmd = "update `" + table + "` set " + columns + " where " + where;
    	return SQLUtil.ExecSql(cmd);
    }
    
    
    /***
     * 像数据库内更新数据
     * @param stmt
     * @param table 表名
     * @param columnnames 列名
     * @param values 值
     * @param where 条件
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
    public static int Update(Statement stmt, String table,String[] columnnames,Object[] values,String where)
    {
    	String columns = "";
    	String valuess = "";
    	if(columnnames.length < 1)
    		return -1;
    	for (int i = 0; i < columnnames.length; i++) {
    		if(null == columnnames[i] || null == values[i])
    			continue;
			String classtype = values[i].getClass().getName();
			if(classtype.equals("java.lang.String"))
			{
				if(values[i].toString().equals("NULL"))
					valuess = "`" + columnnames[i] + "`="+values[i].toString();
				else
					valuess = "`" + columnnames[i] + "`='"+values[i].toString()+"'";
			}else {
				valuess = "`" + columnnames[i] + "`=" + values[i].toString();
			}
			columns += valuess + ",";
		}
    	columns = columns.substring(0, columns.length() - 1);
    	String cmd = "";
    	cmd = "update `" + table + "` set " + columns + " where " + where;
    	return SQLUtil.ExecSql(stmt,cmd);
    }
    
    
    /***
     * 向数据库内更新数据
     * @param table 表名
     * @param data 键值
     * @param where 条件
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
    public static int Update(String table,Map<String, Object> data,String where)
    {
    	ArrayList<String> columns = new ArrayList<String>();
    	ArrayList<Object> values = new ArrayList<Object>();
    	
    	Iterator<Map.Entry<String, Object>> iter = data.entrySet().iterator();
    	while (iter.hasNext()) {
    		Map.Entry<String, Object> entry = iter.next();
    		columns.add(entry.getKey());
    		values.add(entry.getValue());
		}
    	String[] column_s = new String[columns.size()];
    	Object[] values_s = new Object[columns.size()];
    	for (int i = 0; i < columns.size(); i++) {
			column_s[i] = columns.get(i);
			values_s[i] = values.get(i);
		}
    	return Update(table, column_s, values_s,where);
    }
    
    
    /***
     * 向数据库内更新数据
     * @param table 表名
     * @param data 键值
     * @param where 条件
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
    public static int Update(Statement stmt,String table,Map<String, Object> data,String where)
    {
    	ArrayList<String> columns = new ArrayList<String>();
    	ArrayList<Object> values = new ArrayList<Object>();
    	
    	Iterator<Map.Entry<String, Object>> iter = data.entrySet().iterator();
    	while (iter.hasNext()) {
    		Map.Entry<String, Object> entry = iter.next();
    		columns.add(entry.getKey());
    		values.add(entry.getValue());
		}
    	String[] column_s = new String[columns.size()];
    	Object[] values_s = new Object[columns.size()];
    	for (int i = 0; i < columns.size(); i++) {
			column_s[i] = columns.get(i);
			values_s[i] = values.get(i);
		}
    	return Update(stmt, table, column_s, values_s,where);
    }
    

    /***
     * 向数据库删除数据
     * @param table 表名
     * @param where 条件
     * @param param 值
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
	@SuppressWarnings("unused")
	public static int Delete(String table,String where,Object[] param)
    {
    	for (Object object : param) {
    		String classtype = object.getClass().getName();
			if(classtype.equals("java.lang.String"))
				where = where.replaceFirst("\\?", "'"+object.toString()+"'");
			else
				where = where.replaceFirst("\\?", ""+object.toString()+"");
		}
    	String cmd = "";
    	if(null == param)
    		cmd = "delete from `"+table+"`";
    	else
    		cmd = "delete from `"+table+"` where " + where;
    	System.err.println(cmd);
    	return SQLUtil.ExecSql(cmd);
    }
	
	/***
     * 向数据库删除数据
     * @param stmt
     * @param table 表名
     * @param where 条件
     * @param param 值
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     */
	@SuppressWarnings("unused")
	public static int Delete(Statement stmt,String table,String where,Object[] param)
    {
    	for (Object object : param) {
    		String classtype = object.getClass().getName();
			if(classtype.equals("java.lang.String"))
				where = where.replaceFirst("\\?", "'"+object.toString()+"'");
			else
				where = where.replaceFirst("\\?", ""+object.toString()+"");
		}
    	String cmd = "";
    	if(null == param)
    		cmd = "delete from `"+table+"`";
    	else
    		cmd = "delete from `"+table+"` where " + where;
    	System.err.println(cmd);
    	return SQLUtil.ExecSql(stmt, cmd);
    }
	
	/***
	 * 安全关闭数据库连接
	 * @param obj 连接对象
	 */
	public static void safeClose(Object obj)
	{
		if (obj == null)  
        {  
            return;  
        }  
        try  
        {  
            if (obj instanceof Statement)  
            {  
                ((Statement) obj).getConnection().close();  
            } else if (obj instanceof PreparedStatement)  
            {  
                ((PreparedStatement) obj).getConnection().close();  
            } else if (obj instanceof ResultSet)  
            {  
                ((ResultSet) obj).getStatement().getConnection().close();  
            } else if (obj instanceof Connection)  
            {  
                ((Connection) obj).close();  
            }
        } catch (SQLException ex)  
        {  
        	NCException.printStackTrace(ex,false);
        }  
	}
	/**
	 * 提交或回滚事务
	 * @param conn
	 * @param commit true:提交 false:回滚
	 * @return 成功返回true  失败返回false
	 */
	public static boolean Submit(Connection conn, boolean commit){
		if(null == conn)
			return false;
		try {
			if (commit)
				conn.commit();
			else
				conn.rollback();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e, false);
		}
		return false;
	}
	
	public static ResultSet Select(String table, String[] fields, SQLCommandLogic[] where, LinkedHashMap<String, Object> wheres){
		String cmd = "select ";
		String field = "";
		String wh = "";
		if(null == table || table.equals(""))
			return null;
		if(wheres != null && null != where && (wheres.size() - where.length != 1))
			return null;
		if(null == fields || fields.length < 1)
			field = "*";
		else{
			for (String string : fields) {
				if(field.equals(""))
					field = "`" + string + "`";
				else
					field += ",`" + string + "`";
			}
		}
		cmd += field + " from " + table;
		if(null != wheres){
			//where = " where ";
			Iterator<Map.Entry<String, Object>> iter = wheres.entrySet().iterator();
			int i = 0;
			while (iter.hasNext()) {
				Entry<String, Object> entry = iter.next();
				Object value = entry.getValue();
				String classtype = entry.getValue().getClass().getName();
				if(0 != i)
					wh += " " + where[i - 1] + " ";
				wh += "`"+entry.getKey()+"`=";
				if(classtype.equals("java.lang.String"))
					wh += "'"+value+"'";
				else
					wh += value.toString();
				i++;
			}
			wh = " where " + wh;
		}
		cmd += wh;
		System.err.println(cmd);
		return SQLUtil.getResultSet(cmd);
	}
	
	/**
	 * 执行检索指令
	 * @param conn 数据库连接
 	 * @param table 表名
	 * @param fields 字段集。如果需要检索所有,则 NULL
	 * @param where 按顺序，每个条件 间的逻辑
	 * @param wheres 条件集
	 * @return NULL创建失败。
	 */
	public static ResultSet Select(Connection conn,String table, String[] fields, SQLCommandLogic[] where, LinkedHashMap<String, Object> wheres){
		String cmd = "select ";
		String field = "";
		String wh = "";
		if(null == table || table.equals(""))
			return null;
		if(wheres != null && null != where && (wheres.size() - where.length != 1))
			return null;
		if(null == fields || fields.length < 1)
			field = "*";
		else{
			for (String string : fields) {
				if(field.equals(""))
					field = "`" + string + "`";
				else
					field += ",`" + string + "`";
			}
		}
		cmd += field + " from " + table;
		if(null != wheres){
			//where = " where ";
			Iterator<Map.Entry<String, Object>> iter = wheres.entrySet().iterator();
			int i = 0;
			while (iter.hasNext()) {
				Entry<String, Object> entry = iter.next();
				Object value = entry.getValue();
				String classtype = entry.getValue().getClass().getName();
				if(0 != i)
					wh += " " + where[i - 1] + " ";
				wh += "`"+entry.getKey()+"`=";
				if(classtype.equals("java.lang.String"))
					wh += "'"+value+"'";
				else
					wh += value.toString();
				i++;
			}
			wh = " where " + wh;
		}
		cmd += wh;
		System.err.println(cmd);
		return SQLUtil.getResultSet(conn,cmd);
	}
	
	/**
	 * 执行检索指令
 	 * @param table 表名
	 * @param fields 字段集。如果需要检索所有,则 NULL
	 * @param where 按顺序，每个条件 间的逻辑
	 * @param wheres 条件集
	 * @return NULL创建失败。
	 */
	public enum SQLCommandLogic{
		OR,AND;

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return super.toString().toLowerCase();
		}
	}
}
