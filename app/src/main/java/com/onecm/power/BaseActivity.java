package com.onecm.power;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.onecm.util.SPUtils;

/**
 * Created by Administrator on 2015/5/25 0025.
 */
public abstract class BaseActivity extends AppCompatActivity {

    Toast mToast;

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

    public void startMyActivity(Class<?> c){
        this.startActivity(new Intent(this, c));
    }

    public void startMyActivity(Intent intent){
        this.startActivity(intent);
    }

    public void showToast(final String text){
        if (!TextUtils.isEmpty(text)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mToast==null){
                        mToast  = Toast.makeText(BaseActivity.this,text,Toast.LENGTH_LONG);
                    }else{
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });
        }
    }

    public void showToast(final int resId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast==null){
                    mToast  = Toast.makeText(BaseActivity.this,getString(resId),Toast.LENGTH_LONG);
                }else{
                    mToast.setText(getString(resId));
                }
                mToast.show();
            }
        });
    }


}
