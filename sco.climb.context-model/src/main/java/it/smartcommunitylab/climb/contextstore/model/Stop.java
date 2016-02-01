package it.smartcommunitylab.climb.contextstore.model;

import java.util.ArrayList;
import java.util.List;

public class Stop extends BaseObject {
	private String id;
	private String name;
	private String routeId;
	private String anchorId;
	private String departureTime;
	private boolean start;
	private boolean destination;
	private boolean school;
	private List<String> passengerList = new ArrayList<String>();
	
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
	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	public String getAnchorId() {
		return anchorId;
	}
	public void setAnchorId(String anchorId) {
		this.anchorId = anchorId;
	}
	public boolean isStart() {
		return start;
	}
	public void setStart(boolean start) {
		this.start = start;
	}
	public boolean isDestination() {
		return destination;
	}
	public void setDestination(boolean destination) {
		this.destination = destination;
	}
	public boolean isSchool() {
		return school;
	}
	public void setSchool(boolean school) {
		this.school = school;
	}
	public List<String> getPassengerList() {
		return passengerList;
	}
	public void setPassengerList(List<String> passengerList) {
		this.passengerList = passengerList;
	}
	public String getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}
	
}
