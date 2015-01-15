package com.xiaomi.mobilestats.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class BasicStoreToolsBase {
	  private static final String TAG = "BasicStoreToolsBase";
	  private static final String XM_STAT_SEND_REM = "__XiaoMi_Stat_SendRem";
	  
	  private SharedPreferences sp;
	  private SharedPreferences sp_default;

	  private SharedPreferences getXMSendRemSP(Context paramContext)
	  {
	    if (this.sp == null)
	      this.sp = paramContext.getSharedPreferences(XM_STAT_SEND_REM, 0);
	    return this.sp;
	  }

	  private SharedPreferences getDefaultSP(Context paramContext)
	  {
	    if (this.sp_default == null)
	      this.sp_default = PreferenceManager.getDefaultSharedPreferences(paramContext);
	    return this.sp_default;
	  }

	  public String getSharedString(Context paramContext, String paramString1, String paramString2)
	  {
	    return getDefaultSP(paramContext).getString(paramString1, paramString2);
	  }

	  public void putSharedString(Context paramContext, String paramString1, String paramString2)
	  {
	    getDefaultSP(paramContext).edit().putString(paramString1, paramString2).commit();
	  }

	  public int getSharedInt(Context paramContext, String paramString, int paramInt)
	  {
	    return getDefaultSP(paramContext).getInt(paramString, paramInt);
	  }

	  public void putSharedInt(Context paramContext, String paramString, int paramInt)
	  {
	    getDefaultSP(paramContext).edit().putInt(paramString, paramInt).commit();
	  }

	  public long getSharedLong(Context paramContext, String paramString, long paramLong)
	  {
	    return getDefaultSP(paramContext).getLong(paramString, paramLong);
	  }

	  public void putSharedLong(Context paramContext, String paramString, long paramLong)
	  {
	    getDefaultSP(paramContext).edit().putLong(paramString, paramLong).commit();
	  }

	  public boolean getSharedBoolean(Context paramContext, String paramString, boolean paramBoolean)
	  {
	    return getDefaultSP(paramContext).getBoolean(paramString, paramBoolean);
	  }

	  public void putSharedBoolean(Context paramContext, String paramString, boolean paramBoolean)
	  {
	    getDefaultSP(paramContext).edit().putBoolean(paramString, paramBoolean).commit();
	  }

	  public void removeShare(Context paramContext, String paramString)
	  {
	    getDefaultSP(paramContext).edit().remove(paramString).commit();
	  }

	  public boolean getBoolean(Context paramContext, String paramString, boolean paramBoolean)
	  {
	    return getXMSendRemSP(paramContext).getBoolean(paramString, paramBoolean);
	  }

	  public void putBoolean(Context paramContext, String paramString, boolean paramBoolean)
	  {
	    getXMSendRemSP(paramContext).edit().putBoolean(paramString, paramBoolean).commit();
	  }

	  public int getInt(Context paramContext, String paramString, int paramInt)
	  {
	    return getXMSendRemSP(paramContext).getInt(paramString, paramInt);
	  }

	  public void putInt(Context paramContext, String paramString, int paramInt)
	  {
	    getXMSendRemSP(paramContext).edit().putInt(paramString, paramInt).commit();
	  }

	  public Float getFloatt(Context paramContext, String paramString, int paramInt)
	  {
	    return Float.valueOf(getXMSendRemSP(paramContext).getFloat(paramString, paramInt));
	  }

	  public void putFloat(Context paramContext, String paramString, Float paramFloat)
	  {
	    getXMSendRemSP(paramContext).edit().putFloat(paramString, paramFloat.floatValue()).commit();
	  }

	  public long getLong(Context paramContext, String paramString, long paramLong)
	  {
	    return getXMSendRemSP(paramContext).getLong(paramString, paramLong);
	  }

	  public void putLong(Context paramContext, String paramString, long paramLong)
	  {
	    getXMSendRemSP(paramContext).edit().putLong(paramString, paramLong).commit();
	  }

	  public String getString(Context paramContext, String paramString1, String paramString2)
	  {
	    return getXMSendRemSP(paramContext).getString(paramString1, paramString2);
	  }

	  public void putString(Context paramContext, String paramString1, String paramString2)
	  {
	    getXMSendRemSP(paramContext).edit().putString(paramString1, paramString2).commit();
	  }

	  public void removeString(Context paramContext, String paramString)
	  {
	    getXMSendRemSP(paramContext).edit().remove(paramString).commit();
	  }

	  public boolean updateShareInt(Intent paramIntent, Activity paramActivity, String paramString, int paramInt)
	  {
	    if (paramIntent != null)
	    {
	      int i = paramIntent.getIntExtra(paramString, paramInt);
	      if (i != getSharedInt(paramActivity, paramString, paramInt))
	      {
	        putSharedInt(paramActivity, paramString, i);
	        return true;
	      }
	    }
	    return false;
	  }

	  public boolean updateShareBoolean(Intent paramIntent, Activity paramActivity, String paramString)
	  {
	    return updateShareBoolean(paramIntent, paramActivity, paramString, true);
	  }

	  public boolean updateShareBoolean(Intent paramIntent, Activity paramActivity, String paramString, boolean paramBoolean)
	  {
	    if (paramIntent != null)
	    {
	      boolean bool = paramIntent.getBooleanExtra(paramString, paramBoolean);
	      if (bool != getSharedBoolean(paramActivity, paramString, paramBoolean))
	      {
	        putSharedBoolean(paramActivity, paramString, bool);
	        return true;
	      }
	    }
	    return false;
	  }

	  public boolean updateShareString(Intent paramIntent, Activity paramActivity, String paramString)
	  {
	    if (paramIntent != null)
	    {
	      String str = paramIntent.getStringExtra(paramString);
	      if (str != null)
	      {
	        str = str.trim();
	        if (str.length() == 0)
	          str = null;
	      }
	      if (!TextUtils.equals(str, getSharedString(paramActivity, paramString, null)))
	      {
	        putSharedString(paramActivity, paramString, str);
	        return true;
	      }
	    }
	    return false;
	  }
}
