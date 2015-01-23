package com.xiaomi.mobilestats.data;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.mobilestats.common.CommonUtil;

public class WriteFileThread extends Thread {
	public static final String ENCODE_KEY = "c0e9fcff59ecc3b8b92939a1a2724a44";
	private Context context;
	private String filePath;
	private String data;

	public WriteFileThread(Context context, String filePath,String data) {
		super();
		this.context = context;
		this.filePath = filePath;
		this.data = data;
	}

	@Override
	public void run() {
		super.run();
		writeToFile(this.data);
	}

	private void writeToFile(String data) {
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && CommonUtil.checkPermissions(context, "android.permission.WRITE_EXTERNAL_STORAGE")) {
				File file = new File(filePath);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream out = new FileOutputStream(filePath, true);
				if(!TextUtils.isEmpty(data)){
					Log.i("test",data);
					out.write(data.getBytes());
				}
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
