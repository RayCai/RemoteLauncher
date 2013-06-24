package com.ray.remotelauncher.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.ray.remotelauncher.ApplicationInfo;
import com.ray.remotelauncher.SerializableBitmap;

public class Connectivity {

	private Socket 				mSocket				= null;
	private OutputStream 		mOut				= null;
	private ObjectInputStream	mOis				= null;
	private ConnectionListener	mListener			= null;
	private final static byte	Refress_List		= 0x1;
	private final static byte	Start_Activity		= 0x2;
	private final static byte	Get_App_Icon		= 0x3;
	
	public interface ConnectionListener {
		void onConnected();
	}
	
	private static Connectivity conn				= null;
	
	public static Connectivity getInstance() {
		if (conn == null) {
			conn = new Connectivity();
		}
		return conn;
	} 
	
	private byte[] Short2Byte(short num) {
		byte[] buf = new byte[2];
		buf[0] = (byte)(num & 0xFF);
		buf[1] = (byte)(num >> 8);

		return buf;
	}
	
	public void setOnConnectedListener(ConnectionListener listerner) {
		mListener = listerner;
	}
	
	public boolean connect(String server, int port) {
		try {
			if (isConnected()) return true;
			
			mSocket = new Socket(server, port);
			mOut = mSocket.getOutputStream();
			mOis = new ObjectInputStream(mSocket.getInputStream());
			
			// call listener
			if (mListener != null) {
				mListener.onConnected();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean disconnect() {
		try {
			if (isConnected()) {
				mSocket.close();
				mSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean isConnected() {
		if (mSocket != null && mSocket.isConnected()) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ApplicationInfo> getApplist() {
		if (!isConnected()) return null;
		
		ArrayList<ApplicationInfo> appList = null;
		try {
			mOut.write(new byte[]{Refress_List});
			appList = (ArrayList<ApplicationInfo>)mOis.readObject();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return appList;
	}
	
	public void startActivity(short appIdx) {
		if (!isConnected()) return;
		
		byte[] buf = new byte[3];
		buf[0] = Start_Activity;
		byte[] pos = Short2Byte(appIdx);
		System.arraycopy(pos, 0, buf, 1, pos.length);
		
		try {
			mOut.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SerializableBitmap getAppIcon(short appIdx) {
		if (!isConnected()) return null;
		
		byte[] buf = new byte[3];
		buf[0] = Get_App_Icon;
		byte[] pos = Short2Byte(appIdx);
		System.arraycopy(pos, 0, buf, 1, pos.length);
		
		try {
			mOut.write(buf);
			
			return (SerializableBitmap)mOis.readObject();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
