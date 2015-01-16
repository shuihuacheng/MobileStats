package com.xiaomi.mobilestats.controller;

import java.lang.ref.WeakReference;
import java.util.Timer;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.xiaomi.mobilestats.data.BasicStoreTools;
import com.xiaomi.mobilestats.data.SendStrategyEnum;

public class LogController {
	  private static HandlerThread logThread = new HandlerThread("LogSenderThread");
	  private boolean isOnWifi = false;
	  private SendStrategyEnum sendStragegy = SendStrategyEnum.APP_START;
	  private int logSendIntervalHour = 1;
	  private Timer e;
	  private int logSendDelayedTime = 0;
	  private boolean g = false;
	  private WeakReference<Context> contextWR;
	  private static Handler handler;
	  private static LogController instance  = new LogController();

	  private LogController()
	  {
		  logThread.start();
		  handler = new Handler(logThread.getLooper());
	  }
	  
	  public static LogController geInstance(){
		  return instance;
	  }
	  
	  private void holdWRContext(Context context)
	  {
	    if (context == null)
	    	//TODO 记录错误
	    if ((this.contextWR == null) && (context != null))
	      this.contextWR = new WeakReference(context);
	  }
	  
	  public void setSendDelayedTime(int seconds){
		  if(seconds >= 0 && seconds<=30){
			  this.logSendDelayedTime = seconds;
		  }
	  }
	  
	  public void setSendStrategy(Context context,SendStrategyEnum sendStrategyEnum,int timeInterval,boolean onWifi){
		  if(sendStrategyEnum.equals(SendStrategyEnum.SET_TIME_INTERVAL)){
			  if(timeInterval >=1 && timeInterval <=24){
				  this.logSendIntervalHour = timeInterval;
				  this.sendStragegy = SendStrategyEnum.SET_TIME_INTERVAL;
			        BasicStoreTools.getInstance().setSendStrategy(context, this.sendStragegy.ordinal());
			        BasicStoreTools.getInstance().setSendStrategyTime(context, this.logSendIntervalHour);
			  }else{
			  //TODO timeInterval参数无效
			  }
		 }else{
			 this.sendStragegy = sendStrategyEnum;
			 BasicStoreTools.getInstance().setSendStrategy(context, this.sendStragegy.ordinal());
			 if(sendStrategyEnum.equals(SendStrategyEnum.ONCE_A_DAY)){
				 BasicStoreTools.getInstance().setSendStrategyTime(context, 24);
			 }
		 }
		  this.isOnWifi = onWifi;
	  }
}
