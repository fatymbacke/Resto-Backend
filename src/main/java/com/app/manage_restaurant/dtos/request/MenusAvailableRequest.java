package com.app.manage_restaurant.dtos.request;
import java.util.UUID;

public class MenusAvailableRequest {
 private UUID id;
 private boolean available ;
 
 private boolean state ;

public UUID getId() {
	return id;
}
public void setId(UUID id) {
	this.id = id;
}
public boolean getAvailable() {
	return available;
}
public void setAvailable(boolean isAvailable) {
	this.available = isAvailable;
}

public boolean isState() {
	return state;
}
public void setState(boolean state) {
	this.state = state;
}
@Override
public String toString() {
	return "MenusAvailableRequest [id=" + id + ", available=" + available + ", state=" + state + "]";
}


 
}