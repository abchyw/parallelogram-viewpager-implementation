package com.even.graphiclab;

import java.util.HashMap;
import java.util.Map;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import com.even.graphiclab.DummyFragment.Callback;
import com.even.graphiclab.parallelogram.ParallelogramView;
import com.even.graphiclab.parallelogram.ParallelogramePagerAdapter;
import com.even.graphiclab.parallelogram.ParentViewPager;
import com.example.labatory.R;

public class PagerMainActivity extends FragmentActivity implements Callback {
	protected static final ParallelogramView v = null;
	protected static final String TAG = PagerMainActivity.class.getSimpleName();
	int imgArr[] = { R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5 };

	// the background ViewPager
	private ViewPager mBgPager;
	private FragmentPagerAdapter mAdapter;

	public static Bitmap mCache;
	private Map<Integer, Fragment> mFragMap = new HashMap<Integer, Fragment>();

	private boolean isAnimating = false;
	private ViewPropertyAnimator anim;
	private int mLastCurrent = -1;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pager_main);

		final ParentViewPager v = (ParentViewPager) findViewById(R.id.pager2);
		v.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return imgArr.length;
			}

			@Override
			public Fragment getItem(int position) {
				Fragment f = new SecondFragment();
				mFragMap.put(position, f);
				return f;
			}
		});

		mBgPager = (ViewPager) findViewById(R.id.pager);

		v.setChildPager(mBgPager);
		mAdapter = new ParallelogramePagerAdapter(getSupportFragmentManager(), imgArr);
		mBgPager.setAdapter(mAdapter);

		v.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_IDLE && mLastCurrent != v.getCurrentItem()) {
					for (Fragment f : mFragMap.values()) {
						ViewGroup content = (ViewGroup) f.getView();
						if (content != null) {
							content.getChildAt(0).setAlpha(0);
						}
					}

					int position = v.getCurrentItem();
					mLastCurrent = position;
					Fragment f = mFragMap.get(position);
					if (anim != null && isAnimating) {
						anim.cancel();
					}

					if (f != null) {
						ViewGroup content = (ViewGroup) f.getView();
						final View root = content.getChildAt(0);
						anim = root.animate();
						anim.alpha(1).setDuration(500).start();

						anim.setListener(new AnimatorListener() {
							@Override
							public void onAnimationStart(Animator animation) {
								isAnimating = true;
							}

							@Override
							public void onAnimationRepeat(Animator animation) {
							}

							@Override
							public void onAnimationEnd(Animator animation) {
								isAnimating = false;
							}

							@Override
							public void onAnimationCancel(Animator animation) {
								root.setAlpha(0);
							}
						});

					}
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		View v = getWindow().getDecorView().findViewById(android.R.id.content);
		v.setDrawingCacheEnabled(false);
	}

	@Override
	public void onButtonClick(Bitmap bmp) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pager_main, menu);
		return true;
	}

}
