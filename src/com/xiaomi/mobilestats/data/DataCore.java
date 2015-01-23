package com.xiaomi.mobilestats.data;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xiaomi.mobilestats.common.CommonUtil;

public class DataCore extends BasicStoreToolsBase{
	
	  private static MobileInfo mobileInfo = new MobileInfo();
	  
	  public static MobileInfo getMoblieInfo() {
		return mobileInfo;
	}

	public static String getAppkey(Context context){
		if(TextUtils.isEmpty(mobileInfo.appKey)){
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
	
	  /**
	   * 获取App渠道
	   * @param paramContext
	   * @return
	   */
	  public static String getAppChannel(Context paramContext)
	  {
	      if (TextUtils.isEmpty(mobileInfo.appChannel)){
	    	  String spAppChannel = BasicStoreTools.getInstance().getAppChannel(paramContext);
	  	       if(!TextUtils.isEmpty(spAppChannel)){
	  	    	   mobileInfo.appChannel = spAppChannel;
	  	       }else{
	  	    	   String xmlAppChannel = CommonUtil.getAppChannel(paramContext);
	  	    	   if(!TextUtils.isEmpty(xmlAppChannel)){
	  	    		   mobileInfo.appChannel = xmlAppChannel;
	  	    		   BasicStoreTools.getInstance().setAppChannel(paramContext, xmlAppChannel);
	  	    	   }
	  	       }
	      }
	    return mobileInfo.appChannel;
	  }
	  
	/**
	 * 获取MacID
	 * @param context
	 * @return
	 */
	public static String getMacID(Context context)
	  {
	    if (TextUtils.isEmpty(mobileInfo.macID))
	    {
	      String savedMac = BasicStoreTools.getInstance().getAppDeviceMac(context);
	      if (TextUtils.isEmpty(savedMac))
	      {
	        String str2 = CommonUtil.getMacAddress(context);
	        if (str2 != null)
	        {
	          if (TextUtils.isEmpty(mobileInfo.macID)){
	        	  BasicStoreTools.getInstance().setAppDeviceMac(context, mobileInfo.macID);
	          }
	        }
	      }else
	      {
	        mobileInfo.macID= savedMac;
	      }
	    }
	    return mobileInfo.macID;
	  }

	  /**
	   * 获取DeviceId
	   * @param paramTelephonyManager
	   * @param paramContext
	   * @return
	   */
	  public static String getDeviceId(Context paramContext)
	  {
		  if(TextUtils.isEmpty(mobileInfo.deviceId)) 
			  mobileInfo.deviceId = CommonUtil.getDeviceID(paramContext);
	    return mobileInfo.deviceId;
	  }

	  public static String getXMSDKVersion()
	  {
	    return "1.0";
	  }
	  
	  /**
	   * 获取App的VersionCode
	   * @param paramContext
	   * @return
	   */
	  public static int getAppVersionCode(Context paramContext)
	  {
	    if (mobileInfo.appVersionCode == -1)
	      mobileInfo.appVersionCode = CommonUtil.getVersionCode(paramContext);
	    return mobileInfo.appVersionCode;
	  }

	  /**
	   * 获取App的VersionName
	   * @param paramContext
	   * @return
	   */
	  public static String getAppVersionName(Context paramContext)
	  {
	    if (TextUtils.isEmpty(mobileInfo.appVersionName))
	      mobileInfo.appVersionName = CommonUtil.getVersionName(paramContext);
	    return mobileInfo.appVersionName;
	  }

	  public static String getOperator(TelephonyManager paramTelephonyManager)
	  {
	    if (TextUtils.isEmpty(mobileInfo.networkOperator))
	      mobileInfo.networkOperator = paramTelephonyManager.getNetworkOperator();
	    return mobileInfo.networkOperator;
	  }

	  public static String getLinkedWay(Context paramContext)
	  {
	    if (TextUtils.isEmpty(mobileInfo.linkedWay)){
	    	//TODO获取linkedWay
	    }
	    return mobileInfo.linkedWay;
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

	  /**
	   * 检测是否获取WIFI位置信息
	   * @param paramContext
	   * @return
	   */
	  public static boolean checkWifiLocationSetting(Context paramContext)
	  {
	    String str = "false";
	    //TODO 检测Manifest文件中配置  XM_WIFI_LOCATION
	    return (str == null) || (!str.toLowerCase().equals("false"));
	  }

	  /**
	   * 检测是否获取GPS位置信息
	   * @param paramContext
	   * @return
	   */
	  public static boolean checkGPSLocationSetting(Context paramContext)
	  {
	    String str = "false";
	    //TODO 检测Manifest文件中配置
	    return (str == null) || (!str.toLowerCase().equals("false"));
	  }

	  /**
	   * 检测是否获取基站位置信息
	   * @param paramContext
	   * @return
	   */
	  public static boolean checkCellLocationSetting(Context paramContext)
	  {
	    String str = "false";
	    //	    //TODO 检测Manifest文件中配置 XM_CELL_LOCATION
	    return (str == null) || (!str.toLowerCase().equals("false"));
	  }

}
