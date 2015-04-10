package com.onecm.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/5 0005.
 */
public class SPUtils {

    public static final String FILENAME = "share_setting";

    public static void put(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        }
        SharedPreferencesCompat.apply(editor);
    }

    public static Object get(Context context, String key, Object defalutObject) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        if (defalutObject instanceof String) {
            return sp.getString(key, (String) defalutObject);
        } else if (defalutObject instanceof Integer) {
            return sp.getInt(key, (Integer) defalutObject);
        } else if (defalutObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defalutObject);
        } else if (defalutObject instanceof Float) {
            return sp.getFloat(key, (Float) defalutObject);
        } else if (defalutObject instanceof Long) {
            return sp.getLong(key, (Long) defalutObject);
        }
        return null;

    }

    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }


    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        private static Method findApplyMethod() {
            Class clazz = SharedPreferences.Editor.class;
            try {
                return clazz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }


        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            editor.commit();
        }

    }
}
