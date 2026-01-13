package com.app.manage_restaurant.entities;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.AuditableEntity;
@Table("opening_hour")
public class OpeningHour extends AuditableEntity<UUID>{	
	@Column("days")
	private Integer days;
	@Column("open")
	private String	open;
	@Column("close")
	private String close;
	@Column("is_closed")
	private boolean isClosed;
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	public String getClose() {
		return close;
	}
	public void setClose(String close) {
		this.close = close;
	}
	public boolean isClosed() {
		return isClosed;
	}
	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
	public OpeningHour(Integer days, String open, String close, boolean isClosed) {
		super();
		this.days = days;
		this.open = open;
		this.close = close;
		this.isClosed = isClosed;
	}
	public OpeningHour() {
		super();
	}
	
	
	
	
	
}
