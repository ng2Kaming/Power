package com.onecm.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.onecm.bean.Discover;
import com.onecm.power.R;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * Created by kaming on 2015/4/1 0001.
 */
public class ShareUtils {

    /**
     * 分享
     * @param context
     * @param text 内容
     * @param author 作者
     * @param img 图片
     */
    public static void showShare(final Context context, final String text, final String author, final Bitmap img) {
        try {
            ShareSDK.initSDK(context);
            OnekeyShare oks = new OnekeyShare();
            oks.disableSSOWhenAuthorize();
            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                @Override
                public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                    Log.d("FUCK", text + author);
                    paramsToShare.setImageData(img);
                    paramsToShare.setText(Html.fromHtml(text).toString().trim() + " --" + author + context.getString(R.string.from_power));
                }
            });
            oks.setDialogMode();
            oks.show(context);
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }


}
