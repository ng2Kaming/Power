package com.onecm.power;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.onecm.app.AppFinal;
import com.onecm.fragment.AboutFragment;
import com.onecm.fragment.CollectFragment;
import com.onecm.fragment.FightFragment;
import com.onecm.fragment.SettingFragment;
import com.onecm.util.LoaderUtils;
import com.onecm.util.SPUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.update.BmobUpdateAgent;

public class MainActivity extends AppCompatActivity implements ObservableScrollViewCallbacks, View.OnClickListener {
    private static final String SCOPE = "get_simple_userinfo";
    private static final int UPDATE_USER = 0;
    private final static int TIME_TO_EXIT = 2000;
    public static ActionBar bar;
    private Toolbar mTool;
    private Drawer.Result result = null;
    private View mHeader;
    private TextView mNickName;
    private ImageView mPower;
    private ImageView mNickImg;
    private int currentDrawerName = 0;
    private boolean mIsExit = false;
    private Tencent mTencent;
    private UserInfo mInfo;
    private boolean isServerSideLogin;
    private ImageLoader loader = ImageLoader.getInstance();
    private Handler mExitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mIsExit = false;
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_USER) {
                JSONObject jObj = (JSONObject) msg.obj;
                if (jObj != null) {
                    try {
                        loginDialog.dismiss();
                        Toast.makeText(MainActivity.this, getString(R.string.login_ok), Toast.LENGTH_LONG).show();
                        mPower.setVisibility(View.GONE);
                        mNickImg.setVisibility(View.VISIBLE);
                        loader.displayImage(jObj.getString("figureurl_qq_2"), mNickImg, LoaderUtils.getDisplayImageOptions());
                        mNickName.setText(jObj.getString("nickname"));
                        SPUtils.put(MainActivity.this, "nickName", jObj.getString("nickname"));
                        SPUtils.put(MainActivity.this, "nickImg", jObj.getString("figureurl_qq_2"));
                        SPUtils.put(MainActivity.this, "isLogin", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "6aed4d0a6462bbb4e6f55be316ab9183");
        mTencent = Tencent.createInstance("1104476313", this);
        initView(savedInstanceState);
        initSettingData();
        initSettingBySP();
        checkIsLogin();
    }

    /**
     * 检查是否登陆过
     */
    private void checkIsLogin() {
        boolean isLogin = (boolean) SPUtils.get(this, "isLogin", false);
        if (isLogin) {
            mTencent.setOpenId(SPUtils.get(this, "openid", "").toString());
            mTencent.setAccessToken(SPUtils.get(this, "access_token", "").toString(), SPUtils.get(this, "expires_in", "").toString());
            udateUserInfo();
        }
    }

    /**
     * 更新用户信息
     */
    private void udateUserInfo(){
        mPower.setVisibility(View.GONE);
        mNickImg.setVisibility(View.VISIBLE);
        loader.displayImage(SPUtils.get(this,"nickImg","").toString(), mNickImg, LoaderUtils.getDisplayImageOptions());
        mNickName.setText(SPUtils.get(this,"nickName","").toString());
    }


    private void initSettingBySP() {
        if ((boolean) SPUtils.get(this, "pushMode", true)) {
            BmobInstallation.getCurrentInstallation(this).save();
            BmobPush.startWork(this, "6aed4d0a6462bbb4e6f55be316ab9183");
        }
        if ((boolean) SPUtils.get(this, "wifiMode", true)) {
            BmobUpdateAgent.update(this);
        }
    }

    private void initSettingData() {
        SPUtils.put(this, "nightMode", false);
    }



    private void initView(Bundle savedInstanceState) {
        loader.init(ImageLoaderConfiguration.createDefault(this));
        mTool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTool);
        bar = getSupportActionBar();
        mHeader = getLayoutInflater().inflate(R.layout.drawer_header, null);
        mNickName = (TextView) mHeader.findViewById(R.id.user_name);
        mPower = (ImageView) mHeader.findViewById(R.id.imageView);
        mNickImg = (ImageView) mHeader.findViewById(R.id.user_img);
        result = new Drawer()
                .withActivity(this)
                .withToolbar(mTool)
                .withDisplayBelowToolbar(true)
                .withSavedInstance(savedInstanceState)
                .withHeaderClickable(true)
                .withHeader(mHeader)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerWidthDp(getDrawerWidth())
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_discover).withIcon(FontAwesome.Icon.faw_eye),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_collect).withIcon(FontAwesome.Icon.faw_archive),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_about).withIcon(FontAwesome.Icon.faw_question),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_finish).withIcon(FontAwesome.Icon.faw_warning)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                    }

                    @Override
                    public void onDrawerClosed(View view) {
                        getSupportActionBar().setTitle(currentDrawerName);
                    }

                    @Override
                    public void onDrawerSlide(View view, float v) {

                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {
                            getSupportActionBar().setTitle(((Nameable) drawerItem).getNameRes());
                            currentDrawerName = ((Nameable) drawerItem).getNameRes();
                        }
                        startFragment(position, drawerItem);
                    }
                })
                .build();
        result.setSelection(AppFinal.DRAWERITEM_FIGHT);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    private void startFragment(int position, IDrawerItem drawerItem) {
        switch (position) {
            case AppFinal.DRAWERITEM_HEADER:
                if (!mTencent.isSessionValid()) {
                    showLoginDialog();
                } else {
                    Toast.makeText(this, getString(R.string.power), Toast.LENGTH_SHORT).show();
                }
                break;
            case AppFinal.DRAWERITEM_FIGHT:
                Fragment fightFragment = FightFragment.newInstance(getResources().getString(((Nameable) drawerItem).getNameRes()), this);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fightFragment).commit();
                break;
            case AppFinal.DRAWERITEM_COLLECTS:
                Fragment collectFragment = CollectFragment.newInstance(getResources().getString(((Nameable) drawerItem).getNameRes()), this);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, collectFragment).commit();
                break;
            case AppFinal.DRAWERITEM_ABOUT:
                Fragment aboutFragment = AboutFragment.newInstance(getResources().getString(((Nameable) drawerItem).getNameRes()));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, aboutFragment).commit();
                break;
            case AppFinal.DRAWERITEM_SETTING:
                Fragment settingFragment = SettingFragment.newInstance(getResources().getString(((Nameable) drawerItem).getNameRes()));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, settingFragment).commit();
                break;
            case AppFinal.DRAWERITEM_FINISH:
                finish();
                break;
        }

    }


    private AlertDialog loginDialog;

    private void showLoginDialog() {
        AlertDialog.Builder alertLogin = new AlertDialog.Builder(this);
        alertLogin.setTitle(getString(R.string.instapaper_login));
        View view = View.inflate(this, R.layout.login_item, null);
        ImageButton mQQ = (ImageButton) view.findViewById(R.id.loginByQQ);
        ImageButton mPower = (ImageButton) view.findViewById(R.id.loginByPower);
        mQQ.setOnClickListener(this);
        mPower.setOnClickListener(this);
        alertLogin.setView(view);
        loginDialog = alertLogin.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.RESULT_LOGIN) {
                Tencent.handleResultData(data, mBaseUiListener);
            }
        } else if (requestCode == Constants.REQUEST_APPBAR) {
            if (resultCode == Constants.RESULT_LOGIN) {
                updateUserInfo();
                Toast.makeText(MainActivity.this, getString(R.string.login_ok), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (mIsExit) {
            finish();
            MobclickAgent.onKillProcess(this);
            System.exit(0);
        } else {
            mIsExit = true;
            Toast.makeText(getApplicationContext(), R.string.click_to_exit, Toast.LENGTH_SHORT).show();
            mExitHandler.sendEmptyMessageDelayed(0, TIME_TO_EXIT);
        }
    }

    private int getDrawerWidth() {
        float scale = getResources().getDisplayMetrics().density;
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        float winWidth = metric.widthPixels;
        return (int) ((winWidth * 5 / 7) / scale + 0.5f);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_finish:
                finish();
                break;
            case R.id.action_logout:
                if (mTencent.isSessionValid()) {
                    mTencent.logout(this);
                    mNickName.setText(getString(R.string.power));
                    mPower.setImageResource(R.drawable.header_app_icon);
                    mPower.setVisibility(View.VISIBLE);
                    mNickImg.setVisibility(View.GONE);
                    SPUtils.put(this, "nickName", "");
                    SPUtils.put(this, "nickImg", "");
                    SPUtils.put(this, "isLogin", false);
                } else {
                    Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                    showLoginDialog();
                }

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
    public void onScrollChanged(int i, boolean b, boolean b2) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginByQQ:
                if (!mTencent.isSessionValid()) {
                    loginByQQ();
                } else {
                    Toast.makeText(this, getString(R.string.power), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.loginByPower:
                Toast.makeText(this, getString(R.string.developering), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * QQ登陆
     */
    private void loginByQQ() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, SCOPE, mBaseUiListener);
            isServerSideLogin = false;
        } else {
            if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
                mTencent.logout(this);
                mTencent.login(this, "all", mBaseUiListener);
                isServerSideLogin = false;
                return;
            }
            mTencent.logout(this);
            updateUserInfo();
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {

                }

                @Override
                public void onComplete(final Object response) {
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onCancel() {

                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(listener);
        }
    }

    IUiListener mBaseUiListener = new BaseUiListener() {

        @Override
        protected void doComplete(JSONObject values) {
            updateUserInfo();
            /*Message msg = new Message();
            msg.obj = values;
            msg.what = 0;
            mHandler.sendMessage(msg);*/
        }
    };

    /**
     * 登陆界面回调接口
     */
    public class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            JSONObject jObj = (JSONObject) o;
            try {
                mTencent.setOpenId(jObj.getString("openid"));
                mTencent.setAccessToken(jObj.getString("access_token"), jObj.getString("expires_in"));
                SPUtils.put(MainActivity.this, "openid", jObj.getString("openid"));
                SPUtils.put(MainActivity.this, "access_token", jObj.getString("access_token"));
                SPUtils.put(MainActivity.this, "expires_in", jObj.getString("expires_in"));
                doComplete((JSONObject) o);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this, getString(R.string.cancel), Toast.LENGTH_SHORT).show();
        }

    }

}
