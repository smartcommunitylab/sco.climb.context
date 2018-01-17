package it.smartcommunitylab.climb.domain.model.gameconf;


public class PedibusGameConfSummary {
	
	private String ownerId;
	private String instituteId;
	private String schoolId;
	private String pedibusGameId;
	private String pedibusGameConfId;
	private boolean active;
	private String templateName;
	private String templateVersion;
	
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
	public String getPedibusGameId() {
		return pedibusGameId;
	}
	public void setPedibusGameId(String pedibusGameId) {
		this.pedibusGameId = pedibusGameId;
	}
	public String getPedibusGameConfId() {
		return pedibusGameConfId;
	}
	public void setPedibusGameConfId(String pedibusGameConfId) {
		this.pedibusGameConfId = pedibusGameConfId;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getTemplateVersion() {
		return templateVersion;
	}
	public void setTemplateVersion(String templateVersion) {
		this.templateVersion = templateVersion;
	}

}
