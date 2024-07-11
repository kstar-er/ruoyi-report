package com.ruoyi.colorfulfog.model.entity;

import java.util.Date;

public class DateRange {
	private long start;
	private long end;

	public DateRange(Date start, Date end) {
		this.start = start.getTime();
		this.end = end.getTime();
	}

	public long getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start.getTime();
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end.getTime();
	}

}
