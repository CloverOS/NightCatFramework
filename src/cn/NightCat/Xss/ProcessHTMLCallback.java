package cn.NightCat.Xss;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;;

/*
	Create by Crazyist at 2016年3月7日 下午5:01:26 Filename:ProcessHTMLCallback.java
	CopyRight © 2014-2016 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class ProcessHTMLCallback extends ParserCallback{
	
	private ReentrantLock lock = new ReentrantLock();
	private boolean ok = false;
	private String Str_Html = "";
	
	/**
	 * 创建一个Html处理容器
	 * @param tag 允许出现的白名单标签Map 值为 允许该标签出现的属性
	 */
	private Map<Tag, ArrayList<Attribute>> Tags = null;
	public ProcessHTMLCallback(Map<Tag, ArrayList<Attribute>> tags) {
		// TODO Auto-generated constructor stub
		super();
		this.Tags = tags;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
		// TODO Auto-generated method stub
		super.handleStartTag(t, a, pos);
		// 不在白名单列表,直接过滤
		if(!Tags.containsKey(t))
			return;
		Enumeration<Attribute> enumeration = (Enumeration<Attribute>)a.getAttributeNames();
		ArrayList<Attribute> attributes = Tags.get(t);
		Str_Html += "<" + t + " ";
		if(null == attributes){
			Str_Html += ">";
			return;
		}
		while(enumeration.hasMoreElements()){
			String name = enumeration.nextElement().toString();
			if(!attributes.contains(name))
				continue;
			Str_Html += name +"=\""+a.getAttribute(name)+"\" ";
		}
		Str_Html += ">";
	}
	
	@Override
	public void handleText(char[] data, int pos) {
		// TODO Auto-generated method stub
		super.handleText(data, pos);
		Str_Html += new String(data).trim();
	}
	
	@Override
	public void handleEndTag(Tag t, int pos) {
		// TODO Auto-generated method stub
		if(t == Tag.HTML && lock.isLocked())
			try {
				ok = true;
				lock.unlock();
			} catch (Exception e) {
				// TODO: handle exception
			}
		super.handleEndTag(t, pos);
		if(!Tags.containsKey(t))
			return;
		Str_Html += "</"+ t +">";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
		// TODO Auto-generated method stub
		super.handleSimpleTag(t, a, pos);
		// 不在白名单列表,直接过滤
		if(!Tags.containsKey(t))
			return;
		Enumeration<Attribute> enumeration = (Enumeration<Attribute>)a.getAttributeNames();
		ArrayList<Attribute> attributes = Tags.get(t);
		Str_Html += "<" + t + " ";
		if(null == attributes){
			Str_Html += "/>";
			return;
		}
		while(enumeration.hasMoreElements()){
			Object name = enumeration.nextElement();
			if(!attributes.contains(name))
				continue;
			Str_Html += name +"=\""+a.getAttribute(name)+"\" ";
		}
		Str_Html += "/>";
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		Lock();
		return Str_Html;
	}
	
	public String getHTML(){
		Lock();
		return Str_Html;
	}
	
	public void Lock(){
		try {
			if(!lock.isLocked() && !ok)
				lock.tryLock(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
