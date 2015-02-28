package com.xiaomi.mobilestats;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.xiaomi.mobilestats.common.CommonConfig;
import com.xiaomi.mobilestats.common.CommonUtil;
import com.xiaomi.mobilestats.common.CrashHandler;
import com.xiaomi.mobilestats.common.EncodeJsonUtil;
import com.xiaomi.mobilestats.common.NetType;
import com.xiaomi.mobilestats.common.NetworkUtil;
import com.xiaomi.mobilestats.common.StringUtils;
import com.xiaomi.mobilestats.controller.LogController;
import com.xiaomi.mobilestats.data.BasicStoreTools;
import com.xiaomi.mobilestats.data.DataCore;
import com.xiaomi.mobilestats.data.SendStrategyEnum;
import com.xiaomi.mobilestats.data.WriteFileThread;
import com.xiaomi.mobilestats.object.Msg;
import com.xiaomi.mobilestats.object.PageItem;
import com.xiaomi.mobilestats.upload.UploadManager;

public class StatService {
	private static long startMillis;
	private static long endMillis;
	private static long duration;
	private static String session_id = null;

	private static boolean isEnable = true;

	private static WeakHashMap<Object, Object> cacheMap = new WeakHashMap<Object, Object>();
	private static HashMap<String, Object> initMap = new HashMap<String, Object>();

	private static HandlerThread handlerThread = new HandlerThread("XMAgent");

	private static StatService instance = new StatService();

	public static StatService getInstance() {
		return instance;
	}

	private StatService() {
		if (handlerThread != null) {
			handlerThread.start();
		}
	}

	// 该函数设置所有的操作是否全部不可用。
	public static void setEnable(boolean enable) {
		StatService.isEnable = enable;
		if (!isEnable) {
			LogController.deleteAllFloder();
		}

	}

