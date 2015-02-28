package com.xiaomi.mobilestats.data;

import java.io.File;
import java.io.FileInputStream;

import com.xiaomi.mobilestats.common.CommonConfig;
import com.xiaomi.mobilestats.common.NetworkUtil;
import com.xiaomi.mobilestats.controller.FileLruCache;
import com.xiaomi.mobilestats.object.Msg;

public class ReadFromFileThead extends Thread{
	private static final String TAG = "ReadFromFileThead";
	private String filePath;
	
	public ReadFromFileThead(String filePath){
		super();
		this.filePath = filePath;
	}

	@Override
	public void run() {
		super.run();
		String fileContent = readFile(filePath);
//		CommonUtil.printLog(TAG, "filrePath:"+filePath);
		Msg msg = NetworkUtil.post(CommonConfig.PREURL, fileContent);
//		CommonUtil.printLog(TAG, "msg.flag:"+msg.isFlag());
		if(msg.isFlag()){
			File file = new File(filePath);
			if(file.exists()) file.delete();
			FileLruCache.getInstance().removeFile(file.getName());
		}
	}
	
	private String readFile(String filePath){
		String result = "";
		File file;
		FileInputStream in;
		try {
			file = new File(filePath);
			if(!file.exists()){
				return result;
			}
			in = new FileInputStream(file);
			StringBuffer sb = new StringBuffer();
			int i = 0;
			byte[] b = new byte[1014];
			while((i=in.read(b)) != -1){
				sb.append(new String(b,0,i));
			}
			result = sb.toString();
			if(in !=null){
				in.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
