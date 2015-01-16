package com.xiaomi.mobilestats;

import java.io.File;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.xiaomi.mobilestats.common.CommonConfig;
import com.xiaomi.mobilestats.common.CommonUtil;
import com.xiaomi.mobilestats.common.CrashHandler;
import com.xiaomi.mobilestats.common.NetworkUtil;
import com.xiaomi.mobilestats.common.StringUtils;
import com.xiaomi.mobilestats.data.ReadFromFileThead;
import com.xiaomi.mobilestats.data.SendStrategyEnum;
import com.xiaomi.mobilestats.data.WriteFileThread;
import com.xiaomi.mobilestats.object.Msg;
import com.xiaomi.mobilestats.receiver.MAlarmReceiver;

public class XMAgent {
    private static boolean mUseLocationService = true;
    private static long start = 0;
    private static long end = 0;//
    private static String start_millis = null;// The start time point
    private static String end_millis = null;// The end time point
    private static String duration = null;// run time
    private static String session_id = null;
    private static String activities = null;// currnet activity's name
    private static String appkey = "";
    private static String stacktrace = null;// error info
    private static String time = null; // error time
    private static String os_version = null;
    private static String deviceID = null;

    private static String curVersion = null;// app version
    private static String packagename = null;// app packagename
    private static String sdk_version = null;// Sdk version
    private static boolean mUpdateOnlyWifi = true;
    private static SendStrategyEnum sendStrategy = SendStrategyEnum.APP_START;
    private static Handler handler;
    HandlerThread handlerThread = new HandlerThread("XMAgent");
    
    private static boolean isPostFile = true;
    private static boolean isFirst = true;
    private static XMAgent instance = new XMAgent();
    
    public static XMAgent getInstance(){
    	return instance;
    }
    
    private XMAgent(){
    	handlerThread.start();
    	XMAgent.handler = new Handler(handlerThread.getLooper());
    }
	
	 /**
	  *  用于统计单个Activity页面开始时间
	  *  嵌入位置：Activity的onResume()函数中
	  *  调用方式：StatService.onResume(this); 
	  * @param paramContext
	  */
	 public static synchronized void onResume(final Context context)
	  {
	        Runnable postOnResumeInfoRunnable = new Runnable() {

	            @Override
	            public void run() {
	                postOnResumeInfo(context);
	            }
	        };
	        handler.post(postOnResumeInfoRunnable);
	  }

	 /**
	  * 用于统计单个Activity页面结束时间
	  * 嵌入位置：Activity的onPause()函数中 
	  * 调用方式：StatService.onPause(this); 
	  * @param context
	  */
	  public static synchronized void onPause(final Context context){
	        Runnable postOnPauseinfoRunnable = new Runnable() {

	            @Override
	            public void run() {
	                postOnPauseInfo(context);
	            }
	        };
	        handler.post(postOnPauseinfoRunnable);
	  }
	  
	  /**
	   * 用于统计单个自定义页面的起始和onPageEnd同时使用，不可单独使用
	   * 嵌入位置：Fragment的onResume()函数中 activity的onResume()函数中或者自定义页面的起始函数中 
	   * @param context
	   * @param pageName
	   */
	  public static synchronized void  onPageStart(Context context, String pageName){
		  
	  } 
	  
	  /**
	   *  用于统计单个Activity页面结束时间
	   *  嵌入位置：Fragment的onPause()函数中 activity的onPause()函数中或者自定义页面的结束函数中 
	   * @param context
	   * @param pageName
	   */
	  public static synchronized void onPageEnd(Context context, String pageName) {
		  
	  }
	  
	  /**
	   * 用于统计自定义事件的发生次数
	   * 嵌入位置：任意，一般在开发者自定义事件(如点击事件等)的监听位置
	   * @param context
	   * @param event_id
	   * @param label
	   */
	  public static void onEvent(final Context context,final String eventId,final String label) {
	        Runnable onEventRunnable = new Runnable() {

	            @Override
	            public void run() {
	            	postOnEventInfo(context,eventId,label);
	            }
	        };
	        handler.post(onEventRunnable);
	  }
	  
	  /**
	   *  用于统计自定义事件的时长，此为开启计时的函数
	   * @param context
	   * @param event_id
	   * @param label
	   */
	  public static void onEventStart(Context context, String event_id, String label) {
		  
	  }
	  
