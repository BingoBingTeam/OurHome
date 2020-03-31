package com.lotus.opengallery.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.lotus.opengallery.R;
import com.lotus.opengallery.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 显示附件：图片、视频
 * @author zl
 */
public class ShowAdjunctActivity extends AppCompatActivity {
    public static final String DATA_FILE_PATH_STRING = "data_string";//地址以","分隔的String对象传递过来
    public static final String DATA_FILE_PATH_LIST_STRING = "data_list_string";//地址以ArrayList<String>对象传递过来
    public static final String DATA_INDEX = "data_index";//位序
    public static final String DATA_FILE_TYPE = "file_type";//文件类型（必须项）
    public static final String DATA_FILE_TYPE_IMAGE = "image";
    public static final String DATA_FILE_TYPE_VIDEO = "video";
    public static final String DATA_FILE_TYPE_AUDIO = "audio";

    private SimpleDateFormat mmssFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);

    private ImageView btnClose;
    private RelativeLayout mLayoutImage;
    private ViewPager viewPager;
    private VideoView mVideoView;
    private TextView tvIndex;
    private RelativeLayout mLayoutAudio;
    private ImageButton mImgPlayAudio;
    private TextView mTvAudioProgress;
    private SeekBar mAudioSeekbar;
    private TextView mTvAudioSumTime;

    private int currentIndex = 0;
    private List<String> mDataPaths;//所有路径
    private MediaController mMediaController;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_adjunct);

        //隐藏状态栏和导航栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = this.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        btnClose = (ImageView) findViewById(R.id.btn_close);
        mLayoutImage = (RelativeLayout)findViewById(R.id.layout_image);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        mVideoView = (VideoView)findViewById(R.id.video_view);
        tvIndex = (TextView) findViewById(R.id.tv_index);
        mLayoutAudio = (RelativeLayout) findViewById(R.id.layout_audio);
        mImgPlayAudio = (ImageButton) findViewById(R.id.imgbtn_play_audio);
        mTvAudioProgress = (TextView) findViewById(R.id.tv_audio_progress);
        mAudioSeekbar = (SeekBar) findViewById(R.id.seekbar_audio);
        mTvAudioSumTime = (TextView) findViewById(R.id.tv_audio_sum_time);

        //关闭页面
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //播放、暂停音频
        mImgPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMediaStatus();
            }
        });
        //音频进度条
        mAudioSeekbar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        initData();
    }

    /**
     * 准备数据
     */
    private void initData(){
        Bundle bundle = getIntent().getExtras();
        if (bundle == null || !bundle.containsKey(DATA_FILE_TYPE)) {
            finish();
        }
        mDataPaths = new ArrayList<>();
        //地址以","分隔的String对象传递过来
        if (bundle.containsKey(DATA_FILE_PATH_STRING)) {
            String paths = bundle.getString(DATA_FILE_PATH_STRING);
            if (!TextUtils.isEmpty(paths)) {
                String[] imagePaths = paths.split(",");
                if (imagePaths.length > 0) {
                    mDataPaths.addAll(Arrays.asList(imagePaths));
                }
            }
        }
        //地址以ArrayList<String>对象传递过来
        if (bundle.containsKey(DATA_FILE_PATH_LIST_STRING)) {
            ArrayList<String> stringList = bundle.getStringArrayList(DATA_FILE_PATH_LIST_STRING);
            if(stringList != null && stringList.size() > 0){
                mDataPaths.addAll(stringList);
            }
        }
        if (mDataPaths == null || mDataPaths.size() == 0) {
            finish();
        }
        if(mDataPaths.size() > 1){
            tvIndex.setVisibility(View.VISIBLE);
        }else {
            tvIndex.setVisibility(View.GONE);
        }
        //文件类型
        switch (bundle.getString(DATA_FILE_TYPE)){
            case DATA_FILE_TYPE_IMAGE:
                //位序
                if (bundle.containsKey(DATA_INDEX)) {
                    currentIndex = bundle.getInt(DATA_INDEX, 0);
                }
                showImage();
                break;
            case DATA_FILE_TYPE_VIDEO:
                showVideo();
                break;
            case DATA_FILE_TYPE_AUDIO:
                showAudio();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
        if (mMediaController != null) {
            mMediaController.hide();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView.setMediaController(null);
            mVideoView.setOnPreparedListener(null);
        }
        mHandler.removeCallbacksAndMessages(null);
        mAudioSeekbar.setProgress(0);
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 显示图片
     */
    private void showImage(){
        mVideoView.setVisibility(View.GONE);
        mLayoutAudio.setVisibility(View.GONE);
        mLayoutImage.setVisibility(View.VISIBLE);
        if (mDataPaths.size() > 0) {
            ViewPagerAdapter myAdapter = new ViewPagerAdapter(ShowAdjunctActivity.this);
            viewPager.setAdapter(myAdapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    tvIndex.setText((position + 1) + "/" + mDataPaths.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            viewPager.setCurrentItem(currentIndex);
            tvIndex.setText((currentIndex + 1) + "/" + mDataPaths.size());
        }
    }

    /**
     * 显示视频
     */
    private void showVideo(){
        mVideoView.setVisibility(View.VISIBLE);
        mLayoutImage.setVisibility(View.GONE);
        mLayoutAudio.setVisibility(View.GONE);
        mVideoView.resume();

        mVideoView.requestFocus();

        String filePath = mDataPaths.get(0);
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                mMediaController = new MediaController(this);
                mMediaController.setAnchorView(mVideoView);
                mVideoView.setMediaController(mMediaController);
                mVideoView.setKeepScreenOn(true);
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer arg0) {
                        mVideoView.start();
                    }
                });
                mVideoView.setVideoPath(filePath);
            } else {
                ToastUtil.showToast(this,"视频丢失，无法播放");
            }
        }
    }

    /**
     * 显示音频
     */
    private void showAudio(){
        mVideoView.setVisibility(View.GONE);
        mLayoutImage.setVisibility(View.GONE);
        mLayoutAudio.setVisibility(View.VISIBLE);
        String filePath = mDataPaths.get(0);
        if (!initializePlayer(filePath)) {
            ToastUtil.showToast(this,"文件丢失，无法播放");
        }else {
            toggleMediaStatus();
        }
    }

    private static final int handler_changetime = 1;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case handler_changetime:
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        int position = mMediaPlayer.getCurrentPosition();
                        mAudioSeekbar.setProgress(position);
                        mTvAudioProgress.setText(mmssFormat.format(new Date(position)));
                        mHandler.sendEmptyMessageDelayed(handler_changetime, 100);
                    }
                    break;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mTvAudioProgress.setText(mmssFormat.format(new Date(seekBar.getProgress())));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.seekTo(progress);
                }
            }
        }
    };

    private boolean initializePlayer(String path) {
        boolean result = false;

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();

            mAudioSeekbar.setMax(mMediaPlayer.getDuration());
            mAudioSeekbar.setProgress(0);

            mTvAudioSumTime.setText(mmssFormat.format(new Date(mAudioSeekbar.getMax())));

            result = true;
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                ToastUtil.showToast(ShowAdjunctActivity.this,"无法播放音频文件");
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                mImgPlayAudio.setVisibility(View.GONE);
                return false;
            }
        });

        // 播放完毕时
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0);
                mAudioSeekbar.setProgress(0);
                mediaPlayer.pause();
                mTvAudioProgress.setText(mmssFormat.format(new Date(mediaPlayer.getCurrentPosition())));
                mImgPlayAudio.setImageResource(R.mipmap.img_button_notification_play_play_grey);
            }
        });

        return result;
    }

    private void toggleMediaStatus() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mImgPlayAudio.setImageResource(R.mipmap.img_button_notification_play_play_grey);
            } else {
                mMediaPlayer.start();
                mMediaPlayer.seekTo(mAudioSeekbar.getProgress());
                mImgPlayAudio.setImageResource(R.mipmap.img_button_notification_play_pause_grey);
                mHandler.sendEmptyMessage(handler_changetime);
            }
        }
    }

    public class ViewPagerAdapter extends PagerAdapter {
        private Context mContext;

        public ViewPagerAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mDataPaths.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(mContext);
            String path = mDataPaths.get(position);
            Glide.with(mContext).load(path).into(photoView);
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}

