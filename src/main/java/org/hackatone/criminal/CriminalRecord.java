package org.hackatone.criminal;

import org.apache.log4j.Logger;

public class CriminalRecord {
	private static Logger LOGGER = Logger.getLogger(CriminalRecord.class.getPackage().getName());

	private String Case_Number;
	private String Date;
	private String Time_24HR;
	private String Address;
	private String UCR_1_Category;
	private String UCR_1_Description;
	private String UCR_1_Code;
	private String UCR_2_Category;
	private String UCR_2_Description;
	private String UCR_2_Code;
	private String Neighborhood;
	private String geom;
	
	public String getCase_Number() {
		return Case_Number;
	}
	public void setCase_Number(String case_Number) {
		Case_Number = case_Number;
	}
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public String getTime_24HR() {
		return Time_24HR;
	}
	public void setTime_24HR(String time_24hr) {
		Time_24HR = time_24hr;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getUCR_1_Category() {
		return UCR_1_Category;
	}
	public void setUCR_1_Category(String uCR_1_Category) {
		UCR_1_Category = uCR_1_Category;
	}
	public String getUCR_1_Description() {
		return UCR_1_Description;
	}
	public void setUCR_1_Description(String uCR_1_Description) {
		UCR_1_Description = uCR_1_Description;
	}
	public String getUCR_1_Code() {
		return UCR_1_Code;
	}
	public void setUCR_1_Code(String uCR_1_Code) {
		UCR_1_Code = uCR_1_Code;
	}
	public String getUCR_2_Category() {
		return UCR_2_Category;
	}
	public void setUCR_2_Category(String uCR_2_Category) {
		UCR_2_Category = uCR_2_Category;
	}
	public String getUCR_2_Description() {
		return UCR_2_Description;
	}
	public void setUCR_2_Description(String uCR_2_Description) {
		UCR_2_Description = uCR_2_Description;
	}
	public String getUCR_2_Code() {
		return UCR_2_Code;
	}
	public void setUCR_2_Code(String uCR_2_Code) {
		UCR_2_Code = uCR_2_Code;
	}
	public String getNeighborhood() {
		return Neighborhood;
	}
	public void setNeighborhood(String neighborhood) {
		Neighborhood = neighborhood;
	}
	public String getGeom() {
		return geom;
	}
	public void setGeom(String geom) {
		this.geom = geom;
	}
}
