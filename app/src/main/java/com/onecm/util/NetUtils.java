package com.onecm.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {

	public static boolean checkNet(Context context) {
		boolean wifiConntected = isWifiConntected(context);
		boolean mobileConntected = isMobileConnected(context);
		if (wifiConntected==false&&mobileConntected==false) {
			return false;
		}
		return true;
	}

	private static boolean isMobileConnected(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mNetworkInfo!=null&&mNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	private static boolean isWifiConntected(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mNetworkInfo!=null&&mNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}


	
}
