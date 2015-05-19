package com.onecm.power;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.CheckBox;
import com.onecm.app.AppFinal;
import com.onecm.bean.Comment;
import com.onecm.bean.Discover;
import com.onecm.util.SPUtils;
import com.tencent.tauth.Tencent;

import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by kaming on 2015/5/16 0016.
 */
public class CommentActivity extends AppCompatActivity {

    public static final String COMMENT = "comment";
    public static final int PLEASE_UPDATE = 0;
    private Tencent mTencent;
    private String mNikeName;
    private String mNikeUrl;
    private TelephonyManager mTelephonyManager;
    private Toolbar mToolbar;
    private EditText mCommentText;
    private CheckBox mIsNoName;
    private Discover mDisconer;
    private Comment mComment;
    private String comContent;
    private WindowManager windowManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        mDisconer = (Discover) getIntent().getSerializableExtra(ContentActivity.DISCOVER);
        mTencent = Tencent.createInstance(AppFinal.APPID, this);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        initShade();
        checkLogin();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCommentText = (EditText) findViewById(R.id.comment_content);
        mIsNoName = (CheckBox) findViewById(R.id.checkBox);
        mIsNoName.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(boolean isChecked) {
                if (isChecked) {
                    mNikeName = getString(R.string.noname);
                    mNikeUrl = "";
                } else {
                    mNikeName = (String) SPUtils.get(CommentActivity.this, "nickName", "Power");
                    mNikeUrl = (String) SPUtils.get(CommentActivity.this, "nickImg", "");
                }
            }
        });
        mToolbar.setTitle(getString(R.string.add_comment));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initShade() {
        if ((boolean) SPUtils.get(CommentActivity.this, "nightMode", false)) {
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

    private void checkLogin() {
        if (mTencent.isSessionValid()) {
            mNikeName = (String) SPUtils.get(this, "nickName", "Power");
            mNikeUrl = (String) SPUtils.get(this, "nickImg", "");
        } else {
            mNikeName = getString(R.string.visitor) +": " +mTelephonyManager.getLine1Number();
            mNikeUrl = "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                SendComment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 评论上传到服务端
     */
    private void SendComment() {
        comContent = mCommentText.getText().toString().trim();
        if (TextUtils.isEmpty(comContent)) {
            Toast.makeText(this, getString(R.string.comment_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        mComment = new Comment();
        mComment.setContent(comContent);
        mComment.setIconUrl(mNikeUrl);
        mComment.setNickName(mNikeName);
        mComment.setDiscover(mDisconer);
        mComment.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                addCardToUser();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(CommentActivity.this, getString(R.string.waitingToTry), Toast.LENGTH_SHORT).show();
                finish();
            }

        });
    }

    private void addCardToUser() {
        if (TextUtils.isEmpty(mDisconer.getObjectId())) {
            Toast.makeText(CommentActivity.this, getString(R.string.waitingToTry), Toast.LENGTH_SHORT).show();
            return;
        }
        BmobRelation relation = new BmobRelation();
        relation.add(mComment);
        mDisconer.setComments(relation);
        mDisconer.update(this, new UpdateListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(CommentActivity.this, getString(R.string.comment_ok), Toast.LENGTH_SHORT).show();
                setResult(PLEASE_UPDATE);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(CommentActivity.this, getString(R.string.waitingToTry), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

}
