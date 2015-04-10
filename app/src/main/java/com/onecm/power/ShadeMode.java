package com.onecm.power;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/4/8 0008.
 */
public class ShadeMode {
    private final WindowManager mWindowManager;
    private TextView nothing;
    private static ShadeMode shadeMode;
    private Context context;
    private WindowManager.LayoutParams lp;
    private PopupWindow popupWindow;
    private View parent;

    public ShadeMode(Context context) {
        this.context = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        createShade();
    }

    public static ShadeMode getInstance(Context context) {
        if (shadeMode == null){
            shadeMode = new ShadeMode(context);
        }
        return shadeMode;
    }


    public void addShade() {
        mWindowManager.addView(nothing, lp);
    }

    public void removeShade() {
        mWindowManager.removeViewImmediate(nothing);
    }

    public void createShade() {
        lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.CENTER;
        if (nothing == null){
            nothing = new TextView(context);
            nothing.setBackgroundColor(0x99000000);
        }
    }

    private void InitPopupWindow() {
        View view = LayoutInflater.from(context).inflate(R.layout.night_shade, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x55000000));
        popupWindow.setFocusable(false);
        popupWindow.setTouchable(false);
        popupWindow.setAnimationStyle(R.style.animation);
    }

    public void showShade(){
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void hideShade(){
        popupWindow.dismiss();
    }


}
