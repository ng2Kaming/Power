package com.oencm.power;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onecm.bean.Discover;
import com.onecm.util.DataUtils;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);
    }

    /**
     * 全部数据
     */
    public void testForGetDataList() {
        DataUtils data = new DataUtils(getContext());
        data.getDataList();
    }

    /**
     * 新的一条数据
     */
    public void testForGetNew() {
        DataUtils data = new DataUtils(getContext());

    }

    /**
     * 更新数据
     */
    public void testForGetUpdateDataList() {
        DataUtils data = new DataUtils(getContext());
        data.getUpdateDataList();
    }

    /**
     * +1 Like
     */
    public void testForAddLike() {
        DataUtils data = new DataUtils(getContext());
        Discover dis = data.getNew("sEi48114");
        Log.d("what",dis.toString());
        data.addLike(dis);
    }

    /**
     * -1 Like
     */
    public void testForReduceLike() {
        DataUtils data = new DataUtils(getContext());
        data.reduceLike(data.getNew("sEi48114"));
    }

}