	public static void initExtraMap(HashMap<String, Object> map) {
		if (!isEnable) {
			return;
		}
		if (map != null) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				initMap.put(key, value);
			}
		}
	}

	public HashMap<String, Object> getInitMap() {
		return initMap;
	}

	public static final int MSG_TIMER = 0x1000;
	public static Handler handler = new Handler(handlerThread.getLooper()) {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_TIMER:
				CommonUtil.printLog(CommonConfig.TAG, "MSG_TIMER");
				if (UploadManager.isHasCacheFile()) {
					UploadManager.uploadCachedUploadFiles(handler);
				}
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 用于统计单个Activity页面开始时间 嵌入位置：Activity的onResume()函数中
	 * 调用方式：StatService.onResume(this);
	 * 
	 * @param paramContext
	 */
	public static synchronized void onResume(final Context context) {
		if (!isEnable) {
			return;
		}
		Runnable postOnResumeInfoRunnable = new Runnable() {

			@Override
			public void run() {
				postOnResumeInfo(context);
			}
		};
		handler.post(postOnResumeInfoRunnable);
	}

	/**
	 * 用于统计单个Activity页面结束时间 嵌入位置：Activity的onPause()函数中
	 * 调用方式：StatService.onPause(this);
	 * 
	 * @param context
	 */
	public static synchronized void onPause(final Context context) {
		if (!isEnable) {
			return;
		}
		Runnable postOnPauseinfoRunnable = new Runnable() {

			@Override
			public void run() {
				postOnPauseInfo(context);
			}
		};
		handler.post(postOnPauseinfoRunnable);
	}

	/**
	 * 用于统计单个自定义页面的起始和onPageEnd同时使用，不可单独使用 嵌入位置：Fragment的onResume()函数中
	 * activity的onResume()函数中或者自定义页面的起始函数中
	 * 
	 * @param context
	 * @param object
	 *            为当前调用Activity/Fragment等对象,一般传this即可,不可为null
	 * @param pageName
	 *            用于显示统计页面名称
	 */
	public static synchronized void onPageStart(final Context context, final Object object, final String pageName) {
		if (!isEnable) {
			return;
		}
		Runnable postOnPageStartInfoRunnable = new Runnable() {

			@Override
			public void run() {
				postOnPageStartInfo(context, object, pageName);
			}
		};
		handler.post(postOnPageStartInfoRunnable);
	}

	/**
	 * 用于统计单个Activity页面结束时间 嵌入位置：Fragment的onPause()函数中
	 * activity的onPause()函数中或者自定义页面的结束函数中
	 * 
	 * @param context
	 *            ＊ @param object 为当前调用Activity/Fragment等对象,一般传this即可,不可为null
	 * @param pageName
	 */
	public static synchronized void onPageEnd(final Context context, final Object object, final String pageName) {
		if (!isEnable) {
			return;
		}
		Runnable postOnPageEndInfoRunnable = new Runnable() {

			@Override
			public void run() {
				postOnPageEndInfo(context, object, pageName);
			}
		};
		handler.post(postOnPageEndInfoRunnable);
	}

	/**
	 * 用于统计自定义事件的发生次数 嵌入位置：任意，一般在开发者自定义事件(如点击事件等)的监听位置
	 * 
	 * @param context
	 *            上下文对象
	 * @param event_id
	 *            自定义事件Id
	 * @param label
	 *            自定义事件label
	 */
	public static void onEvent(final Context context, final String eventId, final String label) {
		if (!isEnable) {
			return;
		}
		Runnable onEventRunnable = new Runnable() {

			@Override
			public void run() {
				postOnEventInfo(context, eventId, label);
			}
		};
		handler.post(onEventRunnable);
	}

	/**
	 * 用于统计自定义事件的时长，此为开启计时的函数
	 * 
	 * @param context
	 * @param eventId
	 * @param label
	 */
	private static void onEventStart(final Context context, final Object object, final String eventId, final String label) {

	}

	/**
	 * 用于统计自定义事件的时长，此为结束计时的函数
	 * 
	 * @param context
	 * @param event_id
	 * @param label
	 */
	private static void onEventEnd(Context context, String event_id, String label) {

	}

	/**
	 * 用于统计自定义事件的发生次数 嵌入位置：任意，一般在开发者自定义事件(如点击事件等)的监听位置
	 * 
	 * @param context
	 *            上下文对象
	 * @param object
	 *            当前所在自定义页面对象,例如Fragment
	 * @param event_id
	 *            自定义事件Id
	 * @param label
	 *            自定义时间label
	 * @param map
	 *            自定义可扩展HashMap<String,String> 参数
	 */
	public static void onEvent(final Context context, final String event_id, final String label, final HashMap<String, String> map) {
		if (!isEnable) {
			return;
		}
		Runnable onEventRunnable = new Runnable() {

			@Override
			public void run() {
				postOnExtensibleEventInfo(context, event_id, label, map);
			}
		};
		handler.post(onEventRunnable);
	}

	/**
	 * 用于统计异常的发生次数(例如网络异常等已捕获的异常)
	 * 
	 * @param context
	 *            上下文对象
	 * @param object
	 *            当前调用对象，可传this
	 * @param map
	 *            自定义可扩展HashMap<String,String> 参数异常信息
	 */
	public static void onError(final Context context, final Exception exception, final HashMap<String, Object> map) {
		if (!isEnable) {
			return;
		}
		Runnable onErrorRunnable = new Runnable() {

			@Override
			public void run() {
				postOnExtensibleErrorInfo(context, exception, map);
			}
		};
		handler.post(onErrorRunnable);
	}

	/**
	 * 统计网络异常发生次数
	 * 
	 * @param context
	 * @param url
	 *            网络异常对应url
	 * @param netException
	 *            网络异常
	 * @param map
	 *            其它扩展参数,可为null
	 */
	public static void onNetError(final Context context, final String url, final Exception netException, final HashMap<String, Object> map) {
		if (!isEnable) {
			return;
		}
		Runnable onNetErrorRunnable = new Runnable() {

			@Override
			public void run() {
				postOnNetErrorInfo(context, url, netException, map);
			}
		};
		handler.post(onNetErrorRunnable);
	}

	/**
	 * 上报客户端详细信息
	 * 
	 * @param context
	 * @param postLocationInfo
	 *            是否上报地理位置信息
	 */
	public static void onPostClientInfo(final Context context, final boolean postLocationInfo) {
		if (!isEnable) {
			return;
		}
		Runnable clientRunnable = new Runnable() {

			@Override
			public void run() {
				postClientData(context, postLocationInfo);
			}
		};
		handler.post(clientRunnable);
	}

	/**
	 * 设置AppKey,AppKey由应用统计平台注册应用后生成的AppKey,用于唯一标识一个应用
	 * 该设置将覆盖AndroidManifest.xml中的XM_APPKEY配置
	 * 
	 * @param appKey
	 */
	public static void setAppKey(Context context, String appkeyValue) {
		DataCore.getMoblieInfo().appKey = appkeyValue;
		BasicStoreTools.getInstance().setAppKey(context, appkeyValue);
	}

	/**
	 * 设置App Channel（发布渠道的推荐方法，可以有效防止代码设置的渠道丢失的问题）,该函数设置channel同时会保存该渠道值，
	 * 并且发送日志以该设置为主，不会发生意外丢失的情况， 若设置saveChannelWithCode为false，那么sdk不会保存该channel
	 * 
	 * @param context
	 * @param appChannel
	 */
	public static void setAppChannel(Context context, String appChannel) {
		DataCore.getMoblieInfo().appChannel = appChannel;
		BasicStoreTools.getInstance().setAppChannel(context, appChannel);
	}

	/**
	 * 用于调试使用的接口，发布时务必去除该调用，或者关闭该调用开关。
	 * 
	 * @param value
	 */
	public static void setDebugOn(boolean value) {
		CommonConfig.setDebugOn(value);
	}

	/**
	 * 用于设定统计开关，当前版本仅支持EXCEPTION_LOG
	 * 
	 * @param context
	 * @param isOn
	 */
	public static void setExceptionOn(final Context context, boolean isOn) {
		if (!isEnable) {
			return;
		}
		if (isOn) {
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
	 * 注：请在StatService.setSendLogStrategy之前调用，否则设置不起作用
	 * 如果设置的是发送策略是启动时发送，那么这个参数就会在发送前检查您设置的这个参数，表示延迟多少S发送。
	 * 
	 * @param seconds
	 */
	public static void setLogSenderDelayed(int seconds) {
		LogController.geInstance().setSendDelayedTime(seconds);
	}

	/**
	 * 设置日志发送策略
	 * 
	 * @param context
	 * @param sst
	 *            日志发送策略 {SendStragegyEnum.REAL_TIME,
	 *            SendStrategyEnum.APP_START, SendStrategyEnum.ONCE_A_DAY,
	 *            SendStrategyEnum.SET_TIME_INTERVAL}
	 * @param rtime_interval
	 *            设置日志发送时间间隔,单位分钟,仅当sst为SendStrategyEnum.SET_TIME_INTERVAL时有效
	 * @param only_wifi
	 *            是否只在wifi网络下发送
	 */
	public static void initService(Context context, SendStrategyEnum sst, int rtime_interval, boolean only_wifi) {
		LogController.geInstance().setSendStrategy(context, sst, rtime_interval, only_wifi);
	}

	public static void initService(Context context) {
		LogController.geInstance().setSendStrategy(context, SendStrategyEnum.APP_START, 0, false);
	}

	/**
	 * 设置Session超时的秒数 单位为秒，大小为1到600之间，默认为30
	 * 
	 * @param seconds
	 */
	private static void setSessionTimeOut(int seconds) {

	}

	private static void postOnResumeInfo(Context context) {
		startMillis = System.currentTimeMillis();
	}

	private static void postOnPauseInfo(Context context) {
		if (!isEnable) {
			return;
		}
		endMillis = System.currentTimeMillis();
		duration = endMillis - startMillis;

		String encodeInfo = EncodeJsonUtil.getEncodeActivityInfo(context, startMillis, endMillis, duration);

//		if (CommonUtil.isNetworkAvailable(context) && LogController.geInstance().sendStragegy.equals(SendStrategyEnum.REAL_TIME)) {
		if(checkRealUpdate(context)){
			Msg msg = NetworkUtil.post(CommonConfig.PREURL, encodeInfo);
			if (!msg.isFlag()) {
				saveInfoToFile("page", encodeInfo, context);
			}
		} else {
			saveInfoToFile("page", encodeInfo, context);
		}
	}

	private static void postOnPageStartInfo(Context context, Object object, String pageName) {
		if (!isEnable) {
			return;
		}
		PageItem pageItem = new PageItem();
		pageItem.setPageName(pageName);
		pageItem.setStartTime(System.currentTimeMillis());
		addPageItemToMap(object, pageItem);
	}

	private static void postOnPageEndInfo(Context context, Object object, String pageName) {
		if (!isEnable) {
			return;
		}
		if (cacheMap.containsKey(object)) {
			PageItem pageItem = (PageItem) cacheMap.get(object);
			pageItem.setEndTime(System.currentTimeMillis());
			pageItem.setDuration(System.currentTimeMillis() - pageItem.getStartTime());
		}
		String encodeInfo = getEncodePageInfo(context, object, pageName);

		if(checkRealUpdate(context)){
			if (LogController.isOnlyWifi && !CommonUtil.isWiFiActive(context)) {
				saveInfoToFile("page", encodeInfo, context);
			} else {
				Msg msg = NetworkUtil.post(CommonConfig.PREURL, encodeInfo);
				if (!msg.isFlag()) {
					saveInfoToFile("page", encodeInfo, context);
				}
			}
		} else {
			saveInfoToFile("page", encodeInfo, context);
		}
	}

	private static void postOnEventInfo(Context context, String eventId, String label) {
		if (!isEnable) {
			return;
		}
		String encodeInfo = EncodeJsonUtil.getEncodeEventInfo(context, eventId, label);

		if(checkRealUpdate(context)){
			if (LogController.isOnlyWifi && !CommonUtil.isWiFiActive(context)) {
				saveInfoToFile("event", encodeInfo, context);
			} else {
				Msg msg = NetworkUtil.post(CommonConfig.PREURL, encodeInfo.toString());
				if (!msg.isFlag()) {
					saveInfoToFile("event", encodeInfo, context);
				}
			}
		} else {
			saveInfoToFile("event", encodeInfo, context);
		}
	}

	private static void postOnExtensibleEventInfo(Context context, String eventId, String label, HashMap<String, String> map) {
		if (!isEnable) {
			return;
		}
		String encodeInfo = EncodeJsonUtil.getEncodeExtensibleEventInfo(context, eventId, label, map);

		if(checkRealUpdate(context)){
			if (LogController.isOnlyWifi && !CommonUtil.isWiFiActive(context)) {
				saveInfoToFile("event", encodeInfo, context);
			} else {
				Msg msg = NetworkUtil.post(CommonConfig.PREURL, encodeInfo.toString());
				if (!msg.isFlag()) {
					saveInfoToFile("event", encodeInfo, context);
				}
			}
		} else {
			saveInfoToFile("event", encodeInfo, context);
		}
	}

	private static void postOnExtensibleErrorInfo(Context context, Exception exception, HashMap<String, Object> map) {
		if (!isEnable) {
			return;
		}
		String encodeInfo = EncodeJsonUtil.getEncodeExtensibleErrorInfo(context, exception, map);

		if(checkRealUpdate(context)){
			if (LogController.isOnlyWifi && !CommonUtil.isWiFiActive(context)) {
				saveInfoToFile("error", encodeInfo, context);
			} else {
				Msg msg = NetworkUtil.post(CommonConfig.PREURL, encodeInfo.toString());
				if (!msg.isFlag()) {
					saveInfoToFile("error", encodeInfo, context);
				}
			}
		} else {
			saveInfoToFile("error", encodeInfo, context);
		}
	}

	private static void postOnNetErrorInfo(Context context, String url, Exception exception, HashMap<String, Object> map) {
		if (!isEnable) {
			return;
		}
		String encodeInfo = EncodeJsonUtil.getEncodeNetErrorInfo(context, url, exception, map);

		if(checkRealUpdate(context)){
			if (LogController.isOnlyWifi && !CommonUtil.isWiFiActive(context)) {
				saveInfoToFile("error", encodeInfo, context);
			} else {
				Msg msg = NetworkUtil.post(CommonConfig.PREURL, encodeInfo.toString());
				if (!msg.isFlag()) {
					saveInfoToFile("error", encodeInfo, context);
				}
			}
		} else {
			saveInfoToFile("error", encodeInfo, context);
		}
	}

	/**
	 * 上报客户端各种参数,只在应用启动后上传一次
	 * 
	 * @param context
	 * @param postLocationInfo
	 *            是否上报地理位置信息
	 */
	private static void postClientData(Context context, boolean postLocationInfo) {
		if (!isEnable) {
			return;
		}
		DataCore.getCurrentSessionId(context);

		String encodeInfo = EncodeJsonUtil.getEncodeClientDataInfo(context, postLocationInfo, session_id);
		if(checkRealUpdate(context)){
			if (LogController.isOnlyWifi && !CommonUtil.isWiFiActive(context)) {
				saveInfoToFile("client", encodeInfo, context);
			} else {
				Msg msg = NetworkUtil.post(CommonConfig.PREURL, encodeInfo.toString());
				if (!msg.isFlag()) {
					saveInfoToFile("client", encodeInfo, context);
				}
			}
		} else {
			saveInfoToFile("client", encodeInfo, context);
		}
	}

	public static void saveInfoToFile(String type, String encodeInfo, Context context) {
		String cachePath = LogController.saveInfoToLog(context, type, encodeInfo);
		if (!StringUtils.isEmpty(cachePath) && handler != null) {
			handler.post(new WriteFileThread(context, cachePath, encodeInfo));
		}
	}

	/**
	 * 获取和拼接自定义页面上报参数
	 * 
	 * @param context
	 * @return
	 */
	private static String getEncodePageInfo(Context context, Object object, String pageName) {
		JSONObject info = new JSONObject();
		PageItem pageItem = null;
		if (cacheMap.containsKey(object)) {
			pageItem = (PageItem) cacheMap.get(object);
			if (pageItem != null) {
				try {
					info.put("appkey", DataCore.getAppkey(context));
					info.put("appchannel", DataCore.getAppChannel(context));
					info.put("sessionId", DataCore.getCurrentSessionId(context));
					info.put("activities", CommonUtil.getActivityName(context));
					info.put("type", "page");
					info.put("page_name", pageName);
					info.put("start_millis", pageItem.getStartTime());
					info.put("end_millis", pageItem.getEndTime());
					info.put("duration", pageItem.getDuration());
					info.put("device_id", DataCore.getDeviceId(context));
					info.put("version", DataCore.getAppVersionName(context));
					cacheMap.remove(pageName);

					if (initMap != null) {
						for (Map.Entry<String, Object> entry : initMap.entrySet()) {
							String key = entry.getKey();
							Object value = entry.getValue();
							info.put(key, value);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return StringUtils.encodeJSONData(info);
	}

	private static void addPageItemToMap(Object object, PageItem item) {
		if (item != null && cacheMap != null) {
			if (!cacheMap.containsKey(object)) {
				cacheMap.put(object, item);
			}
		}
	}

	private static boolean checkRealUpdate(Context context){
		if(LogController.geInstance().sendStragegy.equals(SendStrategyEnum.REAL_TIME) && CommonUtil.isNetworkAvailable(context) && !NetType.isNet2G_DOWN(context)){
			return true;
		}
		return false;
	}
}
