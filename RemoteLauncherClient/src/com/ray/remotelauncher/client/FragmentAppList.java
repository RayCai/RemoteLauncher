package com.ray.remotelauncher.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.ray.remotelauncher.ApplicationInfo;
import com.ray.remotelauncher.SerializableBitmap;
import com.ray.remotelauncher.net.Connectivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

public class FragmentAppList extends Fragment {

	private ProgressBar 						mLoadProgressCircle = null;
	private ArrayList<HashMap<String, Object>> 	mAppList 			= new ArrayList<HashMap<String, Object>>();
	private SimpleAdapter 						mGridViewAdapter 	= null;
	private Connectivity						mSocketConn			= Connectivity.getInstance();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_app_list, container, false);

		GridView gridview = (GridView)view.findViewById(R.id.app_grid_view);
		mLoadProgressCircle = (ProgressBar)view.findViewById(R.id.app_progress_bar);
		
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
				if (mSocketConn.isConnected()) {
					mSocketConn.startActivity((short)arg2);
				}
			}
			
		});
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void RefreshAppList() {
		LoadAppsTask task = new LoadAppsTask();
		task.execute();
	}
	
	private class LoadAppsTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			if (mAppList.size() > 0) {
				mAppList.clear();
				mGridViewAdapter.notifyDataSetChanged();
			}
			mLoadProgressCircle.setVisibility(View.VISIBLE);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			Log.i("LoadAppsTask", "=======> Start");
			
			if (mSocketConn.connect("192.168.0.107", 2088))
			{
				File cache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RemoteLauncherClient/Cache");
				if (!cache.exists())
					cache.mkdir();
				
				ArrayList<ApplicationInfo> appList = mSocketConn.getApplist();
				Log.i("LoadAppsTask", "app count: " + appList.size());
				
				short i = 0;
				for (ApplicationInfo app: appList) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					
					Drawable icon = GetCachedIcon(app.mPackageName, app.mTitle, cache);
					if (icon == null) {
						// Get icon from server
						SerializableBitmap bitmap = mSocketConn.getAppIcon(i);
						if (bitmap != null) {
							icon = bitmap.getDrawable(getResources());
							CacheIcon(app.mPackageName, app.mTitle, cache, bitmap.getBitmapBytes());
						}
					}
					else {
						Log.i("LoadAppsTask", "Load " + app.mPackageName + " icon from SDCard");
					}
					
					if (icon != null)
						map.put("ItemImage", icon);
					else
						map.put("ItemImage", R.drawable.ic_launcher);
					map.put("ItemText", app.mTitle);
					mAppList.add(map);
					
					i++;
				}

			}
			Log.i("LoadAppsTask", "=======> Done");
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			mGridViewAdapter.notifyDataSetChanged();
			mLoadProgressCircle.setVisibility(View.GONE);
		}
		
		private Drawable GetCachedIcon(String packageName, String appName, File cache) {
			Drawable icon = null;
			
			if (cache != null && cache.exists()) {
				String fileName = packageName + "." + appName + ".png";
				File file = new File(cache, fileName);
				if (file.exists()) {
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(file);
						Bitmap bmp = BitmapFactory.decodeStream(fis);
						icon = new BitmapDrawable(getResources(), bmp);
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally{
						try {
							if(fis != null){
								fis.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return icon;
		}
		
		private void CacheIcon(String packageName, String appName, File cache, byte[] data) {
			if (cache != null && cache.exists()) {
				String fileName = packageName + "." + appName + ".png";
				File file = new File(cache, fileName);

				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(file);
					fos.write(data);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					try {
						if(fos != null){
							fos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
