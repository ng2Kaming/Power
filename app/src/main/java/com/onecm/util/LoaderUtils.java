package com.onecm.util;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.onecm.power.R;

/**
 * Created by Administrator on 2015/3/30 0030.
 */
public class LoaderUtils {

    public static DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.fail)
                .showImageOnFail(R.drawable.fail)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(100))
                .build();
        return mDisplayImageOptions;
    }


}
