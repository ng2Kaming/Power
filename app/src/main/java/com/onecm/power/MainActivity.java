package com.onecm.power;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import com.onecm.bean.UserInfo;
import com.onecm.fragment.AboutFragment;
import com.onecm.fragment.CollectFragment;
import com.onecm.fragment.FightFragment;
import com.onecm.fragment.SettingFragment;
import com.onecm.util.LoaderUtils;
import com.onecm.util.SPUtils;
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

public class MainActivity extends ActionBarActivity implements ObservableScrollViewCallbacks, View.OnClickListener {

    private static final String SCOPE = "get_simple_userinfo";
    private static final int UPDATE_USER = 0;
    private final static int TIME_TO_EXIT = 2000;
    public static ActionBar bar;
    private Toolbar mTool;
    private Drawer.Result result = null;
    private View mHeader;
    private TextView mNickName;
    private ImageView mNickImg;
    private int currentDrawerName = 0;
    private boolean mIsExit = false;
    private Tencent mTencent;
    private BaseUiListener mBaseUiListener;
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
                UserInfo info = (UserInfo) msg.obj;
                mNickName.setText(info.getNickName());
                loader.displayImage(info.getFigureUrl(), mNickImg, LoaderUtils.getDisplayImageOptions());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "6aed4d0a6462bbb4e6f55be316ab9183");
        mTencent = Tencent.createInstance("1104476313", this.getApplicationContext());
        initView(savedInstanceState);
        initSettingData();
        initSettingBySP();
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
        mNickImg = (ImageView) mHeader.findViewById(R.id.imageView);
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
                Toast.makeText(this, "Giving your power.", Toast.LENGTH_SHORT).show();
                showLoginDialog();
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

    private void showLoginDialog() {
        AlertDialog.Builder alertLogin = new AlertDialog.Builder(this);
        alertLogin.setTitle(getString(R.string.instapaper_login));
        View view = View.inflate(this, R.layout.login_item, null);
        ImageButton mQQ = (ImageButton) view.findViewById(R.id.loginByQQ);
        ImageButton mSina = (ImageButton) view.findViewById(R.id.loginBySina);
        mQQ.setOnClickListener(this);
        mSina.setOnClickListener(this);
        alertLogin.setView(view);
        alertLogin.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data);
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
                loginByQQ();
                break;
            case R.id.loginBySina:

                break;
        }
    }

    /**
     * QQ登陆
     */
    private void loginByQQ() {
        if (!mTencent.isSessionValid()) {
            mBaseUiListener = new BaseUiListener();
            mTencent.login(this, SCOPE, mBaseUiListener);
        }
    }

    /**
     * 获取用户信息
     */
    public void getUserInfoInThread() {
        new Thread() {
            @Override
            public void run() {
               /* JSONObject json = mTencent.request(Constants.GRAPH_SIMPLE_USER_INFO, null, Constants.HTTP_GET);
                UserInfo userInfo = new UserInfo();
                System.out.println(json);
                try {
                    userInfo.setNickName(json.getString("nickname"));
                    userInfo.setFigureUrl(json.getString("figureurl_qq_2"));
                    Message msg = Message.obtain();
                    msg.obj = userInfo;
                    msg.what = UPDATE_USER;
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        }.start();
    }

    /**
     * 登陆界面回调接口
     */
    public class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            JSONObject jObj = (JSONObject) o;
            Log.d("FUCK", jObj.toString());
            try {
                SPUtils.put(MainActivity.this, "openid", jObj.getString("openid"));
                SPUtils.put(MainActivity.this, "access_token", jObj.getString("access_token"));
                SPUtils.put(MainActivity.this, "expires_in", jObj.getString("expires_in"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
