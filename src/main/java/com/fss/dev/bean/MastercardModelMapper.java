package com.fss.dev.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MastercardModelMapper {
	private String Tag;
	private String ID;
	private String Type;
	private String Length;
	private String MaxLength;
	private String SubElementFlag;
	private String PDSFlag;
	private String FieldName;
	private String multiple;
	private String table;
	
	private List<MastercardModelMapper> subElement;
	private List<MastercardModelMapper> subPdsElement;
	private List<MastercardModelMapper> childElements;

	public MastercardModelMapper(String tag, String id, String type, String length, String maxLength, String subElementFlag,
			String pdsFlag, String fieldName, String multiple) {
		Tag = tag;
		ID = id;
		Type = type;
		Length = length;
		MaxLength = maxLength;
		SubElementFlag = subElementFlag;
		PDSFlag = pdsFlag;
		FieldName = fieldName;
		this.multiple = multiple;
	}

	public MastercardModelMapper(String tag, String id, String type, String length, String maxLength, String subElementFlag,
			String pdsFlag, String fieldName, String multiple,String table) {
		Tag = tag;
		ID = id;
		Type = type;
		Length = length;
		MaxLength = maxLength;
		SubElementFlag = subElementFlag;
		PDSFlag = pdsFlag;
		FieldName = fieldName;
		this.multiple = multiple;
		this.table = table;
	}
	
	public String getTag() {
		return Tag;
	}

	public void setTag(String tag) {
		Tag = tag;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getLength() {
		return Length;
	}

	public void setLength(String length) {
		Length = length;
	}

	public String getMaxLength() {
		return MaxLength;
	}

	public void setMaxLength(String maxLength) {
		MaxLength = maxLength;
	}

	public String getSubElementFlag() {
		return SubElementFlag;
	}

	public void setSubElementFlag(String subElementFlag) {
		SubElementFlag = subElementFlag;
	}

	public String getPDSFlag() {
		return PDSFlag;
	}

	public void setPDSFlag(String pDSFlag) {
		PDSFlag = pDSFlag;
	}

	public String getFieldName() {
		return FieldName;
	}

	public void setFieldName(String fieldName) {
		FieldName = fieldName;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public List<MastercardModelMapper> getSubElement() {
		return subElement;
	}

	public void setSubElement(List<MastercardModelMapper> subElement) {
		this.subElement = subElement;
	}

	public List<MastercardModelMapper> getSubPdsElement() {
		return subPdsElement;
	}

	public void setSubPdsElement(List<MastercardModelMapper> subPdsElement) {
		this.subPdsElement = subPdsElement;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}
	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public List<MastercardModelMapper> getChildElements() {
		return childElements;
	}

	public void setChildElements(List<MastercardModelMapper> childElements) {
		this.childElements = childElements;
	}

	@Override
	public String toString() {
		return "Data [ID=" + ID + ", Type=" + Type + ", Length=" + Length + ", MaxLength=" + MaxLength
				+ ", SubElementFlag=" + SubElementFlag + ", PDSFlag=" + PDSFlag + ", FieldName=" + FieldName
				+ ", subElement=" + subElement + ", subPdsElement=" + subPdsElement + "]";
	}

}
