package cn.NightCat.Util;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import cn.NightCat.Config.NCConfig;

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
		Create by Crazyist at 2016年3月17日 上午10:18:40 Filename:LogUtil.java
		CopyRight © 2014-2016 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
/**
 * 日记输出辅助类 V1.0
 * @author 傅承灿
 * 
 * 如果日记输出标记设置为False，则不会有任何日记输出(注意：本工具类为单例)
 *
 */
public final class LogUtil{

	private static LogUtil logUtil  = null;
	private Logger log = Logger.getLogger(LogUtil.class.getName());

	public static final LogUtil getInstance(){
		if(null == logUtil)
			logUtil = new LogUtil();
		return logUtil;
	}
	

	public void debug(Object message, Throwable t) {
		// TODO Auto-generated method stub
		if(NCConfig.Log_Flag && log.isDebugEnabled())
			log.debug(message, t);
	}

	public void debug(Object message) {
		// TODO Auto-generated method stub
		if(NCConfig.Log_Flag && log.isDebugEnabled())
			log.debug(message);
	}

	public void error(Object message, Throwable t) {
		// TODO Auto-generated method stub
		if(NCConfig.Log_Flag)
		log.error(message, t);
	}

	public void error(Object message) {
		// TODO Auto-generated method stub
		if(NCConfig.Log_Flag)
			log.error(message);
	}

	public void fatal(Object message, Throwable t) {
		// TODO Auto-generated method stub
		if(NCConfig.Log_Flag)
			log.fatal(message, t);
	}

	public void fatal(Object message) {
		// TODO Auto-generated method stub
		if(NCConfig.Log_Flag)
			log.fatal(message);
	}

	public void info(Object message, Throwable t) {
		// TODO Auto-generated method stub
		if(NCConfig.Log_Flag && log.isInfoEnabled())
			log.info(message, t);
	}

	public void info(Object message) {
		// TODO Auto-generated method stub
		if(NCConfig.Log_Flag && log.isInfoEnabled())
			log.info(message);
	}

	public void log(Priority priority, Object message, Throwable t) {
		// TODO Auto-generated method stub
		if(NCConfig.Log_Flag && log.isEnabledFor(priority))
			log.log(priority, message, t);
	}

	public void log(Priority priority, Object message) {
		// TODO Auto-generated method stub
		if(NCConfig.Log_Flag && log.isEnabledFor(priority))
			log.log(priority, message);
	}

	public void log(String callerFQCN, Priority level, Object message, Throwable t) {
		// TODO Auto-generated method stub
		if(NCConfig.Log_Flag && log.isEnabledFor(level))
			log.log(callerFQCN, level, message, t);
	}
}
