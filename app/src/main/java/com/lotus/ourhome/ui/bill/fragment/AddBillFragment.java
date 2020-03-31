package com.lotus.ourhome.ui.bill.fragment;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.lotus.base.utils.toasts.ToastUtil;
import com.lotus.ourhome.R;
import com.lotus.ourhome.base.SimpleFragment;
import com.lotus.ourhome.model.bean.BillBean;
import com.lotus.ourhome.model.bean.FamilyMemberBean;
import com.lotus.ourhome.model.bean.MoneyUseTypeBean;
import com.lotus.ourhome.model.db.BillBeanManager;
import com.lotus.ourhome.model.db.FamilyMemberBeanManager;
import com.lotus.ourhome.model.db.MoneyUseTypeBeanManager;
import com.lotus.ourhome.ui.bill.adapter.ChooseFamilyMemberRecyclerAdapter;
import com.lotus.ourhome.ui.bill.adapter.ChooseMoneyUseTypeRecyclerAdapter;
import com.lotus.ourhome.util.CookieUtil;
import com.lotus.ourhome.widget.InputMoneyView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 添加账单
 */
public class AddBillFragment extends SimpleFragment {

    @BindView(R.id.btn_close)
    ImageView btnClose;
    @BindView(R.id.btn_income)
    TextView btnIncome;
    @BindView(R.id.btn_expenses)
    TextView btnExpenses;
    @BindView(R.id.recycler_view_use_type)
    RecyclerView recyclerViewUseType;
    @BindView(R.id.recycler_view_family_member)
    RecyclerView recyclerViewFamilyMember;
    @BindView(R.id.img_money_type)
    ImageView imgMoneyType;
    @BindView(R.id.tv_money_type)
    TextView tvMoneyType;
    @BindView(R.id.tv_happen_member_name)
    TextView tvHappenMemberName;
    @BindView(R.id.tv_use_time)
    TextView tvUseTime;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.ed_description)
    EditText edDescription;
    @BindView(R.id.input_money_view)
    InputMoneyView inputMoneyView;

    private BillBeanManager mBillBeanManager = null;
    private String mType = BillBean.TYPE_EXPENSES;//默认支出
    private long mHappenTime = System.currentTimeMillis();//发生时间
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private TimePickerView mTimePickerBuilder;
    private SaveDataAsync mSaveDataAsync;
    private String mUserId = "";
    private String mUserName = "";

    private FamilyMemberBeanManager mFamilyMemberBeanManager = null;
    private QueryMemberListAsync mQueryMemberListAsync = null;
    private FamilyMemberBean mSelectedFamilyMemberBean = null;
    private ChooseFamilyMemberRecyclerAdapter mChooseFamilyMemberAdapter = null;

    private MoneyUseTypeBeanManager mMoneyUseTypeBeanManager = null;
    private QueryMoneyUseTypeListDataAsync mQueryMoneyUseTypeListDataAsync = null;
    private MoneyUseTypeBean mSelectedMoneyUseTypeBean = null;
    private ChooseMoneyUseTypeRecyclerAdapter mChooseMoneyUseTypeAdapter = null;


    public static AddBillFragment newInstance() {
        Bundle args = new Bundle();
        AddBillFragment fragment = new AddBillFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_add_bill;
    }

    @Override
    protected void initEventAndData() {
        mUserId = CookieUtil.getUserInfo().getId();
        mUserName = CookieUtil.getUserInfo().getName();

        mFamilyMemberBeanManager = new FamilyMemberBeanManager(mContext);
        mMoneyUseTypeBeanManager = new MoneyUseTypeBeanManager(mContext);
        mBillBeanManager = new BillBeanManager(mContext);

        inputMoneyView.setInputMoneyListener(new InputMoneyView.InputMoneyListener() {
            @Override
            public void resultMoney(String money) {
                tvMoney.setText(money);
            }

            @Override
            public void add(String money) {
                addBill(money);
            }
        });

        recyclerViewFamilyMember = initRecycleView(recyclerViewFamilyMember, 5);
        mChooseFamilyMemberAdapter = new ChooseFamilyMemberRecyclerAdapter(mContext);
        mChooseFamilyMemberAdapter.setOnListItemClick(new ChooseFamilyMemberRecyclerAdapter.OnFamilyMemberListItemClick() {
            @Override
            public void onItemClick(FamilyMemberBean content) {
                mSelectedFamilyMemberBean = content;
                tvHappenMemberName.setText(content.getName());
            }
        });
        recyclerViewFamilyMember.setAdapter(mChooseFamilyMemberAdapter);

        recyclerViewUseType = initRecycleView(recyclerViewUseType, 8);
        mChooseMoneyUseTypeAdapter = new ChooseMoneyUseTypeRecyclerAdapter(mContext);
        mChooseMoneyUseTypeAdapter.setOnListItemClick(new ChooseMoneyUseTypeRecyclerAdapter.OnMoneyUseTypeListItemClick() {
            @Override
            public void onItemClick(MoneyUseTypeBean content) {
                mSelectedMoneyUseTypeBean = content;
                tvMoneyType.setText(content.getName());
            }
        });
        recyclerViewUseType.setAdapter(mChooseMoneyUseTypeAdapter);

        getMemberList();
        getMoneyUseTypeList();
    }

    /**
     * 选择时间
     */
    private void showTimePickerDialog(long selectedTime) {
        if (mTimePickerBuilder == null) {
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.set(Calendar.YEAR, 1000);
            calendarStart.set(Calendar.MONTH, 0);
            calendarStart.set(Calendar.DAY_OF_MONTH, 1);

            Calendar calendarCurrent = Calendar.getInstance();
            calendarCurrent.setTimeInMillis(System.currentTimeMillis());

            mTimePickerBuilder = new TimePickerBuilder(mActivity, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {

                    Calendar selectCalendar = Calendar.getInstance();
                    selectCalendar.setTime(date);
                    mHappenTime = selectCalendar.getTimeInMillis();
                    tvUseTime.setText(mSimpleDateFormat.format(new Date(mHappenTime)));
                }
            }).setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    .setSubmitColor(Color.parseColor("#39BAFF"))//确定按钮文字颜色
                    .setCancelColor(Color.parseColor("#39BAFF"))//取消按钮文字颜色
                    .setDate(calendarCurrent) // 如果不设置的话，默认是系统时间
                    .setRangDate(calendarStart, calendarCurrent)//起始终止年月日设定
                    .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .build();
        }

        Calendar calendarSelected = Calendar.getInstance();
        calendarSelected.setTimeInMillis(selectedTime == 0 ? System.currentTimeMillis() : selectedTime);
        mTimePickerBuilder.setDate(calendarSelected);
        mTimePickerBuilder.show();
    }

    /**
     * 新增一笔账
     */
    private void addBill(String money) {
        if (mSelectedMoneyUseTypeBean == null) {
            ToastUtil.showToast(mContext, "必须选择类别");
            return;
        }
        BillBean billBean = new BillBean();
        billBean.setId(BillBean.createId(mUserId, mUserName));
        billBean.setType(mType);
        billBean.setUserId(mUserId);
        billBean.setMoney(money);
        billBean.setMoneyUseTypeId(mSelectedMoneyUseTypeBean.getId());
        billBean.setHappenPerson(mSelectedFamilyMemberBean == null ? FamilyMemberBean.FAMILY_ID : mSelectedFamilyMemberBean.getId());
        billBean.setHappenTime(mHappenTime);
        billBean.setRemark(edDescription.getText().toString().trim());
        billBean.setMoneyUseTypeBean(mSelectedMoneyUseTypeBean);
        billBean.setFamilyMemberBean(mSelectedFamilyMemberBean);

        Calendar selectCalendar = Calendar.getInstance();
        selectCalendar.setTime(new Date(mHappenTime));
        int year = selectCalendar.get(Calendar.YEAR);//获取年份
        int month = selectCalendar.get(Calendar.MONTH);//获取年份
        int day = selectCalendar.get(Calendar.DAY_OF_YEAR);//获取年中第几天
        billBean.setHappenTimeYear(year);
        billBean.setHappenTimeMonth(month);
        billBean.setHappenTimeDay(day);

        if (mSaveDataAsync != null) {
            mSaveDataAsync.cancel(true);
        }
        mSaveDataAsync = new SaveDataAsync(billBean);
        mSaveDataAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class SaveDataAsync extends AsyncTask<Object, Integer, Boolean> {
        BillBean data = null;

        private SaveDataAsync(BillBean bean) {
            this.data = bean;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            return mBillBeanManager.saveBillBean(data);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                ToastUtil.showToast(mContext, "添加成功");
                EventBus.getDefault().post(data);
                _mActivity.onBackPressed();
            } else {
                ToastUtil.showToast(mContext, "添加失败");
            }
        }
    }

    /**
     * 设置RecycleView
     */
    private RecyclerView initRecycleView(RecyclerView recycleView, int spanCount) {
        recycleView.setLayoutManager(new GridLayoutManager(mActivity, spanCount == 0 ? 6 : spanCount) {
            @Override
            public boolean canScrollVertically() {
                //解决ScrollView里存在多个RecyclerView时滑动卡顿的问题
                return false;
            }

        });
        recycleView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = 2;
                outRect.right = 2;
                outRect.top = 2;
                outRect.bottom = 2;
//                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        //解决数据加载不完的问题
        recycleView.setNestedScrollingEnabled(false);
        //当确定Item的改变不会影响RecyclerView的宽高的时候可以设置setHasFixedSize(true)
        recycleView.setHasFixedSize(false);
        //解决数据加载完成后, 没有停留在顶部的问题
        recycleView.setFocusable(false);
        return recycleView;
    }

    private void getMemberList() {
        if (mQueryMemberListAsync != null) {
            mQueryMemberListAsync.cancel(true);
        }
        mQueryMemberListAsync = new QueryMemberListAsync();
        mQueryMemberListAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class QueryMemberListAsync extends AsyncTask<Object, Integer, List<FamilyMemberBean>> {

        @Override
        protected List<FamilyMemberBean> doInBackground(Object... objects) {
            return mFamilyMemberBeanManager.getFamilyMemberByUserId(mUserId);
        }

        @Override
        protected void onPostExecute(List<FamilyMemberBean> result) {
            super.onPostExecute(result);
            mChooseFamilyMemberAdapter.initData(result, true);
        }
    }

    private void getMoneyUseTypeList() {
        if (mQueryMoneyUseTypeListDataAsync != null) {
            mQueryMoneyUseTypeListDataAsync.cancel(true);
        }
        mQueryMoneyUseTypeListDataAsync = new QueryMoneyUseTypeListDataAsync();
        mQueryMoneyUseTypeListDataAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class QueryMoneyUseTypeListDataAsync extends AsyncTask<Object, Integer, List<MoneyUseTypeBean>> {

        @Override
        protected List<MoneyUseTypeBean> doInBackground(Object... objects) {
            return mMoneyUseTypeBeanManager.getMoneyUseTypeByUserIdAndType(mUserId, mType);
        }

        @Override
        protected void onPostExecute(List<MoneyUseTypeBean> result) {
            super.onPostExecute(result);
            mChooseMoneyUseTypeAdapter.initData(result, true);
        }
    }

    @OnClick({R.id.btn_close,R.id.btn_income, R.id.btn_expenses, R.id.tv_use_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                _mActivity.onBackPressed();
                break;
            case R.id.btn_income:
                mType = BillBean.TYPE_INCOME;
                getMoneyUseTypeList();
                break;
            case R.id.btn_expenses:
                mType = BillBean.TYPE_EXPENSES;
                getMoneyUseTypeList();
                break;
            case R.id.tv_use_time:
                showTimePickerDialog(mHappenTime);
                break;
        }
    }
}
