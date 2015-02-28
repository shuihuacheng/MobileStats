package com.xiaomi.mobilestats.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.xiaomi.mobilestats.common.CommonConfig;
import com.xiaomi.mobilestats.common.CommonUtil;
import com.xiaomi.mobilestats.common.FileNameComparator;
import com.xiaomi.mobilestats.common.NetType;
import com.xiaomi.mobilestats.common.StringUtils;
import com.xiaomi.mobilestats.data.BasicStoreTools;
import com.xiaomi.mobilestats.data.SendStrategyEnum;
import com.xiaomi.mobilestats.upload.UploadManager;

public class LogController {
	private static final String TAG = "LogController";
	private static HandlerThread logThread = new HandlerThread("LogSenderThread");
	public static boolean isOnlyWifi = false;
	public SendStrategyEnum sendStragegy = SendStrategyEnum.REAL_TIME;
	public long logSendIntervalHour = 1;
	public long logSendDelayedTime = 0;
	private static Handler handler;
	private Timer timer = null;
	// 日志缓存目录根目录
	public static String baseFilePath = "";
	public static String operatorFileDir = "";
	public static String uploadFileDir = "";

	public static String operatorEventFilePath = "";
	public static String operatorPageFilePath = "";
	public static String operatorCrashFilePath = "";
	public static String operatorErrorFilePath = "";
	public static String operatorClientFilePath = "";
	
	public static boolean is2GDownNet = false;

	private static LogController instance = new LogController();

	private LogController() {
		logThread.start();
		handler = new Handler(logThread.getLooper());

	}

	public static void checkOrCreate(Context context) {
		if (StringUtils.isEmpty(baseFilePath)) {
			File cacheDir = new File(context.getCacheDir(), CommonConfig.DEFAULT_CACHE_DIR);
			String userAgent = "cache/0";
			try {
				String packageName = context.getPackageName();
				PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
				userAgent = "cache/" + info.versionCode;
			} catch (NameNotFoundException e) {
				userAgent = "cache/0";
			}
			baseFilePath = cacheDir.getPath() + File.separator + userAgent;
			operatorFileDir = baseFilePath + File.separator + "operator";
			uploadFileDir = baseFilePath + File.separator + "upload";
			FloderUtils.checkOrCreateFolder(operatorFileDir);
			FloderUtils.checkOrCreateFolder(uploadFileDir);
		}
	}

