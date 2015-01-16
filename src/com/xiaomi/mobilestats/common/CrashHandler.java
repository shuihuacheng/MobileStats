package com.xiaomi.mobilestats.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.mobilestats.XMAgent;
import com.xiaomi.mobilestats.data.SendStrategyEnum;
import com.xiaomi.mobilestats.object.Msg;

public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CrashHandler";
	private static final String CACHE_EXCEPTION_DIR = "__local_except_cache.json";     
	private static CrashHandler mCrashHandler;
	private Context mContext;
	private Object stacktrace;
	private String activities;
	private String time;
	private String appkey;
	private String os_version;

	private CrashHandler() {
		
	}
   
	public static synchronized CrashHandler getInstance() {
		if (mCrashHandler != null) {
			return mCrashHandler;
		} else {
			mCrashHandler = new CrashHandler();
			return mCrashHandler;
		}
	}

	public void init(Context context) {
		this.mContext = context;
	}

	@Override
	public void uncaughtException(Thread thread, final Throwable throwable) {
		Log.d(TAG, "uncaughtException:"+" thread:"+thread.getName());
		new Thread() {
			@Override
			public void run() {
				super.run();
				Looper.prepare();
				
				String throwableInfo = getThrowableInfo(throwable);
				String[] ss = throwableInfo.split("\n\t");
				String headstring = ss[0] + "\n\t" + ss[1] + "\n\t" + ss[2]	+ "\n\t";
				String newErrorInfoString = headstring + throwableInfo;
				stacktrace = newErrorInfoString;
				activities = CommonUtil.getActivityName(mContext);
				time = CommonUtil.getTime();
				appkey = CommonUtil.getAppKey(mContext);
				os_version = CommonUtil.getOsVersion(mContext);
				JSONObject errorJSONObject = getErrorInfoJSONString(mContext);
				
				JSONArray localJSONArray = getCacheExceptions(mContext);
				if(localJSONArray == null){
					localJSONArray = new JSONArray();
				}
				localJSONArray.put(errorJSONObject);
				
				if (CommonUtil.getSendStragegy(mContext).equals(SendStrategyEnum.APP_START) && CommonUtil.isNetworkAvailable(mContext)) {
					if (!TextUtils.isEmpty(stacktrace.toString())) {
						Msg msg = NetworkUtil.post(CommonConfig.PREURL + CommonConfig.errorUrl,errorJSONObject.toString());
						if (!msg.isFlag()) {
							XMAgent.saveInfoToFile("errorInfo", errorJSONObject,mContext);
						}
					}
				} else {
					XMAgent.saveInfoToFile("errorInfo", errorJSONObject, mContext);
				}
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				}, 300);
				Looper.loop();
			}
		}.start();
	}
	
	/**
	 * 将错误信息及设备相关信息放进JSONObject里
	 * @param context
	 * @return
	 */
	private JSONObject getErrorInfoJSONString(Context context) {
		JSONObject errorInfo = new JSONObject();
		try {
			errorInfo.put("stacktrace", stacktrace);
			errorInfo.put("time", time);
			errorInfo.put("version", CommonUtil.getVersionName(context));
			errorInfo.put("activity", activities);
			errorInfo.put("appkey", appkey);
			errorInfo.put("os_version", os_version);
			errorInfo.put("deviceid", CommonUtil.getDeviceName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return errorInfo;
	}

	/**
	 * 获取错误信息
	 * @param throwable
	 * @return
	 */
	private String getThrowableInfo(Throwable throwable) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		throwable.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}
	
	/**
	 * 获取缓存的异常信息放进JSONArray里
	 * @param paramContext
	 * @return
	 */
	protected JSONArray getCacheExceptions(Context paramContext)
	  {
	    if (paramContext == null)
	      return null;
	    JSONArray localJSONArray = null;
	    File localFile = new File(paramContext.getFilesDir(), CACHE_EXCEPTION_DIR);
	    try
	    {
	      if (!localFile.exists())
	        return null;
	      FileInputStream localFileInputStream = paramContext.openFileInput(CACHE_EXCEPTION_DIR);
	      StringBuffer localStringBuffer = new StringBuffer();
	      byte[] arrayOfByte = new byte[1024];
	      int i = 0;
	      while ((i = localFileInputStream.read(arrayOfByte)) != -1)
	        localStringBuffer.append(new String(arrayOfByte, 0, i));
	      localFileInputStream.close();
	      if (localStringBuffer.length() != 0)
	        localJSONArray = new JSONArray(localStringBuffer.toString());
	    } catch (Exception localException1) {
	    	Log.e(TAG, localException1.toString());
	    	//TODO 异常信息处理
	    }
	    try
	    {
	      localFile.delete();
	    } catch (Exception localException2) {
	    	Log.e(TAG, localException2.toString());
	    	//TODO 异常信息处理
	    }
	    return localJSONArray;
	  }

}
