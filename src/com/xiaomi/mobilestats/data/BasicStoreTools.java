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
	  public static final String APP_SET_APPKEY = "mjsetappkey";
	  public static final String APP_MAC_ADDRESS = "mtjsdkmacss";
	  public static final String APP_MAC_ADDRESS_TV = "mtjsdkmacsstv";
	  public static final String APP_LAST_SENDDATA = "lastdata";
	  public static final String APP_FOR_TV = "mtjtv";
	  
	  static BasicStoreTools instance = new BasicStoreTools();

	  public static BasicStoreTools getInstance()
	  {
	    return instance;
	  }
	  
	  protected void setExceptionTurn(Context paramContext, boolean paramBoolean)
	  {
	    putBoolean(paramContext, "exceptionanalysisflag", paramBoolean);
	  }

	  protected boolean getExceptionTurn(Context paramContext)
	  {
	    return getBoolean(paramContext, "exceptionanalysisflag", false);
	  }

	  protected void setExceptionHeadTag(Context paramContext, String paramString)
	  {
	    putString(paramContext, "exceptionanalysistag", paramString);
	  }

	  protected String getExceptionHeadTag(Context paramContext)
	  {
	    return getString(paramContext, "exceptionanalysistag", null);
	  }

	  protected void setSendStrategy(Context paramContext, int paramInt)
	  {
	    putInt(paramContext, "sendLogtype", paramInt);
	  }

	  protected int getSendStrategy(Context paramContext)
	  {
	    return getInt(paramContext, "sendLogtype", 0);
	  }

	  protected void setSendStrategyTime(Context paramContext, int paramInt)
	  {
	    putInt(paramContext, "timeinterval", paramInt);
	  }

	  protected int getSendStrategyTime(Context paramContext)
	  {
	    return getInt(paramContext, "timeinterval", 1);
	  }

	  protected void setOnlyWifi(Context paramContext, boolean paramBoolean)
	  {
	    putBoolean(paramContext, "onlywifi", paramBoolean);
	  }

	  protected boolean getOnlyWifiChannel(Context paramContext)
	  {
	    return getBoolean(paramContext, "onlywifi", false);
	  }

	  protected void setLastSendTime(Context paramContext, long paramLong)
	  {
	    putLong(paramContext, "lastsendtime", paramLong);
	  }

	  protected long getLastSendTime(Context paramContext)
	  {
	    return getLong(paramContext, "lastsendtime", 0L);
	  }

	  protected void setGenerateDeviceId(Context paramContext, String paramString)
	  {
	    putString(paramContext, "device_id", paramString);
	  }

	  protected String getGenerateDeviceId(Context paramContext)
	  {
	    return getString(paramContext, "device_id", null);
	  }

	  protected void setGenerateDeviceCUID(Context paramContext, String paramString)
	  {
	    String str = getString(paramContext, "cuid", null);
	    if (str != null)
	      removeString(paramContext, "cuid");
	    putString(paramContext, "cuidsec", paramString);
	  }

	  protected String getGenerateDeviceCUID(Context paramContext)
	  {
	    return getString(paramContext, "cuidsec", null);
	  }

	  protected void setAppChannelWithPreference(Context paramContext, String paramString)
	  {
	    putString(paramContext, "setchannelwithcodevalue", paramString);
	  }

	  protected String getAppChannelWithPreference(Context paramContext)
	  {
	    return getString(paramContext, "setchannelwithcodevalue", null);
	  }

	  protected void setAppChannelWithCode(Context paramContext, boolean paramBoolean)
	  {
	    putBoolean(paramContext, "setchannelwithcode", paramBoolean);
	  }

	  protected boolean getAppChannelWithCode(Context paramContext)
	  {
	    return getBoolean(paramContext, "setchannelwithcode", false);
	  }

	  protected void setAppKey(Context paramContext, String paramString)
	  {
	    putString(paramContext, "mjsetappkey", paramString);
	  }

	  protected String getAppKey(Context paramContext)
	  {
	    return getString(paramContext, "mjsetappkey", null);
	  }

	  protected void setAppDeviceMac(Context paramContext, String paramString)
	  {
	    putString(paramContext, "mtjsdkmacss", paramString);
	  }

	  protected String getAppDeviceMac(Context paramContext)
	  {
	    return getString(paramContext, "mtjsdkmacss", null);
	  }

	  protected void setLastData(Context paramContext, String paramString)
	  {
	    putString(paramContext, "lastdata", paramString);
	  }

	  protected String getLastData(Context paramContext)
	  {
	    return getString(paramContext, "lastdata", null);
	  }

	  protected void setForTV(Context paramContext, boolean paramBoolean)
	  {
	    putBoolean(paramContext, "mtjtv", paramBoolean);
	  }

	  protected boolean getForTV(Context paramContext)
	  {
	    return getBoolean(paramContext, "mtjtv", false);
	  }

	  protected void setAppDeviceMacTv(Context paramContext, String paramString)
	  {
	    putString(paramContext, "mtjsdkmacsstv", paramString);
	  }

	  protected String getAppDeviceMacTv(Context paramContext)
	  {
	    return getString(paramContext, "mtjsdkmacsstv", null);
	  }
}
