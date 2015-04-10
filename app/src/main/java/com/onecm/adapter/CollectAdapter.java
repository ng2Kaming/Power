package com.onecm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.onecm.bean.Collect;
import com.onecm.power.R;
import com.onecm.util.LoaderUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/4/1 0001.
 */
public class CollectAdapter extends BaseAdapter {
    private List<Collect> mList;
    private Context context;
    public static ImageLoader mImageLoader = ImageLoader.getInstance();
    public CollectAdapter(Context con, List<Collect> collectList) {
        this.mList = collectList;
        context = con;
    }

    public List<Collect> getmList() {
        return mList;
    }

    public void setList(List<Collect> list) {
        if (list != null && !list.isEmpty()) {
            this.mList.clear();
            this.mList.addAll(list);
        }
    }

    public void addList(List<Collect> list) {
        if (list != null && !list.isEmpty()) {
            this.mList.addAll(list);
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        Collect collect = mList.get(position);
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.collect_item,parent,false);
            viewHolder.mImg = (ImageView) convertView.findViewById(R.id.collect_img);
            viewHolder.mDate = (TextView) convertView.findViewById(R.id.collect_date);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        mImageLoader.displayImage(collect.getImgUrl(),viewHolder.mImg, LoaderUtils.getDisplayImageOptions());
        viewHolder.mDate.setText(collect.getDate());
        return convertView;
    }

    private class ViewHolder{
        ImageView mImg;
        TextView mDate;
    }
}
