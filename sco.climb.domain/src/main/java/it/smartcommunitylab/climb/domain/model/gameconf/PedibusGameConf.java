package it.smartcommunitylab.climb.domain.model.gameconf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;


public class PedibusGameConf extends BaseObject {
	
	private String pedibusGameId;
	private boolean active;
	private String confTemplateId;
	private List<String> ruleFileTemplates = new ArrayList<>();
	private List<String> actions = new ArrayList<>();
	private List<String> badgeCollections = new ArrayList<>();
	private Map<String, List<String>> challengeModels = new HashMap<>();
	private Map<String, List<String>> points = new HashMap<>();
	private Map<String, Map<String, String>> tasks = new HashMap<>();
	private Map<String, String> params = new HashMap<>();
	
	public String getPedibusGameId() {
		return pedibusGameId;
	}
	public void setPedibusGameId(String pedibusGameId) {
		this.pedibusGameId = pedibusGameId;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getConfTemplateId() {
		return confTemplateId;
	}
	public void setConfTemplateId(String confTemplateId) {
		this.confTemplateId = confTemplateId;
	}
	public List<String> getRuleFileTemplates() {
		return ruleFileTemplates;
	}
	public void setRuleFileTemplates(List<String> ruleFileTemplates) {
		this.ruleFileTemplates = ruleFileTemplates;
	}
	public List<String> getActions() {
		return actions;
	}
	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	public List<String> getBadgeCollections() {
		return badgeCollections;
	}
	public void setBadgeCollections(List<String> badgeCollections) {
		this.badgeCollections = badgeCollections;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public Map<String, List<String>> getChallengeModels() {
		return challengeModels;
	}
	public void setChallengeModels(Map<String, List<String>> challengeModels) {
		this.challengeModels = challengeModels;
	}
	public Map<String, List<String>> getPoints() {
		return points;
	}
	public void setPoints(Map<String, List<String>> points) {
		this.points = points;
	}
	public Map<String, Map<String, String>> getTasks() {
		return tasks;
	}
	public void setTasks(Map<String, Map<String, String>> tasks) {
		this.tasks = tasks;
	}

}
