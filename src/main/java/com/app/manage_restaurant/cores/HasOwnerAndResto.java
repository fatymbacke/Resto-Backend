package com.app.manage_restaurant.cores;

public interface HasOwnerAndResto <U>{
	public U getOwnerCode();
	public void setOwnerCode(U ownerCode);
	public  U getId() ;
	public  void setId(U id) ;

	public  U getRestoCode();
	public  void setRestoCode(U restoCode);
}
