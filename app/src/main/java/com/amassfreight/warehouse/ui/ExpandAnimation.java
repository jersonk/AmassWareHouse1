package com.amassfreight.warehouse.ui;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

public class ExpandAnimation extends Animation {
	private View mAnimatedView;
	private LayoutParams mViewLayoutParams;
	private int mMarginStart, mMarginEnd;
	private boolean mIsVisibleAfter = false;
	private boolean mWasEndedAlready = false;

	public ExpandAnimation(View view, int duration) {
		setDuration(duration);
		mAnimatedView = view;
		mViewLayoutParams = (LayoutParams) view.getLayoutParams();
		// if the bottom margin is 0,
		// then after the animation will end it'll be negative, and invisible.
		mIsVisibleAfter = (mViewLayoutParams.topMargin == 0);
		mMarginStart = mViewLayoutParams.topMargin;
		mMarginEnd = (mMarginStart == 0 ? (0 - view.getHeight()) : 0);
		view.setVisibility(View.VISIBLE);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		if (interpolatedTime < 1.0f) {
			// Calculating the new bottom margin, and setting it
			mViewLayoutParams.topMargin = mMarginStart
					+ (int) ((mMarginEnd - mMarginStart) * interpolatedTime);
			// Invalidating the layout, making us seeing the changes we made
			mAnimatedView.requestLayout();
			// Making sure we didn't run the ending before (it happens!)
		} else if (!mWasEndedAlready) {
			mViewLayoutParams.topMargin = mMarginEnd;
			mAnimatedView.requestLayout();
			if (mIsVisibleAfter) {
				mAnimatedView.setVisibility(View.GONE);
			}
			mWasEndedAlready = true;
		}
	}
}
