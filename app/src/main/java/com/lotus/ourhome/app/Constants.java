package com.lotus.ourhome.app;

import android.os.Environment;

import java.io.File;

public class Constants {

    public static final boolean DEBUG = true;

    //================= KEY ====================
    public static final String FILE_PROVIDER_AUTHORITY = "com.lotus.ourhome.fileprovider";

    //================= PATH ====================
    public static final String PATH_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "codeest" + File.separator + "GeekNews";

    //================= PREFERENCE ====================

    public static final String SHAREDPREFERENCES_NAME = "my_sp";

    public static final String SP_AUTO_LOGIN = "auto_login";//自动登录

    public static final String SP_REMEMBER_PSW = "remember_psw";//记住密码

    public static final String SP_NIGHT_MODE = "night_mode";//夜间模式

    public static final String SP_USER_INFO = "user_info";//用户信息

    public static final String SP_IS_AUTH = "is_auth";//是否验证了身份（用户是否登录了）

    public static final String SP_DEFAULT_SHOW_LEDGER = "default_show_ledger";//默认显示的账本id

    //================= INTENT ====================

}
