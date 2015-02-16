package com.xiaomi.mobilestats.upload;

import java.io.File;

import android.os.Handler;

import com.xiaomi.mobilestats.common.StringUtils;
import com.xiaomi.mobilestats.controller.LogController;
import com.xiaomi.mobilestats.data.ReadFromFileThead;

public class UploadManager {
	private static final String TAG = "UploadManager";

	/**
	 * 判断是否有缓存文件
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isHasCacheFile() {
		try {
			if (StringUtils.isEmpty(LogController.uploadFileDir)) {
				return false;
			}
			File cacheFileDir = new File(LogController.uploadFileDir);
			if (cacheFileDir.exists() && cacheFileDir.isDirectory()) {
				File[] childFiles = cacheFileDir.listFiles();
				if (childFiles != null && childFiles.length > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 取得所有缓存文件
	 * @param context
	 * @return
	 */
	public static File[] getCacheFiles() {
		try {
			File cacheFileDir = new File(LogController.uploadFileDir);
			if (cacheFileDir.exists() && cacheFileDir.isDirectory()) {
				File[] childFiles = cacheFileDir.listFiles();
				if (childFiles != null && childFiles.length > 0) {
					return childFiles;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void uploadCachedUploadFiles(Handler handler) {
		try {
			File[] uploadFiles = getCacheFiles();
			if (uploadFiles != null && uploadFiles.length > 0) {
				for (File file : uploadFiles) {
					Thread readFileThread = new ReadFromFileThead(file.getPath());
					if (handler != null) {
						handler.post(readFileThread);
					} else {
						new Handler().post(readFileThread);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
