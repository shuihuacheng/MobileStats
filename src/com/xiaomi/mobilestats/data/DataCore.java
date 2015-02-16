package com.xiaomi.mobilestats.data;

import java.text.ParseException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xiaomi.mobilestats.common.CommonUtil;
import com.xiaomi.mobilestats.common.StringUtils;

public class DataCore extends BasicStoreToolsBase{
	
	  private static MobileInfo mobileInfo = new MobileInfo();
	  
	  public static MobileInfo getMoblieInfo() {
		return mobileInfo;
	}

	public static String getAppkey(Context context){
		if(mobileInfo != null && TextUtils.isEmpty(mobileInfo.appKey)){
			if(context != null){
				String savedAppkey = BasicStoreTools.getInstance().getAppKey(context);
				if(!TextUtils.isEmpty(savedAppkey)){
					mobileInfo.appKey = savedAppkey;
				}else{
					String xmlAppKey = CommonUtil.getAppKey(context);
					if(!TextUtils.isEmpty(xmlAppKey)){
						mobileInfo.appKey = xmlAppKey;
						BasicStoreTools.getInstance().setAppKey(context, xmlAppKey);
					}
				}
			} 
			return mobileInfo.appKey;
		}
		return "";
	}  
	
	  /**
	   * 获取App渠道
	   * @param context
	   * @return
	   */
	  public static String getAppChannel(Context context)
	  {
	      if (mobileInfo != null && TextUtils.isEmpty(mobileInfo.appChannel)){
	    	  if(context != null){
	    		  String spAppChannel = BasicStoreTools.getInstance().getAppChannel(context);
	    		  if(!TextUtils.isEmpty(spAppChannel)){
	    			  mobileInfo.appChannel = spAppChannel;
	    		  }else{
	    			  String xmlAppChannel = CommonUtil.getAppChannel(context);
	    			  if(!TextUtils.isEmpty(xmlAppChannel)){
	    				  mobileInfo.appChannel = xmlAppChannel;
	    				  BasicStoreTools.getInstance().setAppChannel(context, xmlAppChannel);
	    			  }
	    		  }
	    	  } 
	    	  return mobileInfo.appChannel;
	      }
	      return "";
	  }
	  
	/**
	 * 获取MacID
	 * @param context
	 * @return
	 */
	public static String getMacID(Context context)
	  {
	    if (mobileInfo != null && TextUtils.isEmpty(mobileInfo.macID)){
		     if(context != null){
		    	 String savedMac = BasicStoreTools.getInstance().getAppDeviceMac(context);
		    	 if (!TextUtils.isEmpty(savedMac)) {
		    		 mobileInfo.macID= savedMac;
		    	 }else {
		    		 String macStr = CommonUtil.getMacAddress(context);
		    		 if (!TextUtils.isEmpty(macStr)){
		    			 mobileInfo.macID = macStr;
	    				 BasicStoreTools.getInstance().setAppDeviceMac(context, macStr);
		    		 }
		    	 }
		     }
	    }
	    return mobileInfo.macID;
	  }

	  /**
	   * 获取DeviceId
	   * @param paramTelephonyManager
	   * @param context
	   * @return
	   */
	  public static String getDeviceId(Context context)
	  {
		  if(mobileInfo != null && TextUtils.isEmpty(mobileInfo.deviceId) ) {
			  if(context != null){
				  mobileInfo.deviceId = CommonUtil.getDeviceID(context);
			  }
			  return mobileInfo.deviceId;
		  }
		  return "";
	  }

	  public static String getXMSDKVersion()
	  {
	    return "1.0";
	  }
	  
	  /**
	   * 获取App的VersionCode
	   * @param context
	   * @return
	   */
	  public static int getAppVersionCode(Context context)
	  {
	    if (mobileInfo != null && mobileInfo.appVersionCode == -1){
	    	if(context != null){
	    		mobileInfo.appVersionCode = CommonUtil.getVersionCode(context);
	    	}
	    	return mobileInfo.appVersionCode;
	    }
	    return -1;
	  }

	  /**
	   * 获取App的VersionName
	   * @param paramContext
	   * @return
	   */
	  public static String getAppVersionName(Context paramContext)
	  {
	    if (TextUtils.isEmpty(mobileInfo.appVersionName) && paramContext != null){
	    	mobileInfo.appVersionName = CommonUtil.getVersionName(paramContext);
	    }
	    return mobileInfo.appVersionName;
	  }

	  public static String getOperator(TelephonyManager tm)
	  {
	    if (TextUtils.isEmpty(mobileInfo.networkOperator) && tm != null)
	      mobileInfo.networkOperator = tm.getNetworkOperator();
	    return mobileInfo.networkOperator;
	  }

	  /**
	   * 获取SDK版本
	   * @return
	   */
	  public static String getOSVersion()
	  {
	    if (TextUtils.isEmpty(mobileInfo.OSVersion))
	    	mobileInfo.OSVersion = Build.VERSION.SDK;
	    return mobileInfo.OSVersion;
	  }

	  /**
	   * 获取系统版本
	   * @return
	   */
	  public static String getOSSysVersion()
	  {
	    if (TextUtils.isEmpty(mobileInfo.OSSysVersion))
	    	mobileInfo.OSSysVersion = Build.VERSION.RELEASE;
	    return mobileInfo.OSSysVersion;
	  }

	  public static String getPhoneModel()
	  {
	    if (TextUtils.isEmpty(mobileInfo.phoneModel))
	    	mobileInfo.phoneModel = Build.MODEL;
	    return mobileInfo.phoneModel;
	  }
	  
	  public static String getCurrentSessionId(Context context){
		  if(TextUtils.isEmpty(mobileInfo.sessionId) && context != null){
			  mobileInfo.sessionId = generateSeesion(context);
		  }
		  return mobileInfo.sessionId;
	  }
	  
		/**
		 * 生成唯一SeesionId
		 * @param context
		 * @return
		 * @throws ParseException
		 */
		private static String generateSeesion(Context context) {
			String sessionId = "";
			if(context != null){
				String str = DataCore.getAppkey(context)+DataCore.getDeviceId(context)+System.currentTimeMillis();
				if (str != null) {
					sessionId = StringUtils.md5(str);
					SharedPreferences preferences = context.getSharedPreferences("XM_sessionID", Context.MODE_PRIVATE);
					Editor edit = preferences.edit();
					edit.putString("session_id", sessionId);
					edit.commit();
				}
			}
			return sessionId;
		}
}
