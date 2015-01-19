package com.xiaomi.mobilestats;

import java.io.File;
import java.text.ParseException;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.xiaomi.mobilestats.common.CommonConfig;
import com.xiaomi.mobilestats.common.CommonUtil;
import com.xiaomi.mobilestats.common.CrashHandler;
import com.xiaomi.mobilestats.common.NetworkUtil;
import com.xiaomi.mobilestats.common.StringUtils;
import com.xiaomi.mobilestats.controller.LogController;
import com.xiaomi.mobilestats.data.BasicStoreTools;
import com.xiaomi.mobilestats.data.DataCore;
import com.xiaomi.mobilestats.data.SendStrategyEnum;
import com.xiaomi.mobilestats.data.WriteFileThread;
import com.xiaomi.mobilestats.object.GSMCell;
import com.xiaomi.mobilestats.object.LatitudeAndLongitude;
import com.xiaomi.mobilestats.object.Msg;

public class XMAgent {
	private static final String TAG = "XMAgent";
	private static boolean mUseLocationService = true;
    private static long start = 0;
    private static long end = 0;//
    private static String start_millis = null;// The start time point
    private static String end_millis = null;// The end time point
    private static String duration = null;// run time
    private static String session_id = null;
    private static String activities = null;// currnet activity's name
    private static String time = null; // error time
    private static String pageName  = "";
    private static boolean isFirst = true;     //是否是第一次登录

   private static HandlerThread handlerThread = new HandlerThread("XMAgent");
   
    
    private static boolean isPostFile = true;
    private static XMAgent instance = new XMAgent();
    
    public static XMAgent getInstance(){
    	return instance;
    }
    
    private XMAgent(){
    	handlerThread.start();
//    	XMAgent.handler = new Handler(handlerThread.getLooper());
    }
    
