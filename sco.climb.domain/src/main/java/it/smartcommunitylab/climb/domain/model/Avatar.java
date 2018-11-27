package it.smartcommunitylab.climb.domain.model;

import org.bson.types.Binary;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

public class Avatar extends BaseObject {
	private String resourceId;
	private String resourceType;
	private Binary image;
	private String contentType;
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public Binary getImage() {
		return image;
	}
	public void setImage(Binary image) {
		this.image = image;
	}
}
