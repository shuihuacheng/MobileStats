package com.xiaomi.mobilestats.upload;

import java.io.File;

import com.xiaomi.mobilestats.controller.LogController;

import android.content.Context;

public class UploadManager {
	/**
	 * 判断是否有缓存文件
	 * @param context
	 * @return
	 */
	public static boolean isHasCacheFile(Context context){
		File cacheFileDir = new File(LogController.baseFilePath+File.separator+context.getPackageName()+File.separator+"cache");
		if(cacheFileDir.exists() && cacheFileDir.isDirectory()){
			File[] childFiles = cacheFileDir.listFiles();
			if(childFiles != null && childFiles.length>0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 取得所有缓存文件
	 * @param context
	 * @return
	 */
	public static File[] getCacheFiles(Context context){
		File cacheFileDir = new File(LogController.baseFilePath+File.separator+context.getPackageName()+File.separator+"cache");
		if(cacheFileDir.exists() && cacheFileDir.isDirectory()){
			File[] childFiles = cacheFileDir.listFiles();
			if(childFiles != null && childFiles.length>0){
				return childFiles;
			}
		}
		return null;
	}
}
