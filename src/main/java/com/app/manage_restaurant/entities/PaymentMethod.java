package com.app.manage_restaurant.entities;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.AuditableEntity;

@Table("payment_methods")
public class PaymentMethod extends AuditableEntity<UUID>{
    
    @Column("name")
    private String name;
    
    @Column("type")
    private String type; // 'credit_card', 'debit_card', 'cash', 'mobile_money', 'bank_transfer', 'digital_wallet'
    
    @Column("is_enabled")
    private Boolean isEnabled;
    
    @Column("processing_fee")
    private Double processingFee;
    
    @Column("requires_authentication")
    private Boolean requiresAuthentication;
    
    @Column("description")
    private String description;
    
    @Column("configuration")
    private Map<String, Object> configuration;

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

	public Map<String, Object> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, Object> configuration) {
		this.configuration = configuration;
	}
    
   
    
    
    
}
