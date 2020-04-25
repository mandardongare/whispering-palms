package com.fss.dev.bean;

import java.util.List;

public class OutputModel {
	List<DataModel> mainList;
	List<List<DataModel>> subLists;
	
	public List<DataModel> getMainList() {
		return mainList;
	}
	public void setMainList(List<DataModel> mainList) {
		this.mainList = mainList;
	}
	public List<List<DataModel>> getSubLists() {
		return subLists;
	}
	public void setSubLists(List<List<DataModel>> subLists) {
		this.subLists = subLists;
	}
}
