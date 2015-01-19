package com.xiaomi.mobilestats.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;

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
		JSONObject existJSON=null;
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
				if(sb.length()!=0){
					 existJSON = new JSONObject(sb.toString());
					  	Iterator iterator = jsonObject.keys();
						
						while(iterator.hasNext()){
							String key = (String) iterator.next();
							JSONArray newData = jsonObject.getJSONArray(key);
							
							if(existJSON.has(key)){
								JSONArray newDataArray = existJSON.getJSONArray(key);
								newDataArray.put(newData.get(0));
							}else{
								existJSON.put(key, jsonObject.getJSONArray(key));
							}
						}
						FileOutputStream out = new FileOutputStream(filePath,false);
						out.write(existJSON.toString().getBytes());
						out.flush();
						out.close();
						
				}else{
					Iterator iterator = jsonObject.keys();
					JSONObject newJsonObject = new JSONObject();
					while(iterator.hasNext()){
						String key = (String) iterator.next();
						JSONArray array = jsonObject.getJSONArray(key);
						newJsonObject.put(key, array);
					}
					newJsonObject.put("appkey", CommonUtil.getAppKey(context));
					FileOutputStream out = new FileOutputStream(filePath,false);
					out.write(jsonObject.toString().getBytes());
					out.flush();
					out.close();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
