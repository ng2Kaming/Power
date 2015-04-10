package com.onecm.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2015/3/29 0029.
 */
public class AppUtils {

    public static File getDiskCacheDir(Context context,String uniqueName){
        String cacheDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())||!Environment.isExternalStorageRemovable()) {
            cacheDir = context.getExternalCacheDir().getPath();
        }else{
            cacheDir = context.getCacheDir().getPath();
        }
        return new File(cacheDir+File.separator+uniqueName);
    }

    public static String getAppName(Context context){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1.0+"";
    }


}
