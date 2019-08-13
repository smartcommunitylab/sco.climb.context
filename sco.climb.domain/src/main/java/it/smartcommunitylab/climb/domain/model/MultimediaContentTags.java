package it.smartcommunitylab.climb.domain.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public class MultimediaContentTags {
	@Id
	private String id;
	private List<String> subjects = new ArrayList<>();
	private List<String> schoolYears = new ArrayList<>();
	@Transient
	private List<String> classes = new ArrayList<>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
	public List<String> getSchoolYears() {
		return schoolYears;
	}
	public void setSchoolYears(List<String> schoolYears) {
		this.schoolYears = schoolYears;
	}
	public List<String> getClasses() {
		return classes;
	}
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}
	
}
