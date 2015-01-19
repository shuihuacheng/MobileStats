package com.xiaomi.mobilestats.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.xiaomi.mobilestats.XMAgent;

public class MAlarmReceiver extends BroadcastReceiver{
	  public static final String ALARM_ACTION = "com.xiaomi.mobilestats.sendlog.action";
	  
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ALARM_ACTION)){
			//TODO 周期性check和上报日志信息
			Message msg = XMAgent.handler.obtainMessage();
			msg.what = XMAgent.MSG_ALARM_ACTION;
			msg.sendToTarget();
		}
	}

}
