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
		private List<ItineraryLegData> legs = new ArrayList<>();

		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public String getDescription() { return description; }
		public void setDescription(String description) { this.description = description; }
		public List<ItineraryLegData> getLegs() { return legs; }
		public void setLegs(List<ItineraryLegData> legs) { this.legs = legs; }
	}

	public static class ItineraryLegData {
		private String name;
		private String description;
		private int position;
		private double[] geocoding;
		private String imageUrl;
		private String polyline;
		private int score;
		private String transport;
		private String icon;
		private String sourceItineraryId;
		private String sourceLegId;
		private List<MultimediaData> multimedia = new ArrayList<>();
		private List<Marker> additionalPoints = new ArrayList<>();

		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public String getDescription() { return description; }
		public void setDescription(String description) { this.description = description; }
		public int getPosition() { return position; }
		public void setPosition(int position) { this.position = position; }
		public double[] getGeocoding() { return geocoding; }
		public void setGeocoding(double[] geocoding) { this.geocoding = geocoding; }
		public String getImageUrl() { return imageUrl; }
		public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
		public String getPolyline() { return polyline; }
		public void setPolyline(String polyline) { this.polyline = polyline; }
		public int getScore() { return score; }
		public void setScore(int score) { this.score = score; }
		public String getTransport() { return transport; }
		public void setTransport(String transport) { this.transport = transport; }
		public String getIcon() { return icon; }
		public void setIcon(String icon) { this.icon = icon; }
		public String getSourceItineraryId() { return sourceItineraryId; }
		public void setSourceItineraryId(String sourceItineraryId) { this.sourceItineraryId = sourceItineraryId; }
		public String getSourceLegId() { return sourceLegId; }
		public void setSourceLegId(String sourceLegId) { this.sourceLegId = sourceLegId; }
		public List<MultimediaData> getMultimedia() { return multimedia; }
		public void setMultimedia(List<MultimediaData> multimedia) { this.multimedia = multimedia; }
		public List<Marker> getAdditionalPoints() { return additionalPoints; }
		public void setAdditionalPoints(List<Marker> additionalPoints) { this.additionalPoints = additionalPoints; }
	}

	public static class MultimediaData {
		private String name;
		private String link;
		private String type;
		private boolean sharable;
		private boolean publicLink;

		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public String getLink() { return link; }
		public void setLink(String link) { this.link = link; }
		public String getType() { return type; }
		public void setType(String type) { this.type = type; }
		public boolean isSharable() { return sharable; }
		public void setSharable(boolean sharable) { this.sharable = sharable; }
		public boolean isPublicLink() { return publicLink; }
		public void setPublicLink(boolean publicLink) { this.publicLink = publicLink; }
	}

	private GameData game;
	private ItineraryData itinerary;

	public GameData getGame() { return game; }
	public void setGame(GameData game) { this.game = game; }
	public ItineraryData getItinerary() { return itinerary; }
	public void setItinerary(ItineraryData itinerary) { this.itinerary = itinerary; }
}
