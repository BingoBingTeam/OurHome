package com.lotus.base.utils.media;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询媒体文件工具
 * Created by pxf on 2018/7/16.
 */

public class MediaUtils {

    /**
     * 查询指定路径下的图片
     * @param context
     * @param targetPaths
     * @return
     */
    public static List<String> getTargetFilePic(Context context, String targetPaths){
        if (TextUtils.isEmpty(targetPaths)){
            return  null;
        }
        List<String> picPathList = new ArrayList<>();
        String selection = MediaStore.Images.Media.DATA + " like ?";
        String[] selectionArgs = {"%" + targetPaths + "%"};
        Cursor c = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                selection, selectionArgs, null);
        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                Log.i("MediaUtils","getTargetFilePic.path = " +path);
                picPathList.add(path);
            }
            c.close();
        }
        return picPathList;
    }

    /**
     * 查询指定路劲下的视频
     * @param context
     * @param targetPaths
     * @return
     */
    public static List<String> getTargetFileVideo(Context context, String targetPaths){
        if (TextUtils.isEmpty(targetPaths)){
            return  null;
        }
        List<String> videoPathList = new ArrayList<>();
        String selection = MediaStore.Video.Media.DATA + " like ?";
        String[] selectionArgs = {"%" + targetPaths + "%"};
        Cursor c = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                selection, selectionArgs, null);
        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndex(MediaStore.Video.Media.DATA));
                Log.i("MediaUtils","getTargetFileVideo.path = " +path);
                videoPathList.add(path);
            }
            c.close();
        }
        return videoPathList;
    }

    /**
     * 查询指定路劲下的音频
     * @param context
     * @param targetPaths
     * @return
     */
    public static List<String> getTargetFileAudio(Context context, String targetPaths){
        if (TextUtils.isEmpty(targetPaths)){
            return  null;
        }
        List<String> audioPathList = new ArrayList<>();
        String selection = MediaStore.Audio.Media.DATA + " like ?";
        String[] selectionArgs = {"%" + targetPaths + "%"};
        Cursor c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                selection, selectionArgs, null);
        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                Log.i("MediaUtils","getTargetFileVideo.path = " +path);
                audioPathList.add(path);
            }
            c.close();
        }
        return audioPathList;
    }
}
