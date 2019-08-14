package it.smartcommunitylab.climb.domain.model.multimedia;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

public class MultimediaContent extends BaseObject {
	private String instituteId;
	private String instituteName;
	private String schoolId;
	private String schoolName;
	private String itineraryId;
	private String itineraryName;
	private String legId;
	private String legName;
	private String name;
	private String link;
	private String type;
	private double[] geocoding; // lon/lat (for mongodb)
	private List<String> subjects = new ArrayList<>();
	private List<String> schoolYears = new ArrayList<>();
	private List<String> classes = new ArrayList<>();
	private boolean sharable = true;
	private boolean publicLink = true; 
	private boolean disabled = false;
	private String previewUrl;
	private ContentOwner contentOwner;
	private String contentReferenceId;
	private int position;
	
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getItineraryId() {
		return itineraryId;
	}
	public void setItineraryId(String itineraryId) {
		this.itineraryId = itineraryId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double[] getGeocoding() {
		return geocoding;
	}
	public void setGeocoding(double[] geocoding) {
		this.geocoding = geocoding;
	}
	public String getLegName() {
		return legName;
	}
	public void setLegName(String legName) {
		this.legName = legName;
	}
	public boolean isSharable() {
		return sharable;
	}
	public void setSharable(boolean sharable) {
		this.sharable = sharable;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public String getPreviewUrl() {
		return previewUrl;
	}
	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}
	public ContentOwner getContentOwner() {
		return contentOwner;
	}
	public void setContentOwner(ContentOwner contentOwner) {
		this.contentOwner = contentOwner;
	}
	public String getContentReferenceId() {
		return contentReferenceId;
	}
	public void setContentReferenceId(String contentReferenceId) {
		this.contentReferenceId = contentReferenceId;
	}
	public String getInstituteName() {
		return instituteName;
	}
	public void setInstituteName(String instituteName) {
		this.instituteName = instituteName;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getItineraryName() {
		return itineraryName;
	}
	public void setItineraryName(String itineraryName) {
		this.itineraryName = itineraryName;
	}
	public String getLegId() {
		return legId;
	}
	public void setLegId(String legId) {
		this.legId = legId;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public boolean isPublicLink() {
		return publicLink;
	}
	public void setPublicLink(boolean publicLink) {
		this.publicLink = publicLink;
	}
	public List<String> getClasses() {
		return classes;
	}
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}
	public List<String> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
	public List<String> getSchoolYears() {
		return schoolYears;
	}
	public void setSchoolYears(List<String> schoolYears) {
		this.schoolYears = schoolYears;
	}
	
}
