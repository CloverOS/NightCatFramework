package cn.NightCat.Servlet;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.NightCat.Config.NCConfig;
import cn.NightCat.Session.NCSession;
import cn.NightCat.Util.LogUtil;
import net.sf.json.JSONObject;

/* 
                                  _oo8oo_  
                                 o8888888o  
                                 88" . "88  
                                 (| -_- |)  
                                 0\  =  /0  
                               ___/'==='\___  
                             .' \\|     | '.  
                            / \\|||  :  ||| \  
                           / _||||| -:- |||||_ \  
                          |   | \\\  -  / |   |  
                          | \_|  ''\---/''  |_/ |  
                          \  .-\__  '-'  __/-.  /  
                        ___'. .'  /--.--\  '. .'___  
                     ."" '<  '.___\_<|>_/___.'  >' "".  
                    | | :  `- \`.:`\ _ /`:.`/ -`  : | |  
                    \  \ `-.   \_ __\ /__ _/   .-` /  /  
                =====`-.____`.___ \_____/ ___.`____.-`=====  
                                  `=---=`  
   
   
               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  
  
  						佛祖保佑         永不宕机/永无bug
		Create by Crazyist at 2016年3月20日 上午1:40:36 Filename:SessionServlet.java
		CopyRight © 2014-2016 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
@WebServlet("/Session.action")
public class SessionServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String ClientIP = req.getRemoteAddr();
		String str_session = req.getParameter("Session");
		if(!NCConfig.TrustServer.contains(ClientIP)){
			LogUtil.getInstance().error("IP["+ClientIP+"] not alllow connect!");
			return;
		}
		JSONObject session = NCSession.getInstance().getSession(null, str_session);
		if(null != session){
			String chatset = req.getCharacterEncoding();
			if(null == chatset || chatset.isEmpty())
				chatset = "utf-8";
			resp.setContentType("application/json");
			resp.setCharacterEncoding(chatset);
			resp.getOutputStream().write(session.toString().getBytes(Charset.forName(chatset)));
		}else
			LogUtil.getInstance().error("Session["+str_session+"] not found!");
	}
}
