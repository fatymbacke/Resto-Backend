package com.app.manage_restaurant.dtos.response;

public class PermissionHomeResponse {
    private String name;
    private String code;
    private String module;
    private boolean moduleActive;
    private boolean active;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isModuleActive() {
		return moduleActive;
	}
	public void setModuleActive(boolean moduleActive) {
		this.moduleActive = moduleActive;
	}
   
    
   
}