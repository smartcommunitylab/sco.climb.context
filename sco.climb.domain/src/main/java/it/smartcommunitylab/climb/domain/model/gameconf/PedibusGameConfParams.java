package it.smartcommunitylab.climb.domain.model.gameconf;

import java.util.ArrayList;
import java.util.List;

public class PedibusGameConfParams {
	private List<String> ruleFileTemplates = new ArrayList<String>();
	private List<PedibusGameConfGroup> groups = new ArrayList<PedibusGameConfGroup>();
	
	public List<String> getRuleFileTemplates() {
		return ruleFileTemplates;
	}
	public void setRuleFileTemplates(List<String> ruleFileTemplates) {
		this.ruleFileTemplates = ruleFileTemplates;
	}
	public List<PedibusGameConfGroup> getGroups() {
		return groups;
	}
	public void setGroups(List<PedibusGameConfGroup> groups) {
		this.groups = groups;
	}
	
	
	
}
