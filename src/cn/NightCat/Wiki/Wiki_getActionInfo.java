package cn.NightCat.Wiki;

import javax.servlet.annotation.WebServlet;
import cn.NightCat.Base.BaseAction;
import cn.NightCat.Base.Param;
import cn.NightCat.Base.Wiki_Base;
import cn.NightCat.Config.NCConfig;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
	Create by Crazyist at 2015年12月3日 下午9:59:29 Filename:Wiki_getActionInfo.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
@WebServlet("/Wiki_getActionInfo.action")
public class Wiki_getActionInfo extends Wiki_Base{
	private static final long serialVersionUID = 1L;

	@Override
	protected String processData(String body) throws Exception {
		// TODO Auto-generated method stub
		JSONObject jsonObject = JSONObject.fromObject(body);
		String actionName = jsonObject.getString("Name");
		Class<?> action = Class.forName(actionName);
		
		BaseAction ac = (BaseAction)action.newInstance();
		JSONObject result = new JSONObject();
		result.put("Name", ac.getName());
		result.put("Desc", ac.getDesc());
		result.put("Method", ac.getMethod());
		result.put("Type", "JSON");
		if(ac.NeedPower())
			result.put("Power", "限制接口");
		else
			result.put("Power", "普通接口");
		result.put("LastTime", ac.getLastTime());
		result.put("Author", ac.getAuthor());
		result.put("RequestExample", ac.getRequestExample());
		result.put("ReturnExample", ac.getResponseExample());
		result.put("PowerID", NCConfig.Map_Actions.get(actionName)+"");
		result.put("Url", NCConfig.URL + actionName.replace(".", "/") + NCConfig.Extension);
		result.put("Param", ObjectTOJSON(ac.getParams()));
		result.put("Return", ObjectTOJSON(ac.getReturn()));
		result.put("Limit", (ac.Limit() / 1000) + "");
		return CreateReturnData("10000", result, "获取成功!", null);
	}

	@Override
	protected boolean needLogin() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static JSONArray ObjectTOJSON(Param[] params){
		JSONArray result = new JSONArray();
		for (Param param : params) {
			JSONObject item = JSONObject.fromObject(param);
			result.add(item);
		}
		return result;
	}

}
