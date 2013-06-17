package com.ray.remotelauncher.client;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.InputFilter.LengthFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity  {

	private ViewPager mViewPager;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mViewPager = (ViewPager)findViewById(R.id.view_pager);
		TextView serverName = (TextView)findViewById(R.id.server_name);
		serverName.setOnClickListener(new ServerNameClickListener());
		serverName.setOnLongClickListener(new ServerNameLongClickListener());
		
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
	
	private class ServerNameClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_LONG).show();
		}
		
	}
	
	private class ServerNameLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			Toast.makeText(getApplicationContext(), "long click test", Toast.LENGTH_LONG).show();
			return true;
		}
		
	}
}
