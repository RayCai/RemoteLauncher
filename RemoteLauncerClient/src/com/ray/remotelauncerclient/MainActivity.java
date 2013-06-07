package com.ray.remotelauncerclient;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class MainActivity extends Activity {

	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		viewPager = (ViewPager)findViewById(R.id.view_pager);
		
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
			if (viewPager.getCurrentItem() != tab.getPosition())
				viewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}
		
	}
	
	private class MyPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> list = new ArrayList<Fragment>();
        
		public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
		
		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
