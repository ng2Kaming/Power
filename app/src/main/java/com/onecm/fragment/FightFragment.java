package com.onecm.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.onecm.adapter.ListCardAdapter;
import com.onecm.bean.Discover;
import com.onecm.power.ContentActivity;
import com.onecm.power.R;
import com.onecm.util.NetUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;


/**
 * Created by kaming on 2015/3/26 0026.
 */
public class FightFragment extends Fragment implements ObservableScrollViewCallbacks {

    private static final String KEY_TITILE = "title";
    private static final int LOAD_DATA = 0;
    private static final int UPDATE_DATA = 1;
    private ObservableListView mListView;
    private PtrFrameLayout mPtrFrameLayout;
    private MaterialHeader mMaterialHeader;
    private List<Discover> mDatas = new ArrayList<Discover>();
    private ListCardAdapter mListCardAdapter;
    private Context context;
    private Resources resources;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_DATA) {
                updateView();
            } else if (msg.what == UPDATE_DATA) {
                updateView();
                mPtrFrameLayout.refreshComplete();
            }
        }
    };


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
        mListView = (ObservableListView) getView().findViewById(R.id.listview);
        mPtrFrameLayout = (PtrFrameLayout) getView().findViewById(R.id.ptr_frame);
        mMaterialHeader = new MaterialHeader(getActivity().getBaseContext());
        mMaterialHeader.setPadding(0, 20, 0, 20);
        mMaterialHeader.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setLoadingMinTime(2000);
        mPtrFrameLayout.setDurationToCloseHeader(300);
        mPtrFrameLayout.setHeaderView(mMaterialHeader);
        mPtrFrameLayout.addPtrUIHandler(mMaterialHeader);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view2) {
                return mListView.getCurrentScrollY() == 0;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                if (NetUtils.checkNet(context)) {
                    findUpdateDataList();
                }else{
                    Toast.makeText(context,getString(R.string.not_network),Toast.LENGTH_SHORT).show();
                }
            }
        });

        findDataList();
        mPtrFrameLayout.autoRefresh(true, 1000);
        mListView.setScrollViewCallbacks(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Discover dis = mListCardAdapter.getmList().get(position);
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mDis", dis);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });

    }

    /**
     * 更新视图
     */
    private void updateView() {
        if (null != mDatas && mDatas.size() > 0) {
            if (mListCardAdapter == null) {
                mListCardAdapter = new ListCardAdapter(getActivity(), mDatas);
                mListView.setAdapter(mListCardAdapter);
            } else {
                mListCardAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public void findDataList() {
        BmobQuery<Discover> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.order("-createdAt");
        query.setLimit(8);
        query.findObjects(context, new FindListener<Discover>() {
            @Override
            public void onSuccess(List<Discover> discovers) {
                mDatas = discovers;
                mHandler.sendEmptyMessage(LOAD_DATA);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 查询更新数据
     *
     * @return
     */
    public void findUpdateDataList() {
        BmobQuery<Discover> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.IGNORE_CACHE);
        query.order("-createdAt");
        query.setLimit(8);
        query.findObjects(context, new FindListener<Discover>() {
            @Override
            public void onSuccess(List<Discover> discovers) {
                mDatas = discovers;
                mHandler.sendEmptyMessage(UPDATE_DATA);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onScrollChanged(int scrollY, boolean b, boolean b2) {
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        /*ActionBar ab = MainActivity.bar;
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }*/
    }


}
