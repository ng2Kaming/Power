package com.onecm.power;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.onecm.adapter.CommentsAdapter;
import com.onecm.bean.Comment;
import com.onecm.bean.Discover;
import com.onecm.util.LoaderUtils;
import com.onecm.util.SPUtils;
import com.onecm.util.ShareUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Kaming on 2015/3/30 0030.
 */
public class ContentActivity extends AppCompatActivity implements ObservableScrollViewCallbacks, AdapterView.OnItemClickListener {

    public static final String DISCOVER = "DISCOVER";
    public static final String COMMENT = "COMMENT";
    private static final int UPDATE = 0;
    private static final int UPDATE_STATISTICS = 1;
    private Discover mDiscover;
    private Toolbar mTool;
    private ImageLoader loader = ImageLoader.getInstance();
    private ImageView mImageView;
    private TextView mContent;
    private TextView mAuthor;
    private TextView mCommentTotal;
    private TextView mSeeTotal;
    private ListView mCommentList;
    private List<Comment> mComments= new ArrayList<>();
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    int baseColor;
    private WindowManager windowManager;
    private CommentsAdapter mCommentsAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE) {
                updateComments();
                setListViewForScroll(mCommentList);
                mCommentTotal.setText(mComments.size() + "");
            } else if (msg.what == UPDATE_STATISTICS) {
                mSeeTotal.setText(msg.arg1 + "");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity);
        mDiscover = (Discover) getIntent().getBundleExtra("bundle").getSerializable("mDis");
        baseColor = getResources().getColor(R.color.primary);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        increaseLooked();
        initView();
        initShade();
    }

    private void increaseLooked() {
        mDiscover.increment("looked", 1);
        mDiscover.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 夜间加罩
     */
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
        mCommentList = (ListView) findViewById(R.id.comment_list);
        mCommentTotal = (TextView) findViewById(R.id.comments);
        mSeeTotal = (TextView) findViewById(R.id.looked);
        getSupportActionBar().setTitle(mDiscover.getDate());
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mCommentList.setOnItemClickListener(this);
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
        findAllComment();
        findLookedTotal();
    }


    /**
     * 解决scrollView 嵌套 ListView 问题
     *
     * @param listView
     */
    private void setListViewForScroll(ListView listView) {
        if (listView == null) {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    /**
     * 更新视图
     */
    private void updateComments() {
        if (null != mComments && mComments.size() > 0) {
            if (mCommentsAdapter == null) {
                mCommentsAdapter = new CommentsAdapter(this, mComments);
                mCommentList.setAdapter(mCommentsAdapter);
            } else {
                mCommentsAdapter.notifyDataSetChanged();
                Log.d("FUCK","notifyDataSetChanged");
            }
        }

    }

    /**
     * 查询当前文章所有评论
     */
    private void findAllComment() {
        BmobQuery<Comment> comments = new BmobQuery<>();
        comments.addWhereRelatedTo("comments", new BmobPointer(mDiscover));
        comments.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                mComments.addAll(list);
                mHandler.sendEmptyMessage(UPDATE);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ContentActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 查询阅读次数
     */
    private void findLookedTotal() {
        BmobQuery<Discover> query = new BmobQuery<Discover>();
        query.getObject(this, mDiscover.getObjectId(), new GetListener<Discover>() {

            @Override
            public void onSuccess(Discover object) {
                Message msg = Message.obtain();
                msg.what = UPDATE_STATISTICS;
                msg.arg1 = object.getLooked();
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int code, String arg0) {

            }

        });
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


    /**
     * 跳转评论界面
     *
     * @param view
     */
    public void edit(View view) {
        Intent startComment = new Intent(this, CommentActivity.class);
        startComment.putExtra(DISCOVER, mDiscover);
        startActivityForResult(startComment, 0);
    }

    /**
     * 评论回调 更新视图
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==CommentActivity.PLEASE_UPDATE){
            mComments.clear();
            findAllComment();
            findLookedTotal();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent startCommentCon = new Intent(this, CommentContentActivity.class);
        startCommentCon.putExtra(COMMENT, mComments.get(position));
        startCommentCon.putExtra(DISCOVER,mDiscover);
        startActivity(startCommentCon);
    }

}
