package com.xiaomi.mobilestats.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.xiaomi.mobilestats.StatService;
import com.xiaomi.mobilestats.controller.LogController;
import com.xiaomi.mobilestats.data.DataCore;
import com.xiaomi.mobilestats.data.SendStrategyEnum;
import com.xiaomi.mobilestats.object.Msg;

public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CrashHandler";
	private static CrashHandler mCrashHandler;
	private Context mContext;
	private String throwableInfo;
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
		new Thread() {
			@Override
			public void run() {
				super.run();
				Looper.prepare();
				
				throwableInfo = getThrowableInfo(throwable);
				appkey = DataCore.getAppkey(mContext);
				os_version = CommonUtil.getOsVersion(mContext);
				
				JSONObject errorJSONObject = getErrorInfoJSONString(mContext);
				String encodeInfo = StringUtils.encodeJSONData(errorJSONObject);
				
				if (LogController.geInstance().sendStragegy.equals(SendStrategyEnum.REAL_TIME) && CommonUtil.isNetworkAvailable(mContext) && !NetType.isNet2G_DOWN(mContext)) {
					if (!TextUtils.isEmpty(throwableInfo)) {
						Msg msg = NetworkUtil.post(CommonConfig.PREURL,encodeInfo);
						if (!msg.isFlag()) {
							StatService.saveInfoToFile("crash", encodeInfo,mContext);
						}
					}
				} else {
					StatService.saveInfoToFile("crash", encodeInfo, mContext);
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
			errorInfo.put("type", "crash");
			errorInfo.put("sessionId", DataCore.getCurrentSessionId(context));
			errorInfo.put("throwable", throwableInfo);
			errorInfo.put("time", System.currentTimeMillis());
			errorInfo.put("version", DataCore.getAppVersionName(context));
			errorInfo.put("appkey", appkey);
			errorInfo.put("appchannel", DataCore.getAppChannel(context));
			errorInfo.put("os_version", os_version);
			errorInfo.put("deviceid", DataCore.getDeviceId(context));
			
			HashMap<String,Object> initMap = StatService.getInstance().getInitMap();
			if(initMap != null){
				for (Map.Entry<String, Object> entry : initMap.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					errorInfo.put(key, value);
				}
			}
			
			CommonUtil.printLog(TAG, errorInfo.toString());
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
	
}
