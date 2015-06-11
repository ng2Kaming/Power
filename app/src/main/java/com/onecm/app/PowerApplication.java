package com.onecm.app;

import android.app.Application;

/**
 * Created by Kaming on 2015/6/9 0009.
 */
public class PowerApplication extends Application {
    private String mUsername;
    private static PowerApplication mPowerApplication = new PowerApplication();

    public static PowerApplication getInstance(){
        return mPowerApplication;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

}
