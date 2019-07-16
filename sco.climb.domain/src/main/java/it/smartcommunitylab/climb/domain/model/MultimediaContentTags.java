package it.smartcommunitylab.climb.domain.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class MultimediaContentTags {
	@Id
	private String id;
	private List<String> tags = new ArrayList<>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}
