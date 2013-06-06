package com.ray.remotelauncher;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;

public class BackgroundService extends Service {

    private static final String TAG = "RemoteLauncher";
	private ServerSocket mServerSocket = null;
    private Socket mSocket = null;
    private ArrayList<ApplicationInfo> appList = null;
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i(TAG, "BackgroundService, Service created");
		new Thread(new SocketThread()).start();
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}

	/**
	 * Loads the list of installed applications in mApplications.
	 */
	private void loadApplications() {
		PackageManager manager = getPackageManager();
		
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
		
		if (apps != null) {
			final int count = apps.size();
			
			if (appList == null) {
				appList = new ArrayList<ApplicationInfo>(count);
			}
			appList.clear();
			
			for (int i = 0; i < count; i++) {
				ResolveInfo info = apps.get(i);
				ApplicationInfo application = new ApplicationInfo(info.loadLabel(manager).toString()
						, info.activityInfo.applicationInfo.packageName
						, info.activityInfo.name);

				application.mIcon = info.activityInfo.loadIcon(manager);
				
				appList.add(application);
			}
		}
	}
	
	private void openActivity(short idx) {
		if (appList != null && idx >=0 && idx < appList.size()) {
			ApplicationInfo app = appList.get(idx);
			
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setComponent(new ComponentName(app.mPackageName, app.mClassName));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			
			startActivity(intent);
		}
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
				loadApplications();
				
				mServerSocket = new ServerSocket(2088);

				while (true) {
					if (mSocket != null && mSocket.isConnected()) {
						Log.i(TAG, "BackgroundService, One socket is connected, close it first");
	            		mSocket.close();
	            	}
					Log.i(TAG, "BackgroundService, Start socket listen");
	            	mSocket = mServerSocket.accept();
	            	Log.i(TAG, "BackgroundService, New socket connect, IP: " + mSocket.getInetAddress().toString() + ", Port: " + mSocket.getPort());
	            	
					int len = 0;
					byte[] buffer = new byte[512];
	            	InputStream in = mSocket.getInputStream();
    				ObjectOutputStream oos = new ObjectOutputStream(mSocket.getOutputStream());
    				
	            	while (true) {
	    				len = in.read(buffer);
						Log.i(TAG, "BackgroundService, Read buffer, len = " + len);
						
	    				if (len < 0) break;
	    				else if (len >= 1) {
	    					SOCKET_CMD cmd = ParseCommand(buffer[0]);
	    					switch (cmd) {
							case REFRESH_LIST:
								Log.i(TAG, "BackgroundService, CMD: Refresh List");
								
								oos.writeObject(appList);
								break;
							case OPEN_ACTIVITY:
								Log.i(TAG, "BackgroundService, CMD: Open Activity");
								if (len >= 3) {
									short appIdx = Byte2Short(buffer, 1);
									Log.i(TAG, "BackgroundService, CMD: Open Activity, target: " + appIdx);
									
									openActivity(appIdx);
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
