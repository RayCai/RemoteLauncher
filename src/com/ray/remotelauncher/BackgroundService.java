package com.ray.remotelauncher;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BackgroundService extends Service {

    private static final String TAG = "RemoteLauncher";
	private ServerSocket mServerSocket = null;
    private Socket mSocket = null;
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i(TAG, "BackgroundService created");
		new Thread(new SocketThread()).start();
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}

	private enum SOCKET_CMD {NONE, REFRESH_LIST, OPEN_ACTIVITY};
	
	private class SocketThread implements Runnable {

		private short Byte2Short(byte[] buf, int idx) {
			return (short)(buf[idx] | (buf[idx + 1] << 8));
		}
		
		private SOCKET_CMD ParseCommand(byte cmd) {
			switch (cmd) {
			case 1:
				return SOCKET_CMD.REFRESH_LIST;
			case 2:
				return SOCKET_CMD.OPEN_ACTIVITY;
			}
			return SOCKET_CMD.NONE;
		}
		
		@Override
		public void run() {
			try {
				mServerSocket = new ServerSocket(2088);

				while (true) {
					if (mSocket != null && mSocket.isConnected()) {
	            		mSocket.close();
	            	}
					Log.i(TAG, "Start socket listen");
	            	mSocket = mServerSocket.accept();
	            	Log.i(TAG, "New socket connect, IP: " + mSocket.getInetAddress().toString() + ", Port: " + mSocket.getPort());
	            	
					int len = 0;
					byte[] buffer = new byte[512];
	            	InputStream in = mSocket.getInputStream();
    				ObjectOutputStream oos = new ObjectOutputStream(mSocket.getOutputStream());
	            	while (true) {
	    				len = in.read(buffer);
						Log.i(TAG, "Read buffer, len = " + len);
						
	    				if (len < 0) break;
	    				else if (len >= 1) {
	    					SOCKET_CMD cmd = ParseCommand(buffer[0]);
	    					switch (cmd) {
							case REFRESH_LIST:
								Log.i(TAG, "CMD: Refresh List");
								
								ArrayList<String> list = new ArrayList<String>();
								list.add("test1");
								list.add("test2");
								list.add("test3");
								list.add("test4");
								list.add("test5");
								list.add("test6");
								list.add("test7");
								list.add("test8");
								list.add("test9");
								
								oos.writeObject(list);
								break;
							case OPEN_ACTIVITY:
								Log.i(TAG, "CMD: Open Activity");
								if (len >= 3) {
									short appId = Byte2Short(buffer, 1);
									Log.i(TAG, "CMD: Open Activity, target: " + appId);
								}
								break;
							default:
								break;
							}
	    				}
	            	}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
