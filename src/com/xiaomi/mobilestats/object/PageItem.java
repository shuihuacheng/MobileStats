package com.xiaomi.mobilestats.object;

public class PageItem {
	private String pageName;
	private long startTime;
	private long endTime;
	private long duration;

	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	@Override
	public String toString() {
		return "PageItem [pageName=" + pageName + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", duration=" + duration + "]";
	}
	
	
}
