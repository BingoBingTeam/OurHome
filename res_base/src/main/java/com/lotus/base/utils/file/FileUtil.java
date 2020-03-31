package com.lotus.base.utils.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.lotus.base.utils.log.OptimaLogRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FileUtil {

	public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
	public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
	public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
	public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值

	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bytesToHexString(digest.digest());
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 获取文件拓展名
	 */
	public static String getExtension(File file) {
		String suffix = "";
		String name = file.getName();
		final int idx = name.lastIndexOf(".");
		if (idx > 0) {
			suffix = name.substring(idx + 1);
		}
		return suffix;
	}

	/**
	 * 获取文件MIME类型
	 */
	public static String getMimeType(File file) {
		String extension = getExtension(file);
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	}

	/**
	 * 获取文件指定文件的指定单位的大小
	 * @param filePath 文件路径
	 * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
	 * @return double值的大小
	 */
	public static double getFileOrFilesSize(String filePath, int sizeType) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("获取文件大小", "获取失败!");
		}
		return formatFileSize(blockSize, sizeType);
	}

	/**
	 * 调用此方法自动计算指定文件或指定文件夹的大小
	 * @param filePath 文件路径
	 * @return 计算好的带B、KB、MB、GB的字符串
	 */
	public static String getAutoFileOrFilesSize(String filePath) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("获取文件大小", "获取失败!");
		}
		return formatFileSize(blockSize);
	}

	/**
	 * 获取指定文件大小
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			file.createNewFile();
			Log.e("获取文件大小", "文件不存在!");
		}
		return size;
	}

	/**
	 * 获取指定文件夹的大小
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private static long getFileSizes(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSizes(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}
		}
		return size;
	}

	/**
	 * 转换文件大小单位
	 * @param fileLength
	 * @return
	 */
	private static String formatFileSize(long fileLength) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileLength == 0) {
			return wrongSize;
		}
		if (fileLength < 1024) {
			fileSizeString = df.format((double) fileLength) + "B";
		} else if (fileLength < 1048576) {
			fileSizeString = df.format((double) fileLength / 1024) + "KB";
		} else if (fileLength < 1073741824) {
			fileSizeString = df.format((double) fileLength / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileLength / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	/**
	 * 转换文件大小,指定转换的类型
	 * @param fileLength
	 * @param sizeType
	 * @return
	 */
	private static double formatFileSize(long fileLength, int sizeType) {
		DecimalFormat df = new DecimalFormat("#.00");
		double fileSizeLong = 0;
		switch (sizeType) {
			case SIZETYPE_B:
				fileSizeLong = Double.valueOf(df.format((double) fileLength));
				break;
			case SIZETYPE_KB:
				fileSizeLong = Double.valueOf(df.format((double) fileLength / 1024));
				break;
			case SIZETYPE_MB:
				fileSizeLong = Double.valueOf(df.format((double) fileLength / 1048576));
				break;
			case SIZETYPE_GB:
				fileSizeLong = Double.valueOf(df.format((double) fileLength / 1073741824));
				break;
			default:
				break;
		}
		return fileSizeLong;
	}

	/**
	 * 创建文件夹 ,创建成功返回File，失败返回null, 如果文件存在但是不是文件夹返回null
	 * @param folderPath 路径
	 * @return folder
	 */
	public static File createFolder(String folderPath) {

		if (TextUtils.isEmpty(folderPath)) {
			return null;
		}

		File folder = new File(folderPath);

		if (folder.exists()) { // 文件存在
			if (folder.isDirectory() == false) { // 不是文件键
				return null;
			}
			return folder;
		} else {

			boolean createResult = folder.mkdirs();

			return createResult ? folder : null;
		}
	}

	/**
	 * 获取指定文件夹下所有指定类型文件的路径，不计算子文件夹
	 */
	public List<FileBean> getAllFilesPath(String folderPath, String format) {
		List<FileBean> list = new ArrayList<FileBean>();
		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		if (files == null || files.length == 0) {
			return list;
		}
		for (File file : files) {
			FileBean fileBean = new FileBean();
			if (!file.isDirectory()) {//判断不是文件夹
				String filePath = file.getAbsolutePath();
				if (format == null) {
					fileBean.setFileSize(file.length());
					fileBean.setFilePath(filePath);
					list.add(fileBean);
				} else {
					String fileFormat = filePath.substring(filePath.lastIndexOf(".") + 1);
					if (format.equals(fileFormat)) {
						fileBean.setFileSize(file.length());
						fileBean.setFilePath(filePath);
						list.add(fileBean);
					}
				}
			}
//			else{
//				List<FileBean> pathArr = getAllFilesPath(folderPath, format);
//				list.addAll(pathArr);
//			}
		}

		return list;
	}

	public List<FileBean> traverse(File file) {
		List<FileBean> list = new ArrayList<FileBean>();
		if (file.isDirectory() == false) {
			FileBean fileBean = new FileBean();
			String filePath = file.getAbsolutePath();
			fileBean.setFileSize(file.length());
			fileBean.setFilePath(filePath);
			list.add(fileBean);
		} else {
			File[] files = file.listFiles();
			for (File f : files) {
				traverse(f);
			}
		}
		return list;
	}

	/**
	 * 获取文件夹大小
	 * @param file File实例
	 * @return long
	 */
	public long getFolderSize(File file) {

		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * 删除指定目录下文件及目录
	 * @param filePath
	 * @param deleteThisPath
	 * @return
	 */
	public void deleteFolderFile(String filePath, boolean deleteThisPath) {
		if (!TextUtils.isEmpty(filePath)) {
			try {
				File file = new File(filePath);
				if (file.isDirectory()) {// 处理目录
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						deleteFolderFile(files[i].getAbsolutePath(), true);
					}
				}
				if (deleteThisPath) {
					if (!file.isDirectory()) {// 如果是文件，删除
						file.delete();
					} else {// 目录
						if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
							file.delete();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 格式化单位
	 * @param size
	 * @return
	 */
	public String getFormatSize(double size) {
		if (size <= 0) {
			return "0M";
		}
		DecimalFormat dcmFmt = new DecimalFormat("0.0");
		double kiloByte = size / 1024;
		kiloByte = Double.valueOf(dcmFmt.format(kiloByte));
		if (kiloByte < 1) {
			return kiloByte + "B";
		}

		double megaByte = kiloByte / 1024;
		megaByte = Double.valueOf(dcmFmt.format(megaByte));
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K";
		}

		double gigaByte = megaByte / 1024;
		gigaByte = Double.valueOf(dcmFmt.format(gigaByte));
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
		}

		double teraBytes = gigaByte / 1024;
		teraBytes = Double.valueOf(dcmFmt.format(teraBytes));
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "G";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "T";
	}

	/**
	 * 复制单个文件
	 */
	public static boolean copyFile(String oldPath, String newPath) {

		boolean result = false;

		InputStream inputStream = null;
		FileOutputStream fileOutputStream = null;
		try {

			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				inputStream = new FileInputStream(oldPath); // 读入原文件
				fileOutputStream = new FileOutputStream(newPath);

				byte[] buffer = new byte[1024];
				int byteread = 0;
				while ((byteread = inputStream.read(buffer)) != -1) {
					fileOutputStream.write(buffer, 0, byteread);
				}
			}

			result = true;
		} catch (Exception e) {
			Log.e("copyFile", "复制文件出错");
			e.printStackTrace();

		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	/**
	 * 删除文件
	 */
	public static boolean delFile(String filePathAndName) {
		boolean result = false;
		try {
			File myDelFile = new File(filePathAndName);
			if (myDelFile.isDirectory()) {
				delFolder(filePathAndName);
			} else {
				result = myDelFile.delete();
			}
			result = true;
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 删除文件夹
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件夹里面的所有文件
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 复制整个文件夹内容
	 */
	public void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 移动文件到指定目录
	 */
	public void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);
	}

	/**
	 * 外部设备的目录.这里主要是指SD卡
	 */
	public static String SDPATH = Environment.getExternalStorageDirectory() + "/";

	private static String getSDPATH() {
		return SDPATH;
	}

	/**
	 * 获得路径上一级路径.也就是去掉最后一级路径
	 */
	public static String rollbackPath(String path) {
		String dirPath = "";
		int index = path.lastIndexOf("/");
		System.out.println("index=" + index);
		return path.substring(0, index);
	}

	/**
	 * 在路径后面追加文件名或路径名
	 */
	public static String pathAddToFileName(String path, String fileName) {
		String dirPath = "";
		System.out.println("--" + path.substring(path.length() - 1));
		if ("/".equals(path.substring(path.length() - 1))) {
			dirPath = path + fileName;
		} else {
			dirPath = path + "/" + fileName;
		}
		return dirPath;
	}

	public static String showFileSize(long fileSize) {
		float temp = fileSize / 1024.0f;
		if (temp >= 1024) {
			// 以M为单位
			float t = fileSize / 1048576.0f;
			DecimalFormat dcmFmt = new DecimalFormat("0.00");
			return dcmFmt.format(t) + " MB";
		} else if (temp > 1) {
			// 以k为单位.保留1位小数点
			DecimalFormat dcmFmt = new DecimalFormat("0.0");
			return dcmFmt.format(temp) + " KB";
		} else {
			// 以k为单位.保留3位小数点
			DecimalFormat dcmFmt = new DecimalFormat("0.000");
			return dcmFmt.format(temp) + " KB";
		}
	}

	/**
	 * 在SD卡上创建文件
	 * @param fileName 文件名
	 * @return File 创建的文件对象
	 * @throws IOException
	 */
	public static File createSDFile(String path, String fileName) throws IOException {
		File file = new File(SDPATH + path + "/" + fileName);
		// file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * <p>
	 * 目录名
	 * @return File 创建目录的文件对象
	 */
	public static File createSDDir(String path) {
		File dir = new File(SDPATH + path);
		dir.mkdirs();
		return dir;
	}

	/**
	 * 判断文件是否存在
	 */
	public Boolean isFileExist(String path, String fileName) {
		File file = new File(SDPATH + path + "/" + fileName);
		return file.exists();
	}

	/**
	 * 判断文件是否存在
	 */
	public Boolean isFileExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	/**
	 * 获取文件大小
	 */
	public long getFileSize(String path) {
		File file = new File(path);
		return file.length();
	}

	/**
	 * 判断路径是否存在
	 */
	public static Boolean isDirExist(String path) {
		File file = new File(SDPATH + path);
		return file.exists();
	}

	/**
	 * 获得file对象
	 */
	public static File readFromDS(String fullPath) {
		return new File(fullPath);
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File writedToDS(String path, String fileName, InputStream inputStream) {

		String realFileName = null;
		if (!isDirExist(path)) {
			// 如果文件夹不存在.创建
			createSDDir(path);
		}
		File outFile = new File(SDPATH + path + "/" + fileName);
		try {
			if (isFileExist(path, fileName)) {
				Date date = new Date();
				// 1、如果用“.”作为分隔的话，必须是如下写法：String.split("\\."),这样才能正确的分隔开，不能用String.split(".");
				// 2、如果用“|”作为分隔的话，必须是如下写法：String.split("\\|"),这样才能正确的分隔开，不能用String.split("|");“.”和“|”都是转义字符，必须得加"\\";
				// 3、如果在一个字符串中有多个分隔符，可以用“|”作为连字符，比如：“a=1 and b =2 or
				// c=3”,把三个都分隔出来，可以用String.split("and|or");
				String[] tempfileName = fileName.split("\\.");
				String fileNameNoExt = "";
				for (int i = 0; i < tempfileName.length - 1; i++) {
					fileNameNoExt = fileNameNoExt + tempfileName[i];
				}
				realFileName = fileNameNoExt + "_" + date.getTime() + "." + tempfileName[tempfileName.length - 1];
			} else {
				realFileName = fileName;
			}

			FileOutputStream fos = new FileOutputStream(outFile);
			byte[] byteArr = new byte[1024];
			// 读取的字节数
			int readCount = inputStream.read(byteArr);
			// 如果已到达文件末尾，则返回-1
			while (readCount != -1) {
				fos.write(byteArr, 0, readCount);
				readCount = inputStream.read(byteArr);
			}
			inputStream.close();
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return outFile;
	}

	/**
	 * 检查要打开的文件的后缀是否在遍历后缀数组中
	 */
	public static boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd)) {
				return true;
			}
		}
		return false;
	}

	public static String getMIMEType(File file) {
		String type = "*/*";
		if (null == file) {
			return "";
		}
		String fName = file.getName();
		int fNameLength = fName.length();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0 || fNameLength <= 1) {
			return type;
		}

		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex + 1, fName.length()).toLowerCase();
		if (end == "") {
			return type;
		}
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		return mimeTypeMap.getMimeTypeFromExtension(end);

//		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
//		for (int i = 0; i < MIME_MapTable.length; i++) {
//			if (end.equals(MIME_MapTable[i][0]))
//				type = MIME_MapTable[i][1];
//
//		}
//		return type;
	}

	/**
	 * 打开文件
	 */
	public static void openFile(File file, Context context) {
		// 获取文件file的MIME类型
		String type = getMIMEType(file);
		if (TextUtils.isEmpty(type) || "*/*".equals(type)) {
			Toast.makeText(context, "未发现可打开该文件的应用", Toast.LENGTH_SHORT).show();
			return;
		}

//		List<ResolveInfo> resolveInfos = AppUtil.getOpenApplicationByType(context, type);
//		if (resolveInfos == null || resolveInfos.size() == 0) {
//			Toast.makeText(context,"未发现可打开该文件的应用", Toast.LENGTH_SHORT).show();
//			return ;
//		}

		try {

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_DEFAULT);

			Uri uri = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

				//添加这一句表示对目标应用临时授权该Uri所代表的文件
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			} else {
				// Uri uri = Uri.parse("file://"+file.getAbsolutePath());
				uri = Uri.fromFile(file);
			}
			intent.setDataAndType(uri, type);// 设置intent的data和Type属性。

			context.startActivity(intent);// 跳转
		} catch (Exception e) { //当系统没有携带文件打开软件，提示
			Toast.makeText(context, "无法打开该格式文件!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			OptimaLogRecord.e("AppUtil#installApp", e.getMessage());
		}

	}

	//建立一个MIME类型与文件后缀名的匹配表

	public static final String[][] MIME_MapTable = {

			//{后缀名，    MIME类型}
			{".amr", "audio/amr"}, {".3gpp", "audio/3gpp"}, {".3gp", "video/3gpp"}, {".apk", "application/vnd.android.package-archive"}, {".asf",
			"video/x-ms" + "-asf"}, {".avi", "video/x-msvideo"}, {".bin", "application/octet-stream"}, {".bmp", "image/bmp"}, {".c", "text/plain"}, {".class",
			"application" + "/octet-stream"}, {".conf", "text/plain"}, {".cpp", "text/plain"}, {".doc", "application/msword"}, {".exe", "application/octet" +
			"-stream"}, {".gif", "image/gif"}, {".gtar", "application/x-gtar"}, {".gz", "application/x-gzip"}, {".h", "text/plain"}, {".htm", "text/html"}, {
				".html", "text/html"}, {".jar", "application/java-archive"}, {".java", "text/plain"}, {".jpeg", "image/jpeg"}, {".jpg", "image/jpeg"}, {".js",
			"application/x-javascript"}, {".log", "text/plain"}, {".m3u", "audio/x-mpegurl"}, {".m4a", "audio/mp4a-latm"}, {".m4b", "audio/mp4a-latm"}, {".m4p"
			, "audio/mp4a-latm"}, {".m4u", "video/vnd.mpegurl"}, {".m4v", "video/x-m4v"}, {".mov", "video/quicktime"}, {".mp2", "audio/x-mpeg"}, {".mp3",
			"audio/x-mpeg"}, {".mp4", "video/mp4"}, {".mpc", "application/vnd.mpohun.certificate"}, {".mpe", "video/mpeg"}, {".mpeg", "video/mpeg"}, {".mpg",
			"video/mpeg"}, {".mpg4", "video/mp4"}, {".mpga", "audio/mpeg"}, {".msg", "application/vnd.ms-outlook"}, {".ogg", "audio/ogg"}, {".pdf",
			"application/pdf"}, {".png", "image/png"}, {".pps", "application/vnd.ms-powerpoint"}, {".ppt", "application/vnd.ms-powerpoint"}, {".prop",
			"text" + "/plain"}, {".rar", "application/x-rar-compressed"}, {".rc", "text/plain"}, {".rmvb", "audio/x-pn-realaudio"}, {".rtf",
			"application/rtf"}, {".sh", "text/plain"}, {".tar", "application/x-tar"}, {".tgz", "application/x-compressed"}, {".txt", "text/plain"}, {".wav",
			"audio/x-wav"}, {".wma", "audio/x-ms-wma"}, {".wmv", "audio/x-ms-wmv"}, {".wps", "application/vnd.ms-works"},
			//{".xml",    "text/xml"},
			{".xml", "text/plain"}, {".z", "application/x-compress"}, {".zip", "application/zip"}, {"", "*/*"}};

	/**
	 * 删除文件夹里超过6个月的文件
	 * @return
	 */
	public static boolean deleteTimeOutFile(String path){
		try{
			File fileDir = new File(path);
			if(fileDir.exists()){
				File[] fileArray = fileDir.listFiles();
				if (fileArray != null) {
					for (File file : fileArray) {
						if (file.isFile()) {
							//文件最后一次被修改的时间
							long lastModifiedTime = file.lastModified();
							Date fileDate = new Date(lastModifiedTime);

							//用于比较的时间：当前时间往前退6个月
							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.MONTH, -6);
							Date sixMonthData = cal.getTime();
							if(sixMonthData.after(fileDate)){
								file.delete();
							}
						} else {
							deleteTimeOutFile(file.getAbsolutePath());
						}
					}
				}
			}
		}catch (Exception e){
			return false;
		}
		return true;
	}



}
