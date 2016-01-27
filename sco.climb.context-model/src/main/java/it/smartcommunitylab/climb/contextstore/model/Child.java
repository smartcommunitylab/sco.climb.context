package it.smartcommunitylab.climb.contextstore.model;

public class Child extends BaseObject {
	private String id;
	private String externalId;
	private String name;
	private String parentName;
	private String phone;
	private String schoolId;
	private String classRoom;
	private int wsnId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
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
	public String getClassRoom() {
		return classRoom;
	}
	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}
	public int getWsnId() {
		return wsnId;
	}
	public void setWsnId(int wsnId) {
		this.wsnId = wsnId;
	}
}
