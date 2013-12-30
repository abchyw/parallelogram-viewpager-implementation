package com.even.graphiclab.parallelogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.labatory.R;

public class ParallelogramPager extends ViewPager {
	private static final float SCALE = ParallelogramView.getScale() + .03f;
	private FragmentActivity mActivity;

	public ParallelogramPager(Context context) {
		this(context, null);
	}

	public ParallelogramPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mActivity = (FragmentActivity) context;
		init();
	}

	private void init() {
		setPageTransformer(true, transformer);
		setOffscreenPageLimit(2);
		setOnPageChangeListener(listener);

	}

	private ViewPager.PageTransformer transformer = new PageTransformer() {
		@SuppressLint("NewApi")
		@Override
		public void transformPage(View v, float postion) {
			int pageWidth = v.getWidth();
			ParallelogramView pv = (ParallelogramView) ((ViewGroup) v).getChildAt(0);

			v.setScaleX(SCALE);
			v.setScaleY(SCALE);
			// content.setScaleX(1 / SCALE);
			// content.setScaleY(1 / SCALE);

			if (postion < -2) {
				v.setAlpha(0);
				// pv.scrollBackground(0, 0);
			} else if (postion <= -1) {
				v.setAlpha(1);
				if (pv.getBgTranslateX() != pageWidth / 2) {
					pv.scrollBackground(pageWidth / 2, 0);
				}
				// v.setTranslationX(pageWidth * -(postion + 1));
			} else if (postion <= 0) {
				v.setAlpha(1);
				v.setTranslationX(0);
				pv.scrollBackground((int) (-postion * pageWidth / 2), 0);
			} else if (postion <= 1) {
				v.setAlpha(1);
				v.setTranslationX(pageWidth * 0.3f * -postion);
				pv.scrollBackground((int) (-postion * pageWidth / 2), 0);
			} else if (postion <= 2) {
				v.setAlpha(1);
				v.setTranslationX(pageWidth * 0.3f * -postion);
				if (pv.getBgTranslateX() != -pageWidth / 2) {
					pv.scrollBackground(-pageWidth / 2, 0);
				}
			} else {
				v.setAlpha(0);
			}

		}
	};

	protected int mCurrentPosition;

	private OnPageChangeListener listener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {

		}

		@Override
		public void onPageScrolled(int position, float arg1, int arg2) {

		}

		@SuppressLint("NewApi")
		@Override
		public void onPageScrollStateChanged(int state) {

			if (state == ViewPager.SCROLL_STATE_IDLE) {
				int position = getCurrentItem();
				if (position == mCurrentPosition) {
					return;
				}
				mCurrentPosition = position;
				int count = getAdapter().getCount();

				// transformation
				for (int i = 0; i < count; i++) {
					// ParallelogramPageFragment f = (ParallelogramPageFragment) mActivity.getSupportFragmentManager().findFragmentByTag(
					// "android:switcher:" + R.id.pager + ":" + i);
					ParallelogramPageFragment f = (ParallelogramPageFragment) ((ParallelogramePagerAdapter) getAdapter()).getFragment(i);

					if (f != null && f.isTransformed()) {
						f.reverseTransform();
					}
				}
				// trick to obtain the current fragment
				ParallelogramPageFragment f = (ParallelogramPageFragment) mActivity.getSupportFragmentManager().findFragmentByTag(
						"android:switcher:" + R.id.pager + ":" + position);
				Log.d("onPageChange", "current item: " + position);
				if (f != null) {
					f.transform();
				}
			}
		}
	};

}
