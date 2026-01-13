package com.app.manage_restaurant.dtos.response;

import java.util.UUID;

public class OpeningHourResponse {

    private UUID id;
    private Integer days;
    private String open;
    private String close;
    private Boolean isClosed;

    // =====================
    // GETTERS & SETTERS
    // =====================
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }
}
