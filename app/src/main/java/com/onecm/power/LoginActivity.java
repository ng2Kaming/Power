package com.onecm.power;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;

import com.onecm.app.PowerApplication;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Kaming on 2015/5/19 0019.
 */
public class LoginActivity extends BaseActivity {
    private Toolbar mToolbar;
    private EditText mUsername, mPassword;
    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    public void register(View view) {
        startActivityForResult(new Intent(this, RegisterActivity.class), 0);
    }

    public void login(View view) {
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            showToast(getString(R.string.empty_username));
            return;
        }
        if(TextUtils.isEmpty(password)){
            showToast(getString(R.string.empty_password));
            return;
        }
        if (verifyPassword(username, password)) {
            PowerApplication.getInstance().setUsername(username);
            setResult(RESULT_OK);
            finish();
        }

    }

    /**
     * —È÷§√‹¬Î
     * @param username
     * @param password
     * @return
     */
    private boolean verifyPassword(String username, final String password) {
        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username);
        query.findObjects(this, new FindListener<BmobUser>() {
            @Override
            public void onSuccess(List<BmobUser> list) {
                if (password.equals(list.get(0).getPassword())) {
                    isLogin = true;
                } else {
                    isLogin = false;
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast(s);
            }

        });
        return isLogin;
    }

    @Override
    protected void initView() {
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.login));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
