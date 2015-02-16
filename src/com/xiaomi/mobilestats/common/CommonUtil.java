package com.xiaomi.mobilestats.common;

import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.mobilestats.data.SendStrategyEnum;
import com.xiaomi.mobilestats.data.WriteFileThread;
import com.xiaomi.mobilestats.object.GSMCell;
import com.xiaomi.mobilestats.object.LatitudeAndLongitude;

public class CommonUtil {

	public static void saveInfoToFile(Handler handler, String type, String filePath, String data, Context context) {
		if (handler != null) {
			handler.post(new WriteFileThread(context, filePath, data));
		}
	}

	/**
	 * checkPermissions
	 * @param context
	 * @param permission
	 * @return true or false
	 */
	public static boolean checkPermissions(Context context, String permission) {
		if(context != null){
			PackageManager localPackageManager = context.getPackageManager();
			if(localPackageManager != null){
				return localPackageManager.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
			}
		}
		return false;
	}

	/**
	 * Determine the current networking is WIFI
	 * @param context
	 * @return
	 */
	public static boolean currentNoteworkTypeIsWIFI(Context context) {
		if(context != null){
			ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
			if(networkInfo != null){
				return connectionManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
			}
		}
		return false;
	}

	/**
	 * return UserIdentifier
	 */
	public static String getUserIdentifier(Context context) {
		if(context != null){
			String packageName = context.getPackageName();
			SharedPreferences localSharedPreferences = context.getSharedPreferences("xm_agent_online_setting_" + packageName, 0);
			if(localSharedPreferences != null){
				return localSharedPreferences.getString("identifier", "");
			}
		}
		return "";
	}

