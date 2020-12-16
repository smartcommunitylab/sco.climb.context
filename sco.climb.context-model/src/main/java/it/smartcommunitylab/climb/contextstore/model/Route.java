package it.smartcommunitylab.climb.contextstore.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Route extends BaseObject {
	private String name;
	private String schoolId;
	private String instituteId;
	private Date from;
	private Date to;
	private double distance;
	private List<String> volunteerList = new ArrayList<String>();
	private boolean returnTrip = false;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
	public List<String> getVolunteerList() {
		return volunteerList;
	}
	public void setVolunteerList(List<String> volunteerList) {
		this.volunteerList = volunteerList;
	}
	public boolean isReturnTrip() {
		return returnTrip;
	}
	public void setReturnTrip(boolean returnTrip) {
		this.returnTrip = returnTrip;
	}
}
