package com.xiaomi.mobilestats.controller;

import java.io.File;

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
import com.xiaomi.mobilestats.upload.UploadManager;

public class LogController {
	  private static final String TAG = "LogController";
	  private static HandlerThread logThread = new HandlerThread("LogSenderThread");
	  public  static boolean isOnlyWifi = false;
	  public  SendStrategyEnum sendStragegy = SendStrategyEnum.REAL_TIME;
	  public  int logSendIntervalHour = 1;
	  public  int logSendDelayedTime = 0;
	  private static Handler handler;
	  //日志缓存目录根目录
	  public static String baseFilePath = Environment.getExternalStorageDirectory()+File.separator;
	  public static String operatorFileDir = baseFilePath+"cache"+File.separator+"operator";
	  public static String uploadFileDir = baseFilePath+"cache"+File.separator+"upload";
	  
	  public static String operatorEventFilePath = "";
	  public static String operatorPageFilePath = "";
	  public static String operatorCrashFilePath = "";
	  
	  private static LogController instance  = new LogController();

	  private LogController()
	  {
		  logThread.start();
		  handler = new Handler(logThread.getLooper());
		  Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				if(sendStragegy.equals(SendStrategyEnum.REAL_TIME) ){
					if(UploadManager.isHasCacheFile()){
						UploadManager.uploadCachedUploadFiles(handler);
					}
					handler.postDelayed(this, CommonConfig.update_check_inteval);
				}
			}
		};
		handler.postDelayed(runnable, 1000);
	  }
	  
	  public static LogController geInstance(){
		  return instance;
	  }
	  
	  public void setSendDelayedTime(int seconds){
		  if(seconds >= 0 && seconds<=30){
			  this.logSendDelayedTime = seconds;
		  }
	  }
	  
	  public void setSendStrategy(Context context,SendStrategyEnum sendStrategyEnum,int timeInterval,boolean onWifi){
		  if(sendStrategyEnum.equals(SendStrategyEnum.SET_TIME_INTERVAL)){
			  if(timeInterval<1){
				  timeInterval = 1;
			  }else if(timeInterval>24){
				  timeInterval = 24;
			  }
			  if(timeInterval >=1 && timeInterval <=24){
				  this.logSendIntervalHour = timeInterval;
				  this.sendStragegy = SendStrategyEnum.SET_TIME_INTERVAL;
			        BasicStoreTools.getInstance().setSendStrategy(context, this.sendStragegy.ordinal());
			        BasicStoreTools.getInstance().setSendStrategyTime(context, this.logSendIntervalHour);
			        
					 AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					 Intent intent = new Intent(context,MAlarmReceiver.class);
					 intent.setAction(MAlarmReceiver.ALARM_ACTION);
					 PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,0);
					 alarm.cancel(pendingIntent);
					 alarm.setRepeating(AlarmManager.RTC_WAKEUP, CommonConfig.kContinueSessionMillis, timeInterval*3600000, pendingIntent);
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
			 }else{
				 AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				 Intent intent = new Intent(context,MAlarmReceiver.class);
				 intent.setAction(MAlarmReceiver.ALARM_ACTION);
				 PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,0);
				 alarm.cancel(pendingIntent);
			 }
		 }
		  LogController.isOnlyWifi = onWifi;
	  }
}
