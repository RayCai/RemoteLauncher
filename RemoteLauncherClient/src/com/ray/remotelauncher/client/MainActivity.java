package com.ray.remotelauncher.client;

import java.util.ArrayList;

import com.ray.remotelauncher.net.Connectivity;
import com.ray.remotelauncher.net.Connectivity.ConnectionListener;
import com.ray.remotelauncher.net.ServerDiscover;
import com.sothree.SlidingUpPanelLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

public class MainActivity extends FragmentActivity  {

	private ViewPager mViewPager;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private SlidingUpPanelLayout mSlidingLayout = null;
	private Handler mHandler = new MyHandler();
//	private ServerDiscover mServerDiscover;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mViewPager = (ViewPager)findViewById(R.id.view_pager);
		
//		mServerDiscover = new ServerDiscover(this);
		mSlidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
		
		final float density = getResources().getDisplayMetrics().density;
		mSlidingLayout.setPanelHeight((int) (30 * density + 0.5f));
		mSlidingLayout.setDragView(findViewById(R.id.server_name));
		mSlidingLayout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
		mSlidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                /*if (slideOffset < 0.2) {
                    if (getActionBar().isShowing()) {
                        getActionBar().hide();
                    }
                } else {
                    if (!getActionBar().isShowing()) {
                        getActionBar().show();
                    }
                }*/
            }

            @Override
            public void onPanelExpanded(View panel) {


            }

            @Override
            public void onPanelCollapsed(View panel) {

            }
        });
		
		fragments.add(Fragment.instantiate(this, FragmentAppList.class.getName()));
		fragments.add(Fragment.instantiate(this, FragmentRemoteControl.class.getName()));
		
		MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
	        @Override
	        public void onPageSelected(int position) {
	            getActionBar().setSelectedNavigationItem(position);
	        }
		});
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		
		MyTabListener tabListener = new MyTabListener();
		Tab tab = actionBar.newTab().setText(R.string.apps).setTabListener(tabListener);
		actionBar.addTab(tab);
		tab = actionBar.newTab().setText(R.string.remote_control).setTabListener(tabListener);
		actionBar.addTab(tab);
		
		Connectivity.getInstance().setOnConnectedListener(new SocketConnectionListener());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Connectivity.getInstance().disconnect();
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
	}

	private class MyTabListener implements TabListener {

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (mViewPager.getCurrentItem() != tab.getPosition())
				mViewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}
		
	}
	
	private class MyPagerAdapter extends FragmentPagerAdapter {
        
		public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            
        }
		
		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
		
	}
	
	private class SocketConnectionListener implements ConnectionListener {

		@Override
		public void onConnected() {
			Message msg = new Message();
			msg.what = 1;
			mHandler.sendMessage(msg);
		}
		
	}
	
	private class MyHandler extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: // socket connected
				if (mSlidingLayout.isExpanded()) {
					mSlidingLayout.collapsePane();
				}
				
				((FragmentAppList) fragments.get(0)).RefreshAppList();
				break;
			}
		}
		
	}
	
}
