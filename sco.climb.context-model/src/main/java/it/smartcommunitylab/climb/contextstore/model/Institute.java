package it.smartcommunitylab.climb.contextstore.model;

public class Institute extends BaseObject {
	private String name;
	private String address;
	private String warningBatteryLowMail;
	private boolean addPedibusPhoto;
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null) {
			if(obj instanceof Institute) {
				Institute institute = (Institute) obj;
				if(this.getObjectId() != null) {
					if(institute.getObjectId() != null) {
						return this.getObjectId().equals(institute.getObjectId());
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if(this.getObjectId() != null) {
			return this.getObjectId().hashCode();
		}
		return super.hashCode();
	}
	
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
