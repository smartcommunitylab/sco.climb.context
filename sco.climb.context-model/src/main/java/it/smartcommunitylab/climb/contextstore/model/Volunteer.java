package it.smartcommunitylab.climb.contextstore.model;

public class Volunteer extends BaseObject {
	private String name;
	private String address;
	private String phone;
	private String schoolId;
	private String password;
	private int wsnId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public int getWsnId() {
		return wsnId;
	}
	public void setWsnId(int wsnId) {
		this.wsnId = wsnId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
