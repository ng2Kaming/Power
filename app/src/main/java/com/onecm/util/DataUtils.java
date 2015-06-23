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
 * Created by kaming on 2015/3/28 0028.
 */
public class DataUtils {
    private static final String TAG = "what";
    private Context context;
    private BmobQuery<Discover> query;
    private List<Discover> mList = new ArrayList<>();
    private Discover dis;

    /**
     * 构造
     * @param context
     */
    public DataUtils(Context context) {
        this.context = context;
        query = new BmobQuery<>();
    }


    /**
     * 获取新的数据
     * @param objectId
     * @return
     */
    public Discover getNew(String objectId){
        BmobQuery<Discover> query = new BmobQuery<>();
        query.getObject(context, objectId, new GetListener<Discover>() {
            @Override
            public void onSuccess(Discover discover) {
                dis = discover;
                Log.d(TAG, discover.toString());
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e(TAG, s);
            }
        });
        return dis;
    }

    /**
     * 获取全部数据
     * @return
     */
    public List<Discover> getDataList() {
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.order("-createdAt");
        query.setLimit(8);
        query.findObjects(context, new FindListener<Discover>() {
            @Override
            public void onSuccess(List<Discover> discovers) {
                mList = discovers;
                Log.d(TAG,discovers.toString());
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG,s);
            }
        });
        return mList;
    }

    /**
     * 获取更新的数据
     * @return
     */
    public List<Discover> getUpdateDataList(){
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        query.order("-createdAt");
        query.setLimit(8);
        query.findObjects(context, new FindListener<Discover>() {
            @Override
            public void onSuccess(List<Discover> discovers) {
                mList = discovers;
                Log.d(TAG, mList.toString());
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG, s);
            }
        });
        return mList;
    }


    /**
     * +1 点赞
     * @param dis
     */
    public void addLike(final Discover dis) {
        dis.increment("like",1);
        dis.update(context,new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"addLike");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG,s);
            }
        });
    }

    /**
     * -1点赞
     * @param dis
     */
    public void reduceLike(final Discover dis) {
        dis.increment("like",-1);
        dis.update(context,new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"reduceLike");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG,s);
            }
        });
    }

    /**
     * +1 收藏
     * @param dis
     */
    public void addCollect(final Discover dis) {
        dis.increment("collect",1);
        dis.update(context,new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"addCollect");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG,s);
            }
        });
    }

    /**
     * -1 收藏
     * @param dis
     */
    public void reduceCollect(final Discover dis) {
       dis.increment("collect",-1);
        dis.update(context,new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"reduceCollect");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG,s);
            }
        });
    }

}
