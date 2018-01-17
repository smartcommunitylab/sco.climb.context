package it.smartcommunitylab.climb.domain.model.gameconf;

import java.util.ArrayList;
import java.util.List;

public class PedibusGameConfGroup {
	private String groupName;
	private List<PedibusGameConfField> fields = new ArrayList<PedibusGameConfField>();
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<PedibusGameConfField> getFields() {
		return fields;
	}
	public void setFields(List<PedibusGameConfField> fields) {
		this.fields = fields;
	}
	
}