    public static final int MSG_ALARM_ACTION = 0x1000;
    public static Handler handler  = new Handler(handlerThread.getLooper()){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case MSG_ALARM_ACTION:
				CommonUtil.printLog(TAG, "MSG_ALARM_ACTION");
				
				break;
				default:
					break;
			}
		}
    	
    };
	
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
	            	postClientDatas(context);
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
	  public static synchronized void  onPageStart(final Context context, final String pageName){
	        Runnable postOnPageStartInfoRunnable = new Runnable() {

	            @Override
	            public void run() {
	            	postClientDatas(context);
	     	       postOnPageStartInfo(context,pageName);
	            }
	        };
	        handler.post(postOnPageStartInfoRunnable);
	  } 
	  


	/**
	   *  用于统计单个Activity页面结束时间
	   *  嵌入位置：Fragment的onPause()函数中 activity的onPause()函数中或者自定义页面的结束函数中 
	   * @param context
	   * @param pageName
	   */
	  public static synchronized void onPageEnd(final Context context, final String pageName) {
	        Runnable postOnPageEndInfoRunnable = new Runnable() {

	            @Override
	            public void run() {
	                postOnPageEndInfo(context,pageName);
	            }
	        };
	        handler.post(postOnPageEndInfoRunnable);
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
	  public static void setAppKey(Context context,String appkeyValue){
		  DataCore.getMoblieInfo().appKey  = appkeyValue;
		  BasicStoreTools.getInstance().setAppKey(context, appkeyValue);
	  }
	  
	  /**
	   * 设置App Channel（发布渠道的推荐方法，可以有效防止代码设置的渠道丢失的问题）,该函数设置channel同时会保存该渠道值，
	   * 并且发送日志以该设置为主，不会发生意外丢失的情况， 若设置saveChannelWithCode为false，那么sdk不会保存该channel
	   * @param context
	   * @param appChannel
	   */
	  public void setAppChannel(Context context, String appChannel){
		  DataCore.getMoblieInfo().appChannel = appChannel;
		  BasicStoreTools.getInstance().setAppChannel(context, appChannel);
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
		  LogController.geInstance().setSendDelayedTime(seconds);
	  }
	  
	  /**
	   * 设置日志发送策略
	   * @param context
	   * @param sst
	   * @param rtime_interval
	   * @param only_wifi:是否只在wifi网络下发送
	   */
	  public static void setSendLogStrategy(Context context, SendStrategyEnum sst, int rtime_interval, boolean only_wifi){
		 LogController.geInstance().setSendStrategy(context, sst, rtime_interval, only_wifi);
	  } 
	  
	  /**
	   * 设置Session超时的秒数
	   * 单位为秒，大小为1到600之间，默认为30
	   * @param seconds
	   */
	  public static void setSessionTimeOut(int seconds){
		  
	  }
	  
	  /****************  待优化方法*************/
	    private static void postOnResumeInfo(Context context) {
//	        if (!CommonUtil.isNetworkAvailable(context) && LogController.geInstance().sendStragegy.equals(SendStrategyEnum.APP_START)) {
//	           
//	        } else {
//	            if (XMAgent.isPostFile) {
//	                Thread thread = new ReadFromFileThead(filePath);
//	                thread.start();
//	                XMAgent.isPostFile = false;
//	            }
//	        }
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
	        
	        JSONObject info = getActivityJSONObject(context);
	        
	        if (CommonUtil.isNetworkAvailable(context) &&  LogController.geInstance().sendStragegy.equals(SendStrategyEnum.APP_START)) {
	            Msg msg = NetworkUtil.post(CommonConfig.PREURL + CommonConfig.activityUrl, info.toString());
	            if (!msg.isFlag()) {
	            	saveInfoToFile("activityInfo", info, context);
	            }
	        } else {
	            	saveInfoToFile("activityInfo", info, context);
	        }
	    }
	    
		  private static void postOnPageStartInfo(Context context, String pageName) {
//		        if (!CommonUtil.isNetworkAvailable(context)) {
//			           
//		        } else {
//		            if (XMAgent.isPostFile) {
//		                Thread thread = new ReadFromFileThead(filePath);
//		                thread.start();
//		                XMAgent.isPostFile = false;
//		            }
//		        }
		        isCreateNewSessionID(context);

		        activities = CommonUtil.getActivityName(context);
		        XMAgent.pageName = pageName;
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
		  
		    private static void postOnPageEndInfo(Context context,String pageName) {
		        saveSessionTime(context);
		        end_millis = CommonUtil.getTime();
		        end = Long.valueOf(System.currentTimeMillis());
		        duration = end - start + "";
		        XMAgent.pageName = pageName;
		        
		        JSONObject info = getPageJSONObject(context);
		        
		        if (LogController.geInstance().sendStragegy.equals(SendStrategyEnum.APP_START)  && CommonUtil.isNetworkAvailable(context)) {
		        	if(LogController.isOnlyWifi && !CommonUtil.isWiFiActive(context)){
		        		saveInfoToFile("activityInfo", info, context);
		        	}else{
		        		Msg msg = NetworkUtil.post(CommonConfig.PREURL + CommonConfig.activityUrl, info.toString());
		        		if (!msg.isFlag()) {
		        			saveInfoToFile("activityInfo", info, context);
		        		}
		        	}
		        } else {
		            	saveInfoToFile("activityInfo", info, context);
		        }
		    }
	    
	    private static void postOnEventInfo(Context context,String eventId,String label) {
	        JSONObject info = getEventJSONObject(context,eventId,label);
	        
	        if (LogController.geInstance().sendStragegy.equals(SendStrategyEnum.APP_START)  && CommonUtil.isNetworkAvailable(context)) {
	        	if(LogController.isOnlyWifi && !CommonUtil.isWiFiActive(context)){
	        		saveInfoToFile("eventInfo", info, context);
	        	}else{
		            Msg msg = NetworkUtil.post(CommonConfig.PREURL + CommonConfig.eventUrl, info.toString());
		            if (!msg.isFlag()) {
		            	saveInfoToFile("eventInfo", info, context);
		            }
	        	}
	        } else {
	            	saveInfoToFile("eventInfo", info, context);
	        }
	    }
	    
	    /**
	     * 上报客户端各种参数,只在应用启动后上传一次
	     * @param context
	     */
        private static void postClientDatas(Context context) {
            if (isFirst) {
                JSONObject clientData = getClientDataJSONObject(context);

    	        if (LogController.geInstance().sendStragegy.equals(SendStrategyEnum.APP_START)  && CommonUtil.isNetworkAvailable(context)) {
    	        	if(LogController.isOnlyWifi && !CommonUtil.isWiFiActive(context)){
    	        		  saveInfoToFile("clientData", clientData, context);
    	        	}else{
                    Msg msg =  NetworkUtil.post(CommonConfig.PREURL  + CommonConfig.clientDataUrl, clientData.toString());
	                    if (!msg.isFlag()) {
	                        saveInfoToFile("clientData", clientData, context);
	                    }
    	        	}
                } else {
                    saveInfoToFile("clientData", clientData, context);
                }
                isFirst = false;
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
	    	String packageName = context.getPackageName();
	    	String cacheDir = LogController.baseFilePath+packageName+File.separator+"cache"+File.separator;
	    	File uploadDir = new File(cacheDir+"upload");
	    	File operatorDir = new File(cacheDir+"operator");
	    	if(!uploadDir.exists()) uploadDir.mkdirs();
	    	if(!operatorDir.exists()) operatorDir.mkdirs();
	    	
	    	String cachePath = LogController.baseFilePath+packageName+File.separator+"cache"+File.separator+type+"_cache.json";
	        JSONArray jsonArray = new JSONArray();
	        try {
	            jsonArray.put(0, info);
	            if (handler != null) {
	                JSONObject jsonObject = new JSONObject();
	                jsonObject.put(type, jsonArray);
	                handler.post(new WriteFileThread(context,cachePath,jsonObject));
	            } else {
	                CommonUtil.printLog(CommonUtil.getActivityName(context), "handler--null");
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    /**
	     * 获取和拼接Activity页面上报参数
	     * @param context
	     * @return
	     */
	    private static JSONObject getActivityJSONObject(Context context) {
	        JSONObject info = new JSONObject();
	        try {
	        	info.put("appkey", DataCore.getAppkey(context));
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
	    
	    /**
	     * 获取和拼接自定义页面上报参数
	     * @param context
	     * @return
	     */
	    private static JSONObject getPageJSONObject(Context context) {
	        JSONObject info = new JSONObject();
	        try {
	        	info.put("appkey", DataCore.getAppkey(context));
	        	info.put("activities", activities);
	        	info.put("page_name", XMAgent.pageName);
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
	    
	    /**
	     * 获取和拼接自定义时间Json上报数据
	     * @param context
	     * @param eventId
	     * @param label
	     * @return
	     */
	    private static JSONObject getEventJSONObject(Context context,String eventId,String label) {
	        JSONObject info = new JSONObject();
	        try {
	        	info.put("appkey", DataCore.getAppkey(context));
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
	    
	    /**
	     * 获取和拼接用户设备详细信息
	     * @param context
	     * @return
	     */
	    private static JSONObject getClientDataJSONObject(Context context) {
	        TelephonyManager tm = (TelephonyManager) (context .getSystemService(Context.TELEPHONY_SERVICE));
	        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	        DisplayMetrics displaysMetrics = new DisplayMetrics();
	        manager.getDefaultDisplay().getMetrics(displaysMetrics);
	        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	        JSONObject clientData = new JSONObject();
	        try {
	            clientData.put("os_version",DataCore.getOSVersion());
	            clientData.put("platform", "android");
	            clientData.put("language", Locale.getDefault().getLanguage());
	            clientData.put("deviceid",DataCore.getDeviceId(tm, context));
	            clientData.put("appkey", DataCore.getAppkey(context));
	            clientData.put("resolution", displaysMetrics.widthPixels + "x"+ displaysMetrics.heightPixels);
	            clientData.put("ismobiledevice", true);
	            clientData.put("phonetype", tm.getPhoneType());//
	            clientData.put("imsi", tm.getSubscriberId());
	            clientData.put("network", CommonUtil.getNetworkTypeWIFI2G3G(context));
	            clientData.put("time", CommonUtil.getTime());
	            clientData.put("version", DataCore.getAppVersionCode(context));
//	            clientData.put("userId", CommonUtil.getUserIdentifier(context));  //从服务端获取的用户ID

	            GSMCell gsmCell = CommonUtil.getCellInfo(context);
	            clientData.put("mccmnc", gsmCell != null ? "" + gsmCell.MCCMNC : "");
	            clientData.put("cellid", gsmCell != null ? gsmCell.CID + "" : "");
	            clientData.put("lac", gsmCell != null ? gsmCell.LAC + "" : "");
	            clientData.put("modulename", Build.PRODUCT);
	            clientData.put("devicename", CommonUtil.getDeviceName());
	            clientData.put("wifimac", wifiManager.getConnectionInfo().getMacAddress());
	            clientData.put("havebt", adapter == null ? false : true);
	            clientData.put("havewifi", CommonUtil.isWiFiActive(context));
	            clientData.put("havegps", locationManager == null ? false : true);
	            clientData.put("havegravity", CommonUtil.isHaveGravity(context));

	            LatitudeAndLongitude coordinates = CommonUtil.getLatitudeAndLongitude(context,XMAgent.mUseLocationService);
	            clientData.put("latitude", coordinates.latitude);
	            clientData.put("longitude", coordinates.longitude);
	        } catch (JSONException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return clientData;
	    }
	    
}
