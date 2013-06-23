package com.ray.remotelauncher.net;

import java.net.InetAddress;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

public class ServerDiscover {

    public static final String SERVICE_TYPE = "_http._tcp.local.";//
    public static final String TAG			= "ServerDiscover";
    
    private NsdManager 						mNsdManager			= null;
    private NsdManager.ResolveListener		mResolveListener	= null;
    private NsdManager.DiscoveryListener	mDiscoveryListener	= null;
    private ServerFoundListener				mServerFoundListener = null;
    private boolean							mDiscovering		= false;
    
	public ServerDiscover(Context context) {
		mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
		
		Initialize();
	}
	
    private void Initialize() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);
                if (mServerFoundListener != null) {
                	mServerFoundListener.OnServerFound(serviceInfo.getServiceName(), serviceInfo.getHost(), serviceInfo.getPort());
                }
            }
        };
        
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else {
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);
                
            }
            
            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);        
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                //mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void discoverServices() {
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        mDiscovering = true;
    }
    
    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        mDiscovering = false;
    }
    
    public void setServerFoundListener(ServerFoundListener listener){
    	mServerFoundListener = listener;
    }
    
    public boolean isDiscovering() {
    	return mDiscovering;
    }
    
    public interface ServerFoundListener {
    	public void OnServerFound(String serverName, InetAddress ip, int port);
    }
}
