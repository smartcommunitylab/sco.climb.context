package it.smartcommunitylab.climb.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItineraryImport {

	public static class GameData {
		private String id;
		private String name;
		private Date from;
		private Date to;
		private boolean roundTrip;
		private List<Boolean> daysOfWeek = new ArrayList<>();
		private Map<String, Map<String, Integer>> mobilityParams = new HashMap<>();

		public String getId() { return id; }
		public void setId(String id) { this.id = id; }
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public Date getFrom() { return from; }
		public void setFrom(Date from) { this.from = from; }
		public Date getTo() { return to; }
		public void setTo(Date to) { this.to = to; }
		public boolean isRoundTrip() { return roundTrip; }
		public void setRoundTrip(boolean roundTrip) { this.roundTrip = roundTrip; }
		public List<Boolean> getDaysOfWeek() { return daysOfWeek; }
		public void setDaysOfWeek(List<Boolean> daysOfWeek) { this.daysOfWeek = daysOfWeek; }
		public Map<String, Map<String, Integer>> getMobilityParams() { return mobilityParams; }
		public void setMobilityParams(Map<String, Map<String, Integer>> mobilityParams) { this.mobilityParams = mobilityParams; }
	}

	public static class ItineraryData {
		private String name;
		private String description;
		private List<PedibusItineraryLeg> legs = new ArrayList<>();

		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public String getDescription() { return description; }
		public void setDescription(String description) { this.description = description; }
		public List<PedibusItineraryLeg> getLegs() { return legs; }
		public void setLegs(List<PedibusItineraryLeg> legs) { this.legs = legs; }
	}

	private GameData game;
	private ItineraryData itinerary;

	public GameData getGame() { return game; }
	public void setGame(GameData game) { this.game = game; }
	public ItineraryData getItinerary() { return itinerary; }
	public void setItinerary(ItineraryData itinerary) { this.itinerary = itinerary; }
}
