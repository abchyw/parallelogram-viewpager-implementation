package com.even.graphiclab.parallelogram;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.even.graphiclab.parallelogram.ParallelogramView.ImageTransformer;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public abstract class ParallelogramPageFragment extends Fragment {
	private static final String TAG = ParallelogramPageFragment.class.getSimpleName();
	private static int ANIM_DURATION = 500;
	private static int ANIM_MAX_VALUE = 100;

	private ParallelogramView mParallelogramView;

	private ValueAnimator anim;
	private int endValue;
	private TransformationProxy mTransformProxy = new TransformationProxy();

	private boolean isTransformed;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		// View root = inflater.inflate(getLayoutId(), container, false);
		// mParallelogramView = (ParallelogramView) root.findViewById(R.id.parallelogram_view);
		// if (mParallelogramView == null) {
		// throw new IllegalStateException("ParallelogramPageFragment needs a ParallelogramView whose id is R.id.parallelogram_view in its layout");
		// }

		mParallelogramView = new ParallelogramView(getActivity());
		mParallelogramView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mParallelogramView.setTransformer(mTransformProxy);
		mParallelogramView.setOriginalImage(getImage());
		mParallelogramView.setIndex(getIndex());

		return mParallelogramView;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onDestroy() {
		mParallelogramView.doRecycle();
		super.onDestroy();
	}

	protected void resetImage() {
		mParallelogramView.resetImage();
	}

	protected void executeImageTransformation() {
		mParallelogramView.executeImageTransformation();
	}

	protected void reShape() {
		mParallelogramView.reShape();
	}

	/**
	 * begin to reverse the transformation previous taken.
	 */
	@SuppressLint("NewApi")
	public void reverseTransform() {
		// cancel the previous unfinished animation.
		if (anim != null) {
			anim.cancel();
		}

		isTransformed = false;

		anim = ValueAnimator.ofInt(0, ANIM_MAX_VALUE);
		anim.setDuration(ANIM_DURATION);
		anim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int f = (Integer) animation.getAnimatedValue();
				// Matrix m = new Matrix(mtx);
				// mtx.postTranslate(1f, 0);
				mTransformProxy.fraction = (ANIM_MAX_VALUE - f) / (float) ANIM_MAX_VALUE * endValue / ANIM_MAX_VALUE;
				executeImageTransformation();
			}
		});

		anim.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// note that the animation has finished. when the animation is cancelled, the fraction of the darkness is max - endvalue
				endValue = ANIM_MAX_VALUE - (Integer) anim.getAnimatedValue();
				anim = null;
				resetImage();
				Log.d(TAG, "end");
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				Log.d(TAG, "orig cancel");
			}
		});
		anim.start();
	}

	/**
	 * begin the image transformation.
	 */
	@SuppressLint("NewApi")
	public void transform() {
		// View v = sImg;

		if (anim != null) {
			anim.cancel();
		}

		anim = ValueAnimator.ofInt(0, ANIM_MAX_VALUE);
		anim.setDuration(ANIM_DURATION);
		// final ParallelogramView sv = (ParallelogramView) v.findViewById(R.id.image_view);
		anim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int f = (Integer) animation.getAnimatedValue();
				// Matrix m = new Matrix(mtx);
				// mtx.postTranslate(1f, 0);
				mTransformProxy.fraction =
				// endValue / (float) ANIM_MAX_VALUE + (ANIM_MAX_VALUE - endValue) * f / (float) ANIM_MAX_VALUE;
				(endValue + (ANIM_MAX_VALUE - endValue) * f / (float) ANIM_MAX_VALUE) / (float) ANIM_MAX_VALUE;
				executeImageTransformation();
			}
		});
		isTransformed = true;

		anim.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// when transformation is cancelled, fraction of becoming darker is endValue
				endValue = (Integer) anim.getAnimatedValue();
				// endValue = ANIM_MAX_VALUE;
				anim = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				Log.d(TAG, "cancel");
			}
		});
		anim.start();
	}

	/**
	 * method to tell if the ParallelogramView has finished its transformation. the flag is set to true in the method transform();
	 * 
	 * @return
	 */
	public boolean isTransformed() {
		return isTransformed;
	}

	/**
	 * the index of the page within the ViewPager
	 * 
	 * @return
	 */
	abstract public int getIndex();

	/**
	 * return the image to display
	 * 
	 * @return
	 */
	abstract public Bitmap getImage();

	/**
	 * return the layoutId of the Fragment
	 * 
	 * @return
	 */
	abstract public int getLayoutId();

	/**
	 * update the Bitmap during transformation
	 * 
	 * @param b
	 *            bitmap on the transformation takes on
	 * @param fraction
	 *            fraction of the entire transformation
	 */
	abstract public void updateImageInTransformation(Bitmap b, float fraction);

	abstract public void initImage(Bitmap ori);

	private class TransformationProxy implements ImageTransformer {
		private float fraction;

		@Override
		public void updateImageInTransformation(Bitmap b) {
			ParallelogramPageFragment.this.updateImageInTransformation(b, fraction);
		}

		@Override
		public void initImage(Bitmap ori) {
			ParallelogramPageFragment.this.initImage(ori);
		}

	}

}
