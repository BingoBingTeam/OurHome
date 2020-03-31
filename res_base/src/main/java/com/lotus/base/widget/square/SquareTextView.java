package com.lotus.base.widget.square;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * com.optima.base.widget
 * LiuYang 2019/12/19 13:52
 */
@SuppressLint("AppCompatCustomView")
public class SquareTextView extends TextView {
	public SquareTextView(Context context) {
		super(context);
	}

	public SquareTextView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public SquareTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// For simple implementation, or internal size is always 0. We depend on the container to specify the layout size of our view. We can't really know what it is since we will be adding and
		// removing different arbitrary views and do not want the layout to change as this happens.
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

		// Children are just made to fill our space.
		int childWidthSize = getMeasuredWidth();
		// int childHeightSize = getMeasuredHeight();

		heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
