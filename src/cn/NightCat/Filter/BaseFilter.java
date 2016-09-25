package cn.NightCat.Filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.NightCat.Base.BaseAction;
import cn.NightCat.Config.NCConfig;
import cn.NightCat.Exception.NCException;
import cn.NightCat.Util.LogUtil;

/*
	Create by Crazyist at 2015年8月14日 下午7:10:25 Filename:BaseFilter
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public final class BaseFilter implements Filter{
	public static final String TAG = "BaseFilter.json";
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		if(NCConfig.URL.indexOf(req.getServerName()) == -1){
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "this domain not supported!");
			return ;
		}
		doOptions(res);
		if(!NCConfig.OpenAction){
			res.sendError(HttpServletResponse.SC_NOT_FOUND, req.getRequestURI() + " is not found!");
			return;
		}
		String remoteIP = getRemortIP(req);
		String remotePort = req.getRemotePort()+"";
		String charset = req.getCharacterEncoding();
		if(null == charset || charset.equals(""))
			charset = "utf-8";
		response.setContentType("application/json");
		response.setCharacterEncoding(charset);
		String Method = req.getMethod();	
		String Action =req.getRequestURI();
		try {
			if(!NCConfig.ProjectName.equals(""))
				Action = Action.replace("/" + NCConfig.ProjectName, "");
			Action = Action.substring(1, Action.length()).replace("/", ".");
			Action = Action.substring(0, Action.length() - NCConfig.Extension.length());
			if(null == Method || !(Method.equals("POST")))
			{
				LogUtil.getInstance().error("user["+remoteIP+"] use method[" + Method + "] has been to intercept!");
				res.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service Unavailable");
				return;
			}
			if(!NCConfig.Map_Actions.containsKey(Action)){
				res.sendError(HttpServletResponse.SC_NOT_FOUND, req.getRequestURI() + " is not found!");
				return;
			}
			String body = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ServletInputStream sis = req.getInputStream();
			int buff = -1;
			while((buff = sis.read()) != -1)
				bos.write(buff);
			body = new String(bos.toByteArray(),charset);
			sis.close();
			bos.close();
			if(null != body && !body.equals(""))
				BaseAction.disposeaAction(Charset.forName(charset), Action, res, remoteIP, remotePort, body);
			else
				res.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,"Data is null");
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace("User["+remoteIP+":"+remotePort+"] visit " + Action + " method["+Method+"]  charset is " + charset,e,true);
			res.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,"Service Unavailable");
		}
		
	}
	
	private void doOptions(HttpServletResponse response) {
		// TODO Auto-generated method stub
		String white = "";
		for(int i = 0; i < NCConfig.WhiteSites.size(); i++) white += NCConfig.WhiteSites.getString(i) + ",";
		white = white.substring(0, white.length() - 1);
		((HttpServletResponse)response).setHeader(NCConfig.ACCESS_CONTROL_ALLOW_METHODS_HEADER, "POST, GET, OPTIONS");
		((HttpServletResponse)response).setHeader(NCConfig.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, white);
		((HttpServletResponse)response).setHeader(NCConfig.ACCESS_CONTROL_ALL_HEADER, NCConfig.FLAG);
		
	}
	
	/**
	 * 获取客户端IP地址
	 * @return
	 */
	public String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null)
			return request.getRemoteAddr();
		return request.getHeader("x-forwarded-for");
	}
	
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}
}
