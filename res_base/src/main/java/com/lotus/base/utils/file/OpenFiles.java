package com.lotus.base.utils.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class OpenFiles {

    /** 消息类型 - 系统通知 {新建组、成员变更、组信息修改} */
    public static final String _TYPE_NOTIFICATION = "NOTIFICATION";

    /** 消息类型 - 常规文字 */
    public static final String _TYPE_TEXT = "text";

    /** 消息类型-语音*/
    public static final String _TYPE_AUDIO = "audio";

    /** 消息类型-视频*/
    public static final String _TYPE_VIDEO = "video";

    /** 消息类型-office*/
    public static final String _TYPE_APPLICATION = "application";

    /** 消息类型-图片*/
    public static final String _TYPE_IAMGE = "image";

    // Word、excel、txt、pdf、

    /** 消息类型 - 文件 - 音频 - MP3 */
    public static final String _TYPE_MP3 = "mp3";
    /** 消息类型 - 文件 - 音频 - RAM */
    public static final String _TYPE_RAM = "ram";

    /** 消息类型 - 文件 - 视频 - MP4 */
    public static final String _TYPE_MP4 = "mp4";
    /** 消息类型 - 文件 - 视频 - 3GP */
    public static final String _TYPE_3GP = "3gp";
    /** 消息类型 - 文件 - 视频 - AVI */
    public static final String _TYPE_AVI = "avi";

    /** 消息类型 - 文件 - 图片 - JPGE */
    public static final String _TYPE_JPGE = "jpeg";
    /** 消息类型 - 文件 - 图片 - PNG */
    public static final String _TYPE_PNG = "png";
    /** 消息类型 - 文件 - 图片 - GIF */
    public static final String _TYPE_GIF = "gif";

    /** 消息类型 - 文件 - 文本 - EXCEL */
    public static final String _TYPE_EXCEL = "execle";
    /** 消息类型 - 文件 - 文本 - PDF */
    public static final String _TYPE_PDF = "pdf";
    /** 消息类型 - 文件 - 文本 - txt */
    public static final String _TYPE_TXT = "txt";
    /** 消息类型 - 文件 - 文本 - WORD */
    public static final String _TYPE_WORD = "word";
    /** 消息类型 - 文件 - 文本 - PPT */
    public static final String _TYPE_PPT = "ppt";
    /** 消息类型 - 其他*/
    public static final String _TYPE_UNKONW = "unkonw";

	/** 打开文件*/
	public static boolean openFile(String filePath,String type,Context context){
		try{
			switch (type) {
			case _TYPE_PDF:
				Intent intentPdf = OpenFiles.getPPTFileIntent(filePath);
				context.startActivity(intentPdf);
				break;
			case _TYPE_TXT:
				Intent intentTxt = getTextFileIntent(filePath);
				context.startActivity(intentTxt);
				break;
			case _TYPE_WORD:
				Intent intentWord = getWordFileIntent(filePath);
				context.startActivity(intentWord);
				break;
			case _TYPE_EXCEL:
				Intent intentExecl = getExcelFileIntent(filePath);
				context.startActivity(intentExecl);
				break;
			case _TYPE_PPT:
				Intent intentPpt = getPPTFileIntent(filePath);
				context.startActivity(intentPpt);
				break;
			default:
				break;
			}
        }catch (Exception e){
        	return false;
        }
		return true;
	}

	//android获取一个用于打开HTML文件的intent
    public static Intent getHtmlFileIntent(String Path) {
        File file = new File(Path);
        Uri uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider")
        		.scheme("content").encodedPath(file.toString()).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    //android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    //android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    //android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    //android获取一个用于打开音频文件的intent
    public static Intent getAudioFileIntent(String Path)  {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    //android获取一个用于打开视频文件的intent
    public static Intent getVideoFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "video/*");
        return intent;
    }


    //android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }


    //android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //android获取一个用于打开PPT文件的intent
    public static Intent getPPTFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //android获取一个用于打开apk文件的intent
    public static Intent getApkFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }
}
