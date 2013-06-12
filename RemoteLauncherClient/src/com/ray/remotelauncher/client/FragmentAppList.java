package com.ray.remotelauncher.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import com.ray.remotelauncher.ApplicationInfo;
import com.ray.remotelauncher.SerializableBitmap;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

public class FragmentAppList extends Fragment {

	private ArrayList<HashMap<String, Object>> mAppList = new ArrayList<HashMap<String, Object>>();
	private SimpleAdapter mGridViewAdapter = null;
	private Socket socket = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_app_list, container, false);

		GridView gridview = (GridView)view.findViewById(R.id.app_grid_view);
		
		mGridViewAdapter = new SimpleAdapter(view.getContext(),
														mAppList,
														R.layout.launcher_item,
														new String[] {"ItemImage","ItemText"},
														new int[] {R.id.itemImage,R.id.itemText});
		mGridViewAdapter.setViewBinder(new ViewBinder(){
			public boolean setViewValue(View view, Object data, String textRepresentation){
				if(view instanceof ImageView && data instanceof Drawable){
					ImageView iv= (ImageView)view;
					iv.setImageDrawable((Drawable)data);
					return true;
				}
				else
					return false;
			}
		});
		
		gridview.setAdapter(mGridViewAdapter);
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (socket != null && socket.isConnected()) {
					try {
						OutputStream out = socket.getOutputStream();
						
						byte[] buf = new byte[3];
						buf[0] = 2;
						byte[] pos = Short2Byte((short)arg2);
						System.arraycopy(pos, 0, buf, 1, pos.length);
						
						out.write(buf);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		});

		LoadAppsTask task = new LoadAppsTask();
		task.execute();
		
		return view;
	}
	
	private byte[] Short2Byte(short num) {
		byte[] buf = new byte[2];
		buf[0] = (byte)(num & 0xFF);
		buf[1] = (byte)(num >> 8);

		return buf;
	}

	private class LoadAppsTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				if (socket == null || !socket.isConnected()) {
					socket = new Socket("10.0.2.2", 3000);
				}
				OutputStream out = socket.getOutputStream();
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				
				byte[] buffer = new byte[5];
				buffer[0] = 1;
				
				out.write(buffer, 0, 1);
				ArrayList<ApplicationInfo> appList = (ArrayList<ApplicationInfo>)ois.readObject();
				Log.i("LoadAppsTask", "=======>" + appList.size());
				
				mAppList.clear();
				short i = 0;
				for (ApplicationInfo app: appList) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					
					// Get icon from server
					buffer[0] = 3;
					byte[] pos = Short2Byte((short)i);
					System.arraycopy(pos, 0, buffer, 1, pos.length);

					out.write(buffer);
					SerializableBitmap bitmap = (SerializableBitmap)ois.readObject();
					app.SetIcon(getResources(), bitmap.getBitmapBytes());
					
					if (app.mIcon != null) {
						map.put("ItemImage", app.mIcon);
					}
					else {
						map.put("ItemImage", R.drawable.ic_launcher);
					}
					map.put("ItemText", app.mTitle);
					mAppList.add(map);
					
					i++;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			mGridViewAdapter.notifyDataSetChanged();
		}
		
	}
}
