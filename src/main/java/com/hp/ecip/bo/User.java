package com.hp.ecip.bo;

public class User {
	private String userId;
	
	private String instanceId;
	
	private String partyId;
	
	private String userName;
	
	private String userPassword;
	
	private String homeDirectory;
	
	private Integer enableFlag;
	
	private Integer writePermission;
	
	private Integer maxLoginNumber;
	
	private Integer maxLoginPerip;
	
	private Integer idleTime;
	
	private Integer uploadRate;
	
	private Integer downloadRate;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getHomeDirectory() {
		return homeDirectory;
	}

	public void setHomeDirectory(String homeDirectory) {
		this.homeDirectory = homeDirectory;
	}

	public Integer getEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(Integer enableFlag) {
		this.enableFlag = enableFlag;
	}

	public Integer getWritePermission() {
		return writePermission;
	}

	public void setWritePermission(Integer writePermission) {
		this.writePermission = writePermission;
	}

	public Integer getMaxLoginNumber() {
		return maxLoginNumber;
	}

	public void setMaxLoginNumber(Integer maxLoginNumber) {
		this.maxLoginNumber = maxLoginNumber;
	}

	public Integer getMaxLoginPerip() {
		return maxLoginPerip;
	}

	public void setMaxLoginPerip(Integer maxLoginPerip) {
		this.maxLoginPerip = maxLoginPerip;
	}

	public Integer getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(Integer idleTime) {
		this.idleTime = idleTime;
	}

	public Integer getUploadRate() {
		return uploadRate;
	}

	public void setUploadRate(Integer uploadRate) {
		this.uploadRate = uploadRate;
	}

	public Integer getDownloadRate() {
		return downloadRate;
	}

	public void setDownloadRate(Integer downloadRate) {
		this.downloadRate = downloadRate;
	}
}
