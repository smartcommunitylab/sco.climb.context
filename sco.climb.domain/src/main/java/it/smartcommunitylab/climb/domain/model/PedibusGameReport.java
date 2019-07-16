package it.smartcommunitylab.climb.domain.model;

import java.util.Date;

public class PedibusGameReport {
	private String pedibusGameId;
	private String gameName;
	private String gameDescription;
	private Date from;
	private Date to;
	private int finalScore;
	private int legs;
	private String firstLeg;
	private String finalLeg;
	private String itineraryId;
	
	public PedibusGameReport() {}
	
	public PedibusGameReport(PedibusGame game) {
		this.pedibusGameId = game.getObjectId();
		this.gameName = game.getGameName();
		this.gameDescription = game.getGameDescription();
		this.from = game.getFrom();
		this.to = game.getTo();
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

	public String getPedibusGameId() {
		return pedibusGameId;
	}

	public void setPedibusGameId(String pedibusGameId) {
		this.pedibusGameId = pedibusGameId;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getGameDescription() {
		return gameDescription;
	}

	public void setGameDescription(String gameDescription) {
		this.gameDescription = gameDescription;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public String getItineraryId() {
		return itineraryId;
	}

	public void setItineraryId(String itineraryId) {
		this.itineraryId = itineraryId;
	}
}
