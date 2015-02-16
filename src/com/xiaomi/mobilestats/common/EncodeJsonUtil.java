package com.xiaomi.mobilestats.common;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.xiaomi.mobilestats.StatService;
import com.xiaomi.mobilestats.data.DataCore;
import com.xiaomi.mobilestats.object.GSMCell;
import com.xiaomi.mobilestats.object.LatitudeAndLongitude;

public class EncodeJsonUtil {
	private static final String TAG = "EncodeJSON";

	private static JSONObject makeBaseLogJson(Context context, String type) {
		JSONObject jsonInfo = new JSONObject();
		try {
			jsonInfo.put("appkey", DataCore.getAppkey(context));
			jsonInfo.put("appchannel", DataCore.getAppChannel(context));
			jsonInfo.put("sessionId", DataCore.getCurrentSessionId(context));
			jsonInfo.put("type", type);
			jsonInfo.put("activity", CommonUtil.getActivityName(context));
			jsonInfo.put("time", System.currentTimeMillis());
			jsonInfo.put("device_id", DataCore.getDeviceId(context));
			jsonInfo.put("version", DataCore.getAppVersionName(context));

			HashMap<String, Object> initMap = StatService.getInstance().getInitMap();
			if (initMap != null) {
				for (Map.Entry<String, Object> entry : initMap.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					jsonInfo.put(key, value);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonInfo;
	}

	/**
	 * 获取和拼接Activity页面上报参数
	 * 
	 * @param context
	 * @return
	 */
	public static String getEncodeActivityInfo(Context context, long startMillis, long endMillis, long duration) {
		JSONObject jsonInfo = makeBaseLogJson(context, "page");
		try {
			jsonInfo.put("start_millis", startMillis);
			jsonInfo.put("end_millis", endMillis);
			jsonInfo.put("duration", duration);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		CommonUtil.printLog(TAG, jsonInfo.toString());
		return StringUtils.encodeJSONData(jsonInfo);
	}

	/**
	 * 获取和拼接自定义时间Json上报数据
	 * 
	 * @param context
	 * @param object
	 *            调用对象(一般传递this)
	 * @param eventId
	 * @param label
	 * @return
	 */
	public static String getEncodeEventInfo(Context context, String eventId, String label) {
		JSONObject jsonInfo = makeBaseLogJson(context, "event");
		try {
			jsonInfo.put("event_id", eventId);
			jsonInfo.put("label", label);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		CommonUtil.printLog(TAG, jsonInfo.toString());
		return StringUtils.encodeJSONData(jsonInfo);
	}

	public static String getEncodeExtensibleEventInfo(Context context, String eventId, String label, HashMap<String, String> map) {
		JSONObject jsonInfo = makeBaseLogJson(context, "event");
		try {
			jsonInfo.put("event_id", eventId);
			jsonInfo.put("label", label);
			if (map != null) {
				for (Map.Entry<String, String> entry : map.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					jsonInfo.put(key, value);
				}
			}
			CommonUtil.printLog(TAG, jsonInfo.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return StringUtils.encodeJSONData(jsonInfo);
	}

	public static String getEncodeExtensibleErrorInfo(Context context, Exception exception, HashMap<String, Object> map) {
		JSONObject jsonInfo = makeBaseLogJson(context, "error");
		try {
			if (exception != null) {
				Throwable throwable = exception.getCause();
				if(throwable != null){
					String exceptionCause = throwable.toString();
					if(exceptionCause.contains(":")){
						exceptionCause = exceptionCause.split(":")[0];
					}
					jsonInfo.put("exception", exceptionCause);
				}else{
					jsonInfo.put("exception", "Unkown");
				}
				jsonInfo.put("exception_detail", exception.toString());
			}
			if (map != null) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					jsonInfo.put(key, value);
				}
			}
			CommonUtil.printLog(TAG, jsonInfo.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return StringUtils.encodeJSONData(jsonInfo);
	}

	public static String getEncodeNetErrorInfo(Context context, String url, Exception exception, HashMap<String, Object> map) {
		JSONObject jsonInfo = makeBaseLogJson(context, "error");
		try {
			jsonInfo.put("network_type", CommonUtil.getNetworkType(context));
			jsonInfo.put("network", CommonUtil.getNetworkTypeWIFI2G3G(context));
			if (exception != null) {
				Throwable throwable = exception.getCause();
				if(throwable != null){
					String exceptionCause = throwable.toString();
					if(exceptionCause.contains(":")){
						exceptionCause = exceptionCause.split(":")[0];
					}
					jsonInfo.put("exception", exceptionCause);
				}else{
					jsonInfo.put("exception", "Unkown");
				}
				jsonInfo.put("exception_detail", exception.toString());
			}
			if (!TextUtils.isEmpty(url)) {
				jsonInfo.put("url", url);
			}
			if (map != null) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					jsonInfo.put(key, value);
				}
			}
			CommonUtil.printLog(TAG, jsonInfo.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return StringUtils.encodeJSONData(jsonInfo);
	}

	/**
	 * 获取和拼接用户设备详细信息
	 * 
	 * @param context
	 * @return
	 */
	public static String getEncodeClientDataInfo(Context context, boolean postLocationInfo, String sessionId) {
		TelephonyManager tm = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics displaysMetrics = new DisplayMetrics();
		if (windowManager != null) {
			windowManager.getDefaultDisplay().getMetrics(displaysMetrics);
		}

		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		JSONObject clientData = new JSONObject();
		try {
			clientData.put("type", "client");
			clientData.put("appkey", DataCore.getAppkey(context));
			clientData.put("appchannel", DataCore.getAppChannel(context));
			clientData.put("sessionId", DataCore.getCurrentSessionId(context));
			clientData.put("time", System.currentTimeMillis());
			clientData.put("deviceid", DataCore.getDeviceId(context));
			clientData.put("version", DataCore.getAppVersionCode(context));
			clientData.put("os_version", DataCore.getOSVersion());
			clientData.put("platform", "android");
			clientData.put("language", Locale.getDefault().getLanguage());
			clientData.put("resolution", displaysMetrics.widthPixels + "x" + displaysMetrics.heightPixels);
			clientData.put("phonetype", tm != null ? tm.getPhoneType() : "");
			clientData.put("imsi", tm != null ? tm.getSubscriberId() : "");
			clientData.put("network", CommonUtil.getNetworkTypeWIFI2G3G(context));

			clientData.put("modulename", Build.PRODUCT);
			clientData.put("devicename", CommonUtil.getDeviceName());
			clientData.put("wifimac", wifiManager != null ? wifiManager.getConnectionInfo().getMacAddress() : "");
			clientData.put("havebt", adapter == null ? false : true);
			clientData.put("havewifi", CommonUtil.isWiFiActive(context));
			clientData.put("havegravity", CommonUtil.isHaveGravity(context));

			if (postLocationInfo) {
				GSMCell gsmCell = CommonUtil.getCellInfo(context);
				clientData.put("mccmnc", gsmCell != null ? "" + gsmCell.MCCMNC : "");
				clientData.put("cellid", gsmCell != null ? gsmCell.CID + "" : "");
				clientData.put("lac", gsmCell != null ? gsmCell.LAC + "" : "");
				LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
				LatitudeAndLongitude coordinates = CommonUtil.getLatitudeAndLongitude(context);
				clientData.put("havegps", locationManager == null ? false : true);
				clientData.put("latitude", coordinates.latitude);
				clientData.put("longitude", coordinates.longitude);
			}
			HashMap<String, Object> initMap = StatService.getInstance().getInitMap();
			if (initMap != null) {
				for (Map.Entry<String, Object> entry : initMap.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					clientData.put(key, value);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return StringUtils.encodeJSONData(clientData);
	}

}
