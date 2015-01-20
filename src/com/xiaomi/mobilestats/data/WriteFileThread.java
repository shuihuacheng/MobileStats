package com.xiaomi.mobilestats.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;

import com.xiaomi.mobilestats.common.CommonUtil;

public class WriteFileThread extends Thread{
	private Context context;
	private String filePath;
	private JSONObject jsonObject;

	public WriteFileThread(Context context,String filePath,JSONObject jsonObject) {
		super();
		this.context = context;
		this.filePath = filePath;
		this.jsonObject = jsonObject;
	}
	
	@Override
	public void run() {
		super.run();
		writeToFile(this.jsonObject);
	}
	
	private void writeToFile(JSONObject jsonObject){
		try {
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && CommonUtil.checkPermissions(context, "android.permission.WRITE_EXTERNAL_STORAGE")){
				File file = new File(filePath);
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				if(!file.exists()){
					file.createNewFile();
				}
				FileInputStream in = new FileInputStream(file);
				StringBuffer sb = new StringBuffer();
				int i=0;
				byte[] s = new byte[1024*4];
				while((i=in.read(s))!=-1){
					sb.append(new String(s,0,i));
				}
				sb.append(Base64.encodeToString(jsonObject.toString().getBytes(), Base64.DEFAULT)+"\r\n");
				FileOutputStream out = new FileOutputStream(filePath,false);
				out.write(sb.toString().getBytes());
				out.flush();
				out.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
