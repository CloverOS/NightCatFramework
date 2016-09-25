package cn.NightCat.Wiki;

import javax.servlet.annotation.WebServlet;
import cn.NightCat.Base.Wiki_Base;
import cn.NightCat.Config.NCConfig;

/*
	Create by Crazyist at 2015年12月3日 下午9:56:39 Filename:Wiki_getAction.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
@WebServlet("/Wiki_getAction.action")
public class Wiki_getAction extends Wiki_Base{
	private static final long serialVersionUID = 1L;

	@Override
	protected String processData(String body) throws Exception {
		// TODO Auto-generated method stub
		return CreateReturnDataArray("10000", NCConfig.getAllAction(), "获取成功!", null);
	}

	@Override
	protected boolean needLogin() {
		// TODO Auto-generated method stub
		return false;
	}

}
