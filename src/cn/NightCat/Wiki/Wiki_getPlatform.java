package cn.NightCat.Wiki;

import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;

import cn.NightCat.Base.Wiki_Base;
import cn.NightCat.Util.SQLUtil;
import net.sf.json.JSONObject;

/*
	Create by Crazyist at 2015年12月4日 下午1:11:29 Filename:Wiki_getPlatform.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
@WebServlet("/Wiki_getPlatform.action")
public class Wiki_getPlatform extends Wiki_Base{
	private static final long serialVersionUID = 1L;

	@Override
	protected String processData(String body) throws Exception {
		// TODO Auto-generated method stub
		ResultSet rs = SQLUtil.getResultSet("select * from `system_setting` where `key` like 'Platform_%'");
		String temp_str = "";
		while(rs.next()){
			temp_str += "<tr><td>" + rs.getString("Key").replace("Platform_", "") + "</td><td>" + rs.getString("Desc") + "</td><td>" + rs.getString("Value") + "</td></tr>";
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("htmlTR", temp_str);
		SQLUtil.safeClose(rs);
		return CreateReturnData("10000", jsonObject, "获取成功!", null);
	}

	@Override
	protected boolean needLogin() {
		// TODO Auto-generated method stub
		return false;
	}

}
