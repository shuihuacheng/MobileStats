package com.xiaomi.mobilestats.common;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

	public static void saveInfoToFile(Handler handler, String type, String filePath,JSONObject info, Context context) {
		JSONArray jsonArray = new JSONArray();
		try {
				jsonArray.put(0, info);
			if (handler != null) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(type, jsonArray);
				handler.post(new WriteFileThread(context,filePath,jsonObject));  
			} else {
				CommonUtil.printLog(CommonUtil.getActivityName(context),"handler--null");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * checkPermissions
	 * 
	 * @param context
	 * @param permission
	 * @return true or false
	 */
	public static boolean checkPermissions(Context context, String permission) {
		PackageManager localPackageManager = context.getPackageManager();
		return localPackageManager.checkPermission(permission,context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * Determine the current networking is WIFI
	 * 
	 * @param context
	 * @return
	 */
	public static boolean currentNoteworkTypeIsWIFI(Context context) {
		ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectionManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * return UserIdentifier
	 */
	public static String getUserIdentifier(Context context) {
		String packageName = context.getPackageName();
		SharedPreferences localSharedPreferences = context.getSharedPreferences("xm_agent_online_setting_" + packageName, 0);
		return localSharedPreferences.getString("identifier", "");
	}

	/**
	 * Judge wifi is available
	 * 
	 * @param inContext
	 * @return
	 */
	public static boolean isWiFiActive(Context inContext) {
		if (checkPermissions(inContext, "android.permission.ACCESS_WIFI_STATE")) {
			Context context = inContext.getApplicationContext();
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getTypeName().equals("WIFI")&& info[i].isConnected()) {
							return true;
						}
					}
				}
			}
			return false;
		} else {
			if (CommonConfig.DEBUG_MODE) {
				Log.e("lost permission","lost--->android.permission.ACCESS_WIFI_STATE");
			}

			return false;
		}
	}

	/**
	 * Testing equipment networking and networking WIFI
	 * 
	 * @param context
	 * @return true or false
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (checkPermissions(context, "android.permission.INTERNET")) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				return true;
			} else {
				if (CommonConfig.DEBUG_MODE) {
					Log.e("error", "Network error");
				}
				return false;
			}
		} else {
			if (CommonConfig.DEBUG_MODE) {
				Log.e(" lost  permission",	"lost----> android.permission.INTERNET");
			}
			return false;
		}
	}

	/**
	 * Get the current time format yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getTime() {
		Date date = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		return sdf.format(date);
		return Long.toString(date.getTime());
	}

	/**
	 *从AndroidManifest.xml中查找XM_APPKEY数据
	 * @param context
	 * @return appkey
	 */
	public static String getAppKey(Context context) {
		return getAndroidManifestMetaData(context, "XM_APPKEY");
	}
	
	/**
	 * 从AndroidManifest.xml中查找XM_APPCHANNEL数据
	 * @param context
	 * @return appchannel
	 */
	public static String getAppChannel(Context context) {
		return getAndroidManifestMetaData(context, "XM_APPCHANNEL");
	}

	/**
	 * get currnet activity's name
	 * 
	 * @param context
	 * @return
	 */
	public static String getActivityName(Context context) {
		if (context == null) {
			return "";
		}
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (checkPermissions(context, "android.permission.GET_TASKS")) {
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			return cn.getShortClassName();
		} else {
			CommonUtil.printLog("no permission", "android.permission.GET_TASKS");
			return "";
		}
	}

	/**
	 * get PackageName
	 * 
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (checkPermissions(context, "android.permission.GET_TASKS")) {
				ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			return cn.getPackageName();
		} else {
			CommonUtil.printLog("no permission", "android.permission.GET_TASKS");
			return null;
		}
	}

	/**
	 * get OS number
	 * 
	 * @param context
	 * @return
	 */
	public static String getOsVersion(Context context) {
		String osVersion = "";
		if (checkPhoneState(context)) {
			osVersion = android.os.Build.VERSION.RELEASE;
		} 
		return osVersion;
	}

	/**
	 * get deviceid
	 * 
	 * @param context
	 *            add <uses-permission android:name="READ_PHONE_STATE" />
	 * @return
	 */
	public static String getDeviceID(Context context) {
		if (context == null) {
			return "";
		}
		String deviceId = "";
		if (checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
			if (checkPhoneState(context)) {
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				deviceId = tm.getDeviceId();
			}
		} else {
			if (CommonConfig.DEBUG_MODE) {
				Log.e("lost permissioin","lost----->android.permission.READ_PHONE_STATE");
			}
		}
		return deviceId;
	}
	
	  /**
	   * 获取Mac地址
	   * @param paramContext
	   * @return
	   */
	  public static String getMacAddress(Context paramContext)
	  {
	    String macAddress  = "";
	    try
	    {
	        WifiManager localWifiManager = (WifiManager)paramContext.getSystemService("wifi");
	        WifiInfo localWifiInfo = localWifiManager.getConnectionInfo();
	        macAddress  = localWifiInfo.getMacAddress();
	    }catch (Exception localException) {
	    	localException.printStackTrace();
	    }
	    return macAddress ;
	  }

	/**
	 * check phone _state is readied ;
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkPhoneState(Context context) {
		PackageManager packageManager = context.getPackageManager();
		if (packageManager.checkPermission("android.permission.READ_PHONE_STATE",context.getPackageName()) != 0) {
			return false;
		}
		return true;
	}

	/**
	 * get sdk number
	 * @param paramContext
	 * @return
	 */
	public static String getSdkVersion(Context paramContext) {
		String osVersion = "";
		if (!checkPhoneState(paramContext)) {
			osVersion = android.os.Build.VERSION.RELEASE;
		} else {
			CommonUtil.printLog("android_osVersion", "OsVerson get failed");
		}
		return osVersion;
	}

	/**
	 * Get the version number of the current program
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int versionCode = -1;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (Exception e) {
			if (CommonConfig.DEBUG_MODE) {
				Log.e("VersionInfo", "Exception", e);
			}
		}
		return versionCode;
	}

	/**
	 * Get the current send model
	 * 
	 * @param context
	 * @return
	 */
	public static SendStrategyEnum getSendStragegy(Context context) {
		String str = context.getPackageName();
		SharedPreferences localSharedPreferences = context.getSharedPreferences("xm_agent_online_setting_" + str, 0);
		int type = localSharedPreferences.getInt("xm_local_report_policy", 0);
		return SendStrategyEnum.values()[type];
	}

	/**
	 * Get the base station information
	 * 
	 * @throws Exception
	 */
	public static GSMCell getCellInfo(Context context) throws Exception {
		GSMCell cell = new GSMCell();
		TelephonyManager mTelNet = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
		if (location == null) {
			if (CommonConfig.DEBUG_MODE) {
				Log.e("GsmCellLocation Error", "GsmCellLocation is null");
			}
			return null;
		}

		String operator = mTelNet.getNetworkOperator();
		int mcc = Integer.parseInt(operator.substring(0, 3));
		int mnc = Integer.parseInt(operator.substring(3));
		int cid = location.getCid();
		int lac = location.getLac();

		cell.MCC = mcc;
		cell.MCCMNC = Integer.parseInt(operator);
		cell.MNC = mnc;
		cell.LAC = lac;
		cell.CID = cid;
		return cell;
	}

	public static LatitudeAndLongitude getLatitudeAndLongitude(Context context,
			boolean mUseLocationService) {
		LatitudeAndLongitude latitudeAndLongitude = new LatitudeAndLongitude();
		if (mUseLocationService) {
			LocationManager loctionManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			List<String> matchingProviders = loctionManager.getAllProviders();
			for (String prociderString : matchingProviders) {
				Location location = loctionManager.getLastKnownLocation(prociderString);
				if (location != null) {
					latitudeAndLongitude.latitude = location.getLatitude() + "";
					latitudeAndLongitude.longitude = location.getLongitude()+ "";
				} else {
					latitudeAndLongitude.latitude = "";
					latitudeAndLongitude.longitude = "";
				}
			}
		} else {
			latitudeAndLongitude.latitude = "";
			latitudeAndLongitude.longitude = "";
		}
		return latitudeAndLongitude;
	}

	/**
	 * To determine whether it contains a gyroscope
	 * @return
	 */
	public static boolean isHaveGravity(Context context) {
		SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if (manager == null) {
			return false;
		}
		return true;
	}

	/**
	 * Get the current networking
	 * 
	 * @param context
	 * @return WIFI or MOBILE
	 */
	public static String getNetworkType(Context context) {
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int type = manager.getNetworkType();
		String typeString = "UNKNOWN";
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
		if (type == 11) {
			typeString = "iDen";
		}
		if (type == 12) {
			typeString = "EVDO_B";
		}
		if (type == 13) {
			typeString = "LTE";
		}
		if (type == 14) {
			typeString = "eHRPD";
		}
		if (type == 15) {
			typeString = "HSPA+";
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
		if (checkPermissions(context, "android.permission.INTERNET")) {
			ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cManager.getActiveNetworkInfo();

			if (info != null && info.isAvailable()&& info.getTypeName().equals("WIFI")) {
				return true;
			} else {
				if (CommonConfig.DEBUG_MODE) {
					Log.e("error", "Network not wifi");
				}
				return false;
			}
		} else {
			if (CommonConfig.DEBUG_MODE) {
				Log.e(" lost  permission",	"lost----> android.permission.INTERNET");
			}
			return false;
		}

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
			if (context == null) {
				return "";
			}
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			if (CommonConfig.DEBUG_MODE) {
				Log.e("XMAgent", "Exception", e);
			}
		}
		return versionName;
	}

	/**
	 * Set the output log
	 * @param tag
	 * @param log
	 */
	public static void printLog(String tag, String log) {
		if (CommonConfig.DEBUG_MODE == true) {
			Log.d(tag, log);
		}
	}

	public static String getNetworkTypeWIFI2G3G(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context	.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = cm.getActiveNetworkInfo();
		String type = info.getTypeName().toLowerCase();
		if (type.equals("wifi")) {

		} else {
			type = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getExtraInfo();
		}
		return type;

	}

	
	/**
	 * Get device name, manufacturer + model
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
	 * Capitalize the first letter
	 * @param s model,manufacturer
	 * @return Capitalize the first letter
	 */
	private static String capitalize(String s) {
		if (s == null || s.length() == 0) {
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
	private static String getAndroidManifestMetaData(Context context,String param){
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
				CommonUtil.printLog("XMAgent","Could not read "+param+" meta-data from AndroidManifest.xml.");
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			CommonUtil.printLog("XMAgent","Could not read "+param+" meta-data from AndroidManifest.xml.");
		}
		return result;
	}
	
}
