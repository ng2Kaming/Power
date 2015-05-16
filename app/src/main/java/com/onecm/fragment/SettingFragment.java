package com.onecm.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.views.Switch;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.onecm.power.R;
import com.onecm.power.ShadeMode;
import com.onecm.util.SPUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

/**
 * Created by Administrator on 2015/3/26 0026.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {

    private static final String KEY_TITILE = "title";
    private Switch nightModeSwitch;
    private CheckBox wifiModeCheckBox, pushModeCheckBox;
    private ButtonRectangle gradeModeBtn, cleanModeBtn, feekbackBtn;
    private TextView cacheCurrentSize;
    private Context context;
    private ImageLoader loader = ImageLoader.getInstance();
    private Resources resources;

    public SettingFragment() {

    }

    public static SettingFragment newInstance(String title) {
        SettingFragment f = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITILE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        resources = context.getResources();
        initView();
        changeViewBySP();
    }


    private void changeViewBySP() {
        boolean nightMode = (boolean) SPUtils.get(context, "nightMode", false);
        nightModeSwitch.setChecked(nightMode);
        boolean wifiMode = (boolean) SPUtils.get(context, "wifiMode", true);
        wifiModeCheckBox.setChecked(wifiMode);
        boolean pushMode = (boolean) SPUtils.get(context, "pushMode", true);
        pushModeCheckBox.setChecked(pushMode);
        nightModeSwitch.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(boolean b) {
                SPUtils.put(context,"nightMode",b);
                try {
                    if (b) {
                        ShadeMode.getInstance(context).addShade();
                        Log.d("TAG",ShadeMode.getInstance(context)+"");
                    } else {
                        //popupWindow.dismiss();
                        ShadeMode.getInstance(context).removeShade();
                        Log.d("TAG",ShadeMode.getInstance(context)+"");
                        Log.d("TAG","here");
                    }
                } catch (Exception e) {
                    Toast.makeText(context,resources.getString(R.string.protectEye),Toast.LENGTH_SHORT).show();
                }
            }
        });
        wifiModeCheckBox.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(boolean b) {
                SPUtils.put(context, "wifiMode", b);
            }
        });
        pushModeCheckBox.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(boolean b) {
                SPUtils.put(context, "pushMode", b);
            }
        });
        cleanModeBtn.setOnClickListener(this);
        gradeModeBtn.setOnClickListener(this);
        feekbackBtn.setOnClickListener(this);
    }

    private void initView() {
        nightModeSwitch = (Switch) getView().findViewById(R.id.nightModeSwitch);
        wifiModeCheckBox = (CheckBox) getView().findViewById(R.id.wifiModeCheckBox);
        pushModeCheckBox = (CheckBox) getView().findViewById(R.id.pushModeCheckBox);
        gradeModeBtn = (ButtonRectangle) getView().findViewById(R.id.gradeModeBtn);
        cleanModeBtn = (ButtonRectangle) getView().findViewById(R.id.cleanModeBtn);
        feekbackBtn = (ButtonRectangle) getView().findViewById(R.id.feekbackBtn);
        cacheCurrentSize = (TextView) getView().findViewById(R.id.cacheCurrent);
        cacheCurrentSize.setText(resources.getString(R.string.cacheSize) + Formatter.formatFileSize(getActivity(),getCacheSize()));
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SplashScreen");
        MobclickAgent.onResume(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashScreen");
        MobclickAgent.onPause(context);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gradeModeBtn:
                try {
                    String mAddress = "market://details?id=" + context.getPackageName();
                    Intent marketIntent = new Intent("android.intent.action.VIEW");
                    marketIntent.setData(Uri.parse(mAddress));
                    marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(marketIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, resources.getString(R.string.noMarket), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cleanModeBtn:
                loader.clearDiskCache();
                cacheCurrentSize.setText(resources.getString(R.string.cacheSize) + String.valueOf(getCacheSize()) + "Kb");
                Toast.makeText(context, resources.getString(R.string.cleanSuccess), Toast.LENGTH_SHORT).show();
                break;
            case R.id.feekbackBtn:
                Intent msgIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:18819207592"));
                msgIntent.putExtra("sms_body", "反馈意见:");
                startActivity(msgIntent);
                break;
        }
    }

    private long getCacheSize() {
        long totalCacheSize = 0;
        File cacheDir = loader.getDiskCache().getDirectory();
        Log.d("TAG",cacheDir.toString());
        File[] cacheFile = cacheDir.listFiles();
        for (int i = 0; i < cacheFile.length; i++) {
            totalCacheSize += cacheFile[i].length();
        }
        return totalCacheSize;
    }




}