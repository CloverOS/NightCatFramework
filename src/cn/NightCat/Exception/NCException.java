package cn.NightCat.Exception;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.NightCat.Config.NCConfig;
import cn.NightCat.Util.LogUtil;
import cn.NightCat.Util.MailUtil;

/*
	Create by Crazyist at 2015年11月18日 下午12:10:51 Filename:NCException.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class NCException extends Exception{
	private static final long serialVersionUID = 1L;
	private static SimpleDateFormat sm = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss,SSS");
	private int Result = -1;
	private String msg = "";
	private String error_msg = "";
	
	
	public NCException(int result,String msg,String error_msg){
		this.Result = result;
		this.msg = msg;
		this.error_msg = error_msg;
	}
	
	public NCException(int result,String msg){
		this.Result = result;
		this.msg = msg;
	}
	
	public int getResult() {
		return Result;
	}
	public String getError_msg() {
		return error_msg;
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		if("".equals(this.msg))
			return "系统出错";
		return this.msg;
	}

	public static void printStackTrace(Exception e,boolean notice) {
		// TODO Auto-generated method stub
		Date now = new Date();
		if(!NCConfig.Log_Flag)
			return;
		String Error_Msg = "";
		StackTraceElement[] error = e.getStackTrace();
		Error_Msg += "System error " + e.getClass().getName() + ":" + e.getMessage() + "\n";
		for (StackTraceElement stack : error) {
			if(stack.getFileName() == null){
				Error_Msg += "\t at " + stack.getClassName() + "."+stack.getMethodName()+"(Unknown Source)";
			}else{
				Error_Msg += "\t at " + stack.getClassName() + "."+stack.getMethodName()+"("+stack.getFileName()+":"+stack.getLineNumber()+")";
			}
			Error_Msg += "\n";
		}
		LogUtil.getInstance().error(Error_Msg);
		if(notice && null != NCConfig.System_Email)
			MailUtil.sendMail("smtp.crazyist.cn", "25", true, "异常捕获报告", "Error@Crazyist.cn", "cc512.1118", NCConfig.System_Email, "["+sm.format(now)+"]捕获异常通知", Error_Msg, false, true);
	}
	public static void printStackTrace(String msg,Exception e,boolean notice) {
		// TODO Auto-generated method stub
		Date now = new Date();
		if(!NCConfig.Log_Flag)
			return;
		String Error_Msg = msg + "\n\n\n";
		StackTraceElement[] error = e.getStackTrace();
		Error_Msg += "System error:" + e.getMessage() + "\n";
		for (StackTraceElement stack : error) {
			if(stack.getFileName() == null){
				Error_Msg += "\t at " + stack.getClassName() + "."+stack.getMethodName()+"(Unknown Source)";
			}else{
				Error_Msg += "\t at " + stack.getClassName() + "."+stack.getMethodName()+"("+stack.getFileName()+":"+stack.getLineNumber()+")";
			}
			Error_Msg += "\n";
		}
		LogUtil.getInstance().error(Error_Msg);
		if(notice && null != NCConfig.System_Email)
			MailUtil.sendMail("smtp.crazyist.cn", "25", true, "异常捕获报告", "Error@Crazyist.cn", "cc512.1118", NCConfig.System_Email, "["+sm.format(now)+"]捕获异常通知", Error_Msg, false, true);
	}
}
