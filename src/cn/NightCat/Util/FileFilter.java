package cn.NightCat.Util;

import java.io.File;

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
		Create by Crazyist at 2016年3月28日 下午4:27:50 Filename:FileFilter.java
		CopyRight © 2014-2016 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class FileFilter implements java.io.FileFilter{

	private String Str_reg;
	public FileFilter(String str_reg) {
		// TODO Auto-generated constructor stub
		this.Str_reg = str_reg;
	}
	
	@Override
	public boolean accept(File pathname) {
		// TODO Auto-generated method stub
		return pathname.getName().matches(Str_reg);
	}

}
