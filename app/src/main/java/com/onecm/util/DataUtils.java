package com.onecm.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.onecm.bean.Discover;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2015/3/28 0028.
 */
public class DataUtils {
    private Context context;
    private BmobQuery<Discover> query;
    private List<Discover> mList = new ArrayList<Discover>();
    private Discover dis;
    public DataUtils(Context context) {
        this.context = context;
        Bmob.initialize(context, "6aed4d0a6462bbb4e6f55be316ab9183");
        query = new BmobQuery<>();
    }


    public Discover getNew(String objectId){
        BmobQuery<Discover> query = new BmobQuery<Discover>();
        query.getObject(context,objectId,new GetListener<Discover>() {
            @Override
            public void onSuccess(Discover discover) {
                dis = discover;
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
        return dis;
    }

    public List<Discover> getDataList() {
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.order("-createdAt");
        query.setLimit(8);
        query.findObjects(context, new FindListener<Discover>() {
            @Override
            public void onSuccess(List<Discover> discovers) {
                mList = discovers;
            }

            @Override
            public void onError(int i, String s) {
            }
        });
        return mList;
    }

    public List<Discover> getUpdateDataList(){
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        query.order("-createdAt");
        query.setLimit(8);
        query.findObjects(context, new FindListener<Discover>() {
            @Override
            public void onSuccess(List<Discover> discovers) {
                mList = discovers;
                Log.d("TAG",mList.toString());
            }

            @Override
            public void onError(int i, String s) {
            }
        });
        return mList;
    }


    /**
     * +1 µãÔÞ
     * @param dis
     */
    public void addLike(final Discover dis) {
        dis.increment("like",1);
        dis.update(context,new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * -1µãÔÞ
     * @param dis
     */
    public void reduceLike(final Discover dis) {
        dis.increment("like",-1);
        dis.update(context,new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public void addCollect(final Discover dis) {
        dis.increment("collect",1);
        dis.update(context,new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public void reduceCollect(final Discover dis) {
       dis.increment("collect",-1);
        dis.update(context,new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

}
