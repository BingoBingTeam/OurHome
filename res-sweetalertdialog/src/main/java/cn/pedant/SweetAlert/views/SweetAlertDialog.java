package cn.pedant.SweetAlert.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.pedant.SweetAlert.R;

public class SweetAlertDialog extends Dialog implements View.OnClickListener {
	private View mDialogView;
	private AnimationSet mModalInAnim;
	private AnimationSet mModalOutAnim;
	private Animation mOverlayOutAnim;
	private Animation mErrorInAnim;
	private AnimationSet mErrorXInAnim;
	private AnimationSet mSuccessLayoutAnimSet;
	private Animation mSuccessBowAnim;
	private TextView mTitleTextView;
	private TextView mContentTextView;
	private String mTitleText;
	private String mContentText;
	private boolean mShowCancel;
	private boolean mShowNeutral;
	private boolean mShowContent;
	private String mCancelText;
	private String mNeutralText;
	private String mConfirmText;
	private int mAlertType;
	private FrameLayout mErrorFrame;
	private FrameLayout mSuccessFrame;
	private FrameLayout mProgressFrame;
	private SuccessTickView mSuccessTick;
	private ImageView mErrorX;
	private View mSuccessLeftMask;
	private View mSuccessRightMask;
	private Drawable mCustomImgDrawable;
	private ImageView mCustomImage;
	private Button mConfirmButton;
	private Button mNeutralButton;
	private Button mCancelButton;
	private ProgressHelper mProgressHelper;
	private FrameLayout mWarningFrame;
	private ImageView mWarningImage;
	private OnSweetClickListener mCancelClickListener;
	private OnSweetClickListener mmNeutralClickListener;
	private OnSweetClickListener mConfirmClickListener;
	private boolean mCloseFromCancel;

	public static final int NORMAL_TYPE = 0;
	public static final int ERROR_TYPE = 1;
	public static final int SUCCESS_TYPE = 2;
	public static final int WARNING_TYPE = 3;
	public static final int CUSTOM_IMAGE_TYPE = 4;
	public static final int PROGRESS_TYPE = 5;

	public static interface OnSweetClickListener {
		public void onClick(SweetAlertDialog sweetAlertDialog, int buttonId);
	}

	public SweetAlertDialog(Context context) {
		this(context, NORMAL_TYPE);
	}

