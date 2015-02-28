package com.xiaomi.mobilestats.common;

public class CommonConfig {
    public static boolean DEBUG_MODE  =  true;
    public static String PREURL="http://10.237.2.123:6015/6015/data/dapp/";   //可配置服务器地址URL
    public static long kContinueSessionMillis =  30000L;
    public static long update_check_inteval = 60000L;                      								//定时检测上传文件夹时间间隔
    public static final String DEFAULT_CACHE_DIR = "StatCache";
    public static final String TAG = "MobileStats";    //全局日志TAG
    
    public static void setDebugOn(boolean isDebugOn){
    	DEBUG_MODE = isDebugOn;
    	if(isDebugOn){
    		PREURL="http://10.237.2.123:6015/6015/data/mishop/";
    	}else{
//    		PREURL="https://api.d.xiaomi.com/6015/data/dapp/";
    		PREURL="http://data.v.mi.com/v1/data/mishop/";
    	}
    }
}
