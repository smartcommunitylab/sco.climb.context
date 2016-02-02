package it.smartcommunitylab.climb.contextstore.model;

public class Anchor extends BaseObject {
	private String name;
	double[] geocoding;
	private int wsnId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double[] getGeocoding() {
		return geocoding;
	}
	public void setGeocoding(double[] geocoding) {
		this.geocoding = geocoding;
	}
	public int getWsnId() {
		return wsnId;
	}
	public void setWsnId(int wsnId) {
		this.wsnId = wsnId;
	}
}
