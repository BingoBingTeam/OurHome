package com.lotus.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 圆形textView，随机填充背景颜色
 *
 * @author 蒲小飞
 * @date 2018年1月5日
 */
@SuppressLint("AppCompatCustomView")
public class CircleTextView extends TextView {

	public static final String TAG = "CircleTextView";

	/** 画笔 */
	private Paint mBgPaint = new Paint();

	/** 颜色组 */
	private int colors[] = new int[] { Color.BLUE, Color.CYAN, Color.GREEN, Color.BLUE, Color.RED, Color.CYAN,
			Color.LTGRAY, Color.CYAN, Color.DKGRAY, Color.LTGRAY };

	PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	public CircleTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CircleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CircleTextView(Context context) {
		super(context);
		init(context);
	}

	public void init(Context context) {
		mBgPaint.setAntiAlias(true);
//		int colorPosition = (int) (Math.random() * 10 - 1);
//		if (colorPosition >= 0 && colorPosition < colors.length) {
//			mBgPaint.setColor(colors[colorPosition]);
//		}else {
//			mBgPaint.setColor(Color.BLUE);
//		}

	}

	/**
	 * 设置背景色
	 * @param color
	 */
	public void setBgColor(int color) {
		mBgPaint.setColor(color);
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measuredWidth = getMeasuredWidth();
		int measuredHeight = getMeasuredHeight();
		int max = Math.max(measuredWidth, measuredHeight);
		setMeasuredDimension(max, max);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.setDrawFilter(pfd);
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, Math.max(getWidth(), getHeight()) / 2, mBgPaint);
		super.draw(canvas);
	}
}
