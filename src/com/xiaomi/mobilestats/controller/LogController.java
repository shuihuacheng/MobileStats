package com.xiaomi.mobilestats.controller;

import java.io.File;
import java.lang.ref.WeakReference;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.xiaomi.mobilestats.common.CommonConfig;
import com.xiaomi.mobilestats.data.BasicStoreTools;
import com.xiaomi.mobilestats.data.SendStrategyEnum;
import com.xiaomi.mobilestats.receiver.MAlarmReceiver;

public class LogController {
	  private static HandlerThread logThread = new HandlerThread("LogSenderThread");
	  public  static boolean isOnlyWifi = false;
	  public  SendStrategyEnum sendStragegy = SendStrategyEnum.APP_START;
	  public  int logSendIntervalHour = 1;
	  public  int logSendDelayedTime = 0;
	  private WeakReference<Context> contextWR;
	  private static Handler handler;
	  //日志缓存目录根目录
	  public static String baseFilePath = Environment.getExternalStorageDirectory()+File.separator;
	  public static String operatorEventFilePath = "";
	  public static String operatorPageFilePath = "";
	  public static String operatorCrashFilePath = "";
	  
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
			        
					 AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					 Intent intent = new Intent(MAlarmReceiver.ALARM_ACTION);
					 PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
					 alarm.setRepeating(AlarmManager.RTC_WAKEUP, CommonConfig.kContinueSessionMillis, timeInterval*3600000, pendingIntent);
			  }else{
			  //TODO timeInterval参数无效
			  }
		 }else{
			 this.sendStragegy = sendStrategyEnum;
			 BasicStoreTools.getInstance().setSendStrategy(context, this.sendStragegy.ordinal());
			 if(sendStrategyEnum.equals(SendStrategyEnum.ONCE_A_DAY)){
				 BasicStoreTools.getInstance().setSendStrategyTime(context, 24);
				 
				 AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				 Intent intent = new Intent(MAlarmReceiver.ALARM_ACTION);
				 PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				 alarm.setRepeating(AlarmManager.RTC_WAKEUP, CommonConfig.kContinueSessionMillis, 24*3600000, pendingIntent);
			 }
		 }
		  LogController.isOnlyWifi = onWifi;
	  }
}
