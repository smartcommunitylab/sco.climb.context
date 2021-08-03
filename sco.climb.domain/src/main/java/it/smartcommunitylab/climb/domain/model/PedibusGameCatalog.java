package it.smartcommunitylab.climb.domain.model;

public class PedibusGameCatalog {
	private PedibusGame pedibusGame;
	private int finalScore;
	private int legs;
	private String firstLeg;
	private String finalLeg;
	private String itineraryId;
	
	public PedibusGameCatalog() {}
	
	public PedibusGameCatalog(PedibusGame game) {
		this.pedibusGame = game;
	}
	
	public int getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(int finalScore) {
		this.finalScore = finalScore;
	}
	public int getLegs() {
		return legs;
	}
	public void setLegs(int legs) {
		this.legs = legs;
	}
	public String getFirstLeg() {
		return firstLeg;
	}
	public void setFirstLeg(String firstLeg) {
		this.firstLeg = firstLeg;
	}
	public String getFinalLeg() {
		return finalLeg;
	}
	public void setFinalLeg(String finalLeg) {
		this.finalLeg = finalLeg;
	}

	public String getItineraryId() {
		return itineraryId;
	}

	public void setItineraryId(String itineraryId) {
		this.itineraryId = itineraryId;
	}

	public PedibusGame getPedibusGame() {
		return pedibusGame;
	}

	public void setPedibusGame(PedibusGame pedibusGame) {
		this.pedibusGame = pedibusGame;
	}
}
