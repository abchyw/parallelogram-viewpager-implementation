package com.even.graphiclab.parallelogram;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * a viewpager which has another viewpager to interact with 
 * @author hyw
 *
 */
public class ParentViewPager extends ViewPager {

	private ViewPager childPager;

	public ParentViewPager(Context context) {
		this(context, null);
	}

	public ParentViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// let the child take care of the touch event
		if (childPager != null) {
			childPager.onTouchEvent(arg0);
		}
		return super.onTouchEvent(arg0);
	}

	public void setChildPager(ViewPager childPager) {
		this.childPager = childPager;
	}

}
