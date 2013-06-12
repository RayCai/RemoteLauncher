package com.ray.remotelauncher;

import java.io.Serializable;

public class SerializableBitmap implements Serializable{

	private static final long serialVersionUID = -4153908669258764530L;

	private byte[] bitmapBytes = null;
	
	SerializableBitmap(byte[] bitmapBytes) {
		this.bitmapBytes = bitmapBytes;
	}
	
	public byte[] getBitmapBytes() {
		return this.bitmapBytes;
	}
}
