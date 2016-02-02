package it.smartcommunitylab.climb.eventstore.test;

import it.smartcommunitylab.climb.contextstore.common.Utils;
import it.smartcommunitylab.climb.contextstore.model.Anchor;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.contextstore.model.VolunteerCalendar;

import java.io.FileReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class JsonTest {
	private static final transient Logger logger = LoggerFactory.getLogger(JsonTest.class);
	
	public static final String networkBaseUrl = "http://localhost:8080/context-store/api/";
	public static final String ownerId = "TEST";
	public static final String token = "<token>";
	public static final String baseDir = "<path>";
	
	public ObjectMapper objectMapper;
	public HttpClient httpClient;
	
	@Before
	public void setUp() throws Exception {
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		httpClient = new HttpClient();
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	private String postJson(Object object, String domain) throws Exception {
		StringWriter json = new StringWriter();
		objectMapper.writeValue(json, object);
		StringRequestEntity requestEntity = new StringRequestEntity(json.toString(),
				"application/json", "UTF-8");
		String url = networkBaseUrl + domain + "/" + ownerId;
		PostMethod postMethod = new PostMethod(url);
		postMethod.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
		postMethod.addRequestHeader("X-ACCESS-TOKEN", token);
		postMethod.setRequestEntity(requestEntity);
		int statusCode = httpClient.executeMethod(postMethod);
		if(logger.isInfoEnabled()) {
			logger.info("postJson:" + statusCode);
		}
		if((statusCode >= 200) && (statusCode < 300)) {
			String jsonResponse = postMethod.getResponseBodyAsString();
			return jsonResponse;
		}
		return null;
	}
	
	private School addSchool(String fileName) throws Exception {
		School result = null;
		FileReader fileReader = new FileReader(baseDir + fileName);
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		School school = Utils.toObject(rootNode, School.class);
		String response = postJson(school, "school");
		if(response != null) {
			JsonNode node = Utils.readJsonFromString(response);
			result = Utils.toObject(node, School.class);
		}
		return result;
	}
	
	private Route addRoute(String schoolId, String fileName) throws Exception {
		Route result = null;
		FileReader fileReader = new FileReader(baseDir + fileName);
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Route route = Utils.toObject(rootNode, Route.class);
		route.setSchoolId(schoolId);
		String response = postJson(route, "route");
		if(response != null) {
			JsonNode node = Utils.readJsonFromString(response);
			result = Utils.toObject(node, Route.class);
		}
		return result;
	}
	
	private List<Stop> addStops(String routeId, List<Child> childList, String fileName) throws Exception {
		List<Stop> result = Lists.newArrayList();
		List<String> passengerList = Lists.newArrayList();
		for(Child child : childList) {
			passengerList.add(child.getObjectId());
		}
		FileReader fileReader = new FileReader(baseDir + fileName);
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> elements = rootNode.elements();
		while(elements.hasNext()) {
			JsonNode node = elements.next();
			Stop stop = Utils.toObject(node, Stop.class);
			stop.setRouteId(routeId);
			stop.setPassengerList(passengerList);
			String response = postJson(stop, "stop");
			if(response != null) {
				JsonNode stopNode = Utils.readJsonFromString(response);
				Stop newStop = Utils.toObject(stopNode, Stop.class);
				result.add(newStop);
			}
		}
		return result;
	}
	
	private List<Child> addChildren(String schoolId, String fileName) throws Exception {
		List<Child> result = Lists.newArrayList();
		FileReader fileReader = new FileReader(baseDir + fileName);
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> elements = rootNode.elements();
		while(elements.hasNext()) {
			JsonNode node = elements.next();
			Child child = Utils.toObject(node, Child.class);
			child.setSchoolId(schoolId);
			String response = postJson(child, "child");
			if(response != null) {
				JsonNode childNode = Utils.readJsonFromString(response);
				Child newChild = Utils.toObject(childNode, Child.class);
				result.add(newChild);
			}
		}
		return result;
	}
	
	private List<Anchor> addAnchors(String fileName) throws Exception {
		List<Anchor> result = Lists.newArrayList();
		FileReader fileReader = new FileReader(baseDir + fileName);
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> elements = rootNode.elements();
		while(elements.hasNext()) {
			JsonNode node = elements.next();
			Anchor anchor = Utils.toObject(node, Anchor.class);
			String response = postJson(anchor, "anchor");
			if(response != null) {
				JsonNode anchorNode = Utils.readJsonFromString(response);
				Anchor newAnchor = Utils.toObject(anchorNode, Anchor.class);
				result.add(newAnchor);
			}
		}
		return result;
	}
	
	private List<Volunteer> addVolunteers(String schoolId, String fileName) throws Exception {
		List<Volunteer> result = Lists.newArrayList();
		FileReader fileReader = new FileReader(baseDir + fileName);
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> elements = rootNode.elements();
		while(elements.hasNext()) {
			JsonNode node = elements.next();
			Volunteer volunteer = Utils.toObject(node, Volunteer.class);
			volunteer.setSchoolId(schoolId);
			String response  = postJson(volunteer, "volunteer");
			if(response != null) {
				JsonNode volNode = Utils.readJsonFromString(response);
				Volunteer newVol = Utils.toObject(volNode, Volunteer.class);
				result.add(newVol);
			}
		}
		return result;
	}
	
	private List<VolunteerCalendar> addVolunteerCalendars(String fileName) throws Exception {
		List<VolunteerCalendar> result = Lists.newArrayList();
		FileReader fileReader = new FileReader(baseDir + fileName);
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> elements = rootNode.elements();
		while(elements.hasNext()) {
			JsonNode node = elements.next();
			VolunteerCalendar calendar = Utils.toObject(node, VolunteerCalendar.class);
			String response  = postJson(calendar, "volunteercal");
			if(response != null) {
				JsonNode calendarNode = Utils.readJsonFromString(response);
				VolunteerCalendar newCalendar = Utils.toObject(calendarNode, VolunteerCalendar.class);
				result.add(newCalendar);
			}
		}
		return result;
	}
	
	@SuppressWarnings("unused")
	@Test
	public void initData() throws Exception {
		School school = addSchool("school.json");
		List<Child> childList = addChildren(school.getObjectId(), "children.json");
		Route route = addRoute(school.getObjectId(), "route.json");
		List<Stop> stopList = addStops(route.getObjectId(), childList, "stops.json");
		List<Anchor> anchorList = addAnchors("anchors.json");
		List<Volunteer> volunteerList = addVolunteers(school.getObjectId(), "volunteers.json");
	}
	
	@Test
	public void initCalendar() throws Exception {
		addVolunteerCalendars("volcalendar.json");
	}
}
