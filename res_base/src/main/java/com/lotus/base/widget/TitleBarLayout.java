package com.lotus.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lotus.base.R;


/**
 * 自定义标题栏
 */
public class TitleBarLayout extends RelativeLayout {

    private TextView tvBack;
    private ImageView ivBack;
    private LinearLayout llBack;

    private TextView tvTitle;
    ImageView ivTitle;
    LinearLayout llTitle;

    private TextView tvAdd;
    private ImageView ivAdd;
    private LinearLayout llAdd;

    private TextView tvMore;
    private ImageView ivMore;
    private LinearLayout llMore;

    View viewLine;

    private RelativeLayout mRootView;
    private Context mContext;


    public TitleBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TitleBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public TitleBarLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("TitleBarLayout", "onDraw");
    }

    /**
     * 初始化界面
     *
     * @param context
     */
    private void init(final Context context, AttributeSet attrs) {
        this.mContext = context;

        mRootView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_title_bar, null);

        llBack = (LinearLayout) mRootView.findViewById(R.id.ll_back);
        ivBack = (ImageView) mRootView.findViewById(R.id.iv_back);
        tvBack = (TextView) mRootView.findViewById(R.id.tv_back);

        llTitle = (LinearLayout) mRootView.findViewById(R.id.ll_title);
        tvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
        ivTitle = (ImageView) mRootView.findViewById(R.id.iv_title);

        llAdd = (LinearLayout) mRootView.findViewById(R.id.ll_add);
        ivAdd = (ImageView) mRootView.findViewById(R.id.iv_add);
        tvAdd = (TextView) mRootView.findViewById(R.id.tv_add);

        llMore = (LinearLayout) mRootView.findViewById(R.id.ll_more);
        ivMore = (ImageView) mRootView.findViewById(R.id.iv_more);
        tvMore = (TextView) mRootView.findViewById(R.id.tv_more);

        viewLine = (View) mRootView.findViewById(R.id.view_line);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);
        int bgColor = typedArray.getResourceId(R.styleable.TitleBar_bgColor, R.color.app_main);

        boolean isImageBackShow = typedArray.getBoolean(R.styleable.TitleBar_imageBackShow, true);
        int imageBackRes = typedArray.getResourceId(R.styleable.TitleBar_imageBackRes, R.mipmap.ic_back);
        boolean isTextBackShow = typedArray.getBoolean(R.styleable.TitleBar_textBackShow, false);
        int textBackColor = typedArray.getResourceId(R.styleable.TitleBar_textBackColor, 0);

        boolean isImageTitleShow = typedArray.getBoolean(R.styleable.TitleBar_imageTitleShow, false);
        int imageTitleRes = typedArray.getResourceId(R.styleable.TitleBar_imageTitleRes, 0);
        boolean isTextTitleShow = typedArray.getBoolean(R.styleable.TitleBar_textTitleShow, true);
        int textTitleColor = typedArray.getResourceId(R.styleable.TitleBar_textTitleColor, 0);

        boolean isImageAddShow = typedArray.getBoolean(R.styleable.TitleBar_imageAddShow, false);
        int imageAddRes = typedArray.getResourceId(R.styleable.TitleBar_imageAddRes, 0);
        boolean isTextAddShow = typedArray.getBoolean(R.styleable.TitleBar_textAddShow, false);
        int textAddColor = typedArray.getResourceId(R.styleable.TitleBar_textAddColor, 0);

        boolean isImageMoreShow = typedArray.getBoolean(R.styleable.TitleBar_imageMoreShow, false);
        int imageMoreRes = typedArray.getResourceId(R.styleable.TitleBar_imageMoreRes,0);
        boolean isTextMoreShow = typedArray.getBoolean(R.styleable.TitleBar_textMoreShow, false);
        int textMoreColor = typedArray.getResourceId(R.styleable.TitleBar_textMoreColor, 0);
        typedArray.recycle();

        mRootView.setBackgroundResource(bgColor);
        mRootView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        addView(mRootView);
    }