	/**
	 * 判断WiFi网络是否可用
	 * @param inContext
	 * @return
	 */
	public static boolean isWiFiActive(Context inContext) {
		if(inContext != null){
			if (checkPermissions(inContext, "android.permission.ACCESS_WIFI_STATE")) {
				Context context = inContext.getApplicationContext();
				ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (cm != null) {
					NetworkInfo[] info = cm.getAllNetworkInfo();
					if (info != null && info.length>0) {
						for (int i = 0; i < info.length; i++) {
							if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
								return true;
							}
						}
					}
				}
				return false;
			} 
		}
			return false;
	}

	public static boolean isNetworkAvailable(Context context) {
		if(context != null){
			if (checkPermissions(context, "android.permission.INTERNET")) {
				ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				if(cm != null){
					NetworkInfo info = cm.getActiveNetworkInfo();
					if (info != null && info.isAvailable()) {
						return true;
					} 
				}
				return false;
			} 
		}
		return false;
	}

	public static String getTime() {
		Date date = new Date();
		return Long.toString(date.getTime());
	}

	/**
	 * 从AndroidManifest.xml中查找XM_APPKEY数据
	 * @param context
	 * @return appkey
	 */
	public static String getAppKey(Context context) {
		return getAndroidManifestMetaData(context, "XM_APPKEY");
	}

	/**
	 * 从AndroidManifest.xml中查找XM_APPCHANNEL数据
	 * 
	 * @param context
	 * @return appchannel
	 */
	public static String getAppChannel(Context context) {
		return getAndroidManifestMetaData(context, "XM_APPCHANNEL");
	}

	/**
	 * 获取当前Activity的类名
	 * @param context
	 * @return
	 */
	public static String getActivityName(Context context) {
		if (context == null) {
			return "";
		}
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (am != null && checkPermissions(context, "android.permission.GET_TASKS")) {
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			if(cn != null){
				return cn.getClassName();
			}
			return "";
		} else {
			CommonUtil.printLog("no permission", "android.permission.GET_TASKS");
			return "";
		}
	}

	/**
	 *  获取包名
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		if(context != null){
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			if (am != null && checkPermissions(context, "android.permission.GET_TASKS")) {
				ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
				if(cn != null){
					return cn.getPackageName();
				}
			} else {
				CommonUtil.printLog("no permission", "android.permission.GET_TASKS");
			}
		}
		return "";
	}

	/**
	 * 获取操作系统版本
	 * @param context
	 * @return
	 */
	public static String getOsVersion(Context context) {
		String osVersion = "";
		if (context != null && checkPhoneState(context)) {
			osVersion = android.os.Build.VERSION.RELEASE;
		}
		return osVersion;
	}

	/**
	 *  获取设备deviceId
	 * @param context
	 * @return
	 */
	public static String getDeviceID(Context context) {
		String deviceId = "";
		if (context != null) {
			if (checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
				if (checkPhoneState(context)) {
					TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
					if(tm != null){
						deviceId = tm.getDeviceId();
					}
				}
			}
			if (!TextUtils.isEmpty(deviceId)) {
				return StringUtils.md5(deviceId);
			}
		}
		return deviceId;
	}

	/**
	 * 获取Mac地址
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {
		String macAddress = "";
		if(context != null){
			try {
				WifiManager localWifiManager = (WifiManager) context.getSystemService("wifi");
				if(localWifiManager != null){
					WifiInfo localWifiInfo = localWifiManager.getConnectionInfo();
					if(localWifiInfo != null){
						macAddress = localWifiInfo.getMacAddress();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return macAddress;
	}

	/**
	 * check phone _state is readied ;
	 * @param context
	 * @return
	 */
	public static boolean checkPhoneState(Context context) {
		if (context != null) {
			PackageManager packageManager = context.getPackageManager();
			if (packageManager.checkPermission("android.permission.READ_PHONE_STATE", context.getPackageName()) != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 *  获取SDK版本
	 * @param context
	 * @return
	 */
	public static String getSdkVersion(Context context) {
		String sdkVersion = "";
		if (context != null && !checkPhoneState(context)) {
			sdkVersion = android.os.Build.VERSION.RELEASE;
		} else {
			CommonUtil.printLog("android_osVersion", "OsVerson get failed");
		}
		return sdkVersion;
	}

	/**
	 * 获取应用程序版本号
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int versionCode = -1;
		if(context != null){
			try {
				PackageManager pm = context.getPackageManager();
				if(pm != null){
					PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
					versionCode = pi.versionCode;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return versionCode;
	}

	/**
	 * 获取当前发送策略
	 * @param context
	 * @return
	 */
	public static SendStrategyEnum getSendStragegy(Context context) {
		if(context != null){
			String str = context.getPackageName();
			SharedPreferences localSharedPreferences = context.getSharedPreferences("xm_agent_online_setting_" + str, 0);
			int type = localSharedPreferences.getInt("xm_local_report_policy", 0);
			return SendStrategyEnum.values()[type];
		}
		return null;
	}

	/**
	 * Get the base station information
	 * 
	 * @throws Exception
	 */
	public static GSMCell getCellInfo(Context context) throws Exception {
		GSMCell cell = new GSMCell();
		if(context != null){
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if(tm != null){
				String operator = tm.getNetworkOperator();
				if(!StringUtils.isEmpty(operator) ){
					cell.MCCMNC = Integer.parseInt(operator);
				}
				GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
				if (location != null) {
					int cid = location.getCid();
					int lac = location.getLac();
					cell.LAC = lac;
					cell.CID = cid;
				}
			}
		}
		return cell;
	}

	public static LatitudeAndLongitude getLatitudeAndLongitude(Context context) {
		LatitudeAndLongitude latitudeAndLongitude = new LatitudeAndLongitude();
		if(context != null){
			LocationManager loctionManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			List<String> matchingProviders = loctionManager.getAllProviders();
			for (String prociderString : matchingProviders) {
				Location location = loctionManager.getLastKnownLocation(prociderString);
				if (location != null) {
					latitudeAndLongitude.latitude = location.getLatitude() + "";
					latitudeAndLongitude.longitude = location.getLongitude() + "";
				} else {
					latitudeAndLongitude.latitude = "";
					latitudeAndLongitude.longitude = "";
				}
			}
		}
		return latitudeAndLongitude;
	}

	/**
	 * To determine whether it contains a gyroscope
	 * 
	 * @return
	 */
	public static boolean isHaveGravity(Context context) {
		if(context != null){
			SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
			if (manager != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取网络类型
	 * @param context
	 * @return WIFI or MOBILE
	 */
	public static String getNetworkType(Context context) {
		String typeString = "UNKNOWN";
		if(context != null){
			TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			int type = manager.getNetworkType();
			if (type == TelephonyManager.NETWORK_TYPE_CDMA) {
				typeString = "CDMA";
			}
			if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
				typeString = "EDGE";
			}
			if (type == TelephonyManager.NETWORK_TYPE_EVDO_0) {
				typeString = "EVDO_0";
			}
			if (type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
				typeString = "EVDO_A";
			}
			if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
				typeString = "GPRS";
			}
			if (type == TelephonyManager.NETWORK_TYPE_HSDPA) {
				typeString = "HSDPA";
			}
			if (type == TelephonyManager.NETWORK_TYPE_HSPA) {
				typeString = "HSPA";
			}
			if (type == TelephonyManager.NETWORK_TYPE_HSUPA) {
				typeString = "HSUPA";
			}
			if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
				typeString = "UMTS";
			}
			if (type == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
				typeString = "UNKNOWN";
			}
			if (type == TelephonyManager.NETWORK_TYPE_1xRTT) {
				typeString = "1xRTT";
			}
			if (type == TelephonyManager.NETWORK_TYPE_IDEN) {
				typeString = "iDen";
			}
			if (type == TelephonyManager.NETWORK_TYPE_EVDO_B) {
				typeString = "EVDO_B";
			}
			if (type == TelephonyManager.NETWORK_TYPE_LTE) {
				typeString = "LTE";
			}
			if (type == TelephonyManager.NETWORK_TYPE_EHRPD) {
				typeString = "eHRPD";
			}
			if (type == TelephonyManager.NETWORK_TYPE_HSPAP) {
				typeString = "HSPAP";
			}
		}
		return typeString;
	}

	/**
	 * Determine the current network type
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkTypeWifi(Context context) {
		if(context != null){
			if (checkPermissions(context, "android.permission.INTERNET")) {
				ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				if(cm != null){
					NetworkInfo info = cm.getActiveNetworkInfo();
					if (info != null && info.isAvailable() && info.getTypeName().equals("WIFI")) {
						return true;
					} else {
						return false;
					}
				}
			} 
		}
		return false;
	}

	/**
	 * Get the current application version number
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			if (context != null) {
				PackageManager pm = context.getPackageManager();
				PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
				versionName = pi.versionName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * Set the output log
	 * 
	 * @param tag
	 * @param log
	 */
	public static void printLog(String tag, String log) {
		if (CommonConfig.DEBUG_MODE == true) {
			Log.d(tag, log);
		}
	}

	public static String getNetworkTypeWIFI2G3G(Context context) {
		String type = "未知";
		if(context != null){
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm == null) {
				return "未知";
			}
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info == null) {
				return "未知";
			}
		    type = info.getTypeName().toLowerCase();
			if (!StringUtils.isEmpty(type) && type.equals("wifi")) {
			} else {
				type = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getExtraInfo();
			}
		}
		return type;

	}

	/**
	 * Get device name, manufacturer + model
	 * 
	 * @return device name
	 */
	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;

		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	/**
	 * 格式化首字母大写
	 * @param s
	 *            model,manufacturer
	 * @return Capitalize the first letter
	 */
	private static String capitalize(String s) {
		if (StringUtils.isEmpty(s)) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	/**
	 * 从AndroidManifest.xml中查找meta-data数据
	 * @param context
	 * @param param
	 * @return
	 */
	private static String getAndroidManifestMetaData(Context context, String param) {
		if (context == null) {
			return "";
		}
		String result = "";
		try {
			PackageManager localPackageManager = context.getPackageManager();
			ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(context.getPackageName(), 128);
			if (localApplicationInfo != null) {
				String xmlData = localApplicationInfo.metaData.getString(param);
				if (!TextUtils.isEmpty(xmlData)) {
					result = xmlData;
					return result.toString();
				}
				CommonUtil.printLog("XMAgent", "Could not read " + param + " meta-data from AndroidManifest.xml.");
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			CommonUtil.printLog("XMAgent", "Could not read " + param + " meta-data from AndroidManifest.xml.");
		}
		return result;
	}

}
