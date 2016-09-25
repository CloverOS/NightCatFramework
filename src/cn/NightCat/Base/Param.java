package cn.NightCat.Base;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.swing.text.html.HTMLEditorKit.Parser;
import javax.swing.text.html.parser.ParserDelegator;

import cn.NightCat.Config.NCConfig;
import cn.NightCat.Exception.NCException;
import cn.NightCat.Xss.ProcessHTMLCallback;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/*
	Create by Crazyist at 2015年11月29日 上午12:30:01 Filename:Param.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class Param {
	private static Parser parser = new ParserDelegator();
	
	private String Name ="";
	private String Desc = "";
	private ParamType Type = null;
	private String Example = "";
	private Param[] Params = null;
	private boolean Must = false;
	private int Lenght = 0;
	private String Reg = null;
	private static final SimpleDateFormat sm = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	/**
	 * Action参数
	 * @param name 参数名
	 * @param type 参数类型
	 * @param desc 介绍
	 * @param exmple 示例
 	 * @param params 如果是JSONObject或JSONArray才有该值
	 */
	public Param(String name,ParamType type,String desc,String example,Param[] params){
		this.Name = name;
		this.Params = params;
		this.Type = type;
		this.Example = example;
		this.Desc = desc;
		if(null == this.Params)
			this.Params = new Param[]{ };
		if(null != example && (type == ParamType.Type_JSONObject || type == ParamType.Type_JSONArray)){
			try {
				JSONSerializer.toJSON(Example);
			} catch (Exception e) {
				// TODO: handle exception
				Object o = null;
				if(type == ParamType.Type_JSONObject){
					o = new JSONObject();
					((JSONObject)o).put(name, example);
				}else{
					o  = JSONArray.fromObject("[\""+example+"\"]");
				}
				this.Example = o.toString();	
			}
		}
	}
	
	/**
	 * Action参数
	 * @param name 参数名
	 * @param type 参数类型
	 * @param desc 介绍
	 * @param exmple 示例
	 */
	public Param(String name,ParamType type,String desc,String exmple){
		this(name, type, desc, exmple, null);
	}
	
	/**
	 * 获取参数名称
	 * @return 参数名
	 */
	public String getName(){
		return this.Name;
	}
	
	/**
	 * 生成一行HTML 参数
	 * @return
	 */
	public String getHtmlTR(){
		String Result = "";
		if(this.Must)
			Result += "<tr><td>" + this.Name + "</td><td>" + this.Type + "</td><td>是</td><td>" + this.Desc + "</td><td>" + this.Example + "</td></tr>";
		else
			Result += "<tr><td>" + this.Name + "</td><td>" + this.Type + "</td><td>否</td><td>" + this.Desc + "</td><td>" + this.Example + "</td></tr>";
		for (Param param : Params) {
			Result += "<tr><td>&emsp;&emsp;" + param.Name + "</td><td>" + param.Type + "</td><td>";
			if(param.Must)
				Result += "是";
			else
				Result += "否";
			Result += "</td><td>" + param.Desc + "</td><td>";
			if(param.getExample().length() > 57)
				Result += param.getExample().substring(0, 57) + "...";
			else
				Result += param.getExample();
			Result += "</td></tr>";
			
		}
		return Result;
	}
	
	/**
	 * 获取说明
	 * @return
	 */
	public String getDesc(){
		return this.Desc;
	}
	
	/**
	 * 获取例子
	 * @return
	 */
	public String getExample(){
		return this.Example;
	}
	
	/**
	 * 获取类型
	 * @return
	 */
	public ParamType getType(){
		return this.Type;
	}
	
	/**
	 * 获取下级参数列表
	 * @return
	 */
	public Param[] getParams(){
		return Params.clone();
	}
	
	public boolean getMust(){
		return this.Must;
	}
	
	public Param setMust(boolean must){
		this.Must = must;
		return this;
	}
	
	public Param setLenght(int lenght){
		this.Lenght = lenght;
		return this;
	}
	
	public Param setRegStr(String reg){
		this.Reg = reg;
		return this;
	}
	
	public static String Encode(String str) {  
	    if(str ==null || str.trim().equals(""))   return str;  
	    StringBuilder encodeStrBuilder = new StringBuilder();  
	    for (int i = 0, len = str.length(); i < len; i++) {  
	       encodeStrBuilder.append(htmlEncode(str.charAt(i)));  
	    }  
	    return encodeStrBuilder.toString();  
	}
	private static String htmlEncode(char c) {  
	    switch(c) {  
	       case '&':   
	    	   return "＆";
	       case '<':   
	    	   return "＜";
	       case '>':  
	    	   return "＞";
	       case '"':   
	    	   return "＂";
	       case ' ':
	    	   return "　";
	       case '/':
	    	   return "／";
	       case '\\':
	    	   return "＼";
	       case '*':
	    	   return "＊";
	       case '#':
	    	   return "＃";
	       case '$':
	    	   return "＄";
	       case '%':
	    	   return "％";
	       case '@':
	    	   return "＠";
	       case '{':
	    	   return "｛";
	       case '}':
	    	   return "｝";
	       case '[':
	    	   return "［";
	       case ']':
	    	   return "］";
	       case '(':
	    	   return "（";
	       case ')':
	    	   return "）";
	       case '.':
	    	   return "．";
	       case ',':
	    	   return "，";
	       case '\'':
	    	   return "＇";
	       case ':':
	    	   return "：";
	       case '?':
	    	   return "？";
	       case '^':
	    	   return "＾";
	       case '+':
	    	   return "＋";
	       case '=':
	    	   return "＝";
	       default:  
	           return c +"";  
	    }  
	} 
	
	
	/**
	 * 检查参数是否符合
	 * @param p 参数
	 * @param paramType 参数类型
	 * @param str 参数
	 * @return 返回处理后的数据
	 * @throws NCException
	 */
	public static final String Check(Param p,ParamType paramType,String str) throws NCException{
		try {
			if(p.Must && null == str)
				throw new NCException(1003, "系统出错!");
			switch (paramType) {
			case Type_Money:
				if(!str.matches("^[0-9]*\\.[0-9]{2}$"))
					throw new NCException(1012, "系统出错!");
				break;
			case Type_UUID:
				UUID uuid = UUID.fromString(str);
				return uuid.toString();
			case Type_DateTime:
				sm.parse(str);
				break;
			case Type_String:
				if(p.Lenght != 0 && str.length() > p.Lenght)
					throw new NCException(1013, "系统出错!");
				if(null != p.Reg && !str.matches(p.Reg))
					throw new NCException(1014, "系统出错!");
				str = Encode(str);
				return str;
			case Type_Integer:
				Integer.parseInt(str);
				break;
			case Type_Double:
				Double.parseDouble(str);
				break;
			case Type_JSONArray:
				JSONArray jsonArray = JSONArray.fromObject(str);
				if(p.Must && jsonArray.isEmpty())
					throw new NCException(1003, "系统出错!");
				return jsonArray.toString();
			case Type_JSONObject:
				JSONObject jsonObject = JSONObject.fromObject(str);
				if(p.Must && jsonObject.isEmpty())
					throw new NCException(1003, "系统出错!");
				return jsonObject.toString();
			case Type_LongDateTime:
				if(!str.matches("^[0-9]{30}$"))
					throw new NCException(1012, "系统出错!");
				sm.parse(str.substring(0, 17));
				break;
			case Type_Telephone:
				if(!str.matches("^[1][34578][0-9]{9}$"))
					throw new NCException(1012, "系统出错!");
				break;
			case Type_Email:
				if(!str.matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"))
					throw new NCException(1012, "系统出错!");
				return str;
			case Type_MD5:
				if(!str.matches("^[a-f0-9]{32}$"))
					throw new NCException(1012, "系统出错!");
				break;
			case Type_Number:
				if(p.Lenght != 0 && str.length() != p.Lenght)
					throw new NCException(1013, "系统出错!");
				if(!str.matches("^[0-9]{1,}$"))
					throw new NCException(1012, "系统出错!");
				break;
			case Type_Url:
				if(!str.matches("^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$"))
					throw new NCException(1012, "系统出错!");
				return str;
			case Type_HTML:
				ProcessHTMLCallback processHTMLCallback = new ProcessHTMLCallback(NCConfig.whites);
				parser.parse(new InputStreamReader(new ByteArrayInputStream(str.getBytes())), processHTMLCallback, false);
				str = processHTMLCallback.toString();
				return str;
			}
			return str;
		} catch(NCException e){
			throw e;
		}catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e, false);
			throw new NCException(-1, "系统出错!");
		}
	}
	
	/**
	 * 检查参数是否符合
	 * @param p 参数
	 * @param paramType 参数类型
	 * @param params 参数列表
	 * @param str 参数
	 * @return 返回处理后的数据
	 * @throws NCException
	 */
	public static String Check(Param p,ParamType paramType,Param[] params,String str) throws NCException{
		try {
			if(p.Must && null == str)
				throw new NCException(1003, "系统出错!");
			if(params.length < 1)
				throw new NCException(1021, "系统错误!");
			switch (paramType) {
			case Type_JSONObject:
				JSONObject jsonObject = JSONObject.fromObject(str);
				if(p.Must && jsonObject.size() == 0)
					throw new NCException(1003, "系统出错!");
				for (Param param : params) {
					String value = null;
					if(jsonObject.has(param.getName()))
						value = jsonObject.getString(param.getName());
					if(null == value && param.Must)
						throw new NCException(1003, "系统出错!");
					if(value != null)
						jsonObject.put(param.getName(), Check(param,param.getType(), value));
				}
				return jsonObject.toString();
			case Type_JSONArray:
				JSONArray jsonArray = JSONArray.fromObject(str);
				JSONArray result = new JSONArray();
				if(p.Must && jsonArray.size() == 0)
					throw new NCException(1003, "系统出错!");
				for (Object object : jsonArray) {
					if(1 == params.length)
						result.add(Check(params[0],params[0].getType(), object.toString()));
					else
						result.add(Check(p,ParamType.Type_JSONObject, params, object.toString()));
				}
				return result.toString();
			default:
				throw new NCException(1011, "系统出错!");
			}
		} catch(NCException e){
			throw e;
		}catch (Exception e) {
			// TODO: handle exception
			NCException.printStackTrace(e, false);
			throw new NCException(-1, "系统异常!");
		}
	}
	
	public enum ParamType{
		Type_UUID,Type_DateTime,Type_String,Type_Integer,Type_Double,Type_JSONArray,Type_JSONObject,Type_LongDateTime,Type_Telephone,Type_Email,Type_MD5,Type_Number,Type_Url,Type_Money,Type_HTML;
	}
}
