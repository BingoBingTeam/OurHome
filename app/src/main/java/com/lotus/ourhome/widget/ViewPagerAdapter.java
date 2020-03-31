package com.lotus.ourhome.widget;

import android.content.Context;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import java.util.Iterator;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

	public List<View> mListViews;
	public Context context;
	public List<String> mTitles;

	public ViewPagerAdapter(List<View> mListViews) {
		this.mListViews = mListViews;
	}

	public ViewPagerAdapter(List<View> listViews, List<String> title, Context context) {
		this(listViews, context);

		this.mTitles = title;
	}
	
	public ViewPagerAdapter(List<View> listViews, Context context) {
		this.context = context;
		this.mListViews = listViews;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

		if (mListViews != null && mListViews.size() > 0) {
			Iterator<View> iterator = mListViews.iterator();
			while (iterator.hasNext()) {
				View view = iterator.next();

				if (view.getPaddingBottom() == 0 ) {
					view.setPadding(view.getPaddingStart(), view.getPaddingTop(), view.getPaddingEnd(), 20);
				}
			}
		}
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(mListViews.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(mListViews.get(arg1), 0);
		return mListViews.get(arg1);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return mTitles.get(position);
	}
}
