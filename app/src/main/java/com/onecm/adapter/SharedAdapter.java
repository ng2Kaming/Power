package com.onecm.adapter;

import android.view.View;

import cn.sharesdk.framework.authorize.AuthorizeAdapter;

/**
 * Created by Administrator on 2015/3/31 0031.
 */
public class SharedAdapter extends AuthorizeAdapter {
    public void onCreate() {
        int count = getTitleLayout().getChildCount();
        getTitleLayout().getChildAt(count - 1).setVisibility(View.GONE);
    }
}
