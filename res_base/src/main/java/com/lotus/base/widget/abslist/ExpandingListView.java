package com.lotus.base.widget.abslist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 完全展开的listview 即不折叠，使用时建议父级容器足够高，或者可以滑动
 * @author LY
 *
 */
public class ExpandingListView extends ListView {
	public ExpandingListView(Context context, AttributeSet aSet) {
		super(context, aSet);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 1, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
