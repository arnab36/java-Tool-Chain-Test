package com.ibm.contracts.advisor.vo;

public class StrategyObject {

	private String userId = null;
	private String strategyName = null;
	private String accessType = null;
	private String strategyType = null;

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public void setStrategyType(String strategyType) {
		this.strategyType = strategyType;
	}

	public String getUserId() {
		return this.userId;
	}

	public String getStrategyName() {
		return this.strategyName;
	}

	public String getAccessType() {
		return this.accessType;
	}

	public String getStrategyType() {
		return this.strategyType;
	}

}