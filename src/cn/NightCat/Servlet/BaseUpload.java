package cn.NightCat.Servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


import net.sf.json.JSONObject;
import cn.NightCat.Config.NCConfig;
import cn.NightCat.Exception.NCException;
import cn.NightCat.Session.NCSession;
import cn.NightCat.Util.LogUtil;
import cn.NightCat.Util.Sign;

public abstract class BaseUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected File FileParentPath = null;
	protected String Extension = "";
	private String ServletName = "";
	protected String RemoteIP = "";
	protected String RemotePort = "";
	private String key = "";
	protected JSONObject json_UserInfo = null;
	protected String Head = "";
	private SimpleDateFormat sm = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	private DiskFileItemFactory factory = null;
	private ServletFileUpload upload = null;

	/**
	 * 是否需要登录权限
	 * @return true 需要,默认为 true
	 */
	protected boolean NeedPower(){
		return true;
	}
	
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		ServletName = getServletConfig().getServletName();
		if(ServletName.lastIndexOf(".") != -1)
			ServletName = ServletName.substring(ServletName.lastIndexOf(".") + 1, ServletName.length());
		FileParentPath = new File(NCConfig.Web_Path + NCConfig.getStringValue("Nightcat.Upload." + ServletName, "/Image/"));
		Extension = NCConfig.getStringValue("Nightcat.Upload." + ServletName + ".Extension", ".png");
		if(!FileParentPath.exists())
			FileParentPath.mkdirs();
		factory = new DiskFileItemFactory();
		upload = new ServletFileUpload(factory);
		LogUtil.getInstance().debug("Upload["+ServletName+"] is init ok!");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().print("no support method!");
	}
	
	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String white = "";
		for(int i = 0; i < NCConfig.WhiteSites.size(); i++) white += NCConfig.WhiteSites.getString(i) + ",";
		white = white.substring(0, white.length() - 1);
		((HttpServletResponse)response).setHeader(NCConfig.ACCESS_CONTROL_ALLOW_METHODS_HEADER, "POST, GET, OPTIONS");
		((HttpServletResponse)response).setHeader(NCConfig.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, white);
		((HttpServletResponse)response).setHeader(NCConfig.ACCESS_CONTROL_ALL_HEADER, NCConfig.FLAG);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("resource")
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doOptions(request, response);
		String charset = request.getCharacterEncoding();
		if(null == charset || charset.isEmpty())
			charset = "utf-8";
		response.setCharacterEncoding(charset);
		InputStream in = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayOutputStream tmp_param = new ByteArrayOutputStream();
		ByteArrayInputStream bis = null;
		RemoteIP = getRemortIP(request);
		RemotePort = request.getRemotePort() + "";
		String result = "";
		BufferedImage image = null;
		response.setContentType("application/json");
		JSONObject param = new JSONObject();
		try {
			ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if(isMultipart){
				param = new JSONObject();
				List<FileItem> items = upload.parseRequest(new ServletRequestContext(request));
				for (FileItem fileItem : items) {
					if(fileItem.isFormField())
						param.put(fileItem.getFieldName(), fileItem.getString().replace("\n", "").trim());
					else{
						in = fileItem.getInputStream();
				        int buff = 0;
				        while ((buff = in.read()) != -1)
				          bos.write(buff);
				        if(bos.size() < 1)
							throw new NCException(1010, "对不起,系统异常!", null);
				        bis = new ByteArrayInputStream(bos.toByteArray());
						image = ImageIO.read(bis);
						if(null == image)
							throw new NCException(10001, "请勿上传非图片文件", null);
						images.add(image);
					}
				}
				String verifyData = verifySign2(param);
				if(verifyData != null)
					throw new NCException(1001, "对不起,数据获取失败!", verifyData);
				else{
					if(NeedPower()){
						if(!param.has("Session"))
							throw new NCException(1002, "对不起,数据获取失败!", "Session is NULL");
						json_UserInfo = NCSession.getInstance().getSession("", param.getString("Session"));
						if(null == json_UserInfo)
							throw new NCException(1002, "对不起,数据获取失败!", "Can't found Session");
					}
					result = receiveFile(param.getJSONObject("Body"), images.toArray(new BufferedImage[images.size()]));
				}
			}else{
				in = request.getInputStream();
				int buff;
				while((buff = in.read()) != -1 && buff != 10)
					tmp_param.write(buff);
				param = JSONObject.fromObject(tmp_param.toString(charset));
				String verifyData = verifySign(param);
				if(verifyData != null)
					throw new NCException(1001, "对不起,数据获取失败!", verifyData);
				if(NeedPower()){
					if(!param.has("Session"))
						throw new NCException(1002, "对不起,数据获取失败!", "Session is NULL");
					json_UserInfo = NCSession.getInstance().getSession("",param.getString("Session"));
					if(null == json_UserInfo)
						throw new NCException(1002, "对不起,数据获取失败!", "Can't found Session");
				}
				while((buff = in.read()) != -1)
					bos.write(buff);
				if(bos.size() < 1)
					throw new NCException(1010, "对不起,系统异常!", null);
				bis = new ByteArrayInputStream(bos.toByteArray());
				image = ImageIO.read(bis);
				if(null == image)
					result =CreateSignData(10001, null, "请勿上传非图片类文件!", null);
				else
					result = receiveFile(param.getJSONObject("Body"), new BufferedImage[]{ image });
			}
		}catch(NCException e){
			result = CreateSignData(e.getResult(), null, e.getMessage(), e.getError_msg());
		}catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e, false);
			result = CreateSignData(10006, null, "对不起,系统异常!", e.getMessage());
		}finally{
			safeClose(tmp_param);
			safeClose(bos);
			safeClose(in);
			safeClose(bis);
		}
		response.getWriter().println(result);
		LogUtil.getInstance().debug("User["+RemoteIP+":"+RemotePort+"] Upload[" + ServletName + "] \n\t receive Data:"+param.toString()+"\n\t result Data:"+ result);
	}
	
	private void safeClose(Object o){
		if(null == o)
			return;
		try {
			if(o instanceof ByteArrayOutputStream)
				((ByteArrayOutputStream)o).close();
			else if(o instanceof InputStream)
				((InputStream)o).close();
			else if(o instanceof ServletInputStream)
				((ServletInputStream)o).close();
			else if(o instanceof ByteArrayInputStream)
				((ByteArrayInputStream)o).close();
		} catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e, false);
		}
	}

	/**
	 * 当收到文件时,将会回调该方法
	 * @param param 上传者参数
	 * @param filebyte 收到的字节流
	 * @return
	 * @throws Exception
	 */
	protected abstract String receiveFile(JSONObject param,BufferedImage[] image) throws Exception;

	private String verifySign(JSONObject data) throws Exception
	{
		if(!data.has("Head"))
			return "Head is not fount";
		else if(!data.has("Body"))
			return "Body is not fount";
		else if(!data.has("Timestamp"))
			return "Timestamp is not fount";
		else if(!data.getString("Timestamp").matches("[0-9]{13}"))
			return "Timestamp is not allow format";
		else if(!data.has("Sign"))
			return "Sign is not fount";
		Head = data.getString("Head");
		key = NCConfig.KeyMap.get("Platform_" + Head + "");
		if(null == key || "".equals(key))
			return "小朋友,你这是想干什么呢?";
		if(Sign.VerifySign(data.getJSONObject("Body").toString(), key, data.getString("Timestamp"),data.getString("Sign")))
			return null;
		return "the Sign is error![The correct Sign is:" + Sign.getSign(data.getString("Body"), key) + " key:" + Sign.getKey(key, data.getString("Timestamp"))+"]";
	}
	private String verifySign2(JSONObject data) throws Exception
	{
		if(!data.has("Head"))
			return "Head is not fount";
		else if(!data.has("Body"))
			return "Body is not fount";
		else if(!data.has("Timestamp"))
			return "Timestamp is not fount";
		else if(!data.getString("Timestamp").matches("[0-9]{13}"))
			return "Timestamp is not allow format";
		else if(!data.has("Sign"))
			return "Sign is not fount";
		Head = data.getString("Head");
		key = NCConfig.KeyMap.get("Platform_" + Head + "");
		if(null == key || "".equals(key))
			return "小朋友,你这是想干什么呢?";
		long Timestamp = data.getLong("Timestamp");
		if(System.currentTimeMillis() - Timestamp >= 600000)
			return "请求已过期!";
		data.put("Body", new String(Base64.decode(data.getString("Body")),Charset.forName("utf-8")));
		if(Sign.VerifySign(data.getString("Body"), key, data.getString("Timestamp"),data.getString("Sign")))
			return null;
		return "the Sign is error![The correct Sign is:" + Sign.getSign(data.getString("Body"), key) + " key:" + Sign.getKey(key, data.getString("Timestamp"))+"]";
	}
	/***
	 * 数据报文进行签名
	 * @param result 返回代码
	 * @param body 数据内容
	 * @param msg 信息
	 * @param error_msg 错误信息
	 * @return 签名后的JSON数据报
	 */
	protected final String CreateSignData(int result,String body,String msg,String error_msg)
	{
		Date now = new Date();
		if(null == body || "".equals(body))
			body = "{}";
		if(null == key)
			key = "";
		key = Sign.getKey(key, sm.format(now));
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Result", result+"");
		jsonObject.put("Body", JSONObject.fromObject(body));
		if(null != msg && !"".equals(msg))
			jsonObject.put("Msg", msg);
		if(null != error_msg && !"".equals(error_msg))
			jsonObject.put("Error_Msg", error_msg.replace("\"", ""));
		jsonObject.put("Timestamp", sm.format(now));
		jsonObject.put("Sign", Sign.getSign(body, key));
		return jsonObject.toString();
	}
	
	public String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null)
			return request.getRemoteAddr();
		return request.getHeader("x-forwarded-for");
	}
}
