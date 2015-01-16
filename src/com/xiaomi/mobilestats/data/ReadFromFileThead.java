package com.xiaomi.mobilestats.data;

import java.io.File;
import java.io.FileInputStream;

public class ReadFromFileThead extends Thread{
	private String filePath;
	
	public ReadFromFileThead(String filePath){
		super();
		this.filePath = filePath;
	}

	@Override
	public void run() {
		super.run();
		String fileContent = readFile(filePath);
		//TODO 上传文件内容
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
