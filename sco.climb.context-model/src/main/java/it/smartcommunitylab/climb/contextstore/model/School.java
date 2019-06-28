package it.smartcommunitylab.climb.contextstore.model;

import java.util.ArrayList;
import java.util.List;

public class School extends BaseObject {
	private String name;
	private String address;
	private String instituteId;
	private List<ClassRoom> classes = new ArrayList<>();
	private String type;
	
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
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<ClassRoom> getClasses() {
		return classes;
	}
	public void setClasses(List<ClassRoom> classes) {
		this.classes = classes;
	}
}