	public static void deleteAllFloder() {
		if (!StringUtils.isEmpty(baseFilePath)) {
			try {
				FloderUtils.deleteFloder(new File(baseFilePath));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public static LogController geInstance() {
		return instance;
	}

	public void setSendDelayedTime(int seconds) {
		if (seconds >= 0 && seconds <= 30) {
			this.logSendDelayedTime = seconds;
		}
	}

	public void setSendStrategy(Context context, SendStrategyEnum sendStrategyEnum, int timeInterval, boolean onWifi) {
		if (context == null) {
			return;
		}
		NetType.isNet2G_DOWN(context);
		
		checkOrCreate(context);
		firstCopyLogToUploadFloder(context);
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		long currentTime = System.currentTimeMillis();
		long lastSendTime = BasicStoreTools.getInstance().getLastSendTime(context);
		int lastSendStrategy = BasicStoreTools.getInstance().getSendStrategy(context);
		if (lastSendStrategy == SendStrategyEnum.SET_TIME_INTERVAL.ordinal() || lastSendStrategy == SendStrategyEnum.ONCE_A_DAY.ordinal()) {
			if (lastSendTime != 0) {
				long lastSendInterval = BasicStoreTools.getInstance().getSendStrategyTime(context) * 60 * 1000;
				if (currentTime - lastSendTime >= lastSendInterval) {
					if (UploadManager.isHasCacheFile()) {
						UploadManager.uploadCachedUploadFiles(handler);
						BasicStoreTools.getInstance().setLastSendTime(context, System.currentTimeMillis());
					}
				}
			}
		} else {
			if (lastSendStrategy == SendStrategyEnum.APP_START.ordinal()) {
				if (UploadManager.isHasCacheFile()) {
					UploadManager.uploadCachedUploadFiles(handler);
				}
			}
		}
		this.sendStragegy = sendStrategyEnum;
		BasicStoreTools.getInstance().setSendStrategy(context, this.sendStragegy.ordinal());
		LogController.isOnlyWifi = onWifi;
		if (this.sendStragegy.equals(SendStrategyEnum.SET_TIME_INTERVAL)) {
			setSendStrategySetTimer(context, timeInterval, lastSendTime);
		} else {
			if (this.sendStragegy.equals(SendStrategyEnum.ONCE_A_DAY)) {
				setSendStrategySetOneDay(context, lastSendTime);
			} else {
				if (this.sendStragegy.equals(SendStrategyEnum.REAL_TIME)) {
					setSendStrategyRealTime(context);
				} else {
					setSendStrategyAppStart(context);
				}
			}
		}
	}

	private void setSendStrategyAppStart(Context context) {
		if (context == null) {
			return;
		}
	}

	private void setSendStrategySetTimer(Context context, long timeInterval, long lastSendTime) {
		if (context == null) {
			return;
		}
		if (this.sendStragegy.equals(SendStrategyEnum.SET_TIME_INTERVAL)) {
			timeInterval = timeInterval < 1 ? 1 : timeInterval;
			timeInterval = timeInterval > 24 * 60 ? 24 * 60 : timeInterval;
			this.logSendIntervalHour = timeInterval;
			BasicStoreTools.getInstance().setSendStrategyTime(context, this.logSendIntervalHour);
			if (lastSendTime == 0) {
				BasicStoreTools.getInstance().setLastSendTime(context, System.currentTimeMillis());
			}

			timer = new Timer();
			timer.schedule(new MTimerTask(), CommonConfig.kContinueSessionMillis, timeInterval * 60000);
		}
	}

	private void setSendStrategySetOneDay(Context context, long lastSendTime) {
		if (context == null) {
			return;
		}
		if (this.sendStragegy.equals(SendStrategyEnum.ONCE_A_DAY)) {
			BasicStoreTools.getInstance().setSendStrategyTime(context, 24 * 60);
			if (lastSendTime == 0) {
				BasicStoreTools.getInstance().setLastSendTime(context, System.currentTimeMillis());
				lastSendTime = BasicStoreTools.getInstance().getLastSendTime(context);
			}
			timer = new Timer();
			long diffTime = System.currentTimeMillis()-lastSendTime;
			if(diffTime >= 0 && diffTime <  24*3600000){
				timer.schedule(new MTimerTask(),24*3600000-(System.currentTimeMillis()-lastSendTime), 24 * 3600000);
			}else{
				timer.schedule(new MTimerTask(),CommonConfig.kContinueSessionMillis, 24 * 3600000);
			}
		}
	}

	private void setSendStrategyRealTime(Context context) {
		if (context == null) {
			return;
		}
		if (this.sendStragegy.equals(SendStrategyEnum.REAL_TIME)) {
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					if (sendStragegy.equals(SendStrategyEnum.REAL_TIME)) {
						if (UploadManager.isHasCacheFile()) {
							UploadManager.uploadCachedUploadFiles(handler);
						}
					}
				}
			};
			handler.postDelayed(runnable, 1000);
		}
	}

	/**
	 * 第一次启动应用时把操作日志文件拷贝到提交日志目录并上传提交
	 */
	public static void firstCopyLogToUploadFloder(Context context) {
		CommonUtil.printLog(CommonConfig.TAG, "firstCopyLogToUpload");
		LogController.checkOrCreate(context);
		
		CommonUtil.printLog(CommonConfig.TAG, "File cache size:"+FileLruCache.getInstance().getCacheHashMap().size());
		checkUpdateCache();
		try {
			File operatorDir = new File(LogController.operatorFileDir);
			File[] operatorFiles = operatorDir.listFiles();
			if (operatorFiles != null && operatorFiles.length > 0) {
				for (File file : operatorFiles) {
					copyToUploadDirAndDel(file, LogController.uploadFileDir + File.separator + file.getName());
				}
				clearOperatorFilePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 第一次拷贝操作文件夹文件到待提交文件夹后清空日志文件路径
	 */
	private static void clearOperatorFilePath() {
		LogController.operatorEventFilePath = "";
		LogController.operatorPageFilePath = "";
		LogController.operatorCrashFilePath = "";
		LogController.operatorErrorFilePath = "";
		LogController.operatorClientFilePath = "";
	}
	
	private static void checkUpdateCache() {
		if(FileLruCache.getInstance().getCacheHashMap().isEmpty()){
			try {
				File uploadDir = new File(LogController.uploadFileDir);
				File[] uploadFiles = uploadDir.listFiles();
				if(uploadFiles != null && uploadFiles.length>0){
					//按文件名称中时间升序排序
					sortUploadFiles(uploadFiles);
					for (int i = 0; i < uploadFiles.length; i++) {
						FileLruCache.getInstance().putFile(uploadFiles[i].getName(), uploadFiles[i]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void sortUploadFiles(File[] uploadFiles) {
		if(uploadFiles != null && uploadFiles.length>0){
			Arrays.sort(uploadFiles, new FileNameComparator());
		}
	}

	/**
	 * 将操作文件夹中文件复制到上传文件夹
	 */
	public static void copyToUploadDirAndDel(File fromFile, String newPath) {
		CommonUtil.printLog(CommonConfig.TAG, "copyToUploadDirAndDel:" + fromFile.getName());
		if (fromFile == null) {
			return;
		}
		if (fromFile.length() <= 0) {
			fromFile.delete();
		}
		File newFile = new File(newPath);
		if (!newFile.getParentFile().exists()) {
			newFile.getParentFile().mkdirs();
		}
		if (newFile.exists()) {
			newFile.delete();
		}
		try {
			FileInputStream in = new FileInputStream(fromFile);
			FileOutputStream out = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (newFile.exists()) {
			fromFile.delete();
			FileLruCache.getInstance().putFile(newFile.getName(), newFile);
		}
	}

	public static String saveInfoToLog(Context context, String type, String encodeInfo) {
		checkOrCreate(context);
		File uploadDir = new File(LogController.uploadFileDir);
		File operatorDir = new File(LogController.operatorFileDir);
		if (!uploadDir.getParentFile().exists())
			uploadDir.getParentFile().mkdirs();
		if (!uploadDir.exists())
			uploadDir.mkdirs();
		if (!operatorDir.exists())
			operatorDir.mkdirs();
		checkOperatorFile(type);

		String cachePath = "";
		if (!TextUtils.isEmpty(type) && type.equals("page")) {
			cachePath = LogController.operatorPageFilePath;
		} else if (!TextUtils.isEmpty(type) && type.equals("event")) {
			cachePath = LogController.operatorEventFilePath;
		} else if (!TextUtils.isEmpty(type) && type.equals("crash")) {
			cachePath = LogController.operatorCrashFilePath;
		} else if (!TextUtils.isEmpty(type) && type.equals("error")) {
			cachePath = LogController.operatorErrorFilePath;
		} else if (!TextUtils.isEmpty(type) && type.equals("client")) {
			cachePath = LogController.operatorClientFilePath;
		}

		File cacheFile = new File(cachePath);
		if (cacheFile.exists()) {
			long cacheSize = cacheFile.length();
			long jsonSize = encodeInfo.getBytes().length;
			if ((cacheSize + jsonSize) > 16 * 1024) {
				CommonUtil.printLog(CommonConfig.TAG, "size is over 16k");
				copyToUploadDirAndDel(cacheFile, LogController.uploadFileDir + File.separator + cacheFile.getName());
				if (!TextUtils.isEmpty(type) && type.equals("page")) {
					LogController.operatorPageFilePath = LogController.operatorFileDir + File.separator + System.currentTimeMillis() + "_page.json";
					cachePath = LogController.operatorPageFilePath;
				} else if (!TextUtils.isEmpty(type) && type.equals("event")) {
					LogController.operatorEventFilePath = LogController.operatorFileDir + File.separator + System.currentTimeMillis() + "_event.json";
					cachePath = LogController.operatorEventFilePath;
				} else if (!TextUtils.isEmpty(type) && type.equals("crash")) {
					LogController.operatorCrashFilePath = LogController.operatorFileDir + File.separator + System.currentTimeMillis() + "_crash.json";
					cachePath = LogController.operatorCrashFilePath;
				} else if (!TextUtils.isEmpty(type) && type.equals("error")) {
					LogController.operatorErrorFilePath = LogController.operatorFileDir + File.separator + System.currentTimeMillis() + "_error.json";
					cachePath = LogController.operatorErrorFilePath;
				} else if (!TextUtils.isEmpty(type) && type.equals("client")) {
					LogController.operatorClientFilePath = LogController.operatorFileDir + File.separator + System.currentTimeMillis() + "_client.json";
					cachePath = LogController.operatorClientFilePath;
				}
			}
		}
		return cachePath;
	}

	/**
	 * 检测操作文件夹中操作文件是否为空,为空则创建
	 */
	private static void checkOperatorFile(String type) {
		if (type.equals("page") && TextUtils.isEmpty(LogController.operatorPageFilePath)) {
			LogController.operatorPageFilePath = LogController.operatorFileDir + File.separator + System.currentTimeMillis() + "_page.json";
			File operatorPageFile = new File(LogController.operatorPageFilePath);
			if (!operatorPageFile.exists()) {
				try {
					operatorPageFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (type.equals("event") && TextUtils.isEmpty(LogController.operatorEventFilePath)) {
			LogController.operatorEventFilePath = LogController.operatorFileDir + File.separator + System.currentTimeMillis() + "_event.json";
			File operatorEventFile = new File(LogController.operatorEventFilePath);
			if (!operatorEventFile.exists()) {
				try {
					operatorEventFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (type.equals("error") && TextUtils.isEmpty(LogController.operatorErrorFilePath)) {
			LogController.operatorErrorFilePath = LogController.operatorFileDir + File.separator + System.currentTimeMillis() + "_error.json";
			File operatorErrorFile = new File(LogController.operatorErrorFilePath);
			if (!operatorErrorFile.exists()) {
				try {
					operatorErrorFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (type.equals("crash") && TextUtils.isEmpty(LogController.operatorCrashFilePath)) {
			LogController.operatorCrashFilePath = LogController.operatorFileDir + File.separator + System.currentTimeMillis() + "_crash.json";
			File operatorCrashFile = new File(LogController.operatorCrashFilePath);
			if (!operatorCrashFile.exists()) {
				try {
					operatorCrashFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (type.equals("client") && TextUtils.isEmpty(LogController.operatorClientFilePath)) {
			LogController.operatorClientFilePath = LogController.operatorFileDir + File.separator + System.currentTimeMillis() + "_client.json";
			File operatorClientFile = new File(LogController.operatorClientFilePath);
			if (!operatorClientFile.exists()) {
				try {
					operatorClientFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
