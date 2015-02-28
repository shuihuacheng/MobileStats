package com.xiaomi.mobilestats.controller;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.text.TextUtils;

public class FileLruCache {
	
	private static FileLruCache instance = null;
	
	private FileLruCache() {
		super();
	}

	public static FileLruCache getInstance(){
		if(instance == null){
			instance = new FileLruCache();
		}
		return instance;
	}
	
	//文件信息LRU缓存大小
	private final static int FILE_CACHE_CAPACITY = 20;
	private static HashMap<String,File> mLogFileCache = new LinkedHashMap<String, File>(FILE_CACHE_CAPACITY, 0.75f, true){

		@Override
		protected boolean removeEldestEntry(Entry<String, File> eldest) {
			if(size()>FILE_CACHE_CAPACITY){
				File eldestFile = eldest.getValue();
				if(eldestFile != null && eldestFile.exists()){
					eldestFile.delete();
				}
				return true;
			}else{
				return false;
			}
		}
	};
	
	public  HashMap<String,File> getCacheHashMap(){
		return mLogFileCache;
	}
	
	public  void putFile(String fileName,File file){
		if(mLogFileCache != null && !mLogFileCache.containsKey(fileName)){
			mLogFileCache.put(fileName, file);
		}
	}
	
	public File getFile(String fileName){
		if(mLogFileCache != null && mLogFileCache.containsKey(fileName)){
			return mLogFileCache.get(fileName);
		}
		return null;
	}
	
	public  void removeFile(String fileName){
		if(TextUtils.isEmpty(fileName)) return;
		if(mLogFileCache != null && mLogFileCache.containsKey(fileName)){
			mLogFileCache.remove(fileName);
		}
	}
	
	public  void clearCache(){
		if(mLogFileCache != null){
			mLogFileCache.clear();
		}
	}
}
