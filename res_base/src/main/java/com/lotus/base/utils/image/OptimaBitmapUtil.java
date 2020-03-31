package com.lotus.base.utils.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OptimaBitmapUtil {

	public static Bitmap blurBitmap(Context context, Bitmap bitmap, boolean recycleBitmap) {
//		Log.i("FuzzyUtil:blurBitmap", "begin time" + (new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA).format(new Date())));
		// Let's create an empty bitmap with the same size of the bitmap we want to blur
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		// Instantiate a new Renderscript
		RenderScript rs = RenderScript.create(context);

		// Create an Intrinsic Blur Script using the Renderscript
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

		// Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
		Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
		Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

		// Set the radius of the blur
		blurScript.setRadius(25.f);

		// Perform the Renderscript
		blurScript.setInput(allIn);
		blurScript.forEach(allOut);

		// Copy the final bitmap created by the out Allocation to the outBitmap
		allOut.copyTo(outBitmap);

		// recycle the original bitmap
		if (recycleBitmap)
			bitmap.recycle();

		// After finishing everything, we destroy the Renderscript.
		rs.destroy();

//		Log.i("FuzzyUtil:blurBitmap", "end time" + (new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA).format(new Date())));
		return outBitmap;

	}

	/**
	 * 高斯模糊
	 */
	public static Bitmap BoxBlurFilter(Bitmap bmp) {

//		Log.i("FuzzyUtil:BoxBlurFilter", "begin time" + (new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA).format(new Date())));

		/** 水平方向模糊度 */
		float hRadius = 10;

		/** 竖直方向模糊度 */
		float vRadius = 10;

		/** 模糊迭代度 */
		int iterations = 5;

		int width = bmp.getWidth();
		int height = bmp.getHeight();

		Bitmap bitmap = Bitmap.createBitmap(width / 2, height / 2, Config.ARGB_8888);

		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];

		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);

		for (int i = 0; i < iterations; i++) {
			blur(inPixels, outPixels, width, height, hRadius);
			blur(outPixels, inPixels, height, width, vRadius);
		}
		blurFractional(inPixels, outPixels, width, height, hRadius);
		blurFractional(outPixels, inPixels, height, width, vRadius);
		bitmap.setPixels(inPixels, 0, width, 0, 0, width/2, height/2);

