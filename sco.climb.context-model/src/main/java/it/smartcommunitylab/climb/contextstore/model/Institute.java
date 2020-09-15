package it.smartcommunitylab.climb.contextstore.model;

public class Institute extends BaseObject {
	private String name;
	private String address;
	private String warningBatteryLowMail;
	private boolean addPedibusPhoto;	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getWarningBatteryLowMail() {
		return warningBatteryLowMail;
	}
	public void setWarningBatteryLowMail(String warningBatteryLowMail) {
		this.warningBatteryLowMail = warningBatteryLowMail;
	}
	public boolean isAddPedibusPhoto() {
		return addPedibusPhoto;
	}
	public void setAddPedibusPhoto(boolean addPedibusPhoto) {
		this.addPedibusPhoto = addPedibusPhoto;
	}
}
