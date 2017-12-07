package it.smartcommunitylab.climb.domain.model.gameconf;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;


public class PedibusGameConfTemplate extends BaseObject {
	
	private String name;
	private String version;
	private String description;
	private PedibusGameConfParams params;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public PedibusGameConfParams getParams() {
		return params;
	}
	public void setParams(PedibusGameConfParams params) {
		this.params = params;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
