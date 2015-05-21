package com.onecm.power;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.onecm.bean.Comment;
import com.onecm.bean.Discover;
import com.onecm.util.LoaderUtils;

/**
 * Created by Kaming on 2015/5/19 0019.
 */
public class CommentContentActivity extends AppCompatActivity {
    private TextView mCommentContent;
    private ImageView mNikeImg;
    private TextView mNikeName;
    private TextView mDate;
    private Comment mComment;
    private Discover mDiscover;
    private Toolbar mToolbar;
    private ImageLoader mLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_content);
        mComment = (Comment) getIntent().getSerializableExtra(ContentActivity.COMMENT);
        mDiscover = (Discover) getIntent().getSerializableExtra(ContentActivity.DISCOVER);
        mLoader.init(ImageLoaderConfiguration.createDefault(this));
        initView();
    }

    private void initView() {
        mNikeImg = (ImageView) findViewById(R.id.nick_img);
        mNikeName  = (TextView) findViewById(R.id.nick_name);
        mDate = (TextView) findViewById(R.id.date);
        mCommentContent = (TextView) findViewById(R.id.comment_content);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        String title = Html.fromHtml(mDiscover.getContent()).subSequence(0,7).toString();
        mToolbar.setTitle(title + "...");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mNikeName.setText(mComment.getNickName());
        mCommentContent.setText(mComment.getContent());
        mDate.setText(mComment.getCreatedAt());
        String iconUrl = mComment.getIconUrl();
        if (iconUrl=="" || iconUrl.length() ==0 ){
            mNikeImg.setImageResource(R.drawable.ic_mood_grey600_48dp);
        }else{
            mLoader.displayImage(mComment.getIconUrl(),mNikeImg, LoaderUtils.getDisplayImageOptions());
        }
    }

}
