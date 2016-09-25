package cn.NightCat.LBS;

import java.math.BigDecimal;
import java.util.ArrayList;

/*
	Create by Crazyist at 2015年11月12日 下午1:06:43 Filename:LBSUtil
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
/**
 * 基于LBS服务辅助类
 * 1、经纬度距离差
 * 2、获取已某个点为中心的正方形区域
 * @author HexCode
 *
 */
public class LBSUtil {
	public static final String TAG = "LBSUtil.json";
	private static double EARTH_RADIUS = 6378.137;//地球半径
	/**
	 * 将角度转换成弧度
	 * @param d 角度大小
	 * @return
	 */
	private static double rad(double d)
	{
		BigDecimal rad  = new BigDecimal(d);
		rad = rad.multiply(new BigDecimal(Math.PI));
		rad = rad.divide(new BigDecimal(180), 10, BigDecimal.ROUND_HALF_DOWN);
	   return rad.doubleValue();
	}
	/**
	 * 将角度转换成弧度
	 * @param d 角度大小
	 * @return
	 */
	private static double rad(String d)
	{
		BigDecimal rad  = new BigDecimal(d);
		rad = rad.multiply(new BigDecimal(Math.PI));
		rad = rad.divide(new BigDecimal(180), 10, BigDecimal.ROUND_HALF_DOWN);
	   return rad.doubleValue();
	}
	/**
	 * 计算不同纬度经度差
	 * @param lat 纬度
	 * @return
	 */
	private static BigDecimal getACCURACY_Longtitude(String lat){
		BigDecimal distance = new BigDecimal(EARTH_RADIUS);
		distance = distance.multiply(new BigDecimal(Math.PI));
		distance = distance.multiply(new BigDecimal(Math.cos(rad(lat))));
		distance = distance.divide(new BigDecimal(180000000), 10, BigDecimal.ROUND_HALF_DOWN);
		return distance;
	}
	/**
	 * 计算两点间的距离
	 * @param lat1	纬度1
	 * @param lng1	经度1
	 * @param lat2	纬度2
	 * @param lng2	经度2
	 * @return
	 */
	public static double GetDistance(double lat1, double lng1, double lat2, double lng2)
	{
	   double radLat1 = rad(lat1);
	   double radLat2 = rad(lat2);
	   double a = radLat1 - radLat2;
	   double b = rad(lng1) - rad(lng2);
	   double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
	    Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	   s = s * EARTH_RADIUS;
	   s = Math.round(s * 10000.0) / 10000.0;
	   return s;
	}
	/**
	 * 计算两点间的距离
	 * @param la1	纬度1
	 * @param ln1	经度1
	 * @param la2	纬度2
	 * @param ln2	经度2
	 * @return
	 */
	public static double GetDistance(String la1, String ln1, String la2, String ln2)
	{
		double lat1 = Double.parseDouble(la1);
		double lng1 = Double.parseDouble(ln1);
		double lat2 = Double.parseDouble(la2);
		double lng2 = Double.parseDouble(ln2);
	   double radLat1 = rad(lat1);
	   double radLat2 = rad(lat2);
	   double a = radLat1 - radLat2;
	   double b = rad(lng1) - rad(lng2);
	   double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
	    Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	   s = s * EARTH_RADIUS;
	   s = Math.round(s * 10000.0) / 10000.0;
	   return s;
	}
	/**
	 * 根号2的值
	 */
	private final static BigDecimal ROOT_TWO = new BigDecimal(Math.sqrt(2d)); 
	/**
	 * 纬度差 0.000001° 所相差的距离
	 */
	private final static BigDecimal ACCURACY_LAT = new BigDecimal("0.00011131949");
	/**
	 * 精度值
	 */
	private final static BigDecimal ACCURACY = new BigDecimal("0.000001");
	/**
	 * 获取以指定经纬度为中心点且指定斜边大小的矩形四个点经纬度
	 * @param lon 经度
	 * @param lat 纬度
	 * @param distance 斜边长度(单位:km )
	 * @return 四个点坐标List 按顺时针顺序
	 */
	public static ArrayList<Location> getLocations(String lon,String lat,String distance){
		ArrayList<Location> bigs = new ArrayList<Location>();
		BigDecimal longtitude = new BigDecimal(lon);
		BigDecimal latitude = new BigDecimal(lat);
		BigDecimal tem_distance = new BigDecimal(distance);
		tem_distance = tem_distance.divide(ROOT_TWO,10,BigDecimal.ROUND_HALF_DOWN);
		BigDecimal dLon = tem_distance.divide(getACCURACY_Longtitude(lat), 0, BigDecimal.ROUND_HALF_DOWN).multiply(ACCURACY);
		BigDecimal dLat = tem_distance.divide(ACCURACY_LAT, 0, BigDecimal.ROUND_HALF_DOWN).multiply(ACCURACY);
		bigs.add(new Location(longtitude.subtract(dLon), latitude.add(dLat)));
		bigs.add(new Location(longtitude.add(dLon), latitude.add(dLat)));
		bigs.add(new Location(longtitude.add(dLon), latitude.subtract(dLat)));
		bigs.add(new Location(longtitude.subtract(dLon), latitude.subtract(dLat)));
		return bigs;
	}
}
