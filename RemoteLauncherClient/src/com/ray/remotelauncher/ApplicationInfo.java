package com.ray.remotelauncher;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
	
	public byte[] GetIconBytes(){
		BitmapDrawable bitmap = (BitmapDrawable)mIcon;
		
		ByteArrayOutputStream baops = new ByteArrayOutputStream();
		bitmap.getBitmap().compress(CompressFormat.PNG, 100, baops);
		
		return baops.toByteArray();
	}
	
	public void SetIcon(Resources res, byte[] data){
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		if (bitmap != null) {
			mIcon = new BitmapDrawable(res, bitmap);
		}
	}
}
