package com.even.graphiclab.parallelogram;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;

import com.example.labatory.R;

/**
 * TODO: document your custom view class. assume that the view fills up the whole screen.
 */
public class ParallelogramView extends View {
	private static final String TAG = ParallelogramView.class.getSimpleName();

	public static final int LAST_INDEX = -1;
	// the width and height of the view
	private int mWidth, mHeight;
	private ShapeDrawable mDrawable;

	private Bitmap bmpOri, bmpToDraw;
	private Path mPath;
	private PathShape mShape;
	private Matrix mDrawableMatrix;
	// private int gap = 100;

	private float angle = 20f;
	// the ratio of height to with
	private float ratio;
	private final static float SCALE = 2.3f;

	private int mIndex;
	// private Paint mPaint;

	private ImageTransformer mTransformer;
	private int bgTranslateX;

	private Allocation input, output;
	private RenderScript mScript;
	private ScriptC_translate mKernel;

	// the listener where we initialize the path. since we need to know the size
	// of the view before creating the path, this is the good place to do the
	// job.
	private OnPreDrawListener mOnPreDrawListener = new OnPreDrawListener() {
		@Override
		public boolean onPreDraw() {
			getViewTreeObserver().removeOnPreDrawListener(this);

			setupPath();
			return true;
		}
	};

	public ParallelogramView(Context context) {
		super(context);
		init(null, 0);
	}

