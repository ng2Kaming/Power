package com.onecm.power;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.onecm.app.PowerApplication;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Kaming on 2015/6/9 0009.
 */
public class RegisterActivity extends BaseActivity {
    private EditText mUsername, mPassword, mConfirm;
    private Toolbar mToolbar;
    private boolean isExistUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    @Override
    protected void initView() {
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mConfirm = (EditText) findViewById(R.id.confirm);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.register));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 注册
     * @param view
     */
    public void register(View view) {
        final String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String confirm = mConfirm.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            showToast(getString(R.string.empty_username));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast(getString(R.string.empty_password));
            return;
        }
        if (TextUtils.isEmpty(confirm)) {
            showToast(getString(R.string.empty_password));
            return;
        }
        if (password.equals(confirm)) {
            showToast(getString(R.string.two_not_match_password));
            return;
        }
        final ProgressDialog pd = new ProgressDialog(this,getString(R.string.registing));
        if (!isExistsUser(username)) {
            pd.show();
            BmobUser user = new BmobUser();
            user.setUsername(username);
            user.setPassword(password);
            user.signUp(this, new SaveListener() {

                @Override
                public void onSuccess() {
                    showToast(R.string.register_successfully);
                    PowerApplication.getInstance().setUsername(username);
                    setResult(RESULT_OK);
                    pd.dismiss();
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                    showToast(s);
                }

            });
        }

    }

    /**
     * 检测用户名是否存在
     * @param username
     * @return
     */
    private boolean isExistsUser(String username) {
        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username);
        query.findObjects(this, new FindListener<BmobUser>() {
            @Override
            public void onSuccess(List<BmobUser> list) {
                if (list == null || list.size() == 0) {
                    isExistUser = false;
                } else {
                    showToast(getString(R.string.exists_username));
                    isExistUser = true;
                }
            }

            @Override
            public void onError(int i, String s) {
                isExistUser = true;
                showToast(s);
            }
        });
        return isExistUser;
    }

}
