package com.xiaomi.mobilestats.data;

import android.text.TextUtils;

public class DataCore {
	
	  private static DataCore instancei = new DataCore();

	  public static DataCore getInstance()
	  {
	    return instancei;
	  }
	
	  public void setAppKey(String paramString)
	  {
	    CooperService.getMoblieInfo().appKey = paramString;
	  }

	  public void setAppChannel(String paramString)
	  {
	    if (TextUtils.isEmpty(paramString)){
	    	
	    }else{
	    	CooperService.getMoblieInfo().appChannel = paramString;
	    }
	  }
	  
}
