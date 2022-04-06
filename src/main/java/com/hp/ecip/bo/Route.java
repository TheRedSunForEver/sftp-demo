package com.hp.ecip.bo;

public class Route {
	private String routeId;
	
	private String businessId;
	
	private String oInstanceId;
	
	private String oUserName;
	
	private String oPartyId;
	
	private String oPath;
	
	private String hInstanceId;
	
	private String hUserName;
	
	private String hPartyId;
	
	private String hPath;

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getoInstanceId() {
		return oInstanceId;
	}

	public void setoInstanceId(String oInstanceId) {
		this.oInstanceId = oInstanceId;
	}

	public String getoUserName() {
		return oUserName;
	}

	public void setoUserName(String oUserName) {
		this.oUserName = oUserName;
	}

	public String getoPartyId() {
		return oPartyId;
	}

	public void setoPartyId(String oPartyId) {
		this.oPartyId = oPartyId;
	}

	public String getoPath() {
		return oPath;
	}

	public void setoPath(String oPath) {
		this.oPath = oPath;
	}

	public String gethInstanceId() {
		return hInstanceId;
	}

	public void sethInstanceId(String hInstanceId) {
		this.hInstanceId = hInstanceId;
	}

	public String gethUserName() {
		return hUserName;
	}

	public void sethUserName(String hUserName) {
		this.hUserName = hUserName;
	}

	public String gethPartyId() {
		return hPartyId;
	}

	public void sethPartyId(String hPartyId) {
		this.hPartyId = hPartyId;
	}

	public String gethPath() {
		return hPath;
	}

	public void sethPath(String hPath) {
		this.hPath = hPath;
	}
}
