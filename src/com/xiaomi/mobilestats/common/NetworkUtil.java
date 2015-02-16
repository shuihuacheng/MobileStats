package com.xiaomi.mobilestats.common;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

import com.xiaomi.mobilestats.object.Msg;

public class NetworkUtil {

	private static final int REQUEST_TIMEOUT = 10 * 1000; // 设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟

	public static Msg httpClientPost(String url, String data) {
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
			result = EntityUtils.toString(response.getEntity());
			if (status == HttpStatus.SC_OK) {
				msg.setFlag(true);
				msg.setMsg(result);
			} else {
				msg.setFlag(false);
				msg.setMsg(result);
			}
		} catch (Exception e) {
			msg.setFlag(false);
			msg.setMsg(result);
		}
		return msg;
	}

	public static Msg post(String urlStr, String data) {
		Msg msg = new Msg();
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/plain");
			conn.setDoOutput(true);
			conn.setConnectTimeout(REQUEST_TIMEOUT);
			conn.setReadTimeout(SO_TIMEOUT);
			OutputStream out = conn.getOutputStream();
			out.write(data.getBytes());
			out.flush();
			out.close();
			int responseCode = conn.getResponseCode();
			CommonUtil.printLog(CommonConfig.TAG, "reqsponseCode:" + responseCode);
			if (responseCode == HttpStatus.SC_OK) {
				msg.setFlag(true);
			} else {
				msg.setFlag(false);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.setFlag(false);
		}
		return msg;
	}

}
