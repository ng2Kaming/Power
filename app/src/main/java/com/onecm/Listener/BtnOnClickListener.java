package com.onecm.Listener;

import android.view.View;

import com.onecm.bean.ViewHolder;

/**
 * Created by kaming on 2015/4/2 0002.
 */
public abstract class BtnOnClickListener implements View.OnClickListener {
    private int position;
    private ViewHolder viewHolder ;

    public BtnOnClickListener(ViewHolder viewHolder , int position){
        this.position = position;
        this.viewHolder = viewHolder;
    }
    @Override
    public void onClick(View v) {
        btnClick(v,viewHolder,position);
    }

    public abstract void btnClick(View v, ViewHolder viewHolder, int position);

}
