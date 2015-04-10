package com.onecm.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.onecm.power.R;
import com.umeng.analytics.MobclickAgent;

import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

/**
 * Created by Administrator on 2015/3/26 0026.
 */
public class AboutFragment extends Fragment {


    private static final String KEY_TITILE = "title";
    private Context context;
    private ButtonRectangle updateBtn;
    private Resources resources;
    private UpdateResponse ur;
    public AboutFragment() {

    }

    public static AboutFragment newInstance(String title) {
        AboutFragment f = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITILE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        resources = context.getResources();
        initView();
    }

    private void initView() {
        updateBtn = (ButtonRectangle) getView().findViewById(R.id.update);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
                    @Override
                    public void onUpdateReturned(int updateStatus, UpdateResponse updateResponse) {
                        if (updateStatus == UpdateStatus.No) {
                            Toast.makeText(context, resources.getString(R.string.noUpdate), Toast.LENGTH_SHORT).show();
                        }else if (updateStatus == UpdateStatus.Yes) {
                            ur = updateResponse;
                        }
                    }
                });
                BmobUpdateAgent.forceUpdate(context);
            }
        });
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
}
