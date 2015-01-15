package com.xiaomi.mobilestats.data;

import android.content.Context;

public class DataCore {
	
	  private static DataCore instancei = new DataCore();

	  public static DataCore getInstance()
	  {
	    return instancei;
	  }
	
	  public void setAppKey(String paramString)
	  {
	    CooperService.a().d = paramString;
	  }

	  public void setAppChannel(String paramString)
	  {
	    if ((paramString == null) || (paramString.equals("")))
	      au.c(new Object[] { "sdkstat", "设置的渠道不能为空或者为null || The channel that you have been set is null or empty, please check it." });
	    CooperService.a().k = paramString;
	  }

	  public void setAppChannel(Context paramContext, String paramString, boolean paramBoolean)
	  {
	    if ((paramString == null) || (paramString.equals("")))
	      au.c(new Object[] { "sdkstat", "设置的渠道不能为空或者为null || The channel that you have been set is null or empty, please check it." });
	    CooperService.a().k = paramString;
	    if ((paramBoolean) && (paramString != null) && (!paramString.equals("")))
	    {
	      BasicStoreTools.getInstance().setAppChannelWithPreference(paramContext, paramString);
	      BasicStoreTools.getInstance().setAppChannelWithCode(paramContext, true);
	    }
	    if (!paramBoolean)
	    {
	      BasicStoreTools.getInstance().setAppChannelWithPreference(paramContext, "");
	      BasicStoreTools.getInstance().setAppChannelWithCode(paramContext, false);
	    }
	  }
	  
}
