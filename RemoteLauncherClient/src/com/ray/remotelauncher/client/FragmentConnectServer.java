package com.ray.remotelauncher.client;

import com.ray.remotelauncher.net.Connectivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragmentConnectServer extends Fragment {

	private TextView mIpInput = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_connect_server, container, false);
		
		mIpInput = (TextView) view.findViewById(R.id.server_address);
		Button btnConnect = (Button) view.findViewById(R.id.btn_connect);
		btnConnect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						String ipAddr = mIpInput.getText().toString();
						Connectivity.getInstance().connect(ipAddr, 2088);
					}
				}).start();
			}
		});
		
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	

}
