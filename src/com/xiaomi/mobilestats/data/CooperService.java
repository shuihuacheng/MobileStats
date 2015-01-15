package com.xiaomi.mobilestats.data;

import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class CooperService extends BasicStoreToolsBase{
	
	  private static JSONObject jsonObject = new JSONObject();
	  private static String c = "activehead";
	  private static HashMap<String, Object> map = new HashMap();
	  private static MobileInfo mobileInfo = new MobileInfo();
	  
	  public static MobileInfo getMoblieInfo() {
		return mobileInfo;
	}

	  private static String a(Context context)
	  {
	    String str = ap.getMacAddress(context);   //TODO获取mac地址
	    if (str != null)
	      str = str.replaceAll(":", "");
	    return str;
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
	      String str1 = BasicStoreTools.getInstance().getAppDeviceMac(context);
	      if (str1 == null)
	      {
	        String str2 = a(context);
	        if (str2 != null)
	        {
	          mobileInfo.macID = getSecretValue(str2);
//	          au.a("sdkstat", "加密=mHeadObject.mHeadObject.macAddr=" + mobileInfo.macID);
	          if (mobileInfo.macID != "")
	            BasicStoreTools.getInstance().setAppDeviceMac(context, mobileInfo.macID);
	        }
	      }
	      else
	      {
	        mobileInfo.macID= str1;
	      }
	    }
	    return mobileInfo.macID;
	  }

	/**
	 * 获取MacID for TV
	 * @param paramContext
	 * @return
	 */
	  public static String getMacIDForTv(Context paramContext)
	  {
	    if ((mobileInfo.macID == null) || ("".equals(mobileInfo.macIDForTV)))
	    {
	      String str1 = BasicStoreTools.getInstance().getAppDeviceMacTv(paramContext);
	      if (str1 == null)
	      {
	        String str2 = ap.a();
	        if ((str2 == null) || (str2.equals("")))
	          str2 = ap.h(paramContext);
	        if (str2 != null)
	        {
	          mobileInfo.macIDForTV = getSecretValue(str2);
	          au.a("sdkstat", "加密=macAddr=" + mobileInfo.macIDForTV);
	          if (mobileInfo.macIDForTV != "")
	            BasicStoreTools.getInstance().setAppDeviceMacTv(paramContext, mobileInfo.macIDForTV);
	        }
	      }
	      else
	      {
	        mobileInfo.macIDForTV = str1;
	      }
	    }
	    return mobileInfo.macIDForTV;
	  }

	  /**
	   * 获取CUID
	   * @param paramContext
	   * @param paramBoolean
	   * @return
	   */
	  public static String getCUID(Context paramContext, boolean paramBoolean)
	  {
	    if (mobileInfo.CUID == null)
	    {
	      mobileInfo.CUID = BasicStoreTools.getInstance().getGenerateDeviceCUID(paramContext);
	      if ((mobileInfo.CUID == null) || ("".equalsIgnoreCase(mobileInfo.CUID)))
	        try
	        {
	          mobileInfo.CUID = map.a(paramContext);
	          Pattern localPattern = Pattern.compile("\\s*|\t|\r|\n");
	          Matcher localMatcher = localPattern.matcher(mobileInfo.CUID);
	          mobileInfo.CUID = localMatcher.replaceAll("");
	          mobileInfo.CUID = getSecretValue(mobileInfo.CUID);
	          BasicStoreTools.getInstance().setGenerateDeviceCUID(paramContext, mobileInfo.CUID);
	        }
	        catch (Exception localException1)
	        {
	          au.c(new Object[] { "sdkstat", localException1.getMessage() });
	        }
	    }
	    if (paramBoolean)
	      return mobileInfo.CUID;
	    try
	    {
	      if (mobileInfo.CUID != null)
	        return new String(aq.b("30212102dicudiab", "30212102dicudiab", at.a(mobileInfo.CUID.getBytes())));
	    }
	    catch (Exception localException2)
	    {
	      localException2.printStackTrace();
	    }
	    return null;
	  }

	  public static int getTagValue()
	  {
	    return 2;
	  }

	  /**
	   * 获取DeviceId
	   * @param paramTelephonyManager
	   * @param paramContext
	   * @return
	   */
	  public static String getDeviceId(TelephonyManager paramTelephonyManager, Context paramContext)
	  {
	    if (paramTelephonyManager == null)
	      return mobileInfo.deviceId;
	    String str1 = mobileInfo.deviceId;
	    if ((str1 == null) || (str1.equals("")))
	    {
	      boolean bool = BasicStoreTools.getInstance().getForTV(paramContext);
	      if (bool)
	      {
	        mobileInfo.deviceId = getMacIDForTv(paramContext);
	        return mobileInfo.deviceId;
	      }
	      Pattern localPattern = Pattern.compile("\\s*|\t|\r|\n");
	      try
	      {
	        String str3 = paramTelephonyManager.getDeviceId();
	        Matcher localMatcher = localPattern.matcher(str3);
	        str1 = localMatcher.replaceAll("");
	        str1 = a(str1, paramContext);
	      }
	      catch (Exception localException2)
	      {
	        au.a(localException2);
	      }
	      if (str1 == null)
	        str1 = a(paramContext);
	      if ((str1 == null) || (str1.equals("000000000000000")))
	        str1 = BasicStoreTools.getInstance().getGenerateDeviceId(paramContext);
	      if ((str1 == null) || (str1.equals("000000000000000")))
	      {
	        String str4 = new Date().getTime() + "";
	        str1 = "hol" + str4.hashCode() + "mes";
	        BasicStoreTools.getInstance().setGenerateDeviceId(paramContext, str1);
	        au.a("sdkstat", "设备id为空，系统生成id =" + str1);
	      }
	      mobileInfo.deviceId = str1;
	      mobileInfo.deviceId = getSecretValue(mobileInfo.deviceId);
	      au.a("sdkstat", "加密=mHeadObject.deviceId=" + mobileInfo.deviceId);
	    }
	    try
	    {
	      String str2 = new String(aq.b("30212102dicudiab", "30212102dicudiab", at.a(mobileInfo.deviceId.getBytes())));
	      au.a("sdkstat", "deviceId=" + str2);
	    }
	    catch (Exception localException1)
	    {
	      localException1.printStackTrace();
	    }
	    return mobileInfo.deviceId;
	  }

	  /**
	   * 获取App渠道
	   * @param paramContext
	   * @return
	   */
	  public static String getAppChannel(Context paramContext)
	  {
	    try
	    {
	      au.a("sdkstat", "----------getAppChannel");
	      if ((mobileInfo.appChannel == null) || (mobileInfo.appChannel.equals("")))
	      {
	        boolean bool = BasicStoreTools.getInstance().getAppChannelWithCode(paramContext);
	        au.a("sdkstat", "----------setChannelWithCode=" + bool);
	        if (bool)
	        {
	          mobileInfo.appChannel = BasicStoreTools.getInstance().getAppChannelWithPreference(paramContext);
	          au.a("sdkstat", "----------mHeadObject.channel=" + mobileInfo.appChannel);
	        }
	        if ((!bool) || (mobileInfo.appChannel == null) || (mobileInfo.appChannel.equals("")))
	          mobileInfo.appChannel = ap.a(paramContext, "BaiduMobAd_CHANNEL");
	      }
	    }
	    catch (Exception localException)
	    {
	      au.a(localException);
	    }
	    return mobileInfo.appChannel;
	  }

	  /**
	   * 获取AppKey
	   * @param paramContext
	   * @return
	   */
	  public static String getAppKey(Context paramContext)
	  {
	    if (mobileInfo.appKey == null)
	      mobileInfo.appKey = ap.a(paramContext, "BaiduMobAd_STAT_ID");
	    return mobileInfo.appKey;
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
	      mobileInfo.appVersionCode = ap.c(paramContext);
	    return mobileInfo.appVersionCode;
	  }

	  /**
	   * 获取App的VersionName
	   * @param paramContext
	   * @return
	   */
	  public static String getAppVersionName(Context paramContext)
	  {
	    if ((mobileInfo.appVersionName == null) || ("".equals(mobileInfo.appVersionName)))
	      mobileInfo.appVersionName = ap.d(paramContext);
	    return mobileInfo.appVersionName;
	  }

	  public static String getOperator(TelephonyManager paramTelephonyManager)
	  {
	    if ((mobileInfo.networkOperator == null) || ("".equals(mobileInfo.networkOperator)))
	      mobileInfo.networkOperator = paramTelephonyManager.getNetworkOperator();
	    return mobileInfo.networkOperator;
	  }

	  public static String getLinkedWay(Context paramContext)
	  {
	    if ((mobileInfo.linkedWay == null) || ("".equals(mobileInfo.linkedWay)))
	      mobileInfo.linkedWay = ap.j(paramContext);
	    return mobileInfo.linkedWay;
	  }
	  
	  /**
	   * 获取SDK版本
	   * @return
	   */
	  public static String getOSVersion()
	  {
	    if ((mobileInfo.OSVersion == null) || ("".equals(mobileInfo.OSVersion)))
	    	mobileInfo.OSVersion = Build.VERSION.SDK;
	    return mobileInfo.OSVersion;
	  }

	  /**
	   * 获取系统版本
	   * @return
	   */
	  public static String getOSSysVersion()
	  {
	    if ((mobileInfo.OSSysVersion == null) || ("".equals(mobileInfo.OSSysVersion)))
	    	mobileInfo.OSSysVersion = Build.VERSION.RELEASE;
	    return mobileInfo.OSSysVersion;
	  }

	  public static String getPhoneModel()
	  {
	    if ((mobileInfo.phoneModel == null) || ("".equals(mobileInfo.phoneModel)))
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
	    String str = ap.a(paramContext, "XM_WIFI_LOCATION");
	    return (str == null) || (!str.toLowerCase().equals("false"));
	  }

	  /**
	   * 检测是否获取GPS位置信息
	   * @param paramContext
	   * @return
	   */
	  public static boolean checkGPSLocationSetting(Context paramContext)
	  {
	    String str = ap.a(paramContext, "XM_GPS_LOCATION");
	    return (str == null) || (!str.toLowerCase().equals("false"));
	  }

	  /**
	   * 检测是否获取基站位置信息
	   * @param paramContext
	   * @return
	   */
	  public static boolean checkCellLocationSetting(Context paramContext)
	  {
	    String str = ap.a(paramContext, "XM_CELL_LOCATION");
	    return (str == null) || (!str.toLowerCase().equals("false"));
	  }

	  /**
	   * 加密字符串
	   * @param paramString
	   * @return
	   */
	  public static String getSecretValue(String paramString)
	  {
	    String str = null;
	    try
	    {
	    	str = paramString;
//	      str = at.a(aq.a("30212102dicudiab", "30212102dicudiab", paramString.getBytes()), "utf-8");
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	    if (str == null)
	      str = "";
	    return str;
	  }
}
