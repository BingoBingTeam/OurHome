package com.lotus.ourhome.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lotus.ourhome.R;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputMoneyView extends LinearLayout {

    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.tv_4)
    TextView tv4;
    @BindView(R.id.tv_7)
    TextView tv7;
    @BindView(R.id.tv_clean)
    TextView tvClean;
    @BindView(R.id.tv_2)
    TextView tv2;
    @BindView(R.id.tv_5)
    TextView tv5;
    @BindView(R.id.tv_8)
    TextView tv8;
    @BindView(R.id.tv_0)
    TextView tv0;
    @BindView(R.id.tv_3)
    TextView tv3;
    @BindView(R.id.tv_6)
    TextView tv6;
    @BindView(R.id.tv_9)
    TextView tv9;
    @BindView(R.id.tv_point)
    TextView tvPoint;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.tv_reduce)
    TextView tvReduce;
    @BindView(R.id.tv_ok)
    TextView tvOk;

    private Context mContext;
    private View mRootView;
    private InputMoneyListener mInputMoneyListener;

    private StringBuilder mInputStrBuilder = new StringBuilder("0");

    public InputMoneyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public InputMoneyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public InputMoneyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 初始化界面
     *
     * @param context
     */
    private void init(final Context context, AttributeSet attrs) {
        this.mContext = context;
        mRootView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_input_money, null);
        ButterKnife.bind(this, mRootView);
        addView(mRootView);
    }

    public void setInputMoneyListener(InputMoneyListener mInputMoneyListener) {
        this.mInputMoneyListener = mInputMoneyListener;
    }

    public void intMoney(String money){
        if(!StringUtils.isEmpty(money)){
            mInputStrBuilder.append(money);
        }
    }

    private void countMoney(boolean isOk){
        String inputStr = mInputStrBuilder.toString();
        String splitStr = "";
        if(inputStr.contains("+")){
            splitStr = "\\+";
        }else if(inputStr.contains("-")){
            splitStr = "-";
        }
        String[] strs = inputStr.split(splitStr);
        BigDecimal result = new BigDecimal("0");
        BigDecimal number1 = new BigDecimal(strs[0]);
        BigDecimal number2 = new BigDecimal(strs[1]);
        if("\\+".equals(splitStr)){
            result = number1.add(number2);
        }else if("-".equals(splitStr)){
            result = number1.subtract(number2);
        }
        mInputStrBuilder.delete(0,mInputStrBuilder.length());
        mInputStrBuilder.append(result);
        showResultMoney();
        if(isOk){
            tvOk.setText("OK");
        }
    }

    private void showResultMoney(){
        if(mInputMoneyListener != null){
            mInputMoneyListener.resultMoney(mInputStrBuilder.toString());
        }
    }

    @OnClick({R.id.tv_1, R.id.tv_4, R.id.tv_7, R.id.tv_clean, R.id.tv_2, R.id.tv_5, R.id.tv_8, R.id.tv_0,
            R.id.tv_3, R.id.tv_6, R.id.tv_9, R.id.tv_point, R.id.tv_add, R.id.tv_reduce, R.id.tv_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_1:
                mInputStrBuilder.append("1");
                showResultMoney();
                break;
            case R.id.tv_4:
                mInputStrBuilder.append("4");
                showResultMoney();
                break;
            case R.id.tv_7:
                mInputStrBuilder.append("7");
                showResultMoney();
                break;
            case R.id.tv_clean:
                mInputStrBuilder.delete(0,mInputStrBuilder.length());
                showResultMoney();
                break;
            case R.id.tv_2:
                mInputStrBuilder.append("2");
                showResultMoney();
                break;
            case R.id.tv_5:
                mInputStrBuilder.append("5");
                showResultMoney();
                break;
            case R.id.tv_8:
                mInputStrBuilder.append("8");
                showResultMoney();
                break;
            case R.id.tv_0:
                mInputStrBuilder.append("0");
                showResultMoney();
                break;
            case R.id.tv_3:
                mInputStrBuilder.append("3");
                showResultMoney();
                break;
            case R.id.tv_6:
                mInputStrBuilder.append("6");
                showResultMoney();
                break;
            case R.id.tv_9:
                mInputStrBuilder.append("9");
                showResultMoney();
                break;
            case R.id.tv_point:
                mInputStrBuilder.append(".");
                showResultMoney();
                break;
            case R.id.tv_add:
                if(mInputStrBuilder.indexOf("+") != -1){
                    countMoney(false);
                }
                mInputStrBuilder.append("+");
                tvOk.setText("=");
                break;
            case R.id.tv_reduce:
                if(mInputStrBuilder.indexOf("-") != -1){
                    countMoney(false);
                }
                mInputStrBuilder.append("-");
                tvOk.setText("=");
                break;
            case R.id.tv_ok:
                if(tvOk.getText().toString().equals("=")){//计算数值
                    countMoney(true);
                }else {
                    //完成编辑
                    if(mInputMoneyListener != null){
                        mInputMoneyListener.add(mInputStrBuilder.toString());
                    }
                }
                break;
        }
    }

    public interface InputMoneyListener{
        public void resultMoney(String money);
        public void add(String money);
    }
}
