package cn.NightCat.Base;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.NightCat.Exception.NCException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class Wiki_Login
 */
public abstract class Wiki_Base extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.sendError(HttpServletResponse.SC_NOT_FOUND,"Wiki_Login not found!");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String chatset = request.getCharacterEncoding();
		if(null == chatset || chatset.trim().equals(""))
			chatset = "utf-8";
		response.setCharacterEncoding(chatset);
		ServletInputStream sis = request.getInputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			
			if(null == sis){
				response.sendError(HttpServletResponse.SC_NOT_FOUND,"Wiki_Login not found!");
				return;
			}
			int buff = -1;
			while((buff = sis.read()) != -1)
				bos.write(buff);
			sis.close();
			String receive_str = new String(bos.toByteArray(),chatset);
			response.getWriter().println(processData(receive_str));
			bos.close();
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e, false);
			response.getWriter().println(CreateReturnData("10001", new JSONObject(), e.getMessage(), null));
		}finally {
			if(sis != null)
				sis.close();
			if(bos != null)
				bos.close();
		}
	}
	/**
	 * 检查Body内是否存在参数 True 均存在
	 * @param body
	 * @param args
	 * @return
	 */
	protected final boolean CheckParam(JSONObject body,String...args){
		for (String string : args) {
			if(!body.has(string))
				return false;
		}
		return true;
	}
	protected final String CreateReturnData(String result,JSONObject body,String msg,String error_msg){
		JSONObject jsonObject = new JSONObject();
		if(body == null)
			body = new JSONObject();
		jsonObject.put("Result", result);
		jsonObject.put("Body", body);
		
		if(null != msg && !msg.equals(""))
			jsonObject.put("Msg", msg);
		
		if(null != error_msg && !error_msg.equals(""))
			jsonObject.put("Error_Msg", error_msg);
		
		return jsonObject.toString();
			
	}
	protected final String CreateReturnDataArray(String result,JSONArray body,String msg,String error_msg){
		JSONObject jsonObject = new JSONObject();
		if(body == null)
			body = new JSONArray();
		jsonObject.put("Result", result);
		jsonObject.put("Body", body);
		
		if(null != msg && !msg.equals(""))
			jsonObject.put("Msg", msg);
		
		if(null != error_msg && !error_msg.equals(""))
			jsonObject.put("Error_Msg", error_msg);
		
		return jsonObject.toString();
			
	}
	protected abstract String processData(String body) throws Exception;
	protected abstract boolean needLogin();

}