	public SweetAlertDialog(Context context, int alertType) {
		super(context, R.style.alert_dialog);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		mProgressHelper = new ProgressHelper(context);
		mAlertType = alertType;
		mErrorInAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.sweetalert_error_frame_in);
		mErrorXInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.sweetalert_error_x_in);
		// 2.3.x system don't support alpha-animation on layer-list drawable
		// remove it from animation set

		mSuccessBowAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.sweetalert_success_bow_roate);
		mSuccessLayoutAnimSet = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.sweetalert_success_mask_layout);
		mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.sweetalert_modal_in);
		mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.sweetalert_modal_out);
		mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mDialogView.setVisibility(View.GONE);
				mDialogView.post(new Runnable() {
					@Override
					public void run() {
						if (mCloseFromCancel) {
							SweetAlertDialog.super.cancel();
						} else {
							SweetAlertDialog.super.dismiss();
						}
					}
				});
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		// dialog overlay fade out
		mOverlayOutAnim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				WindowManager.LayoutParams wlp = getWindow().getAttributes();
				wlp.alpha = 1 - interpolatedTime;
				getWindow().setAttributes(wlp);
			}
		};
		mOverlayOutAnim.setDuration(120);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sweetalert_dialog);

		mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
		mTitleTextView = (TextView) findViewById(R.id.title_text);
		mContentTextView = (TextView) findViewById(R.id.content_text);
		mErrorFrame = (FrameLayout) findViewById(R.id.error_frame);
		mErrorX = (ImageView) mErrorFrame.findViewById(R.id.error_x);
		mSuccessFrame = (FrameLayout) findViewById(R.id.success_frame);
		mProgressFrame = (FrameLayout) findViewById(R.id.progress_dialog);
		mSuccessTick = (SuccessTickView) mSuccessFrame.findViewById(R.id.success_tick);
		mSuccessLeftMask = mSuccessFrame.findViewById(R.id.mask_left);
		mSuccessRightMask = mSuccessFrame.findViewById(R.id.mask_right);
		mCustomImage = (ImageView) findViewById(R.id.custom_image);
		mWarningFrame = (FrameLayout) findViewById(R.id.warning_frame);
		mWarningImage = (ImageView) findViewById(R.id.warning_image);
		mConfirmButton = (Button) findViewById(R.id.confirm_button);
		mNeutralButton = (Button) findViewById(R.id.neutral_button);
		mCancelButton = (Button) findViewById(R.id.cancel_button);
		mProgressHelper.setProgressWheel((ProgressWheel) findViewById(R.id.progressWheel));
		mConfirmButton.setOnClickListener(this);
		mNeutralButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);

		setTitleText(mTitleText);
		setContentText(mContentText);
		setCancelText(mCancelText);
		setNeutralText(mNeutralText);
		setConfirmText(mConfirmText);
		changeAlertType(mAlertType, true);

	}

	private void restore() {
		mCustomImage.setVisibility(View.GONE);
		mErrorFrame.setVisibility(View.GONE);
		mSuccessFrame.setVisibility(View.GONE);
		mWarningFrame.setVisibility(View.GONE);
		mProgressFrame.setVisibility(View.GONE);
		mNeutralButton.setVisibility(View.GONE);
		mConfirmButton.setVisibility(View.VISIBLE);

		mConfirmButton.setBackgroundResource(R.drawable.sweetalert_blue_button_background);
		mErrorFrame.clearAnimation();
		mErrorX.clearAnimation();
		mSuccessTick.clearAnimation();
		mSuccessLeftMask.clearAnimation();
		mSuccessRightMask.clearAnimation();
	}

	private void playAnimation() {
		if (mAlertType == ERROR_TYPE) {
			mErrorFrame.startAnimation(mErrorInAnim);
			mErrorX.startAnimation(mErrorXInAnim);
		} else if (mAlertType == SUCCESS_TYPE) {
			mSuccessTick.startTickAnim();
			mSuccessRightMask.startAnimation(mSuccessBowAnim);
		} else if (mAlertType == WARNING_TYPE) {
			mWarningFrame.startAnimation(mErrorInAnim);
			mWarningImage.startAnimation(mErrorXInAnim);
		}
	}

	private void changeAlertType(int alertType, boolean fromCreate) {
		mAlertType = alertType;
		// call after created views
		if (mDialogView != null) {
			if (!fromCreate) {
				// restore all of views state before switching alert type
				restore();
			}
			switch (mAlertType) {
			case ERROR_TYPE:
				mNeutralButton.setVisibility(View.GONE);
				mErrorFrame.setVisibility(View.VISIBLE);
				break;
			case SUCCESS_TYPE:
				mNeutralButton.setVisibility(View.GONE);
				mSuccessFrame.setVisibility(View.VISIBLE);
				// initial rotate layout of success mask
				mSuccessLeftMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(0));
				mSuccessRightMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(1));
				break;
			case WARNING_TYPE:
				mNeutralButton.setVisibility(View.GONE);
				mConfirmButton.setBackgroundResource(R.drawable.sweetalert_red_button_background);
				mWarningFrame.setVisibility(View.VISIBLE);
				break;
			case CUSTOM_IMAGE_TYPE:
				setCustomImage(mCustomImgDrawable);
				break;
			case PROGRESS_TYPE:
				mProgressFrame.setVisibility(View.VISIBLE);
				mNeutralButton.setVisibility(View.GONE);
				mConfirmButton.setVisibility(View.GONE);
				break;
			}
			if (!fromCreate) {
				playAnimation();
			}
		}
	}

	public int getAlerType() {
		return mAlertType;
	}

	public void changeAlertType(int alertType) {
		changeAlertType(alertType, false);
	}

	public String getTitleText() {
		return mTitleText;
	}

	public SweetAlertDialog setTitleText(String text) {
		mTitleText = text;
		if (mTitleTextView != null && mTitleText != null) {
			mTitleTextView.setText(mTitleText);
		}
		return this;
	}

	public SweetAlertDialog setCustomImage(Drawable drawable) {
		mCustomImgDrawable = drawable;
		if (mCustomImage != null && mCustomImgDrawable != null) {
			mCustomImage.setVisibility(View.VISIBLE);
			mCustomImage.setImageDrawable(mCustomImgDrawable);
		}
		return this;
	}

	public SweetAlertDialog setCustomImage(int resourceId) {
		return setCustomImage(getContext().getResources().getDrawable(resourceId));
	}

	public String getContentText() {
		return mContentText;
	}

	public SweetAlertDialog setContentText(String text) {
		mContentText = text;
		if (mContentTextView != null && mContentText != null) {
			showContentText(true);
			mContentTextView.setText(mContentText);
		}
		return this;
	}

	public boolean isShowCancelButton() {
		return mShowCancel;
	}


	public SweetAlertDialog showCancelButton(boolean isShow) {
		mShowCancel = isShow;
		if (mCancelButton != null) {
			mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
		} else {
			Log.i("SweetAlertDialog", "showCancelButton -> The mCancelButton is null");
		}
		return this;
	}
	public boolean isShowNeutralButton() {
		return mShowNeutral;
	}

	public SweetAlertDialog showNeutralButton(boolean isShow) {
		mShowNeutral = isShow;
		if (mNeutralButton != null) {
			mNeutralButton.setVisibility(mShowNeutral ? View.VISIBLE : View.GONE);
		} else {
			Log.e("SweetAlertDialog", "showCancelButton -> The mNeutralButton is null");
		}
		return this;
	}

	public boolean isShowContentText() {
		return mShowContent;
	}

	public SweetAlertDialog showContentText(boolean isShow) {
		mShowContent = isShow;
		if (mContentTextView != null) {
			mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
		}
		return this;
	}

	//add pxf 20170816 start
	public SweetAlertDialog showConfirmButton(boolean isShow) {
		if (mConfirmButton != null) {
			mConfirmButton.setVisibility(isShow ? View.VISIBLE : View.GONE);
		} else{
			Log.e("SweetAlertDialog", "showConfirmButton -> The mConfirmButton is null");
		}
		return this;
	}

	public boolean isShowConfirmButton() {
		if (mConfirmButton == null) {
			return false;
		}
		return mConfirmButton.getVisibility() == View.VISIBLE;
	}
	//add pxf 20170816 end

	public String getCancelText() {
		return mCancelText;
	}

	public String getNeutralText(){
		return mNeutralText;
	}

	public SweetAlertDialog setCancelText(String text) {
		mCancelText = text;
		if (mCancelButton != null && mCancelText != null) {
			showCancelButton(true);
			mCancelButton.setText(mCancelText);
		}
		return this;
	}

	public SweetAlertDialog setNeutralText(String text) {
		mNeutralText = text;
		if (mNeutralButton != null && mNeutralText != null) {
			showNeutralButton(true);
			mNeutralButton.setText(mNeutralText);
		}
		return this;
	}

	public String getConfirmText() {
		return mConfirmText;
	}

	public SweetAlertDialog setConfirmText(String text) {
		mConfirmText = text;
		if (mConfirmButton != null && mConfirmText != null) {
			mConfirmButton.setText(mConfirmText);
		}
		return this;
	}

	public SweetAlertDialog setCancelClickListener(OnSweetClickListener listener) {
		mCancelClickListener = listener;
		return this;
	}

	public SweetAlertDialog setConfirmClickListener(OnSweetClickListener listener) {
		mConfirmClickListener = listener;
		return this;
	}

	public SweetAlertDialog setNeutralClickListener(OnSweetClickListener listener) {
		mmNeutralClickListener = listener;
		return this;
	}

	protected void onStart() {
		if (mDialogView != null && mDialogView.getVisibility() == View.GONE) {
			WindowManager.LayoutParams wlp = getWindow().getAttributes();
			wlp.alpha = 1;
			getWindow().setAttributes(wlp);

			mDialogView.setVisibility(View.VISIBLE);
		}
		mDialogView.startAnimation(mModalInAnim);
		playAnimation();
	}

	/**
	 * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
	 */
	@Override
	public void cancel() {
		dismissWithAnimation(true);
	}

	/**
	 * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
	 */
	public void dismissWithAnimation() {
		dismissWithAnimation(false);
	}

	private void dismissWithAnimation(boolean fromCancel) {
		mCloseFromCancel = fromCancel;
		mConfirmButton.startAnimation(mOverlayOutAnim);
		mDialogView.startAnimation(mModalOutAnim);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.cancel_button) {
			if (mCancelClickListener != null) {
				mCancelClickListener.onClick(SweetAlertDialog.this, AlertDialog.BUTTON_NEGATIVE);
			} else {
				dismissWithAnimation();
			}
		} else if (v.getId() == R.id.confirm_button) {
			if (mConfirmClickListener != null) {
				mConfirmClickListener.onClick(SweetAlertDialog.this, AlertDialog.BUTTON_POSITIVE);
			} else {
				dismissWithAnimation();
			}
		} else if (v.getId() == R.id.neutral_button) {
			if (mmNeutralClickListener!=null) {
				mmNeutralClickListener.onClick(SweetAlertDialog.this, AlertDialog. BUTTON_NEUTRAL);
			} else{
				dismissWithAnimation();
			}
		}
	}

	public ProgressHelper getProgressHelper() {
		return mProgressHelper;
	}
}