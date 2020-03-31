package com.lotus.ourhome.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import androidx.core.content.SharedPreferencesCompat.EditorCompat;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CacheUtil {
    private static CacheUtil instance;
    private Context context;

    private CacheUtil(Context context) {
        this.context = context;
    }

    public static CacheUtil build(Context ctx) {
        if(instance == null) {
            instance = new CacheUtil(ctx);
        }

        return instance;
    }

    public void put(String key, Object obj) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.context);
        Editor editor = sp.edit();
        if(obj instanceof String) {
            editor.putString(key, (String)obj);
        } else if(obj instanceof Integer) {
            editor.putInt(key, ((Integer)obj).intValue());
        } else if(obj instanceof Boolean) {
            editor.putBoolean(key, ((Boolean)obj).booleanValue());
        } else if(obj instanceof Float) {
            editor.putFloat(key, ((Float)obj).floatValue());
        } else if(obj instanceof Long) {
            editor.putLong(key, ((Long)obj).longValue());
        } else {
            editor.putString(key, obj.toString());
        }

        EditorCompat editorCompat = EditorCompat.getInstance();
        editorCompat.apply(editor);
    }

    public Object get(String key, Object defaultObject) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.context);
        return defaultObject instanceof String?sp.getString(key, (String)defaultObject):(defaultObject instanceof Integer?Integer.valueOf(sp.getInt(key, ((Integer)defaultObject).intValue())):(defaultObject instanceof Boolean?Boolean.valueOf(sp.getBoolean(key, ((Boolean)defaultObject).booleanValue())):(defaultObject instanceof Float?Float.valueOf(sp.getFloat(key, ((Float)defaultObject).floatValue())):(defaultObject instanceof Long?Long.valueOf(sp.getLong(key, ((Long)defaultObject).longValue())):null))));
    }

    public void remove(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.context);
        Editor editor = sp.edit();
        editor.remove(key);
        EditorCompat editorCompat = EditorCompat.getInstance();
        editorCompat.apply(editor);
    }

    public boolean contains(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.context);
        return sp.contains(key);
    }

    public void cleaAll() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.context);
        Editor editor = sp.edit();
        editor.clear();
        EditorCompat editorCompat = EditorCompat.getInstance();
        editorCompat.apply(editor);
    }

    public <T> T getObj(Class<T> classz) {
        ObjectInputStream in = null;

        Object o;
        try {
            FileInputStream fin = this.context.openFileInput(classz.getName());
            in = new ObjectInputStream(fin);
            o = in.readObject();
            in.close();
            Object var5 = o;
            return (T) var5;
        } catch (Exception var9) {
            var9.printStackTrace();
            o = null;
        }

        return (T) o;
    }

    public boolean saveObj(Object obj) {
        ObjectOutputStream out = null;
        if(null == obj) {
            return false;
        } else {
            boolean isSuccess;
            try {
                FileOutputStream fileOut = this.context.openFileOutput(obj.getClass().getName(), 0);
                out = new ObjectOutputStream(fileOut);
                out.writeObject(obj);
                out.close();
                isSuccess = true;
                return isSuccess;
            } catch (Exception var8) {
                var8.printStackTrace();
                isSuccess = false;
            }


            return isSuccess;
        }
    }
}