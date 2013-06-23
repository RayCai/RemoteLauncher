package com.ray.remotelauncher.client;

import java.net.InetAddress;
import java.util.ArrayList;

import com.ray.remotelauncher.net.ServerDiscover;
import com.ray.remotelauncher.net.ServerDiscover.ServerFoundListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FragmentServerList extends Fragment {

	private ListView		mServerListView		= null;
	private ServerDiscover	mServerDiscover		= null;
	private ArrayList<String> mServerList		= new ArrayList<String>();
	private ArrayAdapter<String> mServerListAdapter = null;
	private Handler			mServerFoundHandler = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_server_list, container, false);

		mServerListAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, mServerList);
		mServerListView = (ListView) view.findViewById(R.id.server_list);
		mServerListView.setAdapter(mServerListAdapter);
		
		mServerFoundHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				mServerListAdapter.notifyDataSetChanged();
			}
			
		};
		
		
		return view;
	}

	@Override
	public void onPause() {
		stopDiscovery();
		
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void startDiscovery() {
		if (mServerDiscover == null) {
			mServerDiscover = new ServerDiscover(getActivity());
			mServerDiscover.setServerFoundListener(new ServerFound());
		}
		
		if (!mServerDiscover.isDiscovering()) {
			mServerList.clear();
			mServerListAdapter.notifyDataSetChanged();
			
			mServerDiscover.discoverServices();
		}
	}
	
	public void stopDiscovery() {
		if (mServerDiscover != null && mServerDiscover.isDiscovering()) {
			mServerDiscover.stopDiscovery();
		}
	}
	
	private class ServerFound implements ServerFoundListener {

		@Override
		public void OnServerFound(String serverName, InetAddress ip, int port) {
			mServerList.add(serverName);
			
			Message msg = new Message();
			msg.what = 1;
			mServerFoundHandler.sendMessage(msg);
		}
		
	}
	
	public void OnServerFound(String serverName, InetAddress ip, int port) {
		mServerList.add(serverName);
		
		Message msg = new Message();
		msg.what = 1;
		mServerFoundHandler.sendMessage(msg);
	}
}
