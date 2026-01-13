package com.app.manage_restaurant.dtos.request;

import java.util.UUID;

public class OpeningHourRequest {

    private UUID id;        // Optionnel, si on veut mettre à jour un horaire existant
    private Integer days;   // 1 = Lundi, 7 = Dimanche
    private String open;    // Heure d'ouverture, ex: "08:00"
    private String close;   // Heure de fermeture, ex: "22:00"
    private Boolean isClosed; // true si fermé ce jour

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

	@Override
	public String toString() {
		return "OpeningHourRequest [id=" + id + ", days=" + days + ", open=" + open + ", close=" + close + ", isClosed="
				+ isClosed + "]";
	}
    
}
