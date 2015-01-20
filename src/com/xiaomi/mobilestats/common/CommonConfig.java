package com.xiaomi.mobilestats.common;

public class CommonConfig {
    public static boolean DEBUG_MODE = true;
    public static String PREURL="http://10.237.2.123:8002/v1/mishop/";                 //可配置服务器地址URL
    public static long kContinueSessionMillis =  30000L;
    public static long update_check_inteval = 60000L;                      								//定时检测上传文件夹时间间隔
    
//    public static final Object saveOnlineConfigMutex = new Object();	
//    public static final String eventUrl="/xm/postEvent";
//    public static final String errorUrl = "/xm/postErrorLog";
//    public static final String clientDataUrl = "/xm/postClientData";
//    public static final String updataUrl = "/xm/getApplicationUpdate";
//    public static final String activityUrl = "/xm/postActivityLog";
//    public static final String onlineConfigUrl ="/xm/getOnlineConfiguration";
//    public static final String uploadUrl = "/xm/uploadLog";
}
