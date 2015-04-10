package com.onecm.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.onecm.adapter.ListCardAdapter;
import com.onecm.bean.Discover;
import com.onecm.power.ContentActivity;
import com.onecm.power.MainActivity;
import com.onecm.power.R;
import com.onecm.power.WeclomeActivity;
import com.onecm.util.DataUtils;
import com.onecm.util.NetUtils;
import com.onecm.util.SPUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;


/**
 * Created by Administrator on 2015/3/26 0026.
 */
public class FightFragment extends Fragment implements ObservableScrollViewCallbacks {

    private static final String KEY_TITILE = "title";
    private ObservableListView listView;
    private PtrFrameLayout ptrFrameLayout;
    private MaterialHeader materialHeader;
    private List<Discover> mDatas = new ArrayList<Discover>();
    private boolean isLoadingDataFromNetWork;
    private boolean isConnectedNet = false;
    private boolean isFirstLoad = true;
    private DataUtils dataUtils;
    private ListCardAdapter listCardAdapter;
    private Context context;
    private Resources resources;
    public FightFragment() {

    }

    public static FightFragment newInstance(String title, Context con) {
        FightFragment f = new FightFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITILE, title);
        f.setArguments(args);
        return (f);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fight_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        resources = context.getResources();
        initView();
    }

    private void initView() {
        Log.d("TAG","lal");
        listCardAdapter = new ListCardAdapter(getActivity(), mDatas);
        listView = (ObservableListView) getView().findViewById(R.id.listview);
        dataUtils = new DataUtils(getActivity());
        ptrFrameLayout = (PtrFrameLayout) getView().findViewById(R.id.ptr_frame);
        materialHeader = new MaterialHeader(getActivity().getBaseContext());
        materialHeader.setPadding(0, 20, 0, 20);
        materialHeader.setPtrFrameLayout(ptrFrameLayout);
        ptrFrameLayout.setLoadingMinTime(1500);
        ptrFrameLayout.setDurationToCloseHeader(300);
        ptrFrameLayout.setHeaderView(materialHeader);
        ptrFrameLayout.addPtrUIHandler(materialHeader);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view2) {
                return listView.getCurrentScrollY() == 0;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                ToUpdate();
            }
        });

        isFirstLoad = (boolean) SPUtils.get(context,"isFirstIn",true);
        if (isFirstLoad) {
            Log.d("TAG","isfirst");
            if (NetUtils.checkNet(context)){
                if (WeclomeActivity.discovers.size() != 0){
                    mDatas = WeclomeActivity.discovers;
                }else{
                    mDatas = dataUtils.getDataList();
                }
                listCardAdapter.addList(mDatas);
                SPUtils.put(context,"isFirstIn",false);
            }else{
                Toast.makeText(context,resources.getString(R.string.noNet),Toast.LENGTH_SHORT).show();
            }
        }else{
            mDatas = dataUtils.getDataList();
            listCardAdapter.setList(mDatas);
        }
        listView.setAdapter(listCardAdapter);
        listView.setScrollViewCallbacks(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Discover dis = listCardAdapter.getmList().get(position);
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mDis", dis);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });

    }


    private boolean ToUpdate() {
        if (refreshDatas() == 1) {
            listCardAdapter.notifyDataSetChanged();
            ptrFrameLayout.refreshComplete();
            return true;
        } else {
            Toast.makeText(context, resources.getString(R.string.noNet), Toast.LENGTH_SHORT).show();
            listCardAdapter.notifyDataSetChanged();
            ptrFrameLayout.refreshComplete();
        }
        return false;
    }

    private int refreshDatas() {
        if (NetUtils.checkNet(getActivity())) {
            isConnectedNet = true;
            listCardAdapter.setList(dataUtils.getUpdateDataList());
            return 1;
        } else {
            mDatas = dataUtils.getDataList();
            listCardAdapter.addList(mDatas);
            isConnectedNet = false;
            isLoadingDataFromNetWork = false;
            return -1;
        }
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
    public void onScrollChanged(int scrollY, boolean b, boolean b2) {
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = MainActivity.bar;
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }
    }


}
