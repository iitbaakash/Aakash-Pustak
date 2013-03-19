package com.aakash.online.bookselection;

import java.util.ArrayList;

import android.content.Context;
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

import com.aakash.online.bookitem.OnlineDatabase;
import com.aakash.pustak.R;
import com.aakash.swipeytabs.SwipeyTabs;
import com.aakash.swipeytabs.SwipeyTabsAdapter;

public class OnlineBookSelectionSwipe extends FragmentActivity {
	
	int selectionType;

	ArrayList<String> classList = new ArrayList<String>();
	String[] list;
	OnlineDatabase db;
	private SwipeyTabs mTabs;
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_selection);

		db = new OnlineDatabase(getBaseContext());
		setTitle("Aakash Pustak - Online Repository");
		//classList = db.getClassList();
		//list = classList.toArray(new String[classList.size()]);
		list  = getIntent().getStringArrayExtra("classList");
		Log.d("online-list", list.toString());
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
			return OnlineBookSelectionFragment.newInstance(list[position]);
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
