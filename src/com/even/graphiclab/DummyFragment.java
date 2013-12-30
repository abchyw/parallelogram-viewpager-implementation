package com.even.graphiclab;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptGroup;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.even.graphiclab.parallelogram.ParallelogramPageFragment;
import com.even.graphiclab.parallelogram.ScriptC_dim;
import com.example.labatory.R;

public class DummyFragment extends ParallelogramPageFragment {
	private static final String TAG = DummyFragment.class.getSimpleName();

	private Bitmap bmp;
	private int index;

	// rendering variable
	private RenderScript rs;
	public ScriptGroup group;
	private Allocation output;
	private Allocation input;
	private ScriptIntrinsicBlur script;
	private ScriptC_dim script2;
	private Callback mCallback;

	private final float defaultRadius = 10.f;

	public static DummyFragment newInstanct(int imgId, int index) {
		DummyFragment f = new DummyFragment();
		Bundle args = new Bundle();
		args.putInt("IMG_ID", imgId);
		args.putInt("INDEX", index);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		int imgId = (Integer) args.get("IMG_ID");
		index = (Integer) args.get("INDEX");
		bmp = BitmapFactory.decodeResource(getResources(), imgId);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (Callback) activity;
	}

	@Override
	public void onDestroy() {
		group.destroy();
		script.destroy();
		script2.destroy();
		rs.destroy();
		input.destroy();
		output.destroy();
		super.onDestroy();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public Bitmap getImage() {
		return bmp;
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_image;
	}

	@Override
	public void updateImageInTransformation(Bitmap b, float fraction) {
		script.setRadius(fraction * defaultRadius + 0.01f);
		// 0.5 -- 1
		script2.set_rate(1.f - (0.25f * fraction));
		// script.forEach(output);
		// output.copyTo(b);
		// when the script group is executed the first time, the output bitmap will be a entire white bitmap.
		group.execute();
		if (fraction != 0) {
			output.copyTo(b);
		}
	}

	@Override
	public void initImage(Bitmap ori) {
		if (rs == null) {
			rs = RenderScript.create(getActivity());
			if (input == null) {
				input = Allocation.createFromBitmap(rs, ori, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
			}
			if (output == null) {
				output = Allocation.createTyped(rs, input.getType());
			}
			if (script == null) {
				script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
				script.setRadius(defaultRadius /* e.g. 3.f */);
			}
			// script.setInput(input);
			if (script2 == null) {
				script2 = new ScriptC_dim(rs, getResources(), R.raw.dim);
			}
			if (group == null) {
				ScriptGroup.Builder builder = new ScriptGroup.Builder(rs);
				builder.addKernel(script2.getKernelID_invert());
				builder.addKernel(script.getKernelID());
				builder.addConnection(input.getType(), script2.getKernelID_invert(), script.getFieldID_Input());
				group = builder.create();
				group.setInput(script2.getKernelID_invert(), input);
				group.setOutput(script.getKernelID(), output);
			}
		}
	};

	public static interface Callback {
		void onButtonClick(Bitmap bmp);
	}

}
