package com.onecm.power;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.onecm.bean.Discover;
import com.onecm.util.AppUtils;
import com.onecm.util.DataUtils;
import com.onecm.util.SPUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/2 0002.
 */
public class WeclomeActivity extends Activity {
    private ImageView welcomeImg;
    private TextView appName;
    private TextView appVersion;
    public static List<Discover> discovers = new ArrayList<Discover>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weclome_activity);
        initView();
    }

    private void initView() {
       // View decor = getWindow().getDecorView();
       /* int uiOption = decor.getSystemUiVisibility();
        int newUiOption = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decor.setSystemUiVisibility(newUiOption);*/
        SPUtils.put(this,"isFirstIn",true);
        welcomeImg = (ImageView) findViewById(R.id.welcome_img);
        appName = (TextView) findViewById(R.id.welcome_title);
        appVersion = (TextView) findViewById(R.id.app_version);
        appVersion.setText(getResources().getString(R.string.app_version)+AppUtils.getAppName(this));
        appName.setText(getResources().getString(R.string.app_name));
        Animation welcomeAnim = AnimationUtils.loadAnimation(this,R.anim.welcome_img);
        welcomeImg.startAnimation(welcomeAnim);
        welcomeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                discovers.addAll(new DataUtils(WeclomeActivity.this).getUpdateDataList());
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intentMain = new Intent(WeclomeActivity.this,MainActivity.class);
                startActivity(intentMain);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void finish() {
        welcomeImg.destroyDrawingCache();
        super.finish();
    }
}
