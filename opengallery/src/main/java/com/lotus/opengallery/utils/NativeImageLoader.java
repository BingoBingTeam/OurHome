package com.lotus.opengallery.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.collection.LruCache;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地图片加载器,采用的是异步解析本地图片，单例模式利用getInstance()获取NativeImageLoader实例
 * 调用loadNativeImage()方法加载本地图片，此类可作为一个加载本地图片的工具类
 */
public class NativeImageLoader {
	private LruCache<String, Bitmap> mMemoryCache;
	private static NativeImageLoader mInstance = new NativeImageLoader();
	private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(4);//线程池，固定线程池，只能有一个线程

	private NativeImageLoader(){
		//获取应用程序的最大内存
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		//用最大内存的1/4来存储图片
		final int cacheSize = maxMemory / 4;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			//获取每张图片的大小
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
	}

	/**
	 * 通过此方法来获取NativeImageLoader的实例
	 * @return
	 */
	public static NativeImageLoader getInstance(){
		return mInstance;
	}


	/**
	 * 加载本地图片，对图片不进行裁剪
	 * @param path
	 * @param mCallBack
	 * @return
	 */
	public Bitmap loadNativeImage(final String path, final NativeImageCallBack mCallBack){
		return this.loadNativeImage(path, null, mCallBack);
	}

	/**
	 * 此方法来加载本地图片，这里的mPoint是用来封装ImageView的宽和高，我们会根据ImageView控件的大小来裁剪Bitmap
	 * 如果你不想裁剪图片，调用loadNativeImage(final String path, final NativeImageCallBack mCallBack)来加载
	 * @param path
	 * @param mPoint
	 * @param mCallBack
	 * @return
	 */
	public Bitmap loadNativeImage(final String path, final Point mPoint, final NativeImageCallBack mCallBack){
		//先获取内存中的Bitmap
		Bitmap bitmap = getBitmapFromMemCache(path);
		final Handler mHander = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				mCallBack.onImageLoader((Bitmap)msg.obj, path);
			}

		};

		//若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
		if(bitmap == null){
			mImageThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					//先获取图片的缩略图
					Bitmap mBitmap = decodeThumbBitmapForFile(path, mPoint == null ? 0: mPoint.x, mPoint == null ? 0: mPoint.y);
					Message msg = mHander.obtainMessage();
					msg.obj = mBitmap;
					mHander.sendMessage(msg);

					//将图片加入到内存缓存
					addBitmapToMemoryCache(path, mBitmap);
				}
			});
		}
		return bitmap;

	}

	/**
     * 获取指定文件大小
     * @return
     * @throws Exception 　　
     */
    public String getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return FormetFileSize(size);
    }

    public String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

	/**
	 * 往内存缓存中添加Bitmap
	 *
	 * @param key
	 * @param bitmap
	 */
	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 根据key来获取内存中的图片
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}


	/**
	 * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();
		//设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		//设置缩放比例
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		//设置为false,解析Bitmap对象加入到内存中
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, options);
	}


	/**
	 * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
	 * @param options
	 */
	private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight){
		int inSampleSize = 1;
		if(viewWidth == 0 || viewWidth == 0){
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;

		//假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
		if(bitmapWidth > viewWidth || bitmapHeight > viewWidth){
			int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

			//为了保证图片不缩放变形，我们取宽高比例最小的那个
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}


	/**
	 * 加载本地图片的回调接口
	 *
	 * @author xiaanming
	 *
	 */
	public interface NativeImageCallBack{
		/**
		 * 当子线程加载完了本地的图片，将Bitmap和图片路径回调在此方法中
		 * @param bitmap
		 * @param path
		 */
		public void onImageLoader(Bitmap bitmap, String path);
	}

	/** 获取视频文件的缩略图*/
	public Bitmap getVideoThumbnail(final String filePath , final Point mPoint, final NativeImageCallBack mCallBack) {
		//Bitmap bitmap = getBitmapFromMemCache(filePath);
		Bitmap bitmap = null;
		final Handler mHander = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				mCallBack.onImageLoader((Bitmap)msg.obj, filePath);
			}
		};

		//若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
		if(bitmap == null){
			mImageThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					MediaMetadataRetriever retriever = new MediaMetadataRetriever();
					try {
						retriever.setDataSource(filePath);
						Bitmap mBitmap = retriever.getFrameAtTime();
						Message msg = mHander.obtainMessage();
						msg.obj = mBitmap;
						mHander.sendMessage(msg);
						//将图片加入到内存缓存
						addBitmapToMemoryCache(filePath, mBitmap);
					}   catch(IllegalArgumentException e) {
						e.printStackTrace();
					}   catch (RuntimeException e) {
						e.printStackTrace();
					}   finally {
						try {
							retriever.release();
						}catch (RuntimeException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
		return bitmap;
	}
}
