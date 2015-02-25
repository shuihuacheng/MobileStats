package com.xiaomi.mobilestats.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetType {
	
	public static final int NETTYPE_INVALID = 0;
	public static final int NETTYPE_WAP = 1;
	public static final int NETTYPE_2G = 2;
	public static final int NETTYPE_3G_UP = 3;
	public static final int NETTYPE_WIFI = 4;
	
	public static boolean isNet2G_DOWN(Context context){
		int netType = getNetType(context);
		boolean result = (netType == NETTYPE_2G|| netType == NETTYPE_WAP);
		return result;
	}
	
	@SuppressWarnings("deprecation")
	public static int getNetType(Context context){
		int netType = -1;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected()){
			String type = networkInfo.getTypeName();
			if(type.equalsIgnoreCase("wifi")){
				netType = NETTYPE_WIFI;
			}else if(type.equalsIgnoreCase("mobile")){
				String proxyHost = android.net.Proxy.getDefaultHost();
				if(!StringUtils.isEmpty(proxyHost)){
					netType = NETTYPE_WAP;
				}else{
					if(isFastMobileNetwork(context)){
						netType = NETTYPE_2G;
					}else{
						netType = NETTYPE_3G_UP;
					}
				}
			}
		}else{
			netType = NETTYPE_INVALID;
		}
		return netType;
	}
	
	private static boolean isFastMobileNetwork(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = tm.getNetworkType();
		switch(networkType){
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return false;
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return false;
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return false;
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return true;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return true;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return true;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return true;
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return false;
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return true;
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return true;
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			// ~10-20Mbps
			return true;
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			// ~1-23Mbps	
			return true;
		case TelephonyManager.NETWORK_TYPE_IDEN:
			// ~ 25 kbps
			return false;
		case TelephonyManager.NETWORK_TYPE_LTE:
			// ~10+ Mbps
			return true;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return true;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return false;
			default:
				return false;
		}
	}
}