	  /**
	   * 用于统计自定义事件的时长，此为结束计时的函数
	   * @param context
	   * @param event_id
	   * @param label
	   */
	  public static void onEventEnd(Context context, String event_id, String label){
		  
	  }
	  
	  /**
	   *  设置AppKey
	   *  该设置将覆盖AndroidManifest.xml中的XiaoMi_STAT_ID配置
	   * @param appKey
	   */
	  public static void setAppKey(String appkeyValue){
		  XMAgent.appkey = appkeyValue;
	  }
	  
	  /**
	   * 设置App Channel（发布渠道的推荐方法，可以有效防止代码设置的渠道丢失的问题）,该函数设置channel同时会保存该渠道值，
	   * 并且发送日志以该设置为主，不会发生意外丢失的情况， 若设置saveChannelWithCode为false，那么sdk不会保存该channel
	   * @param context
	   * @param appChannel
	   * @param saveChannelWithCode
	   */
	  public void setAppChannel(Context context, String appChannel, boolean saveChannelWithCode){
		  
	  }
	  
	  /**
	   * 用于调试使用的接口，发布时务必去除该调用，或者关闭该调用开关。
	   * @param value
	   */
	  public static void setDebugOn(boolean value) {
		  CommonConfig.DEBUG_MODE = value;
	  }
	  
	  /**
	   * 用于设定统计开关，当前版本仅支持EXCEPTION_LOG
	   * @param context
	   * @param isOn
	   */
	  public static void setExceptionOn(final Context context, boolean isOn){
		   if(isOn){
			   Runnable exceptionRunnable = new Runnable() {
				   @Override
				   public void run() {
					   CrashHandler handler = CrashHandler.getInstance();
					   handler.init(context.getApplicationContext());
					   Thread.setDefaultUncaughtExceptionHandler(handler);
				   }
			   };
			   handler.post(exceptionRunnable);
		   }
	  } 
	  
	  /**
	   * 设置启动时日志发送延时的秒数单位为秒，大小为0s到30s之间
	   * 注：请在StatService.setSendLogStrategy之前调用，否则设置不起作用 如果设置的是发送策略是启动时发送，那么这个参数就会在发送前检查您设置的这个参数，表示延迟多少S发送。
	   * @param seconds
	   */
	  public static void setLogSenderDelayed(int seconds){
		  
	  }
	  
	  /**
	   * 设置日志发送策略
	   * @param context
	   * @param sst
	   * @param rtime_interval
	   * @param only_wifi:是否只在wifi网络下发送
	   */
	  public static void setSendLogStrategy(Context context, SendStrategyEnum sst, int rtime_interval, boolean only_wifi){
		  //TODO 根据context和setSendLogStrategy()检测
		 XMAgent.sendStrategy = sst;
		 XMAgent.mUpdateOnlyWifi = only_wifi;
		 
		 AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		 Intent intent = new Intent(MAlarmReceiver.ALARM_ACTION);
		 PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		 alarm.setRepeating(AlarmManager.RTC_WAKEUP, CommonConfig.kContinueSessionMillis, rtime_interval*3600000, pendingIntent);
	  } 
	  
	  /**
	   * 设置Session超时的秒数
	   * 单位为秒，大小为1到600之间，默认为30
	   * @param seconds
	   */
	  public static void setSessionTimeOut(int seconds){
		  
	  }
	  
