package com.even.graphiclab.parallelogram;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ParentViewPager extends ViewPager {

	private ViewPager childPager;

	public ParentViewPager(Context context) {
		this(context, null);
	}

	public ParentViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);

		// setPageTransformer(true, new PageTransformer() {
		// @Override
		// public void transformPage(View v, float position) {
		// if (position < -1) {
		// v.setAlpha(0);
		// // v.setTranslationX(pageWidth * -(postion + 1));
		// } else if (position <= 0) {
		// v.setAlpha((float) (1 - Math.sqrt(-position)));
		// } else if (position <= 1) {
		// v.setAlpha(0);
		// v.setAlpha((float) (1 - Math.sqrt(position)));
		// } else {
		// v.setAlpha(0);
		// }
		// }
		// });
		setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				switch (state) {
				case ViewPager.SCROLL_STATE_IDLE:

					break;
				default:
					break;
				}

			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (childPager != null) {
			childPager.onTouchEvent(arg0);
		}
		return super.onTouchEvent(arg0);
	}

	public void setChildPager(ViewPager childPager) {
		this.childPager = childPager;
	}

}
