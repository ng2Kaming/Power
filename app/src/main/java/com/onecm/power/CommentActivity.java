package com.onecm.power;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gc.materialdesign.views.CheckBox;
import com.onecm.app.AppFinal;
import com.onecm.util.SPUtils;
import com.tencent.tauth.Tencent;

/**
 * Created by Administrator on 2015/5/16 0016.
 */
public class CommentActivity extends AppCompatActivity {

    private Tencent mTencent;
    private String nikeName;
    private String nikeUrl;
    private TelephonyManager mTelephonyManager;
    private Toolbar mToolbar;
    private EditText mComment;
    private CheckBox mIsNoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        mTencent = Tencent.createInstance(AppFinal.APPID, this);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        checkLogin();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mComment = (EditText) findViewById(R.id.comment_content);
        mIsNoName = (CheckBox) findViewById(R.id.checkBox);
        mIsNoName.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(boolean isChecked) {
                if (isChecked) {
                    nikeName = getString(R.string.noname);
                    nikeUrl = "";
                } else {
                    nikeName = (String) SPUtils.get(CommentActivity.this, "nickName", "Power");
                    nikeUrl = (String) SPUtils.get(CommentActivity.this, "nickImg", "");
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

    private void checkLogin() {
        if (mTencent.isSessionValid()) {
            nikeName = (String) SPUtils.get(this, "nickName", "Power");
            nikeUrl = (String) SPUtils.get(this, "nickImg", "");
        } else {
            nikeName = getString(R.string.visitor) + mTelephonyManager.getLine1Number();
            nikeUrl = "";
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

    private void SendComment() {
        Toast.makeText(this, "send to server", Toast.LENGTH_LONG).show();
    }
}
