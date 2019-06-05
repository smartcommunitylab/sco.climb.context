package it.smartcommunitylab.climb.domain.scheduled;

import java.util.Set;

import com.google.common.collect.Sets;

public class ChildStatus {

	private String childId;
	private String nickname;
	private String classRoom;
	private Set<String> stops;
	private boolean inRange = false;
	private boolean inPedibus = false;
	private boolean arrived = false;
	
	private Double score;
	
	public ChildStatus(String childId) {
		this.childId = childId;
		stops = Sets.newLinkedHashSet();
	}

	public Set<String> getStops() {
		return stops;
	}

	public void setStops(Set<String> anchors) {
		this.stops = anchors;
	}

	public boolean isInRange() {
		return inRange;
	}

	public void setInRange(boolean inRange) {
		this.inRange = inRange;
	}

	public boolean isInPedibus() {
		return inPedibus;
	}

	public void setInPedibus(boolean inPedibus) {
		this.inPedibus = inPedibus;
	}

	public boolean isArrived() {
		return arrived;
	}

	public void setArrived(boolean arrived) {
		this.arrived = arrived;
	}
	
	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return childId + " (" + inRange + "," + inPedibus + "," + arrived + ") => " + stops + " = " + ((score != null)? score: "");
	}

	public String getChildId() {
		return childId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getClassRoom() {
		return classRoom;
	}

	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}

}
