package com.onecm.power;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.onecm.bean.Discover;
import com.onecm.util.LoaderUtils;
import com.onecm.util.SPUtils;
import com.onecm.util.ShareUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by kaming on 2015/5/19 0019.
 */
public class CollectItemActivity extends BaseActivity implements ObservableScrollViewCallbacks {
    private Discover mDiscover;
    private Toolbar mTool;
    private ImageLoader loader = ImageLoader.getInstance();
    private ImageView mImageView;
    private TextView mContent;
    private TextView mAuthor;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    private int baseColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_content);
        mDiscover = (Discover) getIntent().getBundleExtra("bundle").getSerializable("mDis");
        baseColor = getResources().getColor(R.color.style_color_primary);
        initView();
        super.initShade();
    }

    @Override
    protected void initView() {
        loader.init(ImageLoaderConfiguration.createDefault(this));
        mTool = (Toolbar) findViewById(R.id.toolbar);
        mTool.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, baseColor));
        setSupportActionBar(mTool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImageView = (ImageView) findViewById(R.id.image);
        mContent = (TextView) findViewById(R.id.content);
        mAuthor = (TextView) findViewById(R.id.author);
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
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_finish:
                finish();
                break;
            case R.id.action_share:
                ShareUtils.showShare(this,mDiscover.getContent(),mDiscover.getAuthor(),Bitmap.createBitmap(mImageView.getDrawingCache()));
                mImageView.setDrawingCacheEnabled(false);
                break;
        }
        return super.onOptionsItemSelected(item);
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
}
