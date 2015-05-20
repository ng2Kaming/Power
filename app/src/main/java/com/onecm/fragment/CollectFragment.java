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
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.onecm.Dao.CollectDao;
import com.onecm.adapter.CollectAdapter;
import com.onecm.bean.Collect;
import com.onecm.bean.Discover;
import com.onecm.power.CollectItemActivity;
import com.onecm.power.ContentActivity;
import com.onecm.power.MainActivity;
import com.onecm.power.R;
import com.onecm.util.SPUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2015/4/1 0001.
 */
public class CollectFragment extends Fragment implements ObservableScrollViewCallbacks{
    private ObservableGridView gridView ;
    private CollectAdapter collectAdapter;
    private List<Collect> mDatas = new ArrayList<Collect>();
    private CollectDao dao;
    private static final String KEY_TITILE = "title";
    private boolean isFirstLoad = true;
    private Context context ;
    private Resources resources;
    public CollectFragment() {

    }

    public static CollectFragment newInstance(String title, Context con) {
        CollectFragment f = new CollectFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITILE, title);
        f.setArguments(args);
        return (f);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.collect_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        resources = context.getResources();
        initView();
        if(collectAdapter.getmList().size()==0){
            Toast.makeText(context,resources.getString(R.string.noFavorites),Toast.LENGTH_SHORT).show();
        }else{
            boolean isFirstIn = (boolean) SPUtils.get(context,"isFirstInCollect",true);
            if (isFirstIn){
                Toast.makeText(context,resources.getString(R.string.longdel),Toast.LENGTH_SHORT).show();
                SPUtils.put(context,"isFirstInCollect",false);
            }
        }


    }

    private void initView() {
        gridView = (ObservableGridView) getView().findViewById(R.id.gridView);
        collectAdapter = new CollectAdapter(getActivity(),mDatas);
        dao = new CollectDao(getActivity());
        if (isFirstLoad) {
            mDatas = dao.selectAll();
            collectAdapter.addList(mDatas);
            isFirstLoad = false;
        }
        gridView.setAdapter(collectAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Collect collect = collectAdapter.getmList().get(position);
                Discover dis = new Discover();
                BmobFile bmobFile = new BmobFile();
                bmobFile.setUrl(collect.getImgUrl());
                dis.setDisImg(bmobFile);
                dis.setDate(collect.getDate());
                dis.setAuthor(collect.getAuthor());
                dis.setContent(collect.getContent());
                Intent intent = new Intent(getActivity(), CollectItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mDis",dis);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final String delObjecId = collectAdapter.getmList().get(position).getObjectId();
                final TextView date = (TextView) view.findViewById(R.id.collect_date);
                final ButtonFlat deleteBtn = (ButtonFlat) view.findViewById(R.id.delete);
                deleteBtn.setVisibility(View.VISIBLE);
                date.setVisibility(View.GONE);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dao.deleteCollects(delObjecId);
                        dao.insertCollectTag(delObjecId, 0);
                        collectAdapter.getmList().remove(position);
                        collectAdapter.notifyDataSetChanged();
                        date.setVisibility(View.VISIBLE);
                        deleteBtn.setVisibility(View.GONE);
                    }
                });
                return true;
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
    public void onScrollChanged(int i, boolean b, boolean b2) {

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
