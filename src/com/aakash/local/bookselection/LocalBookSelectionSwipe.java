package com.aakash.local.bookselection;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aakash.local.bookitem.LocalDatabase;
import com.aakash.pustak.PustakHome;
import com.aakash.pustak.R;
import com.aakash.swipeytabs.SwipeyTabs;
import com.aakash.swipeytabs.SwipeyTabsAdapter;

public class LocalBookSelectionSwipe extends FragmentActivity {
	
	int selectionType;

	ArrayList<String> classList = new ArrayList<String>();
	String[] list;
	LocalDatabase db;
	private SwipeyTabs mTabs;
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_selection);

		db = new LocalDatabase(getBaseContext());
		setTitle("Aakash Pustak - Local Repository");
		classList = db.getColumnValues("booklist", "class", 1, "classsort", "ASC");
		list = classList.toArray(new String[classList.size()]);	
		Log.d("lists length", list.length + "");
		if(list.length == 0) {
			Intent backMain = new Intent(getApplicationContext(), PustakHome.class);
			backMain.putExtra("showAlert", true);
			Log.d("lists back", "trye");
			startActivity(backMain);
		}
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

		SwipeyTabsPagerAdapter adapter = new SwipeyTabsPagerAdapter(this, getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mTabs.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(mTabs);
		mViewPager.setCurrentItem(0);
	}

	private class SwipeyTabsPagerAdapter extends FragmentPagerAdapter implements SwipeyTabsAdapter {
		private final Context mContext;

		public SwipeyTabsPagerAdapter(Context context, FragmentManager fm) {
			super(fm);
			this.mContext = context;
		}

		@Override
		public Fragment getItem(int position) {
			return LocalBookSelectionFragment.newInstance(list[position]);
		}

		@Override
		public int getCount() {
			return list.length;
		}

		public TextView getTab(final int position, SwipeyTabs root) {
			TextView view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.swipey_tab_indicator, root, false);
			view.setText(list[position]);
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mViewPager.setCurrentItem(position);
				}
			});
			return view;
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book_selection_swipe, menu);
		return true;
	}

}
