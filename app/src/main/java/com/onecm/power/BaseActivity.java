package com.onecm.power;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.onecm.util.SPUtils;

/**
 * Created by Administrator on 2015/5/25 0025.
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * “πº‰º”’÷
     */
    protected void initShade() {
        WindowManager windowManager  = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if ((boolean) SPUtils.get(this, "nightMode", false)) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            lp.gravity = Gravity.CENTER;
            TextView nothing = new TextView(this);
            nothing.setBackgroundColor(0x99000000);
            windowManager.addView(nothing, lp);
        }
    }

    protected abstract void initView();

}
