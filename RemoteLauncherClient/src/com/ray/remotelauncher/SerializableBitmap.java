package com.ray.remotelauncher;

import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class SerializableBitmap implements Serializable{

	private static final long serialVersionUID = -4153908669258764530L;

	private byte[] bitmapBytes = null;
	
	SerializableBitmap(byte[] bitmapBytes) {
		this.bitmapBytes = bitmapBytes;
	}
	
	public byte[] getBitmapBytes() {
		return this.bitmapBytes;
	}
	
	public Drawable getDrawable(Resources res) {
		Drawable drawable = null;
		
		if (bitmapBytes != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			if (bitmap != null) {
				drawable = new BitmapDrawable(res, bitmap);
			}
		}
		return drawable;
	}
}
