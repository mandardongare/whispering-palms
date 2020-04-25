package com.fss.dev.bean;

public class DataModel {
	private String column;
	private String value;
	
	
	public DataModel() {
		super();
	}
	public DataModel(String column, String value) {
		super();
		this.column = column;
		this.value = value;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		return "["+column+","+value+"]";
	}
	
}
