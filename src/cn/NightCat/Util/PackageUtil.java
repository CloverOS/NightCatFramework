package cn.NightCat.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.NightCat.Config.NCConfig;

/*
	Create by Crazyist at 2015年8月14日 上午9:38:01 Filename:PackageUtil
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class PackageUtil {
    private static ArrayList<String> getClassName(String filePath, List<String> className) {  
        ArrayList<String> myClassName = new ArrayList<String>();  
        File file = new File(filePath);  
        File[] childFiles = file.listFiles();  
        for (File childFile : childFiles) {  
            if (!childFile.isDirectory()) {
                String childFilePath = childFile.getPath();  
                if(childFilePath.endsWith("class")){
                	childFilePath = childFilePath.replace(file.getPath(), "").replace("\\", "").replace(".class", "").replace("/", "");
                	childFilePath = NCConfig.PackageName + "." + childFilePath;
                	myClassName.add(childFilePath);  
                }
            }
        }
        return myClassName;  
    }
    /***
	 * 获取包内所有类
	 * @param packageName
	 * @return
	 */
	public static ArrayList<String> getClassName(String packageName) {  
        String filePath = NCConfig.PackagePath + packageName.replace(".", "/");  
        ArrayList<String> fileNames = getClassName(filePath, null);  
        return fileNames;  
    }
    
}
