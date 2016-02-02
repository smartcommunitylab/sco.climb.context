package it.smartcommunitylab.climb.contextstore.model;

import java.util.Date;

public class VolunteerCalendar extends BaseObject {
	private Date date;
	private String schoolId;
	private String routeId;
	private String driverId;
	private String helperId;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getHelperId() {
		return helperId;
	}
	public void setHelperId(String helperId) {
		this.helperId = helperId;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
}
