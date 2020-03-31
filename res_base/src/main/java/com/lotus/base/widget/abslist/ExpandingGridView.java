package com.lotus.base.widget.abslist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 完全展开的gridview 即不折叠，使用时建议父级容器足够高，或者可以滑动
 *
 * @author LY
 */
public class ExpandingGridView extends GridView {

	public ExpandingGridView(Context context) {
		super(context);
	}

	public ExpandingGridView(Context context, AttributeSet aSet) {
		super(context, aSet);
	}

	public ExpandingGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
