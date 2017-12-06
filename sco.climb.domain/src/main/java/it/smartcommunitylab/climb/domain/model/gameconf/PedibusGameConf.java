package it.smartcommunitylab.climb.domain.model.gameconf;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;


public class PedibusGameConf extends BaseObject {
	
	private String instituteId;
	private String schoolId;
	private String pedibusGameId;
	private boolean active;
	private String confTemplateId;
	private PedibusGameConfParams params;
	
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
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public PedibusGameConfParams getParams() {
		return params;
	}
	public void setParams(PedibusGameConfParams params) {
		this.params = params;
	}
	public String getConfTemplateId() {
		return confTemplateId;
	}
	public void setConfTemplateId(String confTemplateId) {
		this.confTemplateId = confTemplateId;
	}

}
