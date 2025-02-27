package it.smartcommunitylab.climb.domain.model;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

import java.util.Date;

public class Excursion extends BaseObject {
	private String pedibusGameId;
	private Date day;
	private String classRoom;
	private String meteo;
	private Integer children;
	private Double distance;
	private String name;
	private boolean goodAction = false;
	
	public Date getDay() {
		return day;
	}
	public void setDay(Date day) {
		this.day = day;
	}
	public String getMeteo() {
		return meteo;
	}
	public void setMeteo(String meteo) {
		this.meteo = meteo;
	}
	public String getClassRoom() {
		return classRoom;
	}
	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}
	public Integer getChildren() {
		return children;
	}
	public void setChildren(Integer children) {
		this.children = children;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPedibusGameId() {
		return pedibusGameId;
	}
	public void setPedibusGameId(String pedibusGameId) {
		this.pedibusGameId = pedibusGameId;
	}
	public boolean isGoodAction() {
		return goodAction;
	}
	public void setGoodAction(boolean goodAction) {
		this.goodAction = goodAction;
	}
}
