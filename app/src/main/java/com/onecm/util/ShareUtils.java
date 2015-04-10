package com.onecm.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.onecm.bean.Discover;
import com.onecm.power.R;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by Administrator on 2015/4/1 0001.
 */
public class ShareUtils {

    private Discover dis;
    private Context context;
    private Bitmap bitmap ;

    public ShareUtils(Discover mDiscover, Context con,Bitmap img){
        this.dis = mDiscover;
        this.context = con;
        this.bitmap =img;
    }
    public void showShare() {
        try {
            ShareSDK.initSDK(context);
            OnekeyShare oks = new OnekeyShare();
            oks.disableSSOWhenAuthorize();
            //oks.setSilent(false);
            //oks.setTheme(OnekeyShareTheme.SKYBLUE);
            oks.setShareContentCustomizeCallback(new ShareContentCustomize());
            oks.setDialogMode();
            oks.show(context);
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    public class ShareContentCustomize implements ShareContentCustomizeCallback {

        @Override
        public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
            String text = dis.getContent();
            String author = dis.getAuthor();
            if ("Wechat".equals(platform.getName())) {
                paramsToShare.setImageData(bitmap);
                paramsToShare.setText(Html.fromHtml(text+"--"+author).toString());
            }
            if ("WechatMoments".equals(platform.getName())) {
                paramsToShare.setImageData(bitmap);
                paramsToShare.setText(Html.fromHtml(text+"--"+author).toString());
            } else {
                paramsToShare.setImageData(bitmap);
                paramsToShare.setAuthor(dis.getAuthor());
                paramsToShare.setText(Html.fromHtml(text+"--"+author).toString());
            }
        }
    }

}
