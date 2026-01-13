package com.app.manage_restaurant.dtos.request;
import java.util.Map;
import java.util.UUID;

public class PaymentMethodRequest {
	 private UUID id;
	    private String name;
	    private String type;
	    private Boolean isEnabled;
	    private Double processingFee;
	    private Boolean requiresAuthentication;
	    private String description;
	    private UUID restoCode;
	    private UUID ownerCode;
	    private Map<String, Object> configuration;
	    private Boolean active;
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public Double getProcessingFee() {
		return processingFee;
	}
	public void setProcessingFee(Double processingFee) {
		this.processingFee = processingFee;
	}
	public Boolean getRequiresAuthentication() {
		return requiresAuthentication;
	}
	public void setRequiresAuthentication(Boolean requiresAuthentication) {
		this.requiresAuthentication = requiresAuthentication;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public UUID getRestoCode() {
		return restoCode;
	}
	public void setRestoCode(UUID restoCode) {
		this.restoCode = restoCode;
	}
	public UUID getOwnerCode() {
		return ownerCode;
	}
	public void setOwnerCode(UUID ownerCode) {
		this.ownerCode = ownerCode;
	}
	public Map<String, Object> getConfiguration() {
		return configuration;
	}
	public void setConfiguration(Map<String, Object> configuration) {
		this.configuration = configuration;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
    
    
}