	public ParallelogramView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public ParallelogramView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ParallelogramView, defStyle, 0);

		a.recycle();

		// initialize the fields of which the initialization can be done.
		mPath = new Path();
		// TODO mShape does not make any sense right now. because any time when
		// the path changes, the shape does not get updated.
		mShape = new PathShape(mPath, 0, 0);
		// mDrawable = new ShapeDrawable();
		mDrawable = new ShapeDrawable(mShape);
		mDrawable.getPaint().setColor(0xFFFF00FF);
		mDrawable.getPaint().setStyle(Paint.Style.FILL);
		mDrawableMatrix = new Matrix();
		// shape and shader yet need to be set for the drawable.

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
	}

	/**
	 * set up the frame of the image to be drawn using Path.
	 */
	private void setupPath() {
		// the ratio
		ratio = (float) (mHeight / (mHeight * Math.tan(Math.toRadians(angle)) + mWidth));

		int width = (int) ((mWidth + mHeight * Math.tan(Math.toRadians(angle))) / SCALE);
		int height = (int) (width * ratio);
		int gap = (int) (height * (Math.tan(Math.toRadians(angle))));
		int x0 = 0;
		int y0 = 0;
		int startX, startY, oppX, oppY;

		if (mIndex == 0) {
			// for the first page, don't clip the left top corner.
			startX = x0;
			startY = y0;
		} else {
			startX = x0 + gap;
			startY = y0;
		}

		if (mIndex == LAST_INDEX) {
			// for the last page, don't clip the right bottom corner.
			oppX = x0 + width;
			oppY = y0 + height;
		} else {
			oppX = x0 + width - gap;
			oppY = y0 + height;
		}

		mPath.reset();
		mPath.moveTo(startX, startY);
		mPath.lineTo(x0, y0 + height);
		mPath.lineTo(oppX, oppY);
		mPath.lineTo(x0 + width, y0);
		mPath.lineTo(startX, startY);

		// RectShape s = new RectShape();
		// s.resize(width, height);
		// mDrawable = new ShapeDrawable(s);
		mShape.resize(width, height);

		int dx = mWidth / 2 - (x0 + x0 + width) / 2;
		int dy = mHeight / 2 - (y0 + y0 + height) / 2;

		mDrawable.setShape(new PathShape(mPath, width, height));
		mDrawable.setBounds(x0 + dx, y0 + dy, x0 + width + dx, y0 + height + dy);
	}

	public void reShape() {
		// the ratio
		ratio = (float) (mHeight / (mHeight * Math.tan(Math.toRadians(angle)) + mWidth));

		int width = (int) ((mWidth + mHeight * Math.tan(Math.toRadians(angle))) / SCALE);
		int height = (int) (width * ratio);
		int gap = (int) (height * (Math.tan(Math.toRadians(angle))));
		// int width = img.getMeasuredWidth();
		// int height = img.getMeasuredHeight();
		int x0 = 0;
		int y0 = 0;
		int startX, startY, oppX, oppY;

		startX = x0;
		startY = y0;

		oppX = x0 + width;
		oppY = y0 + height;

		mPath.reset();
		mPath.moveTo(startX, startY);
		mPath.lineTo(x0, y0 + height);
		mPath.lineTo(oppX, oppY);
		mPath.lineTo(x0 + width, y0);
		mPath.lineTo(startX, startY);

		mShape.resize(width, height);

		int dx = mWidth / 2 - (x0 + x0 + width) / 2;
		int dy = mHeight / 2 - (y0 + y0 + height) / 2;

		mDrawable.setShape(new PathShape(mPath, width, height));
		mDrawable.setBounds(x0 + dx, y0 + dy, x0 + width + dx, y0 + height + dy);

		invalidate();
	}

	private void setupShader() {
		Shader fillShader = new BitmapShader(bmpToDraw, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		fillShader.setLocalMatrix(mDrawableMatrix);
		mDrawable.getPaint().setShader(fillShader);
	}

	/**
	 * get a copy of the original bitmap. the copy one will be drawn on the canvas.
	 */
	private void copyBitmap() {
		bmpToDraw = bmpOri.copy(bmpOri.getConfig(), true);
	}

	private Bitmap bit;

	/**
	 * 
	 * @param dx
	 *            distance to scroll along the x-axis
	 * @param dy
	 *            distance to scroll along the y-axis
	 */
	public void scrollBackground(int dx, int dy) {
		// adjust the matrix;
		bgTranslateX = dx;
		mDrawableMatrix.setTranslate(bgTranslateX, dy);
		mDrawable.getPaint().getShader().setLocalMatrix(mDrawableMatrix);

		// renderscript version
		// input.copyFrom(bmpOri);
		// mKernel.set_d((int) dx/2);
		// mKernel.set_gIn(input);
		// mKernel.forEach_invert(input, output);
		// output.copyTo(bmpToDraw);

		invalidate();
	}

	/**
	 * fires the image transformation. the @ImageTransformer updateImageInTransformation() method will be called;
	 */
	public void executeImageTransformation() {
		if (mTransformer != null) {
			mTransformer.updateImageInTransformation(bmpToDraw);
			BitmapShader shader = new BitmapShader(bmpToDraw, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			shader.setLocalMatrix(mDrawableMatrix);
			mDrawable.getPaint().setShader(shader);
			invalidate();
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.save();
		mDrawable.draw(canvas);
		canvas.restore();
	}

	public void setOriginalImage(Bitmap bmp) {
		if (bmpToDraw != null) {
			bmpToDraw.recycle();
		}
		bmpOri = bmp;
		// //////
		bmpOri = bmpOri.createScaledBitmap(bmpOri, (int) (bmpOri.getWidth() / 2.2), (int) (bmpOri.getHeight() / 2.2), false);

		// Log.d(TAG, "width: "+bmpOri.getWidth() + " height: "+bmpOri.getHeight());

		copyBitmap();

		if (mTransformer != null) {
			mTransformer.initImage(bmpOri);
		}

		// set up the shader
		setupShader();
		// //////////

		// mScript = RenderScript.create(getContext());
		// input = Allocation.createFromBitmap(mScript, bmpOri);
		// output = Allocation.createFromBitmap(mScript, bmpToDraw);
		// mKernel = new ScriptC_translate(mScript, getResources(), R.raw.translate);

		// remember we need to initialize the path before drawing.
		getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
	}

	public void setIndex(int index) {
		this.mIndex = index;
	}

	public void doRecycle() {
		if (bmpToDraw != null) {
			bmpToDraw.recycle();
		}
	}

	/**
	 * set the Bitmap to draw to the original Bitmap and update the shader
	 */
	public void resetImage() {
		if (bmpToDraw != null) {
			bmpToDraw.recycle();
		}
		copyBitmap();
		BitmapShader shader = new BitmapShader(bmpToDraw, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		shader.setLocalMatrix(mDrawableMatrix);
		mDrawable.getPaint().setShader(shader);
		invalidate();
	}

	public void setTransformer(ImageTransformer transformer) {
		this.mTransformer = transformer;
	}

	public static float getScale() {
		return SCALE;
	}

	public int getBgTranslateX() {
		return bgTranslateX;
	}

	/**
	 * implement this interface to implement the transformation job.
	 * 
	 * @author w
	 * 
	 */
	public static interface ImageTransformer {
		/**
		 * update the bitmap
		 * 
		 * @param b
		 *            Bitmap on which the transformation will take.
		 */
		void updateImageInTransformation(Bitmap b);

		/**
		 * initialization work. Stuff like initializing the Renderscript should take place here.
		 * 
		 * @param ori
		 *            the original Bitmap of the ParallelogramView
		 * 
		 */
		void initImage(Bitmap ori);
	}
}
