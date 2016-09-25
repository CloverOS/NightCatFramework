package cn.NightCat.Wiki;

import javax.servlet.annotation.WebServlet;

import cn.NightCat.Base.Wiki_Base;
import cn.NightCat.Config.NCConfig;

/*
	Create by Crazyist at 2015年12月3日 下午9:46:52 Filename:Wiki_getGroup.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
@WebServlet("/Wiki_getGroup.action")
public class Wiki_getGroup extends Wiki_Base{
	private static final long serialVersionUID = 1L;

	@Override
	protected String processData(String body) throws Exception {
		// TODO Auto-generated method stub
		return CreateReturnDataArray("10000", NCConfig.getAllGroup(), "获取成功!", null);
	}

	@Override
	protected boolean needLogin() {
		// TODO Auto-generated method stub
		return false;
	}

}
