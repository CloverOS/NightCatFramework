package cn.NightCat.Util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import javax.servlet.http.HttpServletRequest;

/*
	Create by Crazyist at 2016年1月24日 上午10:01:33 Filename:ClientInfo.java
	CopyRight © 2014-2016 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class ClientInfo {
	public static final String getRemoteAddress(HttpServletRequest request, boolean real) {
		if(!real)
			request.getRemoteAddr();
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown"))
            ip = request.getRemoteAddr();
        return ip;
    }
	/**
	 * 获取客户端MAC地址
	 * @param ip 连接方IP地址
	 * @return NULL为该IP未连接至本地
	 */
	public static final String getMACAddress(String ip) {
        String str = "";
        String macAddress = null;
        try {
            Process p = Runtime.getRuntime().exec("nbtstat -a " + ip);
            InputStreamReader ir = new InputStreamReader(p.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    //if (str.indexOf("MAC Address") > 1) {
                    if (str.indexOf("MAC") > 1) {
                        macAddress = str.substring(
                                str.indexOf("=") + 2, str.length());
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return macAddress;
    }
}
