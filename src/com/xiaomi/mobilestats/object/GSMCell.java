package com.xiaomi.mobilestats.object;

public class GSMCell extends SCell{
	public int LAC;		//位置区码
	public int CID;    //手机平台版本
	
	@Override
	public String toString() {
	    return this.cellType + "," + this.MCCMNC + "," + this.MCC + "," + this.MNC + "" + this.LAC + "," + this.CID;
	}
	
}
