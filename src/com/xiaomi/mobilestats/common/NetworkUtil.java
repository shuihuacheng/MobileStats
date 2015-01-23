package com.xiaomi.mobilestats.common;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.xiaomi.mobilestats.object.Msg;

public class NetworkUtil {
	
	private static final int REQUEST_TIMEOUT = 10*1000;	//设置请求超时10秒钟  
    private static final int SO_TIMEOUT = 10*1000; 				 //设置等待数据超时时间10秒钟
	
	public static Msg post(String url, String data) {
		String result = "";
		Msg msg = new Msg();
		BasicHttpParams httpParams = new BasicHttpParams();  
	    HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);  
	    HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);  
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		try {
			HttpPost httpPost = new HttpPost(url);
			StringEntity se = new StringEntity(data, HTTP.UTF_8);
			se.setContentType("text/plain");
			httpPost.setEntity(se);
			HttpResponse response = httpclient.execute(httpPost);
			int status = response.getStatusLine().getStatusCode();
			result  = EntityUtils.toString(response.getEntity());
			Log.i("test","NetworkUtil--->status:"+status+" result:"+result);
			if(status == HttpStatus.SC_OK){
				msg.setFlag(true);
				msg.setMsg(result);
			}else{
				msg .setFlag(false);
				msg .setMsg(result);
			}
		} catch (Exception e) {	
				msg .setFlag(false);
				msg .setMsg(result);
		}
		return msg ;
	}
	
}