	  /****************  待优化方法*************/
	    private static String filePath = Environment.getExternalStorageDirectory()+File.separator+"__local_log_cache.json ";
	    private static void postOnResumeInfo(Context context) {
	        if (!CommonUtil.isNetworkAvailable(context)) {
	           
	        } else {
	            if (XMAgent.isPostFile) {
	                Thread thread = new ReadFromFileThead(filePath);
	                thread.start();
	                XMAgent.isPostFile = false;
	            }
	        }
	        isCreateNewSessionID(context);

	        activities = CommonUtil.getActivityName(context);
	        try {
	            if (session_id == null) {
	                generateSeesion(context);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        start_millis = CommonUtil.getTime();
	        start = Long.valueOf(System.currentTimeMillis());
	    }
	    
	    private static void postOnPauseInfo(Context context) {
	        saveSessionTime(context);
	        end_millis = CommonUtil.getTime();
	        end = Long.valueOf(System.currentTimeMillis());
	        duration = end - start + "";
	        appkey = CommonUtil.getAppKey(context);
	        
	        JSONObject info = getJSONObject(context);
	        
	        if (CommonUtil.isNetworkAvailable(context) && sendStrategy.equals(SendStrategyEnum.APP_START)) {
	            Msg msg = NetworkUtil.post(CommonConfig.PREURL + CommonConfig.activityUrl, info.toString());
	            if (!msg.isFlag()) {
	            	saveInfoToFile("activityInfo", info, context);
	            }
	        } else {
	            	saveInfoToFile("activityInfo", info, context);
	        }
	    }
	    
	    private static void postOnEventInfo(Context context,String eventId,String label) {
	        saveSessionTime(context);
	        appkey = CommonUtil.getAppKey(context);
	        
	        JSONObject info = getEventJSONObject(context,eventId,label);
	        
	        if (CommonUtil.isNetworkAvailable(context) && sendStrategy.equals(SendStrategyEnum.APP_START)) {
	            Msg msg = NetworkUtil.post(CommonConfig.PREURL + CommonConfig.eventUrl, info.toString());
	            if (!msg.isFlag()) {
	            	saveInfoToFile("eventInfo", info, context);
	            }
	        } else {
	            	saveInfoToFile("eventInfo", info, context);
	        }
	    }
	    
	    private static void isCreateNewSessionID(Context context) {
	        long currenttime = System.currentTimeMillis();

	        SharedPreferences preferences = context.getSharedPreferences("XM_session_ID_savetime", Context.MODE_PRIVATE);
	        long session_save_time = preferences.getLong("session_save_time", currenttime);
	        if (currenttime - session_save_time > CommonConfig.kContinueSessionMillis) {
	            try {
	                generateSeesion(context);
	            } catch (ParseException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    }
	    
	    private static String generateSeesion(Context context)
	            throws ParseException {
	        String sessionId = "";
	        String str = CommonUtil.getAppKey(context);
	        if (str != null) {
	            String localDate = CommonUtil.getTime();
	            str = str + localDate;
	            sessionId = StringUtils.md5(str);
	            SharedPreferences preferences = context.getSharedPreferences("XM_sessionID", Context.MODE_PRIVATE);
	            Editor edit = preferences.edit();
	            edit.putString("session_id", sessionId);
	            edit.commit();
	            saveSessionTime(context);
	            session_id = sessionId;
	            return sessionId;
	        }
	        return sessionId;
	    }
	    
	    private static void saveSessionTime(Context context) {
	        SharedPreferences preferences2sessiontime = context.getSharedPreferences("XM_session_ID_savetime", Context.MODE_PRIVATE);
	        Editor editor = preferences2sessiontime.edit();
	        long currenttime = System.currentTimeMillis();
	        editor.putLong("session_save_time", currenttime);
	        editor.commit();
	    }
	    
	    public static void saveInfoToFile(String type, JSONObject info, Context context) {
	        JSONArray jsonArray = new JSONArray();
	        try {
	            jsonArray.put(0, info);
	            if (handler != null) {
	                JSONObject jsonObject = new JSONObject();
	                jsonObject.put(type, jsonArray);
	                handler.post(new WriteFileThread(context,filePath,jsonObject));
	            } else {
	                CommonUtil.printLog(CommonUtil.getActivityName(context), "handler--null");
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    private static JSONObject getJSONObject(Context context) {
	        JSONObject info = new JSONObject();
	        try {
	        	info.put("appkey", appkey);
	        	info.put("activities", activities);
	            info.put("session_id", session_id);
	            info.put("start_millis", start_millis);
	            info.put("end_millis", end_millis);
	            info.put("duration", duration);
	            info.put("version", CommonUtil.getVersionName(context));
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
	        return info;
	    }
	    
	    private static JSONObject getEventJSONObject(Context context,String eventId,String label) {
	        JSONObject info = new JSONObject();
	        try {
	        	info.put("appkey", appkey);
	        	info.put("activities", activities);
	            info.put("session_id", session_id);
	            info.put("time", time);
	            info.put("event_id", eventId);
	            info.put("label", label);
	            info.put("version", CommonUtil.getVersionName(context));
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
	        return info;
	    }
	    
}
