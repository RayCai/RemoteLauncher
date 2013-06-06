package com.ray.remotelauncher;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class ApplicationInfo implements Serializable{

	private static final long serialVersionUID = 2980622959127678954L;
	
	public String mTitle;
	public String mPackageName;
	public transient String mClassName;
	public transient Drawable mIcon;
	
	ApplicationInfo(String title, String packageName, String className){
		mTitle = title;
		mPackageName = packageName;
		mClassName = className;
	}
}
