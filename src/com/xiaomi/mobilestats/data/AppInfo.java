package com.xiaomi.mobilestats.data;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class AppInfo {
	
	  public static String a(Context paramContext, String paramString)
	  {
	    String str1 = "";
	    PackageManager localPackageManager = paramContext.getPackageManager();
	    ApplicationInfo localApplicationInfo;
	    String str2;
	    try
	    {
	      localApplicationInfo = localPackageManager.getApplicationInfo(paramContext.getPackageName(), 128);
	    }  catch (PackageManager.NameNotFoundException localNameNotFoundException){
	      if (paramString == "XM_STAT_ID")
	      {
	        str2 = "不能在manifest.xml中找到APP Key||can't find app key in manifest.xml.";
	        au.c(str2);
	      }
	      return str1;
	    }
	    if (localApplicationInfo != null)
	    {
	      Object localObject = null;
	      if (localApplicationInfo.metaData != null)
	        localObject = localApplicationInfo.metaData.get(paramString);
	      if (localObject == null)
	      {
	        au.a("StatSDK", "null,can't find information for key:" + paramString);
	        if (paramString == "XM_STAT_ID")
	        {
	          str2 = "不能在manifest.xml中找到APP Key||can't find app key in manifest.xml.";
	          au.c(str2);
	        }
	        return str1;
	      }
	      str1 = localObject.toString();
	      if ((str1.trim().equals("")) && (paramString == "XM_STAT_ID"))
	      {
	        str2 = "APP Key值为空||The value of APP Key is empty.";
	        au.c(str2);
	      }
	    }
	    return str1;
	  }
	  
	  /**
	   * 获取显示度量
	   * @param paramContext
	   * @return
	   */
	  public static DisplayMetrics getDisplayMetrics(Context paramContext)
	  {
	    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
	    ((WindowManager)paramContext.getApplicationContext().getSystemService("window")).getDefaultDisplay().getMetrics(localDisplayMetrics);
	    return localDisplayMetrics;
	  }
	  
	  public static int getDisplayWidth(Context paramContext)
	  {
	    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
	    try
	    {
	      localDisplayMetrics =getDisplayMetrics(paramContext);
	    }catch (Exception localException)
	    {
	      au.a("createAdReqURL", localException);
	    }
	    int i = localDisplayMetrics.widthPixels;
	    return i;
	  }

	  public static int getDisplayHeight(Context paramContext)
	  {
	    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
	    try
	    {
	      localDisplayMetrics = getDisplayMetrics(paramContext);
	    }
	    catch (Exception localException)
	    {
	      au.a("createAdReqURL", localException);
	    }
	    int i = localDisplayMetrics.heightPixels;
	    return i;
	  }
	  
	  /**
	   * 获取VersionCode
	   * @param paramContext
	   * @return
	   */
	  public static int getVersionCode(Context paramContext)
	  {
	    PackageManager localPackageManager = paramContext.getPackageManager();
	    String str = paramContext.getPackageName();
	    try
	    {
	      PackageInfo localPackageInfo = localPackageManager.getPackageInfo(str, 0);
	      return localPackageInfo.versionCode;
	    }
	    catch (PackageManager.NameNotFoundException localNameNotFoundException)
	    {
	      au.c(new Object[] { "sdkstat", "get app version code exception" });
	    }
	    return 1;
	  }
	  
	  public static String d(Context paramContext)
	  {
	    PackageManager localPackageManager = paramContext.getPackageManager();
	    String str = paramContext.getPackageName();
	    try
	    {
	      PackageInfo localPackageInfo = localPackageManager.getPackageInfo(str, 0);
	      return localPackageInfo.versionName;
	    }
	    catch (PackageManager.NameNotFoundException localNameNotFoundException)
	    {
	      au.c(new Object[] { "sdkstat", "get app version name exception" });
	    }
	    return "";
	  }
}
