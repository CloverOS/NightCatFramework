package cn.NightCat.LBS;

import java.math.BigDecimal;

/*
	Create by Crazyist at 2015年11月11日 下午7:25:50 Filename:Location
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class Location {
	public static final String TAG = "Location.json";
	private BigDecimal lon;
	private BigDecimal lat;
	public Location(BigDecimal lon,BigDecimal lat){
		this.lon = lon;
		this.lat = lat;
	}
	public BigDecimal getLon() {
		return lon;
	}
	public BigDecimal getLat() {
		return lat;
	}
	
}
