package com.xiaomi.mobilestats.common;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.xiaomi.mobilestats.object.Msg;

public class NetworkUtil {
	
	public static Msg post(String url, String data) {
		String result = "";
		Msg msg = new Msg();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		try {
			StringEntity se = new StringEntity(data, HTTP.UTF_8);
			se.setContentType("text/plain");
			httppost.setEntity(se);
			HttpResponse response = httpclient.execute(httppost);
			int status = response.getStatusLine().getStatusCode();
			result  = EntityUtils.toString(response.getEntity());
			Log.i("test","NetworkUtil--->result:"+result);
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
