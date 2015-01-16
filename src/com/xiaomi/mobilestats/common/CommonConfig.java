package com.xiaomi.mobilestats.common;

public class CommonConfig {
    public static boolean DEBUG_MODE = true;
    
    public static long kContinueSessionMillis =  30000L;
    public static final Object saveOnlineConfigMutex = new Object();	
    public static final String eventUrl="/xm/postEvent";
    public static final String errorUrl = "/xm/postErrorLog";
    public static final String clientDataUrl = "/xm/postClientData";
    public static final String updataUrl = "/xm/getApplicationUpdate";
    public static final String activityUrl = "/xm/postActivityLog";
    public static final String onlineConfigUrl ="/xm/getOnlineConfiguration";
    public static final String uploadUrl = "/xm/uploadLog";
    public static final String tagUser="/xm/postTag";
    public static String PREURL="";                 //可配置服务器地址URL
}
