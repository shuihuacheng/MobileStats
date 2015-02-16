package com.xiaomi.mobilestats.controller;

import java.util.TimerTask;

import android.os.Message;

import com.xiaomi.mobilestats.StatService;

public class MTimerTask extends TimerTask{

	@Override
	public void run() {
		if(StatService.handler != null){
			Message msg = StatService.handler.obtainMessage();
			msg.what = StatService.MSG_TIMER;
			msg.sendToTarget();
		}
	}

}
