package it.smartcommunitylab.climb.contextstore.model;

public class ClassRoom {
	private String name;
	private String yearOfStudy;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getYearOfStudy() {
		return yearOfStudy;
	}
	public void setYearOfStudy(String yearOfStudy) {
		this.yearOfStudy = yearOfStudy;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
