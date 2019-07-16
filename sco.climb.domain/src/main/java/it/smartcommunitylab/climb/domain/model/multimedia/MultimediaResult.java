package it.smartcommunitylab.climb.domain.model.multimedia;

import java.util.ArrayList;
import java.util.List;

public class MultimediaResult {
	private MultimediaContent referenceContent;
	private List<MultimediaContent> contents = new ArrayList<>();
	
	public MultimediaContent getReferenceContent() {
		return referenceContent;
	}
	public void setReferenceContent(MultimediaContent referenceContent) {
		this.referenceContent = referenceContent;
	}
	public List<MultimediaContent> getContents() {
		return contents;
	}
	public void setContents(List<MultimediaContent> contents) {
		this.contents = contents;
	}
	
	
}