//		Log.i("FuzzyUtil:BoxBlurFilter", "end time" + (new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA).format(new Date())));
		return bitmap;
	}

	private static void blur(int[] in, int[] out, int width, int height, float radius) {
		int widthMinus1 = width - 1;
		int r = (int) radius;
		int tableSize = 2 * r + 1;
		int divide[] = new int[256 * tableSize];

		for (int i = 0; i < 256 * tableSize; i++)
			divide[i] = i / tableSize;

		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;
			int ta = 0, tr = 0, tg = 0, tb = 0;

			for (int i = -r; i <= r; i++) {
				int rgb = in[inIndex + clamp(i, 0, width - 1)];
				ta += (rgb >> 24) & 0xff;
				tr += (rgb >> 16) & 0xff;
				tg += (rgb >> 8) & 0xff;
				tb += rgb & 0xff;
			}

			for (int x = 0; x < width; x++) {
				out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16) | (divide[tg] << 8) | divide[tb];

				int i1 = x + r + 1;
				if (i1 > widthMinus1)
					i1 = widthMinus1;
				int i2 = x - r;
				if (i2 < 0)
					i2 = 0;
				int rgb1 = in[inIndex + i1];
				int rgb2 = in[inIndex + i2];

				ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
				tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
				tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
				tb += (rgb1 & 0xff) - (rgb2 & 0xff);
				outIndex += height;
			}
			inIndex += width;
		}
	}

	private static void blurFractional(int[] in, int[] out, int width, int height, float radius) {
		radius -= (int) radius;
		float f = 1.0f / (1 + 2 * radius);
		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;

			out[outIndex] = in[0];
			outIndex += height;
			for (int x = 1; x < width - 1; x++) {
				int i = inIndex + x;
				int rgb1 = in[i - 1];
				int rgb2 = in[i];
				int rgb3 = in[i + 1];

				int a1 = (rgb1 >> 24) & 0xff;
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = rgb1 & 0xff;
				int a2 = (rgb2 >> 24) & 0xff;
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = rgb2 & 0xff;
				int a3 = (rgb3 >> 24) & 0xff;
				int r3 = (rgb3 >> 16) & 0xff;
				int g3 = (rgb3 >> 8) & 0xff;
				int b3 = rgb3 & 0xff;
				a1 = a2 + (int) ((a1 + a3) * radius);
				r1 = r2 + (int) ((r1 + r3) * radius);
				g1 = g2 + (int) ((g1 + g3) * radius);
				b1 = b2 + (int) ((b1 + b3) * radius);
				a1 *= f;
				r1 *= f;
				g1 *= f;
				b1 *= f;
				out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
				outIndex += height;
			}
			out[outIndex] = in[width - 1];
			inIndex += width;
		}
	}

	private static int clamp(int x, int a, int b) {
		return (x < a) ? a : (x > b) ? b : x;
	}

	/**
	 * 图片质量压缩
	 *
	 * @param image
	 * @param max_kb
	 *            图片压缩后的最大字节数
	 * @return
	 */
	public static Bitmap getCompressImageOfQuality(Bitmap image, int max_kb) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		// int options = 100;
		// while (baos.toByteArray().length / 1024 > max_kb) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
		// baos.reset();// 重置baos即清空baos
		// image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		// options -= 10;// 每次都减少10
		// }

		int options = 100;
		if (baos.toByteArray().length > max_kb * 1024) {
			options = (int) (max_kb * 1024.0 / baos.toByteArray().length * 100);
		}
		if (options == 0) {
			options = 1;
		}

		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		baos.reset();
		baos = null;

		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 根据原始图的路径将图片按照传入的尺寸压缩，压缩后的图片尺寸最大不超过传入的长或宽
	 *
	 * @param srcPath
	 * @param height
	 *            传入的最大高度
	 * @param width
	 *            传入的最大宽度
	 * @return
	 */
	public static Bitmap getCompressImageOfSize(String srcPath, float height, float width) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
		newOpts.inJustDecodeBounds = false;

		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > width) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / width);
		} else if (w < h && h > height) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / height);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;
	}

	/**
	 * 根据Bitmap将图片按照传入的尺寸压缩，压缩后的图片尺寸最大不超过传入的长或宽
	 *
	 * @param image
	 * @param height
	 *            传入的最大高度
	 * @param width
	 *            传入的最大宽度
	 * @return
	 */
	public static Bitmap getCompressImageOfSize(Bitmap image, float width, float height) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, options);
		options.inJustDecodeBounds = false;
		int oWight = options.outWidth;
		int oHeight = options.outHeight;
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int inSampleSize = 1;// be=1表示不缩放
		if (oWight > oHeight && oWight > width) {// 如果宽度大的话根据宽度固定大小缩放
			inSampleSize = (int) (options.outWidth / width);
		} else if (oWight < oHeight && oHeight > height) {// 如果高度高的话根据宽度固定大小缩放
			inSampleSize = (int) (options.outHeight / height);
		}
		if (inSampleSize <= 0 || inSampleSize >= 0) {
			inSampleSize = 1;
		}
		options.inSampleSize = inSampleSize;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, options);
		return bitmap;
	}

	/**
	 * 缩略图 ，缩放比例计算
	 *
	 * @param options
	 * @param width
	 * @param height
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options, int width, int height) {

		int oWight = options.outWidth;
		int oHeight = options.outHeight;
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int inSampleSize = 1;// be=1表示不缩放
		if (oWight > oHeight && oWight > width) {// 如果宽度大的话根据宽度固定大小缩放
			inSampleSize = (int) (options.outWidth / width);
		} else if (oWight < oHeight && oHeight > height) {// 如果高度高的话根据宽度固定大小缩放
			inSampleSize = (int) (options.outHeight / height);
		}
		if (inSampleSize <= 0)
			inSampleSize = 1;

		return inSampleSize;
	}

	// 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
	private static Bitmap createScaleBitmap(Bitmap src, int width, int height) {

		int temp = width;
		if (temp < height)
			temp = height;
		// Bitmap bitmap = Bitmap.createScaledBitmap(src, width, height, false);
		Bitmap bitmap = resize(src, temp);

		if (src != bitmap) {// 如果没有缩放，那么不回收
			src.recycle(); // 释放Bitmap的native像素数组
			src = null;
		}
		return bitmap;
	}

	private static Bitmap resize(Bitmap bitmap, int S) {
		try {
			int imgWidth = bitmap.getWidth();
			int imgHeight = bitmap.getHeight();
			double partion = imgWidth * 1.0 / imgHeight;
			double sqrtLength = Math.sqrt(partion * partion + 1); // 新的缩略图大小
			double newImgW = S * (partion / sqrtLength);
			double newImgH = S * (1 / sqrtLength);
			float scaleW = (float) (newImgW / imgWidth);
			float scaleH = (float) (newImgH / imgHeight);
			Matrix mx = new Matrix(); // 对原图片进行缩放
			mx.postScale(scaleW, scaleH);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, imgWidth, imgHeight, mx, true);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return bitmap;
		}
	}

	/**
	 * 从Resources中加载图片
	 *
	 * @param resources
	 * @param resourceId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources resources, int resourceId, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, resourceId, options); // 读取图片长款
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);// 计算inSampleSize
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId, options); // 载入一个稍大的缩略图
		return createScaleBitmap(bitmap, reqWidth, reqHeight); // 进一步得到目标大小的缩略图
	}

	/**
	 * 从sd卡上加载图片
	 *
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromSD(String pathName, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(pathName, options);
		return createScaleBitmap(bitmap, reqWidth, reqHeight);
	}

	/**
	 * 从sd卡上加载图片
	 */
	public static Bitmap getBitmapFromSD(String pathName) {
		return BitmapFactory.decodeFile(pathName);
	}

	/**
	 * 根据sd卡图片路径 和限制大小返回位图
	 *
	 * @param pathName
	 * @param max_kb
	 * @return
	 */
	public static Bitmap getCompressImageOfQualityFromSD(String pathName, int max_kb) {

		Bitmap bitmap = getCompressImageOfQuality(BitmapFactory.decodeFile(pathName), max_kb);

		return bitmap;
	}

	/**
	 * 获取网络图片
	 *
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static Bitmap getWebImage(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10 * 1000);// 连接超时时间
		conn.setConnectTimeout(10 * 1000);
		conn.setRequestMethod("GET");
		InputStream is = null;
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			is = conn.getInputStream();
		} else {
			is = null;
		}
		if (is == null) {
			throw new RuntimeException("stream is null");
		} else {
			try {
				byte[] data = readStream(is);
				if (data != null) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					return bitmap;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			is.close();
			return null;
		}
	}

	/*
	 * 得到图片字节流 数组大小
	 */
	private static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 图片压缩 并保存
	 *
	 * @param folderPath
	 *            文件夹路径
	 * @param imageName
	 *            需要压缩的图片名称
	 * @param inSampleSize
	 *            压缩比例
	 * @return
	 */
	public static boolean ImageQuality(String folderPath, String imageName, int inSampleSize) {

		if (inSampleSize == 1) {
			return true;
		}

		File folder = new File(folderPath);
		if (folder.exists() == false) {
			folder.mkdirs();
		}

		// long availableSize = FileUtil.getAvailableSize(folderPath);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize; // 这个数字越大,图片大小越小.
		Bitmap picture = BitmapFactory.decodeFile(folderPath + "/" + imageName, options);
		// Bitmap picture = BitmapFactory.decodeFile(folderPath + "/" + imageName);

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(folderPath + "/" + imageName);
			if (picture != null) {
				picture.compress(Bitmap.CompressFormat.JPEG, 100 - inSampleSize * 10, fileOutputStream);// 前面不用options时此方式要是比例更高
				// picture.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

				picture.isRecycled();
				picture = null;
			}
			fileOutputStream.flush();
			fileOutputStream.close();

			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean saveBitmapToSD(Bitmap bitmap, String folderPath, String fileName, boolean recycle) {
		boolean result = false;
		if (bitmap != null) {
			File folder = new File(folderPath);
			if (folder.exists() == false) {
				folder.mkdirs();
			}

			File file = new File(folderPath, fileName);
			if (file.exists()) {
				file.delete();
			}

			try {
				FileOutputStream out = new FileOutputStream(file);
				result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				out.close();

				if (recycle) {
					bitmap.recycle();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}


	public static boolean saveBitmapByteToSDImage(byte[] bitmapData, String folderPath, String imageName) {
		boolean result = false;

		File file = new File(folderPath, imageName); // 创建File对象，其中包含文件所在的目录以及文件的命名
		FileOutputStream outputStream = null;// 创建FileOutputStream对象
		BufferedOutputStream bufferedOutputStream = null;// 创建BufferedOutputStream对象
		try {
			if (file.exists()) {
				file.delete();// 如果文件存在则删除
			}

			file.createNewFile(); // 在文件系统中根据路径创建一个新的空文件

			outputStream = new FileOutputStream(file); // 获取FileOutputStream对象
			bufferedOutputStream = new BufferedOutputStream(outputStream); // 获取BufferedOutputStream对象
			bufferedOutputStream.write(bitmapData); // 往文件所在的缓冲输出流中写byte数据
			bufferedOutputStream.flush();// 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
		} catch (Exception e) {
			e.printStackTrace();// 打印异常信息
		} finally {
			if (outputStream != null) {    // 关闭创建的流对象
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bufferedOutputStream != null) {
				try {
					bufferedOutputStream.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return result;
	}
}
