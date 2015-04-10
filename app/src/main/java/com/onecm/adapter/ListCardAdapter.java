package com.onecm.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.onecm.Dao.CollectDao;
import com.onecm.Listener.BtnOnClickListener;
import com.onecm.bean.Discover;
import com.onecm.bean.ViewHolder;
import com.onecm.power.R;
import com.onecm.util.DataUtils;
import com.onecm.util.LoaderUtils;
import com.onecm.util.ShareUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/4/1 0001.
 */
public class ListCardAdapter extends BaseAdapter {
    private List<Discover> mList;
    private Context context;
    public static ImageLoader mImageLoader = ImageLoader.getInstance();
    private int likeTag = 0;
    private int collectTag = 0;
    private CollectDao dao;
    private Discover mDiscover;
    private ViewHolder mViewHolder;
    private DataUtils dataUtils;
    private Resources resources;

    public ListCardAdapter(Context con, List<Discover> discoverList) {
        this.mList = discoverList;
        context = con;
        dao = new CollectDao(con);
        dataUtils = new DataUtils(con);
        resources = context.getResources();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(con));
    }

    public List<Discover> getmList() {
        return mList;
    }

    public void setList(List<Discover> list) {
        if (list != null && !list.isEmpty()) {
            this.mList.clear();
            this.mList.addAll(list);
        }
    }

    public void addList(List<Discover> list) {
        this.mList.addAll(list);
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
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.cards_item, null);
            mViewHolder.mDate = (TextView) convertView.findViewById(R.id.card_date);
            mViewHolder.mContent = (TextView) convertView.findViewById(R.id.card_content);
            mViewHolder.mAuthor = (TextView) convertView.findViewById(R.id.card_author);
            mViewHolder.mImg = (ImageView) convertView.findViewById(R.id.card_img);
            mViewHolder.mShare = (ButtonFlat) convertView.findViewById(R.id.card_share);
            mViewHolder.mCollect = (ButtonFlat) convertView.findViewById(R.id.card_collect);
            mViewHolder.mLike = (ButtonFlat) convertView.findViewById(R.id.card_like);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        bindData(mList.get(position), mViewHolder, position);
        return convertView;
    }


    public void bindData(Discover discover, final ViewHolder viewHolder, int position) {
        mDiscover = discover;
        if (discover.getDate() != null && discover.getDate().length() > 0) {
            viewHolder.mDate.setText(discover.getDate());
        } else {
            viewHolder.mDate.setText(new SimpleDateFormat("yy-MM-dd").format(new Date()));
        }
        if (discover.getContent() != null && discover.getContent().length() > 0) {
            viewHolder.mContent.setText(getSummary(Html.fromHtml(discover.getContent()).toString()));
        } else {
            viewHolder.mContent.setText(context.getResources().getString(R.string.no_data));
        }

        if (discover.getAuthor() != null && discover.getAuthor().length() > 0) {
            viewHolder.mAuthor.setText(discover.getAuthor());
        } else {
            viewHolder.mAuthor.setText(context.getResources().getString(R.string.card_content_author));
        }
        if (discover.getDisImg() != null) {
            viewHolder.mImg.setDrawingCacheEnabled(true);
            mImageLoader.displayImage(discover.getDisImg().getFileUrl(context), viewHolder.mImg, LoaderUtils.getDisplayImageOptions());
        }
        likeTag = dao.getLikeTag(discover.getObjectId());
        if (likeTag == 0) {
            viewHolder.mLike.setText("+" + discover.getLike() + " " + context.getResources().getString(R.string.card_like));
        } else {
            viewHolder.mLike.setText("+" + discover.getLike() + " " + context.getResources().getString(R.string.card_liked));
        }
        collectTag = dao.getCollectTag(discover.getObjectId());
        if (collectTag == 0) {
            viewHolder.mCollect.setText(context.getResources().getString(R.string.card_collect));
        } else {
            viewHolder.mCollect.setText(context.getResources().getString(R.string.card_collected));
        }
        viewHolder.mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap img = Bitmap.createBitmap(viewHolder.mImg.getDrawingCache());
                    new ShareUtils(mDiscover, context, img).showShare();
                    viewHolder.mImg.setDrawingCacheEnabled(false);
                } catch (Exception e) {
                    Toast.makeText(context, resources.getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewHolder.mCollect.setOnClickListener(new BtnOnClickListener(viewHolder, position) {

            @Override
            public void btnClick(View v, ViewHolder viewHolder, int position) {
                mDiscover = mList.get(position);
                collectTag = dao.getCollectTag(mDiscover.getObjectId());
                if (collectTag == 0) {
                    dao.insertCollects(mDiscover);
                    dao.insertCollectTag(mDiscover.getObjectId(), 1);
                    dataUtils.addCollect(mDiscover);
                    viewHolder.mCollect.setText(context.getResources().getString(R.string.card_collected));
                    Toast.makeText(context, context.getResources().getString(R.string.collected), Toast.LENGTH_SHORT).show();
                } else {
                    dao.deleteCollects(mDiscover.getObjectId());
                    dao.insertCollectTag(mDiscover.getObjectId(), 0);
                    dataUtils.reduceCollect(mDiscover);
                    viewHolder.mCollect.setText(context.getResources().getString(R.string.card_collect));
                    Toast.makeText(context, context.getResources().getString(R.string.cancel_collect), Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewHolder.mLike.setOnClickListener(new BtnOnClickListener(viewHolder, position) {
            @Override
            public void btnClick(View v, ViewHolder viewHolder, int position) {
                mDiscover = mList.get(position);
                likeTag = dao.getLikeTag(mDiscover.getObjectId());
                int likes = mDiscover.getLike();
                if (likeTag == 0) {
                    dao.insertLikeTag(mDiscover.getObjectId(), 1);
                    dataUtils.addLike(mDiscover);
                    viewHolder.mLike.setText("+" + (likes + 1) + " " + context.getResources().getString(R.string.card_liked));
                } else {
                    dao.insertLikeTag(mDiscover.getObjectId(), 0);
                    dataUtils.reduceLike(mDiscover);
                    viewHolder.mLike.setText("+" + (likes - 1) + " " + context.getResources().getString(R.string.card_like));
                }
            }
        });
    }

    private String getSummary(String content) {
        String summary;
        if (content.length() > 60) {
            summary = "    " + content.substring(0, 54) + "...";
        } else {
            summary = "    " + content;
        }
        return summary;
    }

}
