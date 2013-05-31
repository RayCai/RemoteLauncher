package com.ray.remotelauncher;

import java.io.IOException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.IBinder;

public class BackgroundService extends Service {

    public static final String SERVICE_TYPE = "_http._tcp.";
    
    private JmDNS jmdns = null;
    private MulticastLock lock;
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		setupServer();
	}
	
	private void setupServer() {
		WifiManager wifi = (WifiManager) getSystemService(android.content.Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("mylockthereturn");
        lock.setReferenceCounted(true);
        lock.acquire();
		try {
			jmdns = JmDNS.create();
			jmdns.registerService(ServiceInfo.create(SERVICE_TYPE, "RemoteLauncher", 0, "RemoteLauncher Server"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		if (jmdns != null) {
			jmdns.unregisterAllServices();
			
			try {
				jmdns.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			jmdns = null;
		}
        lock.release();
		
		super.onDestroy();
	}
}