/**  ============================== 设置控件样式 =================================================================================  */

    /**
     * 标题栏整体背景颜色
     * 默认为app主题色
     * @param bgColor
     */
    public void setBackgroundTitlebar(Integer bgColor){
        if (bgColor != null) {
            mRootView.setBackgroundColor(bgColor);
        }
    }

    public void setBackgroundLayoutBack(Integer bgColor,Drawable bgDrawable){
        setBackgroundLayout(llBack,bgColor,bgDrawable);
    }

    public void setBackgroundLayoutTitle(Integer bgColor,Drawable bgDrawable){
        setBackgroundLayout(llTitle,bgColor,bgDrawable);
    }

    public void setBackgroundLayoutAdd(Integer bgColor,Drawable bgDrawable){
        setBackgroundLayout(llAdd,bgColor,bgDrawable);
    }

    public void setBackgroundLayoutMore(Integer bgColor,Drawable bgDrawable){
        setBackgroundLayout(llMore,bgColor,bgDrawable);
    }

    public void setBackgroundLayout(LinearLayout linearLayout,Integer bgColor,Drawable bgDrawable){
        if (mRootView == null) {
            return;
        }
        linearLayout.setVisibility(VISIBLE);
        linearLayout.setBackground(bgDrawable);
        linearLayout.setBackgroundColor(bgColor);
    }

    /**
     * 设置控件是否可用
     */
    public void setEnabledView(View view,boolean isEnabled) {
        if (mRootView == null) {
            return;
        }
        view.setEnabled(isEnabled);
    }

    /**
     * 设置文本样式
     */
    public void setTitle(String text) {
        tvTitle.setVisibility(VISIBLE);
        tvTitle.setText(text);
    }

    public void setTextStyleBack(String text, Integer textColor, Float textSize) {
        if (mRootView == null) {
            return;
        }
        setTextStyle(tvBack,text,textColor,textSize);
    }

    public void setTextStyleTitle(TextView textView,String text, Integer textColor, Float textSize) {
        if (mRootView == null) {
            return;
        }
        setTextStyle(tvTitle,text,textColor,textSize);
    }

    public void setTextStyleAdd(TextView textView,String text, Integer textColor, Float textSize) {
        if (mRootView == null) {
            return;
        }
        setTextStyle(tvAdd,text,textColor,textSize);
    }

    public void setTextStyleMore(TextView textView,String text, Integer textColor, Float textSize) {
        if (mRootView == null) {
            return;
        }
        setTextStyle(tvMore,text,textColor,textSize);
    }

    public void setTextStyle(TextView textView,String text, Integer textColor, Float textSize) {
        if (mRootView == null) {
            return;
        }
        textView.setVisibility(VISIBLE);
        textView.setText(text);
        if (textSize  != null) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        }
        if (textColor  != null) {
            textView.setTextColor(textColor);
        }
    }

    /**
     * 是否显示底部水平分割线
     * @param isShow
     */
    public void showLineView(boolean isShow) {
        if(isShow){
            viewLine.setVisibility(VISIBLE);
        }else {
            viewLine.setVisibility(GONE);
        }
    }

/**  ============================== 设置控件点击事件监听 =================================================================================  */

    public void setClickListenerIvBack(OnClickListener clickListener) {
        this.ivBack.setVisibility(VISIBLE);
        this.ivBack.setOnClickListener(clickListener);
    }

    public void setClickListenerTvBack(OnClickListener clickListener) {
        this.tvBack.setVisibility(VISIBLE);
        this.tvBack.setOnClickListener(clickListener);
    }

    public void setClickListenerTvTitleClickListener(OnClickListener clickListener) {
        this.tvTitle.setVisibility(VISIBLE);
        this.tvTitle.setOnClickListener(clickListener);
    }

    public void setClickListenerIvTitle(OnClickListener clickListener) {
        this.ivTitle.setVisibility(VISIBLE);
        this.ivTitle.setOnClickListener(clickListener);
    }

    public void setClickListenerIvAdd(OnClickListener clickListener) {
        this.ivAdd.setVisibility(VISIBLE);
        this.ivAdd.setOnClickListener(clickListener);
    }

    public void setClickListenerTvAdd(OnClickListener clickListener) {
        this.tvAdd.setVisibility(VISIBLE);
        this.tvAdd.setOnClickListener(clickListener);
    }

    public void setClickListenerIvMore(OnClickListener clickListener) {
        this.ivMore.setVisibility(VISIBLE);
        this.ivMore.setOnClickListener(clickListener);
    }

    public void setClickListenerTvMore(OnClickListener clickListener) {
        this.tvMore.setVisibility(VISIBLE);
        this.tvMore.setOnClickListener(clickListener);
    }

/**  ============================== 获得控件 =================================================================================  */
    public TextView getTvBack() {
        return tvBack;
    }

    public ImageView getIvBack() {
        return ivBack;
    }

    public LinearLayout getLlBack() {
        return llBack;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public ImageView getIvTitle() {
        return ivTitle;
    }

    public LinearLayout getLlTitle() {
        return llTitle;
    }

    public TextView getTvAdd() {
        return tvAdd;
    }

    public ImageView getIvAdd() {
        return ivAdd;
    }

    public LinearLayout getLlAdd() {
        return llAdd;
    }

    public TextView getTvMore() {
        return tvMore;
    }

    public ImageView getIvMore() {
        return ivMore;
    }

    public LinearLayout getLlMore() {
        return llMore;
    }

    public View getViewLine() {
        return viewLine;
    }
}
