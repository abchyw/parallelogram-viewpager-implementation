package com.even.graphiclab.parallelogram;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.even.graphiclab.DummyFragment;

public class ParallelogramePagerAdapter extends FragmentPagerAdapter {

	private int imgArr[];

	private Map<Integer, ParallelogramPageFragment> mPageReferenceMap = new HashMap<Integer, ParallelogramPageFragment>();

	public ParallelogramePagerAdapter(FragmentManager fm, int imgArr[]) {
		super(fm);
		this.imgArr = imgArr;
	}

	@Override
	public Fragment getItem(int arg0) {
		ParallelogramPageFragment f;
		if (arg0 == getCount() - 1) {
			f = DummyFragment.newInstanct(imgArr[arg0], ParallelogramView.LAST_INDEX);
		} else {
			f = DummyFragment.newInstanct(imgArr[arg0], arg0);
		}
		mPageReferenceMap.put(arg0, f);

		return f;
	}

	@Override
	public int getCount() {
		return imgArr.length;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}

	public Fragment getFragment(int index) {
		return mPageReferenceMap.get(index);
	}

}
