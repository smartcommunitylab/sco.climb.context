package it.smartcommunitylab.climb.domain.model;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

public class PedibusItinerary extends BaseObject {
	private String pedibusGameId;
	private String name;
	private String description;
	
	public String getPedibusGameId() {
		return pedibusGameId;
	}
	public void setPedibusGameId(String pedibusGameId) {
		this.pedibusGameId = pedibusGameId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
