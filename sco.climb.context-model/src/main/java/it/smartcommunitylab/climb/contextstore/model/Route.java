package it.smartcommunitylab.climb.contextstore.model;

import java.util.Date;

public class Route extends BaseObject {
	private String id;
	private String name;
	private String pedibusId;
	private String schoolId;
	private Date from;
	private Date to;
	private double distance;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPedibusId() {
		return pedibusId;
	}
	public void setPedibusId(String pedibusId) {
		this.pedibusId = pedibusId;
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
}
