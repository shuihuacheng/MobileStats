package com.xiaomi.mobilestats.data;

import android.content.Context;

public class BasicStoreTools extends BasicStoreToolsBase{
	  public static final String LAST_SEND_TIME = "lastsendtime";
	  public static final String SEND_LOG_TYPE = "sendLogtype";
	  public static final String TIME_INTERVAL = "timeinterval";
	  public static final String ONLY_WIFI = "onlywifi";
	  public static final String APP_ANALYSIS_EXCEPTION = "exceptionanalysisflag";
	  public static final String DEVICE_ID = "device_id";
	  public static final String DEVICE_CUID = "cuidsec";
	  public static final String APP_ANALYSIS_EXCEPTION_TAG = "exceptionanalysistag";
	  public static final String APP_SET_CHANNEL_WITH_CODE = "setchannelwithcode";
	  public static final String APP_SET_CHANNEL = "setchannelwithcodevalue";
	  public static final String APP_SET_APPKEY = "setappkey";
	  public static final String APP_MAC_ADDRESS = "sdkmacss";
	  public static final String APP_MAC_ADDRESS_TV = "sdkmacsstv";
	  public static final String APP_LAST_SENDDATA = "lastdata";
	  
	  static BasicStoreTools instance = new BasicStoreTools();

	  public static BasicStoreTools getInstance()
	  {
	    return instance;
	  }
	  
	  public void setExceptionTurn(Context paramContext, boolean paramBoolean)
	  {
	    putBoolean(paramContext, "exceptionanalysisflag", paramBoolean);
	  }

	  public boolean getExceptionTurn(Context paramContext)
	  {
	    return getBoolean(paramContext, "exceptionanalysisflag", false);
	  }

	  public void setExceptionHeadTag(Context paramContext, String paramString)
	  {
	    putString(paramContext, "exceptionanalysistag", paramString);
	  }

	  public String getExceptionHeadTag(Context paramContext)
	  {
	    return getString(paramContext, "exceptionanalysistag", null);
	  }

	  public void setSendStrategy(Context paramContext, int paramInt)
	  {
	    putInt(paramContext, "sendLogtype", paramInt);
	  }

	  public int getSendStrategy(Context paramContext)
	  {
	    return getInt(paramContext, "sendLogtype", 0);
	  }

	  public void setSendStrategyTime(Context paramContext, int paramInt)
	  {
	    putInt(paramContext, "timeinterval", paramInt);
	  }

	  public int getSendStrategyTime(Context paramContext)
	  {
	    return getInt(paramContext, "timeinterval", 1);
	  }

	  public void setOnlyWifi(Context paramContext, boolean paramBoolean)
	  {
	    putBoolean(paramContext, "onlywifi", paramBoolean);
	  }

	  public boolean getOnlyWifiChannel(Context paramContext)
	  {
	    return getBoolean(paramContext, "onlywifi", false);
	  }

	  public void setLastSendTime(Context paramContext, long paramLong)
	  {
	    putLong(paramContext, "lastsendtime", paramLong);
	  }

	  public long getLastSendTime(Context paramContext)
	  {
	    return getLong(paramContext, "lastsendtime", 0L);
	  }

	  public void setGenerateDeviceId(Context paramContext, String paramString)
	  {
	    putString(paramContext, "device_id", paramString);
	  }

	  public String getGenerateDeviceId(Context paramContext)
	  {
	    return getString(paramContext, "device_id", null);
	  }

	  public void setGenerateDeviceCUID(Context paramContext, String paramString)
	  {
	    String str = getString(paramContext, "cuid", null);
	    if (str != null)
	      removeString(paramContext, "cuid");
	    putString(paramContext, "cuidsec", paramString);
	  }

	  public String getGenerateDeviceCUID(Context paramContext)
	  {
	    return getString(paramContext, "cuidsec", null);
	  }

	  public void setAppChannel(Context paramContext, String paramString)
	  {
	    putString(paramContext, "appchannel", paramString);
	  }

	  public String getAppChannel(Context paramContext)
	  {
	    return getString(paramContext, "appchannel", null);
	  }

	  public void setAppKey(Context paramContext, String paramString)
	  {
	    putString(paramContext, "setappkey", paramString);
	  }

	  public String getAppKey(Context paramContext)
	  {
	    return getString(paramContext, "setappkey", null);
	  }

	  public void setAppDeviceMac(Context paramContext, String paramString)
	  {
	    putString(paramContext, "sdkmacss", paramString);
	  }

	  public String getAppDeviceMac(Context paramContext)
	  {
	    return getString(paramContext, "dkmacss", null);
	  }

	  public void setLastData(Context paramContext, String paramString)
	  {
	    putString(paramContext, "lastdata", paramString);
	  }

	  public String getLastData(Context paramContext)
	  {
	    return getString(paramContext, "lastdata", null);
	  }

}
