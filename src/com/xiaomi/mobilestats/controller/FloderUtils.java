package com.xiaomi.mobilestats.controller;

import java.io.File;

public class FloderUtils {
	// 递归删除文件和文件夹
	public static void deleteFloder(File file) {
		if (file == null) {
			return;
		}
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				deleteFloder(f);
			}
			file.delete();
		}
	}

	public static boolean checkOrCreateFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdirs();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
