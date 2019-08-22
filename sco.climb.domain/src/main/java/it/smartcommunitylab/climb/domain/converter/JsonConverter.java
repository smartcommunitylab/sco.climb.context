package it.smartcommunitylab.climb.domain.converter;

import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.climb.contextstore.model.Institute;
import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.domain.model.Marker;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.model.PedibusItinerary;
import it.smartcommunitylab.climb.domain.model.PedibusItineraryLeg;
import it.smartcommunitylab.climb.domain.model.multimedia.ContentOwner;
import it.smartcommunitylab.climb.domain.model.multimedia.MultimediaContent;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Component
public class JsonConverter {
	private static final transient Logger logger = LoggerFactory.getLogger(JsonConverter.class);
	
	@Autowired
	RepositoryManager storage;
	
	ObjectMapper objectMapper;
	JsonFactory jsonFactory;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	@PostConstruct
	public void init() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jsonFactory = new JsonFactory();
		jsonFactory.setCodec(objectMapper);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public boolean storePedibusGame(String ownerId, String instituteId, String schoolId, 
			Reader reader) {
		try {
			JsonParser jp = jsonFactory.createParser(reader);
			JsonToken current;
			current = jp.nextToken();
			if (current != JsonToken.START_ARRAY) {
				logger.error("Error: root should be array: quiting.");
				return false;
			}
			int count = 0;
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				try {
					JsonNode rootNode = jp.readValueAsTree();
					PedibusGame pedibusGame = parseGame(ownerId, instituteId, schoolId, rootNode);
					storage.savePedibusGame(pedibusGame, ownerId, true);
				} catch (Exception e) {
					logger.warn("storePedibusGame error:{}/{}", count, e.getMessage());
				}
				count++;
			}
		} catch (Exception e) {
			logger.warn("storePedibusGame error:{}", e.getMessage());
		}
		return true;
	}
	
	public boolean storePedibusItinerary(String ownerId, String instituteId, String schoolId, 
			Reader reader) {
		try {
			JsonParser jp = jsonFactory.createParser(reader);
			JsonToken current;
			current = jp.nextToken();
			if (current != JsonToken.START_ARRAY) {
				logger.error("Error: root should be array: quiting.");
				return false;
			}
			int count = 0;
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				try {
					JsonNode rootNode = jp.readValueAsTree();
					PedibusItinerary itinerary = parseItinerary(ownerId, instituteId, schoolId, rootNode);
					if(storage.existsPedibusGame(itinerary.getPedibusGameId())) {
						storage.savePedibusItinerary(itinerary);
					}	else {
						logger.warn("storePedibusItinerary skip:{}/{}", itinerary.getObjectId(), 
								itinerary.getPedibusGameId());
					}
				} catch (Exception e) {
					logger.warn("storePedibusItinerary error:{}/{}", count, e.getMessage());
				}
				count++;
			}
		} catch (Exception e) {
			logger.warn("storePedibusItinerary error:{}", e.getMessage());
		}
		return true;
	}

	public boolean storePedibusItineraryLeg(String ownerId, String instituteId, String schoolId, 
			Reader reader) {
		try {
			JsonParser jp = jsonFactory.createParser(reader);
			JsonToken current;
			current = jp.nextToken();
			if (current != JsonToken.START_ARRAY) {
				logger.error("Error: root should be array: quiting.");
				return false;
			}
			int count = 0;
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				try {
					JsonNode rootNode = jp.readValueAsTree();
					PedibusItineraryLeg leg = parseItineraryLeg(ownerId, instituteId, schoolId, rootNode);
					if(storage.existsPedibusItinerary(leg.getItineraryId())) {
						storage.savePedibusItineraryLeg(leg, ownerId, true);
						List<MultimediaContent> contents = parseMultimediaContent(ownerId, instituteId, schoolId, 
								leg, rootNode);
						for(MultimediaContent content : contents) {
							storage.saveMultimediaContent(content);
						}
					}					
				} catch (Exception e) {
					logger.warn("storePedibusItineraryLeg error:{}/{}", count, e.getMessage());
				}
				count++;
			}
		} catch (Exception e) {
			logger.warn("storePedibusItineraryLeg error:{}", e.getMessage());
		}
		return true;
	}

	private PedibusGame parseGame(String ownerId, String instituteId, String schoolId,
			JsonNode rootNode) throws Exception {
		PedibusGame game = new PedibusGame();
		game.setOwnerId(ownerId);
		game.setObjectId(rootNode.get("objectId").asText());
		game.setInstituteId(instituteId);
		game.setSchoolId(schoolId);
		game.setGameName(rootNode.get("gameName").asText());
		if(rootNode.hasNonNull("gameDescription")) {
			game.setGameDescription(rootNode.get("gameDescription").asText());
		}
		if(rootNode.hasNonNull("globalTeam")) {
			game.setGlobalTeam(rootNode.get("globalTeam").asText());
		}
		if(rootNode.hasNonNull("from") && rootNode.hasNonNull("to")) {
			Date from = sdf.parse(rootNode.get("from").get("$date").asText());
	    game.setFrom(from);
	    Date to = sdf.parse(rootNode.get("to").get("$date").asText());
			game.setTo(to);
		}
		return game;
	}
	
	private PedibusItinerary parseItinerary(String ownerId, String instituteId, String schoolId,
			JsonNode rootNode) {
		PedibusItinerary itinerary = new PedibusItinerary();
		itinerary.setOwnerId(ownerId);
		itinerary.setObjectId(rootNode.get("objectId").asText());
		itinerary.setPedibusGameId(rootNode.get("pedibusGameId").asText());
		itinerary.setName(rootNode.get("name").asText());
		itinerary.setDescription(rootNode.get("description").asText());
		return itinerary;
	}
	
	private PedibusItineraryLeg parseItineraryLeg(String ownerId, String instituteId, String schoolId,
			JsonNode rootNode) {
		PedibusItineraryLeg leg = new PedibusItineraryLeg();
		leg.setOwnerId(ownerId);
		leg.setObjectId(rootNode.get("objectId").asText());
		leg.setPedibusGameId(rootNode.get("pedibusGameId").asText());
		leg.setItineraryId(rootNode.get("itineraryId").asText());
		leg.setName(rootNode.get("name").asText());
		if(rootNode.hasNonNull("description")) {
			leg.setDescription(rootNode.get("description").asText());
		}
		if(rootNode.hasNonNull("imageUrl")) {
			leg.setImageUrl(rootNode.get("imageUrl").asText());
		}
		if(rootNode.hasNonNull("polyline")) {
			leg.setPolyline(rootNode.get("polyline").asText());			
		}
		if(rootNode.hasNonNull("transport")) {
			leg.setTransport(rootNode.get("transport").asText());
		}
		if(rootNode.hasNonNull("icon")) {
			leg.setIcon(rootNode.get("icon").asText());
		}
		leg.setPosition(rootNode.get("position").asInt());
		leg.setScore(rootNode.get("score").asInt());
		
		double[] geocoding = new double[2];
		int index = 0;
		for(JsonNode node : rootNode.get("geocoding")) {
			geocoding[index] = node.asDouble();
			index++;
		}
		leg.setGeocoding(geocoding);
		
		List<Marker> additionalPoints = new ArrayList<Marker>();
		for(JsonNode node : rootNode.get("additionalPoints")) {
			Marker marker = new Marker();
			marker.setLatitude(node.get("latitude").asDouble());
			marker.setLongitude(node.get("longitude").asDouble());
			additionalPoints.add(marker);
		}
		leg.setAdditionalPoints(additionalPoints);
		return leg;
	}
	
	private List<MultimediaContent> parseMultimediaContent(String ownerId, String instituteId, String schoolId,
			PedibusItineraryLeg leg, JsonNode rootNode) {
		List<MultimediaContent> result = new ArrayList<MultimediaContent>();
		Institute institute = storage.getInstitute(ownerId, instituteId);
		School school = storage.getSchool(ownerId, instituteId, schoolId);
		PedibusItinerary itinerary = storage.getPedibusItinerary(leg.getItineraryId());
		ContentOwner contentOwner = new ContentOwner();
		contentOwner.setName("KGG");
		contentOwner.setUserId("11111");
		int position = 0;
		for(JsonNode node : rootNode.get("externalUrls")) {
			MultimediaContent content = new MultimediaContent();
			content.setOwnerId(ownerId);
			content.setInstituteId(instituteId);
			content.setInstituteName(institute.getName());
			content.setSchoolId(schoolId);
			content.setSchoolName(school.getName());
			content.setItineraryId(itinerary.getObjectId());
			content.setItineraryName(itinerary.getName());
			content.setLegId(leg.getObjectId());
			content.setLegName(leg.getName());
			if(node.hasNonNull("name")) {
				content.setName(node.get("name").asText());
			}
			if(node.hasNonNull("type")) {
				content.setType(node.get("type").asText());
			}
			if(node.hasNonNull("link")) {
				content.setLink(node.get("link").asText());
			}
			content.setGeocoding(leg.getGeocoding());
			content.setPosition(position);
			content.setContentOwner(contentOwner);
			result.add(content);
			position++;
		}
		return result;
	}
	
}
