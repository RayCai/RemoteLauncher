package com.ray.remotelauncher;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackgroundService extends Service {

	private ServerSocket mServerSocket = null;
    private Socket mSocket = null;
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		new Thread(new SocketThread()).start();
	}

	@Override
	public void onDestroy() {

		
		super.onDestroy();
	}
	
	private class SocketThread implements Runnable {

		@Override
		public void run() {
			try {
				mServerSocket = new ServerSocket(2088);

            	if (mSocket != null && mSocket.isConnected()) {
            		mSocket.close();
            	}
            	mSocket = mServerSocket.accept();

            	new Thread(new ReceivingThread()).start();
            	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class ReceivingThread implements Runnable {

		@Override
		public void run() {
        	if (mSocket == null || !mSocket.isConnected()) {
				return;
        	}
        	
        	try {
				InputStream in = mSocket.getInputStream();
				byte[] buffer = new byte[512];
				int len = 0;
				
				while ((len = in.read(buffer)) >= 0) {
					if (len >= 1) {
						
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
