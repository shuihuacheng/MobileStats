package com.xiaomi.mobilestats.object;

public class CDMACell extends SCell{
	  public int stationId;
	  public int networkId;
	  public int systemId;
	  
	  public String toString()
	  {
	    return this.cellType + "," + this.MCCMNC + "," + this.MCC + "," + this.MNC + "" + this.stationId + "," + this.networkId + "," + this.systemId;
	  }
}
