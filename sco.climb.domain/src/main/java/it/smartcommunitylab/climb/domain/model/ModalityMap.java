package it.smartcommunitylab.climb.domain.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class ModalityMap {
	@Id
	private String id;
	private List<Modality> modalities = new ArrayList<>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Modality> getModalities() {
		return modalities;
	}
	public void setModalities(List<Modality> modalities) {
		this.modalities = modalities;
	}
}
