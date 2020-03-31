package com.lotus.base.widget.square;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class SquareButton extends AppCompatButton {

	public SquareButton(Context context) {
		super(context);
	}

	public SquareButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// For simple implementation, or internal size is always 0. We depend on the container to specify the layout size of our view. We can't really know
		// what it is since we will be adding and
		// removing different arbitrary views and do not want the layout to change as this happens.
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

		// Children are just made to fill our space.
		int childWidthSize = getMeasuredWidth();
		// int childHeightSize = getMeasuredHeight();

		heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
