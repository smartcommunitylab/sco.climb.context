package it.smartcommunitylab.climb.domain.model;

import java.util.HashMap;
import java.util.Map;

public class MobilityMode {
	private String day;
	private boolean direct = false;
	private Map<String, Integer>  modalities = new HashMap<>();	
	private String meteo;
	
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getMeteo() {
		return meteo;
	}
	public void setMeteo(String meteo) {
		this.meteo = meteo;
	}
	public Map<String, Integer> getModalities() {
		return modalities;
	}
	public void setModalities(Map<String, Integer> modalities) {
		this.modalities = modalities;
	}
	public boolean isDirect() {
		return direct;
	}
	public void setDirect(boolean direct) {
		this.direct = direct;
	}
	
}
