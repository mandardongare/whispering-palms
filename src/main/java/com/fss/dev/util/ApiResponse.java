package com.fss.dev.util;

public class ApiResponse {
	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String STATUS_FAIL= "FAIL";
	
	public Object apiResponse;
	public String apiError;
	public String status;

	public Object getApiResponse() {
		return apiResponse;
	}
	public void setApiResponse(Object apiResponse) {
		this.apiResponse = apiResponse;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getApiError() {
		return apiError;
	}
	public void setApiError(String apiError) {
		this.apiError = apiError;
	}
	
	
}
