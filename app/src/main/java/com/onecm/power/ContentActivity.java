package com.onecm.power;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.onecm.app.AppFinal;
import com.onecm.bean.Discover;
import com.onecm.util.LoaderUtils;
import com.onecm.util.SPUtils;
import com.onecm.util.ShareUtils;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2015/3/30 0030.
 */
public class ContentActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {
    private Discover mDiscover;
    private Toolbar mTool;
    private ImageLoader loader = ImageLoader.getInstance();
    private ImageView mImageView;
    private TextView mContent;
    private TextView mAuthor;
    private ButtonFloat mFab;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    int baseColor;
    private WindowManager windowManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity);
        mDiscover = (Discover) getIntent().getBundleExtra("bundle").getSerializable("mDis");
        baseColor = getResources().getColor(R.color.primary);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        initView();
        initShade();
    }

    private void initShade() {
        if ((boolean) SPUtils.get(ContentActivity.this, "nightMode", false)) {
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


    private void initView() {
        loader.init(ImageLoaderConfiguration.createDefault(this));
        mTool = (Toolbar) findViewById(R.id.toolbar);
        mTool.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, baseColor));
        setSupportActionBar(mTool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImageView = (ImageView) findViewById(R.id.image);
        mContent = (TextView) findViewById(R.id.content);
        mAuthor = (TextView) findViewById(R.id.author);
        mFab = (ButtonFloat) findViewById(R.id.fab);
        getSupportActionBar().setTitle(mDiscover.getDate());
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mImageView.setDrawingCacheEnabled(true);
        loader.displayImage(mDiscover.getDisImg().getFileUrl(this), mImageView, LoaderUtils.getDisplayImageOptions());
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        mTool.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mContent.setText(Html.fromHtml(mDiscover.getContent()).toString());
        if ((!"".equals(mDiscover.getAuthor()) && mDiscover.getAuthor() != null)) {
            mAuthor.setText(mDiscover.getAuthor());
        } else {
            mAuthor.setText(getResources().getString(R.string.card_content_author));
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_finish:
                finish();
                break;
            case R.id.action_share:
                new ShareUtils(mDiscover, this, Bitmap.createBitmap(mImageView.getDrawingCache())).showShare();
                mImageView.setDrawingCacheEnabled(false);
                break;
        }
        return super.onOptionsItemSelected(item);
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
    public void onScrollChanged(int scrollY, boolean b, boolean b2) {
        float alpha = 1 - (float) Math.max(0, mParallaxImageHeight - scrollY) / mParallaxImageHeight;
        mTool.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    public void edit(View view) {
        Intent startComment = new Intent(this, CommentActivity.class);
        startActivityForResult(startComment,0);
    }


}
