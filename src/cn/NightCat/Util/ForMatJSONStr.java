package cn.NightCat.Util;

/*
	Create by Crazyist at 2016年2月18日 上午8:43:32 Filename:ForMatJSONStr.java
	CopyRight © 2014-2016 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class ForMatJSONStr {
	  
	  /**
	   * 得到格式化json数据  退格用\t 换行用\r
	   */
	  public static String format(String jsonStr) {
	    int level = 0;
	    StringBuffer jsonForMatStr = new StringBuffer();
	    for(int i=0;i<jsonStr.length();i++){
	      char c = jsonStr.charAt(i);
	      if(level>0&&'\n'==jsonForMatStr.charAt(jsonForMatStr.length()-1)){
	        jsonForMatStr.append(getLevelStr(level));
	      }
	      switch (c) {
	      case '{': 
	      case '[':
	        jsonForMatStr.append(c+"\n");
	        level++;
	        break;
	      case ',': 
	        jsonForMatStr.append(c+"\n");
	        break;
	      case '}':
	      case ']':
	        jsonForMatStr.append("\n");
	        level--;
	        jsonForMatStr.append(getLevelStr(level));
	        jsonForMatStr.append(c);
	        break;
	      default:
	        jsonForMatStr.append(c);
	        break;
	      }
	    }
	    
	    return jsonForMatStr.toString();

	  }
	  
	  private static String getLevelStr(int level){
	    StringBuffer levelStr = new StringBuffer();
	    for(int levelI = 0;levelI<level ; levelI++){
	      levelStr.append("\t");
	    }
	    return levelStr.toString();
	  }
	}