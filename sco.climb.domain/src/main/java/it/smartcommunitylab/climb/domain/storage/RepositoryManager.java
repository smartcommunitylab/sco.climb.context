package it.smartcommunitylab.climb.domain.storage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.core.query.Update;

import it.smartcommunitylab.climb.contextstore.model.Anchor;
import it.smartcommunitylab.climb.contextstore.model.Authorization;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Institute;
import it.smartcommunitylab.climb.contextstore.model.PassengerCalendar;
import it.smartcommunitylab.climb.contextstore.model.Pedibus;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.contextstore.model.VolunteerCalendar;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.StorageException;
import it.smartcommunitylab.climb.domain.model.Avatar;
import it.smartcommunitylab.climb.domain.model.CalendarDay;
import it.smartcommunitylab.climb.domain.model.Excursion;
import it.smartcommunitylab.climb.domain.model.ModalityMap;
import it.smartcommunitylab.climb.domain.model.MultimediaContentTags;
import it.smartcommunitylab.climb.domain.model.NodeState;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.model.PedibusItinerary;
import it.smartcommunitylab.climb.domain.model.PedibusItineraryLeg;
import it.smartcommunitylab.climb.domain.model.PedibusPlayer;
import it.smartcommunitylab.climb.domain.model.WsnEvent;
import it.smartcommunitylab.climb.domain.model.gamification.PedibusGameConfTemplate;
import it.smartcommunitylab.climb.domain.model.monitoring.MonitoringPlay;
import it.smartcommunitylab.climb.domain.model.multimedia.MultimediaContent;
import it.smartcommunitylab.climb.domain.security.DataSetInfo;

public class RepositoryManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RepositoryManager.class);
	
	private MongoTemplate mongoTemplate;
	private String defaultLang;
	
	public RepositoryManager(MongoTemplate template, String defaultLang) {
		this.mongoTemplate = template;
		this.defaultLang = defaultLang;
	}
	
	public String getDefaultLang() {
		return defaultLang;
	}
	
	public List<?> findData(Class<?> entityClass, Criteria criteria, Sort sort, String ownerId)
			throws ClassNotFoundException {
		Query query = null;
		if (criteria != null) {
			query = new Query(new Criteria("ownerId").is(ownerId).andOperator(criteria));
		} else {
			query = new Query(new Criteria("ownerId").is(ownerId));
		}
		if (sort != null) {
			query.with(sort);
		}
		query.limit(5000);
		List<?> result = mongoTemplate.find(query, entityClass);
		return result;
	}

	public <T> T findOneData(Class<T> entityClass, Criteria criteria, String ownerId)
			throws ClassNotFoundException {
		Query query = null;
		if (criteria != null) {
			query = new Query(new Criteria("ownerId").is(ownerId).andOperator(criteria));
		} else {
			query = new Query(new Criteria("ownerId").is(ownerId));
		}
		T result = mongoTemplate.findOne(query, entityClass);
		return result;
	}

	public void addSchool(School school) {
		Date actualDate = new Date();
		school.setCreationDate(actualDate);
		school.setLastUpdate(actualDate);
		mongoTemplate.save(school);
	}

	public void updateSchool(School school) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(school.getOwnerId()).and("objectId").is(school.getObjectId()));
		School schoolDB = mongoTemplate.findOne(query, School.class);
		if(schoolDB == null) {
			throw new EntityNotFoundException(String.format("School with id %s not found", school.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		update.set("name", school.getName());
		update.set("address", school.getAddress());
		update.set("classes", school.getClasses());
		update.set("volunteerShiftsLink", school.getVolunteerShiftsLink());
		mongoTemplate.updateFirst(query, update, School.class);
	}

	public void removeSchool(String ownerId, String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		School schoolDB = mongoTemplate.findOne(query, School.class);
		if(schoolDB == null) {
			throw new EntityNotFoundException(String.format("School with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, School.class);
	}

	public void addPedibus(Pedibus pedibus) {
		Date actualDate = new Date();
		pedibus.setCreationDate(actualDate);
		pedibus.setLastUpdate(actualDate);
		mongoTemplate.save(pedibus);
	}

	public void updatePedibus(Pedibus pedibus) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(pedibus.getOwnerId()).and("objectId").is(pedibus.getObjectId()));
		Pedibus entityDB = mongoTemplate.findOne(query, Pedibus.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Pedibus with id %s not found", pedibus.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		update.set("schoolId", pedibus.getSchoolId());
		update.set("from", pedibus.getFrom());
		update.set("to", pedibus.getTo());
		update.set("supervisorId", pedibus.getSupervisorId());
		mongoTemplate.updateFirst(query, update, Pedibus.class);
	}

	public void removePedibus(String ownerId, String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		Pedibus entityDB = mongoTemplate.findOne(query, Pedibus.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Pedibus with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, Pedibus.class);
	}

	public void addRoute(Route route) {
		Date actualDate = new Date();
		route.setCreationDate(actualDate);
		route.setLastUpdate(actualDate);
		mongoTemplate.save(route);
	}

	public void updateRoute(Route route) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(route.getOwnerId()).and("objectId").is(route.getObjectId()));
		Route entityDB = mongoTemplate.findOne(query, Route.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Route with id %s not found", route.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		update.set("name", route.getName());
		update.set("schoolId", route.getSchoolId());
		update.set("from", route.getFrom());
		update.set("to", route.getTo());
		update.set("distance", route.getDistance());
		update.set("volunteerList", route.getVolunteerList());
		update.set("returnTrip", route.isReturnTrip());
		mongoTemplate.updateFirst(query, update, Route.class);
	}

	public void removeRoute(String ownerId, String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		Route entityDB = mongoTemplate.findOne(query, Route.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Route with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, Route.class);
	}

	public void addStop(Stop stop) {
		Date actualDate = new Date();
		stop.setCreationDate(actualDate);
		stop.setLastUpdate(actualDate);
		mongoTemplate.save(stop);
	}

	public void updateStop(Stop stop) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(stop.getOwnerId()).and("objectId").is(stop.getObjectId()));
		Stop entityDB = mongoTemplate.findOne(query, Stop.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Stop with id %s not found", stop.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		update.set("name", stop.getName());
		update.set("routeId", stop.getRouteId());
		update.set("departureTime", stop.getDepartureTime());
		update.set("start", stop.isStart());
		update.set("destination", stop.isDestination());
		update.set("school", stop.isSchool());
		update.set("geocoding", stop.getGeocoding());
		update.set("distance", stop.getDistance());
		update.set("wsnId", stop.getWsnId());
		update.set("position", stop.getPosition());
		update.set("passengerList", stop.getPassengerList());
		mongoTemplate.updateFirst(query, update, Stop.class);
	}

	public void removeStop(String ownerId, String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		Stop entityDB = mongoTemplate.findOne(query, Stop.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Stop with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, Stop.class);
	}

	public void removeStopByRouteId(String ownerId, String routeId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("routeId").is(routeId));
		mongoTemplate.remove(query, Stop.class);
	}
	
	public void addChild(Child child) {
		Date actualDate = new Date();
		child.setCreationDate(actualDate);
		child.setLastUpdate(actualDate);
		mongoTemplate.save(child);
	}

	public void updateChild(Child child) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(child.getOwnerId()).and("objectId").is(child.getObjectId()));
		Child entityDB = mongoTemplate.findOne(query, Child.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Child with id %s not found", child.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		update.set("name", child.getName());
		update.set("surname", child.getSurname());
		update.set("externalId", child.getExternalId());
		update.set("parentName", child.getParentName());
		update.set("phone", child.getPhone());
		update.set("schoolId", child.getSchoolId());
		update.set("instituteId", child.getInstituteId());
		update.set("classRoom", child.getClassRoom());
		update.set("wsnId", child.getWsnId());
		update.set("imageUrl", child.getImageUrl());
		update.set("cf", child.getCf());
		update.set("nickname", child.getNickname());
		mongoTemplate.updateFirst(query, update, Child.class);
	}

	public void removeChild(String ownerId, String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		Child entityDB = mongoTemplate.findOne(query, Child.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Child with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, Child.class);
	}

	public void addAnchor(Anchor anchor) {
		Date actualDate = new Date();
		anchor.setCreationDate(actualDate);
		anchor.setLastUpdate(actualDate);
		mongoTemplate.save(anchor);
	}

	public void updateAnchor(Anchor anchor) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(anchor.getOwnerId()).and("objectId").is(anchor.getObjectId()));
		Anchor entityDB = mongoTemplate.findOne(query, Anchor.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Anchor with id %s not found", anchor.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		update.set("name", anchor.getName());
		update.set("geocoding", anchor.getGeocoding());
		update.set("wsnId", anchor.getWsnId());
		mongoTemplate.updateFirst(query, update, Anchor.class);
	}

	public void removeAnchor(String ownerId, String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		Anchor entityDB = mongoTemplate.findOne(query, Anchor.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Anchor with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, Anchor.class);
	}

	public void addVolunteer(Volunteer volunteer) {
		Date actualDate = new Date();
		volunteer.setCreationDate(actualDate);
		volunteer.setLastUpdate(actualDate);
		mongoTemplate.save(volunteer);
	}

	public void updateVolunteer(Volunteer volunteer) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(volunteer.getOwnerId()).and("objectId").is(volunteer.getObjectId()));
		Volunteer entityDB = mongoTemplate.findOne(query, Volunteer.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Volunteer with id %s not found", volunteer.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		update.set("name", volunteer.getName());
		update.set("address", volunteer.getAddress());
		update.set("phone", volunteer.getPhone());
		update.set("schoolId", volunteer.getSchoolId());
		update.set("instituteId", volunteer.getInstituteId());
		update.set("password", volunteer.getPassword());
		update.set("wsnId", volunteer.getWsnId());
		update.set("cf", volunteer.getCf());
		update.set("email", volunteer.getEmail());
		mongoTemplate.updateFirst(query, update, Volunteer.class);
	}

	public void removeVolunteer(String ownerId, String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		Volunteer entityDB = mongoTemplate.findOne(query, Volunteer.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Volunteer with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, Volunteer.class);
	}

	public void addPassengerCalendar(PassengerCalendar calendar) {
		Date actualDate = new Date();
		calendar.setCreationDate(actualDate);
		calendar.setLastUpdate(actualDate);
		mongoTemplate.save(calendar);
	}

	public void updatePassengerCalendar(PassengerCalendar calendar) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(calendar.getOwnerId()).and("objectId").is(calendar.getObjectId()));
		PassengerCalendar entityDB = mongoTemplate.findOne(query, PassengerCalendar.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("PassengerCalendar with id %s not found", calendar.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		update.set("date", calendar.getDate());
		update.set("routeId", calendar.getRouteId());
		update.set("stopId", calendar.getStopId());
		update.set("absenteeList", calendar.getAbsenteeList());
		mongoTemplate.updateFirst(query, update, PassengerCalendar.class);
	}

	public void removePassengerCalendar(String ownerId, String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		PassengerCalendar entityDB = mongoTemplate.findOne(query, PassengerCalendar.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("PassengerCalendar with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, PassengerCalendar.class);
	}

	public void addVolunteerCalendar(VolunteerCalendar calendar) {
		Date actualDate = new Date();
		calendar.setCreationDate(actualDate);
		calendar.setLastUpdate(actualDate);
		mongoTemplate.save(calendar);
	}

	public void updateVolunteerCalendar(VolunteerCalendar calendar) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(calendar.getOwnerId()).and("objectId").is(calendar.getObjectId()));
		VolunteerCalendar entityDB = mongoTemplate.findOne(query, VolunteerCalendar.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("VolunteerCalendar with id %s not found", calendar.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		update.set("date", calendar.getDate());
		update.set("schoolId", calendar.getSchoolId());
		update.set("routeId", calendar.getRouteId());
		update.set("driverId", calendar.getDate());
		update.set("helperList", calendar.getHelperList());
		mongoTemplate.updateFirst(query, update, VolunteerCalendar.class);
	}

	public void removeVolunteerCalendar(String ownerId, String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		VolunteerCalendar entityDB = mongoTemplate.findOne(query, VolunteerCalendar.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("VolunteerCalendar with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, VolunteerCalendar.class);
	}

	public void addInstitute(Institute institute) {
		Date actualDate = new Date();
		institute.setCreationDate(actualDate);
		institute.setLastUpdate(actualDate);
		mongoTemplate.save(institute);
	}

	public void updateInstitute(Institute institute) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(institute.getOwnerId())
				.and("objectId").is(institute.getObjectId()));
		Institute entityDB = mongoTemplate.findOne(query, Institute.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("Institute with id %s not found", institute.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		update.set("name", institute.getName());
		update.set("address", institute.getAddress());
		update.set("warningBatteryLowMail", institute.getWarningBatteryLowMail());
		update.set("addPedibusPhoto", institute.isAddPedibusPhoto());
		mongoTemplate.updateFirst(query, update, Institute.class);
	}

	public void removeInstitute(String ownerId, String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		Institute institute = mongoTemplate.findOne(query, Institute.class);
		if(institute == null) {
			throw new EntityNotFoundException(String.format("School with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, Institute.class);
	}	

	// EVENTS
	
	public void addEvent(WsnEvent event) {
		Date actualDate = new Date();
		event.setCreationDate(actualDate);
		event.setLastUpdate(actualDate);
		mongoTemplate.save(event);
	}
	
	public void removeEvents(String ownerId, String routeId, Date dateFrom, Date dateTo, int eventType) {
		Criteria criteria = new Criteria("ownerId").is(ownerId).and("routeId").is(routeId);
		if(eventType > 0) {
			criteria = criteria.and("eventType").is(eventType);
		}
		Query query = new Query(criteria);
		query.addCriteria(new Criteria().andOperator(
			Criteria.where("timestamp").lte(dateTo),
			Criteria.where("timestamp").gte(dateFrom)));
		if(logger.isDebugEnabled()) {
			logger.debug("removeEvents:" + query.toString());
		}
		mongoTemplate.findAllAndRemove(query, WsnEvent.class);
	}
	
	public List<WsnEvent> searchEvents(String ownerId, String routeId, Date dateFrom, Date dateTo, 
			List<Integer> eventTypeList, List<String> nodeIdList) {
		Criteria criteria = new Criteria("ownerId").is(ownerId);
		if(Utils.isNotEmpty(routeId)) {
			criteria = criteria.and("routeId").is(routeId);
		}
		if(eventTypeList.size() > 0) {
			criteria = criteria.and("eventType").in(eventTypeList);
		}
		if(nodeIdList.size() > 0) {
			criteria = criteria.and("wsnNodeId").in(nodeIdList);
		}
		Query query = new Query(criteria);
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("timestamp").lte(dateTo),
				Criteria.where("timestamp").gte(dateFrom)));
		query.with(Sort.by(Sort.Direction.ASC, "timestamp"));
		if(logger.isDebugEnabled()) {
			logger.debug("searchEvents:" + query.toString());
		}
		List<WsnEvent> result = mongoTemplate.find(query, WsnEvent.class);
		if(logger.isDebugEnabled()) {
			logger.debug("searchEvents:" + result.size());
		}
		return result;
	}
	
	public List<WsnEvent> searchEventsByLastUpdate(String ownerId, String routeId, Date dateFrom, Date dateTo, 
			List<Integer> eventTypeList, List<String> nodeIdList) {
		Criteria criteria = new Criteria("ownerId").is(ownerId);
		if(Utils.isNotEmpty(routeId)) {
			criteria = criteria.and("routeId").is(routeId);
		}
		if(eventTypeList.size() > 0) {
			criteria = criteria.and("eventType").in(eventTypeList);
		}
		if(nodeIdList.size() > 0) {
			criteria = criteria.and("wsnNodeId").in(nodeIdList);
		}
		Query query = new Query(criteria);
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("lastUpdate").lt(dateTo),
				Criteria.where("lastUpdate").gte(dateFrom)));
		query.with(Sort.by(Sort.Direction.ASC, "lastUpdate"));
		if(logger.isDebugEnabled()) {
			logger.debug("searchEventsByLastUpdate:" + query.toString());
		}
		List<WsnEvent> result = mongoTemplate.find(query, WsnEvent.class);
		if(logger.isDebugEnabled()) {
			logger.debug("searchEventsByLastUpdate:" + result.size());
		}
		return result;
	}
	
	public List<NodeState> checkNodes(String ownerId, String routeId, Date dateFrom, Date dateTo) {
		List<Integer> eventTypeList = new ArrayList<Integer>();
		eventTypeList.add(Const.NODE_IN_RANGE);
		eventTypeList.add(Const.NODE_AT_DESTINATION);
		eventTypeList.add(Const.BATTERY_STATE);
		Criteria criteria = new Criteria("ownerId").is(ownerId);
		if(Utils.isNotEmpty(routeId)) {
			criteria = criteria.and("routeId").is(routeId);
		}
		if(eventTypeList.size() > 0) {
			criteria = criteria.and("eventType").in(eventTypeList);
		}
		Query query = new Query(criteria);
		query.addCriteria(new Criteria().andOperator(
			Criteria.where("timestamp").lte(dateTo),
			Criteria.where("timestamp").gte(dateFrom)));
		query.with(Sort.by(Sort.Direction.ASC, "timestamp"));
		if(logger.isDebugEnabled()) {
			logger.debug("checkNodes:" + query.toString());
		}
		List<WsnEvent> events = mongoTemplate.find(query, WsnEvent.class);
		Map<String, WsnEvent> nodeInRangeMap = new HashMap<String, WsnEvent>();
		Map<String, WsnEvent> nodeAtDestinationMap = new HashMap<String, WsnEvent>();
		Map<String, WsnEvent> batteryLowMap = new HashMap<String, WsnEvent>();
		List<String> passengerList = new ArrayList<String>();
		List<NodeState> nodeStateList = new ArrayList<NodeState>();
		for(WsnEvent event : events) {
			if(!event.getRouteId().equals(routeId)) {
				continue;
			}
			String passengerId = (String) event.getPayload().get("passengerId");
			if(event.getEventType() == Const.NODE_IN_RANGE) {
				nodeInRangeMap.put(passengerId, event);
			} else if(event.getEventType() == Const.NODE_AT_DESTINATION) {
				nodeAtDestinationMap.put(passengerId, event);
			} else if(event.getEventType() == Const.BATTERY_STATE) {
				batteryLowMap.put(passengerId, event);
			} else {
				continue;
			}
			if(!passengerList.contains(passengerId)) {
				passengerList.add(passengerId);
			}
		}
		for(String passengerId : passengerList) {
			WsnEvent eventAtDestination = nodeAtDestinationMap.get(passengerId);
			if(eventAtDestination != null) {
				NodeState nodeState = new NodeState();
				nodeState.setPassengerId(passengerId);
				nodeState.setWsnNodeId(eventAtDestination.getWsnNodeId());
				WsnEvent eventBatteryLow = batteryLowMap.get(passengerId);
				if(eventBatteryLow != null) {
					nodeState.setBatteryLevel((Integer) eventBatteryLow.getPayload().get("batteryLevel"));
					nodeState.setBatteryVoltage((Integer) eventBatteryLow.getPayload().get("batteryVoltage"));
				} else {
					nodeState.setBatteryLevel(0);
					nodeState.setBatteryVoltage(0);
				}
				WsnEvent eventInRange = nodeInRangeMap.get(passengerId);
				if(eventInRange != null) {
					nodeState.setManualCheckIn(false);
				} else {
					nodeState.setManualCheckIn(true);
				}
				nodeStateList.add(nodeState);
			}
		}
		if(logger.isDebugEnabled()) {
			logger.debug("checkNodes:" + nodeStateList.size());
		}
		return nodeStateList;
	}

	public User getUserBySubject(String subject) {
		Query query = new Query(new Criteria("subject").is(subject));
		User result = mongoTemplate.findOne(query, User.class);
		return result;
	}

	public User getUserByCf(String cf) {
		Query query = new Query(new Criteria("cf").regex(cf, "i"));
		User result = mongoTemplate.findOne(query, User.class);
		return result;
	}
	
	public User getUserByEmail(String email) {
		Query query = new Query(new Criteria("email").regex(email, "i"));
		User result = mongoTemplate.findOne(query, User.class);
		return result;
	}
	
	public List<User> getUsersByOwnerIdAndRole(User requestUser, String ownerId, String role, boolean ownerRole) {
		List<User> result = new ArrayList<>();
		List<User> list = mongoTemplate.findAll(User.class);
		try {
			for(User user : list) {
				logger.debug(String.format("getUsersByOwnerIdAndRole: %s - %s - %s", ownerId, role, user.getEmail()));
				Map<String, List<Authorization>> rolesFiltered = new HashMap<>();
				for(String authKey : user.getRoles().keySet()) {
					if(Utils.validateAuthorizationByRole(authKey, ownerId, role, requestUser, ownerRole)) {
						rolesFiltered.put(authKey, user.getRoles().get(authKey));
					}
				}
				if(rolesFiltered.keySet().size() > 0) {
					user.setRoles(rolesFiltered);
					result.add(user);
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		return result;
	}
	
	public DataSetInfo getDataSetInfoBySubject(String subject) {
		Query query = new Query(new Criteria("subject").is(subject));
		DataSetInfo dataSetInfo = mongoTemplate.findOne(query, DataSetInfo.class);
		return dataSetInfo;
	}
	
	public void saveDataSetInfo(DataSetInfo dataSetInfo) {
		Criteria criteria = null;
		if(Utils.isNotEmpty(dataSetInfo.getSubject())) {
			criteria = new Criteria("subject").is(dataSetInfo.getSubject());
		} else if(Utils.isNotEmpty(dataSetInfo.getEmail())) {
			criteria = new Criteria("email").regex(dataSetInfo.getEmail(), "i");
		} else if(Utils.isNotEmpty(dataSetInfo.getCf())) {
			criteria = new Criteria("cf").regex(dataSetInfo.getCf(), "i");
		}
		DataSetInfo dataSetInfoDb = null;
		if(criteria != null) {
			Query query = new Query(criteria);
			dataSetInfoDb = mongoTemplate.findOne(query, DataSetInfo.class);
			if(dataSetInfoDb!= null) {
				Update update = new Update();
				update.set("subject", dataSetInfo.getSubject());
				update.set("name", dataSetInfo.getName());
				update.set("surname", dataSetInfo.getSurname());
				update.set("email", dataSetInfo.getEmail());
				update.set("cf", dataSetInfo.getCf());
				mongoTemplate.updateFirst(query, update, DataSetInfo.class);
			} else {
				mongoTemplate.save(dataSetInfo);
			}
		} else {
			mongoTemplate.save(dataSetInfo);
		}
	}
	
	public List<PedibusGame> getPedibusGames() {
		return mongoTemplate.findAll(PedibusGame.class);		
	}		
	
	public PedibusGame getPedibusGame(String ownerId, String pedibusGameId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(pedibusGameId));
		return mongoTemplate.findOne(query, PedibusGame.class);		
	}		
	
	public PedibusGame getPedibusGame(String pedibusGameId) {
		Query query = new Query(new Criteria("objectId").is(pedibusGameId));
		return mongoTemplate.findOne(query, PedibusGame.class);		
	}
	
	public List<PedibusGame> getPedibusGames(String ownerId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId));
		return mongoTemplate.find(query, PedibusGame.class);		
	}
	
	public List<PedibusGame> getPedibusGamesByShortName(String shortName) {
		Query query = new Query(new Criteria("shortName").is(shortName));
		return mongoTemplate.find(query, PedibusGame.class);		
	}	
	
	public List<PedibusGame> getPedibusGames(String ownerId, String instituteId, String schoolId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId)
				.and("instituteId").is(instituteId).and("schoolId").is(schoolId));
		return mongoTemplate.find(query, PedibusGame.class);		
	}
	
	public PedibusItineraryLeg getPedibusItineraryLeg(String ownerId, String objectId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		return mongoTemplate.findOne(query, PedibusItineraryLeg.class);		
	}		
	
	public List<PedibusItineraryLeg> getPedibusItineraryLegsByGameId(String ownerId, 
			String pedibusGameId, String itineraryId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("pedibusGameId").is(pedibusGameId)
		.and("itineraryId").is(itineraryId)).with(Sort.by(Sort.Direction.ASC, "position"));
		return mongoTemplate.find(query, PedibusItineraryLeg.class);		
	}		
	
	public List<PedibusItineraryLeg> getPedibusItineraryLegsByItineraryId(String itineraryId) {
		Query query = new Query(new Criteria("itineraryId").is(itineraryId))
				.with(Sort.by(Sort.Direction.ASC, "position"));
		return mongoTemplate.find(query, PedibusItineraryLeg.class);		
	}
	
	public PedibusItinerary getPedibusItinerary(String itineraryId) {
		Query query = new Query(new Criteria("objectId").is(itineraryId));
		return mongoTemplate.findOne(query, PedibusItinerary.class);		
	}
	
	public PedibusItinerary getPedibusItinerary(String ownerId, String pedibusGameId, String itineraryId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId)
				.and("pedibusGameId").is(pedibusGameId)
				.and("objectId").is(itineraryId));
		return mongoTemplate.findOne(query, PedibusItinerary.class);		
	}
	
	public List<PedibusItinerary> getPedibusItineraryByGameId(String ownerId, String pedibusGameId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("pedibusGameId").is(pedibusGameId));
		return mongoTemplate.find(query, PedibusItinerary.class);
	}
	
	public long getPedibusItineraryNumByGameId(String ownerId, String pedibusGameId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("pedibusGameId").is(pedibusGameId));
		return mongoTemplate.count(query, PedibusItinerary.class);
	}
	
	public PedibusPlayer getPedibusPlayer(String ownerId, String pedibusGameId,
			String nickname, String classRoom) {
		Query query = new Query(new Criteria("ownerId").is(ownerId)
				.and("pedibusGameId").is(pedibusGameId)
				.and("nickname").is(nickname)
				.and("classRoom").is(classRoom));
		return mongoTemplate.findOne(query, PedibusPlayer.class);		
	}
	
	public List<PedibusPlayer> getPedibusPlayers(String ownerId, String instituteId, String schoolId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("instituteId").is(instituteId)
				.and("schoolId").is(schoolId));
		return mongoTemplate.find(query, PedibusPlayer.class);		
	}
	
	public List<PedibusPlayer> getPedibusPlayers(String ownerId, String pedibusGameId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("pedibusGameId").is(pedibusGameId));
		return mongoTemplate.find(query, PedibusPlayer.class);		
	}
	
	public List<PedibusPlayer> getPedibusPlayersByClassRooms(String ownerId, String pedibusGameId, 
			List<String> classRooms) {
		PedibusGame pedibusGame = getPedibusGame(pedibusGameId);
		if(pedibusGame == null) {
			return new ArrayList<>();
		}
		Query query = new Query(new Criteria("ownerId").is(ownerId)
				.and("pedibusGameId").is(pedibusGameId)
				.and("classRoom").in(classRooms))
		.with(Sort.by(Sort.Direction.ASC, "nickname"));
		return mongoTemplate.find(query, PedibusPlayer.class);		
	}	
	
	/**
	public List<PedibusPlayer> getPedibusPlayersByClassRoom(String ownerId, String instituteId, 
			String schoolId, String classRoom) {
		Query query = new Query(new Criteria("ownerId").is(ownerId)
				.and("instituteId").is(instituteId)
				.and("schoolId").is(schoolId)
				.and("classRoom").is(classRoom))
		.with(new Sort(Sort.Direction.ASC, "nickname"));
		return mongoTemplate.find(query, PedibusPlayer.class);		
	}
	*/
	
	/**
	public List<PedibusPlayer> getPedibusPlayersBySchool(String ownerId, String instituteId, 
			String schoolId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId)
				.and("instituteId").is(instituteId)
				.and("schoolId").is(schoolId))
		.with(new Sort(Sort.Direction.ASC, "nickname"));
		return mongoTemplate.find(query, PedibusPlayer.class);		
	}
	*/
	
	public CalendarDay getCalendarDay(String ownerId, String gameId, String classRoom,
			Date day) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom).and("day").is(day));
		CalendarDay calendarDayDB = mongoTemplate.findOne(query, CalendarDay.class);
		return calendarDayDB;
	}
	
	public List<Excursion> getExcursions(String ownerId, String pedibusGameId, String classRoom,
			Date from, Date to) {
		Criteria criteria = new Criteria("ownerId").is(ownerId)
				.and("pedibusGameId").is(pedibusGameId)
				.and("classRoom").is(classRoom);
		Criteria timeCriteria = new Criteria().andOperator(
				Criteria.where("day").gte(from),
				Criteria.where("day").lte(to));
		criteria = criteria.andOperator(timeCriteria);
		Query query = new Query(criteria);
		query.with(Sort.by(Sort.Direction.DESC, "day"));
		List<Excursion> result = mongoTemplate.find(query, Excursion.class);
		return result;
	}
	
	public List<CalendarDay> getCalendarDays(String ownerId, String pedibusGameId, String classRoom,
			Date from, Date to) {
		Criteria criteria = new Criteria("ownerId").is(ownerId)
				.and("pedibusGameId").is(pedibusGameId)
				.and("classRoom").is(classRoom);
		Criteria timeCriteria = new Criteria().andOperator(
				Criteria.where("day").gte(from),
				Criteria.where("day").lte(to));
		criteria = criteria.andOperator(timeCriteria);
		Query query = new Query(criteria);
		query.with(Sort.by(Sort.Direction.ASC, "day"));
		List<CalendarDay> result = mongoTemplate.find(query, CalendarDay.class);
		return result;
	}	
	
	public Map<String, Boolean> saveCalendarDay(String ownerId, String pedibusGameId, 
			String classRoom,	CalendarDay calendarDay) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(calendarDay.getDay());
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    calendarDay.setDay(cal.getTime());
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("pedibusGameId").is(pedibusGameId)
				.and("classRoom").is(classRoom).and("day").is(calendarDay.getDay()));
		CalendarDay calendarDayDB = mongoTemplate.findOne(query, CalendarDay.class);
		Date now = new Date();
		Boolean merged = Boolean.FALSE; 
		Boolean closed = Boolean.FALSE;
		if(calendarDayDB == null) {
			calendarDay.setCreationDate(now);
			calendarDay.setLastUpdate(now);
			calendarDay.setOwnerId(ownerId);
			calendarDay.setObjectId(Utils.getUUID());
			calendarDay.setPedibusGameId(pedibusGameId);
			calendarDay.setClassRoom(classRoom);
			calendarDay.setClosed(true);
			mongoTemplate.save(calendarDay);
		} else {
			if(calendarDayDB.isClosed()) {
				closed = Boolean.TRUE;
			} else {
				//merge pedibus data with calendar data
				Map<String, String> oldModeMap = calendarDayDB.getModeMap();
				for(String playerId : calendarDay.getModeMap().keySet()) {
					String mode = calendarDay.getModeMap().get(playerId);
					String oldMode = oldModeMap.get(playerId);
					if(oldMode == null) {
						continue;
					} else if(mode.equals(Const.MODE_PEDIBUS) && oldMode.equals(Const.MODE_PEDIBUS)) {
						continue;
					}	else if(mode.equals(Const.MODE_WALK) && oldMode.equals(Const.MODE_PEDIBUS)) {
						calendarDay.getModeMap().put(playerId, Const.MODE_PEDIBUS);
					} else if(oldMode.equals(Const.MODE_PEDIBUS)){
						calendarDay.getModeMap().put(playerId, Const.MODE_PEDIBUS);
						merged = Boolean.TRUE;
					}
				}
				Update update = new Update();
				update.set("meteo", calendarDay.getMeteo());
				update.set("modeMap", calendarDay.getModeMap());
				update.set("modeMapReturnTrip", calendarDay.getModeMapReturnTrip());
				update.set("closed", Boolean.TRUE);
				update.set("lastUpdate", now);
				mongoTemplate.updateFirst(query, update, CalendarDay.class);				
			}
		}
		result.put(Const.MERGED, merged);
		result.put(Const.CLOSED, closed);
		return result;
	}
	
	public List<String> updateCalendarDayFromPedibus(String ownerId, String pedibusGameId, String routeId, 
			String classRoom, Date day, Map<String, String> modeMap) {
		List<String> newPlayers = new ArrayList<>();
		Route route = getRouteById(ownerId, routeId);
		boolean returnTrip = false;
		if(route != null) {
			returnTrip = route.isReturnTrip();
		}
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("pedibusGameId").is(pedibusGameId)
				.and("classRoom").is(classRoom).and("day").is(day));
		CalendarDay calendarDayDB = mongoTemplate.findOne(query, CalendarDay.class);
		Date now = new Date();
		if(calendarDayDB == null) {
			CalendarDay calendarDay = new CalendarDay();
			calendarDay.setCreationDate(now);
			calendarDay.setLastUpdate(now);
			calendarDay.setOwnerId(ownerId);
			calendarDay.setObjectId(Utils.getUUID());
			calendarDay.setPedibusGameId(pedibusGameId);
			calendarDay.setClassRoom(classRoom);
			calendarDay.setDay(day);
			if(returnTrip) {
				calendarDay.setModeMapReturnTrip(modeMap);
			} else {
				calendarDay.setModeMap(modeMap);
			}
			mongoTemplate.save(calendarDay);
			newPlayers.addAll(modeMap.keySet());
			return newPlayers;
		} else {
			if(calendarDayDB.isClosed()) {
				return newPlayers;
			} else {
				Map<String, String> calendarModeMap = null;
				if(returnTrip) {
					calendarModeMap = calendarDayDB.getModeMapReturnTrip();
				} else {
					calendarModeMap = calendarDayDB.getModeMap();
				}
				for(String playerId : modeMap.keySet()) {
					if(calendarModeMap != null) {
						if(!calendarModeMap.containsKey(playerId)) {
							calendarModeMap.put(playerId, modeMap.get(playerId));
							newPlayers.add(playerId);
						}
					}
				}
				Update update = new Update();
				if(returnTrip) {
					update.set("modeMapReturnTrip", calendarDayDB.getModeMapReturnTrip());
				} else {
					update.set("modeMap", calendarDayDB.getModeMap());
				}
				update.set("lastUpdate", now);
				mongoTemplate.updateFirst(query, update, CalendarDay.class);
				return newPlayers;
			}
		}
	}
	
	public void removeCalendarDayByGameId(String ownerId, String pedibusGameId) {
		Query query = new Query(new Criteria("pedibusGameId").is(pedibusGameId).and("ownerId").is(ownerId));
		mongoTemplate.remove(query, CalendarDay.class);
	}

	public PedibusGame savePedibusGame(PedibusGame game, String ownerId, boolean canUpdate) throws StorageException {
		Query query = new Query(new Criteria("objectId").is(game.getObjectId()).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if (gameDB == null) {
			game.setCreationDate(now);
			game.setLastUpdate(now);
			if(Utils.isEmpty(game.getObjectId())) {
				game.setObjectId(Utils.getUUID());
			}
			game.setOwnerId(ownerId);
			mongoTemplate.save(game);
		} else if (canUpdate) {
			Update update = new Update();
			update.set("gameName", game.getGameName());
			update.set("shortName", game.getShortName());
      update.set("sponsorTemplate", game.getSponsorTemplate());
			if(!gameDB.isDeployed()) {
				update.set("instituteId", game.getInstituteId());
				update.set("schoolId", game.getSchoolId());
				update.set("classRooms", game.getClassRooms());
				update.set("gameId", game.getGameId());
				update.set("gameDescription", game.getGameDescription());
				update.set("from", game.getFrom());
				update.set("to", game.getTo());
				update.set("globalTeam", game.getGlobalTeam());
				update.set("fromHour", game.getFromHour());
				update.set("toHour", game.getToHour());
				update.set("interval", game.getInterval());
				update.set("lateSchedule", game.isLateSchedule());
				update.set("usingPedibusData", game.isUsingPedibusData());
				update.set("params", game.getParams());
				update.set("mobilityParams", game.getMobilityParams());
	      update.set("daysOfWeek", game.getDaysOfWeek());
	      update.set("modalities", game.getModalities());
	      update.set("roundTrip", game.isRoundTrip());
	      update.set("useCalendar", game.isUseCalendar());
			}
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		} else {
			logger.warn("Cannot update existing PedibusGame with id " + game.getObjectId());
		}
		return game;
	}
	
	public PedibusGame removePedibusGame(String ownerId, String pedibusGameId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("objectId").is(pedibusGameId).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		if(gameDB == null) {
			throw new EntityNotFoundException(String.format("PedibusGame with id %s not found", pedibusGameId));
		}
		mongoTemplate.findAndRemove(query, PedibusGame.class);
		removePedibusItineraryByGameId(ownerId, pedibusGameId);
		return gameDB;
	}
	
	public void removePedibusItineraryByGameId(String ownerId, String pedibusGameId) {
		Query query = new Query(new Criteria("pedibusGameId").is(pedibusGameId).and("ownerId").is(ownerId));
		mongoTemplate.remove(query, PedibusItinerary.class);
		removePedibusItineraryLegByGameId(ownerId, pedibusGameId);
	}
	
	public void removePedibusItineraryLegByGameId(String ownerId, String pedibusGameId) {
		Query query = new Query(new Criteria("pedibusGameId").is(pedibusGameId).and("ownerId").is(ownerId));
		List<PedibusItineraryLeg> list = mongoTemplate.find(query, PedibusItineraryLeg.class);
		for(PedibusItineraryLeg leg : list) {
			removePedibusItineraryLeg(ownerId, leg.getObjectId());
		}
	}
	
	public void removePedibusItineraryLegByItineraryId(String ownerId, String pedibusGameId,
			String itineraryId) {
		Query query = new Query(new Criteria("pedibusGameId").is(pedibusGameId)
				.and("itineraryId").is(itineraryId).and("ownerId").is(ownerId));
		List<PedibusItineraryLeg> list = mongoTemplate.find(query, PedibusItineraryLeg.class);
		for(PedibusItineraryLeg leg : list) {
			removePedibusItineraryLeg(ownerId, leg.getObjectId());
		}
	}
	
	public void removePedibusItineraryLeg(String ownerId, String objectId) {
		Query query = new Query(new Criteria("objectId").is(objectId)
				.and("ownerId").is(ownerId));
		mongoTemplate.findAndRemove(query, PedibusItineraryLeg.class);
		removeMultimediaContentByLegId(ownerId, objectId);
	}
	
	public void removePedibusItinerary(String ownerId, String pedibusGameId, String objectId) {
		Query query = new Query(new Criteria("objectId").is(objectId)
				.and("pedibusGameId").is(pedibusGameId).and("ownerId").is(ownerId));
		mongoTemplate.findAndRemove(query, PedibusItinerary.class);
		removePedibusItineraryLegByItineraryId(ownerId, pedibusGameId, objectId);
	}
	
	public void updatePedibusGameLastDaySeen(String ownerId, String pedibusGameId, String lastDaySeen) {
		Query query = new Query(new Criteria("objectId").is(pedibusGameId).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if (gameDB != null) {
			Update update = new Update();
			update.set("lastDaySeen", lastDaySeen);
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		}
	}
	
	public void updatePedibusGameGameId(String ownerId, String pedibusGameId, String gameId) {
		Query query = new Query(new Criteria("objectId").is(pedibusGameId).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if (gameDB != null) {
			Update update = new Update();
			update.set("gameId", gameId);
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		}
	}
	
	public void updatePedibusGameDeployed(String ownerId, String pedibusGameId, boolean deployed) {
		Query query = new Query(new Criteria("objectId").is(pedibusGameId).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if (gameDB != null) {
			Update update = new Update();
			update.set("deployed", deployed);
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		}
	}
	
	public void updatePedibusGameConfTemplateId(String ownerId, String pedibusGameId, String templateId) {
		Query query = new Query(new Criteria("objectId").is(pedibusGameId).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if (gameDB != null) {
			Update update = new Update();
			update.set("confTemplateId", templateId);
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		}
	}
	
	public void resetPollingFlag(String ownerId, String pedibusGameId) {
		Query query = new Query(new Criteria("objectId").is(pedibusGameId).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if (gameDB != null) {
			Update update = new Update();
			update.set("pollingFlagMap", new HashMap<String, Boolean>());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		}
	}
	
	public void updatePollingFlag(String ownerId, String pedibusGameId, String routeId, boolean flag) {
		Query query = new Query(new Criteria("objectId").is(pedibusGameId).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if (gameDB != null) {
			gameDB.getPollingFlagMap().put(routeId, flag);
			Update update = new Update();
			update.set("pollingFlagMap", gameDB.getPollingFlagMap());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		}
	}

	
	public void saveExcursion(String ownerId, String pedibusGameId, String classRoom, 
			String name, Integer children, Double distance, Date day, String meteo, boolean goodAction) {
		Excursion excursion = new Excursion();
		Date now = new Date();
		excursion.setOwnerId(ownerId);
		excursion.setObjectId(Utils.getUUID());
		excursion.setCreationDate(now);
		excursion.setLastUpdate(now);
		excursion.setPedibusGameId(pedibusGameId);
		excursion.setDay(day);
		excursion.setClassRoom(classRoom);
		excursion.setChildren(children);
		excursion.setDistance(distance);
		excursion.setMeteo(meteo);
		excursion.setName(name);
		excursion.setGoodAction(goodAction);
		mongoTemplate.save(excursion);
	}
	
	public void removeExcursionByGameId(String ownerId, String pedibusGameId) {
		Query query = new Query(new Criteria("pedibusGameId").is(pedibusGameId).and("ownerId").is(ownerId));
		mongoTemplate.remove(query, Excursion.class);
	}

	public void savePedibusItineraryLeg(PedibusItineraryLeg leg, String ownerId, 
			boolean canUpdate, boolean deployed) throws StorageException {
		Query query = new Query(new Criteria("pedibusGameId").is(leg.getPedibusGameId())
				.and("itineraryId").is(leg.getItineraryId()).and("objectId").is(leg.getObjectId())
				.and("ownerId").is(ownerId));
		PedibusItineraryLeg legDB = mongoTemplate.findOne(query, PedibusItineraryLeg.class);
		Date now = new Date();
		if (legDB == null) {
			leg.setCreationDate(now);
			leg.setLastUpdate(now);
			if(Utils.isEmpty(leg.getObjectId())) {
				leg.setObjectId(Utils.getUUID());
			}
			leg.setOwnerId(ownerId);
			mongoTemplate.save(leg);
		} else if (canUpdate) {
			Update update = new Update();
			update.set("imageUrl", leg.getImageUrl());
			if(!deployed) {
				update.set("name", leg.getName());
				update.set("description", leg.getDescription());
				update.set("position", leg.getPosition());
				update.set("geocoding", leg.getGeocoding());
				update.set("polyline", leg.getPolyline());
				update.set("score", leg.getScore());
				update.set("transport", leg.getTransport());	
				update.set("additionalPoints", leg.getAdditionalPoints());
				update.set("icon", leg.getIcon());
			}
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusItineraryLeg.class);
		} else {
			logger.warn("Cannot update existing PedibusItineraryLeg with gameId " 
			+ leg.getPedibusGameId() + " and id " + leg.getObjectId());
		}
	}
	
	public boolean existsPedibusPlayerByNickname(PedibusPlayer player) {
		Query queryNick = new Query(new Criteria("ownerId").is(player.getOwnerId())
				.and("pedibusGameId").is(player.getPedibusGameId())
				.and("nickname").is(player.getNickname())
				.and("classRoom").is(player.getClassRoom()));
		PedibusPlayer playerDb = mongoTemplate.findOne(queryNick, PedibusPlayer.class);
		return (playerDb != null);
		
	}
	
	public boolean savePedibusPlayer(PedibusPlayer player, boolean canUpdate) throws StorageException {
		Query query = new Query(new Criteria("objectId").is(player.getObjectId())
				.and("ownerId").is(player.getOwnerId()));
		PedibusPlayer playerDB = mongoTemplate.findOne(query, PedibusPlayer.class);
		Date now = new Date();
		if (playerDB == null) {
			if(existsPedibusPlayerByNickname(player)) {
				throw new StorageException("nickname already present");
			}
			player.setCreationDate(now);
			player.setLastUpdate(now);
			player.setObjectId(Utils.getUUID());
			player.setOwnerId(player.getOwnerId());
			mongoTemplate.save(player);
			return false;
		} else if (canUpdate) {
			Update update = new Update();
			update.set("nickname", player.getNickname());
			update.set("classRoom", player.getClassRoom());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusPlayer.class);
			return true;
		} else {
			logger.warn("Cannot update existing PedibusPlayer with id " + player.getObjectId());
			return false;
		}
	}
	
	public PedibusPlayer getPedibusPlayer(String ownerId, String objectId) {
		Query query = new Query(new Criteria("objectId").is(objectId).and("ownerId").is(ownerId));
		return mongoTemplate.findOne(query, PedibusPlayer.class);
	}
	
	public void removePedibusPlayerByGameId(String ownerId, String pedibusGameId) {
		Query query = new Query(new Criteria("pedibusGameId").is(pedibusGameId).and("ownerId").is(ownerId));
		mongoTemplate.remove(query, PedibusPlayer.class);
	}
	
	public PedibusPlayer removePedibusPlayer(String ownerId, String objectId) {
		Query query = new Query(new Criteria("objectId").is(objectId).and("ownerId").is(ownerId));
		return mongoTemplate.findAndRemove(query, PedibusPlayer.class);
	}
	
	public void savePedibusItinerary(PedibusItinerary itinerary) {
		Query query = new Query(new Criteria("ownerId").is(itinerary.getOwnerId()).and("objectId").is(itinerary.getObjectId()));
		PedibusItinerary itineraryDb = mongoTemplate.findOne(query, PedibusItinerary.class);
		Date actualDate = new Date();
		if(itineraryDb == null) {
			itinerary.setCreationDate(actualDate);
			itinerary.setLastUpdate(actualDate);
			if(Utils.isEmpty(itinerary.getObjectId())) {
				itinerary.setObjectId(Utils.getUUID());
			}			
			mongoTemplate.save(itinerary);
		} else {
			Update update = new Update();
			update.set("name", itinerary.getName());
			update.set("description", itinerary.getDescription());
			update.set("lastUpdate", actualDate);
			mongoTemplate.updateFirst(query, update, PedibusItinerary.class);
		}
	}

	public Child getChild(String ownerId, String objectId) {
		Query query = new Query(Criteria.where("objectId").is(objectId)
				.and("ownerId").is(ownerId));
		return mongoTemplate.findOne(query, Child.class);		
	}
	
	public List<Child> getChildrenBySchool(String ownerId, String instituteId, String schoolId) {
		Query query = new Query(Criteria.where("instituteId").is(instituteId)
				.and("schoolId").is(schoolId)
				.and("ownerId").is(ownerId));
		return mongoTemplate.find(query, Child.class);
	}
	
	public List<Route> getRouteBySchool(String ownerId, String instituteId, String schoolId) {
		Query query = new Query(Criteria.where("instituteId").is(instituteId)
				.and("schoolId").is(schoolId)
				.and("ownerId").is(ownerId));
		return mongoTemplate.find(query, Route.class);		
	}
	
	public Route getRouteById(String ownerId, String routeId) {
		Query query = new Query(Criteria.where("objectId").is(routeId)
				.and("ownerId").is(ownerId));
		return mongoTemplate.findOne(query, Route.class);				
	}
	
	public List<Route> getRoutes() {
		Query query = new Query();
		return mongoTemplate.find(query, Route.class);		
	}
	
	public School getSchool(String ownerId, String instituteId, String schoolId) {
		Query query = new Query(Criteria.where("instituteId").is(instituteId)
				.and("objectId").is(schoolId)
				.and("ownerId").is(ownerId));
		return mongoTemplate.findOne(query, School.class);		
	}
	
	public Institute getInstitute(String ownerId, String instituteId) {
		Query query = new Query(Criteria.where("objectId").is(instituteId)
				.and("ownerId").is(ownerId));
		return mongoTemplate.findOne(query, Institute.class);		
	}
	
	public void addUser(User user) {
		Date actualDate = new Date();
		user.setCreationDate(actualDate);
		user.setLastUpdate(actualDate);
		mongoTemplate.save(user);
	}
	
	public void updateUser(User user) throws EntityNotFoundException {
		Query query = new Query(new Criteria("objectId").is(user.getObjectId()));
		User userDb = mongoTemplate.findOne(query, User.class);
		if(userDb == null) {
			throw new EntityNotFoundException(String.format("User with id %s not found", user.getObjectId()));
		}
		Date actualDate = new Date();
		Update update = new Update();
		update.set("name", user.getName());
		update.set("surname", user.getSurname());
		//update.set("email", user.getEmail());
    update.set("termUsage", user.getTermUsage());
		update.set("cf", user.getCf());
		update.set("subject", user.getSubject());
		update.set("lastUpdate", actualDate);
		mongoTemplate.updateFirst(query, update, User.class);
	}

	public User addUserRole(String email, String authKey, List<Authorization> auths) 
			throws EntityNotFoundException {
		Query query = new Query(new Criteria("email").is(email));
		User userDb = mongoTemplate.findOne(query, User.class);
		if(userDb == null) {
			throw new EntityNotFoundException(String.format("User %s not found", email));
		}
		userDb.getRoles().put(authKey, auths);
		Date actualDate = new Date();
		Update update = new Update();
		update.set("roles", userDb.getRoles());
		update.set("lastUpdate", actualDate);
		mongoTemplate.updateFirst(query, update, User.class);
		return userDb;
	}
	
	public void removeUserAuthKey(String email, String authKey) 
			throws EntityNotFoundException {
		Query query = new Query(new Criteria("email").is(email));
		User userDb = mongoTemplate.findOne(query, User.class);
		if(userDb == null) {
			throw new EntityNotFoundException(String.format("User %s not found", email));
		}
		userDb.getRoles().remove(authKey);
		Date actualDate = new Date();
		Update update = new Update();
		update.set("roles", userDb.getRoles());
		update.set("lastUpdate", actualDate);
		mongoTemplate.updateFirst(query, update, User.class);
	}
	
	public void removeUser(String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("objectId").is(objectId));
		User entityDB = mongoTemplate.findOne(query, User.class);
		if(entityDB == null) {
			throw new EntityNotFoundException(String.format("user with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, User.class);
	}

	public List<PedibusGameConfTemplate> getPedibusGameConfTemplates() {
		List<PedibusGameConfTemplate> result = mongoTemplate.findAll(PedibusGameConfTemplate.class);
		return result;
	}
	
	public PedibusGameConfTemplate getPedibusGameConfTemplate(String objectId) {
		Query query = new Query(new Criteria("objectId").is(objectId));
		PedibusGameConfTemplate result = mongoTemplate.findOne(query, PedibusGameConfTemplate.class);
		return result;
	}
	
	public PedibusGameConfTemplate savePedibusGameConfTemplate(PedibusGameConfTemplate confTemplate) {
		PedibusGameConfTemplate confTemplateDb = null;
		Date now = new Date();
		if(Utils.isNotEmpty(confTemplate.getObjectId())) {
			Query query = new Query(new Criteria("objectId").is(confTemplate.getObjectId()));
			confTemplateDb = mongoTemplate.findOne(query, PedibusGameConfTemplate.class);
			if(confTemplateDb == null) {
				confTemplate.setCreationDate(now);
				confTemplate.setLastUpdate(now);
				mongoTemplate.save(confTemplate);
			} else {
				Update update = new Update();
				update.set("name", confTemplate.getName());
				update.set("version", confTemplate.getVersion());
				update.set("description", confTemplate.getDescription());
				update.set("ruleFileTemplates", confTemplate.getRuleFileTemplates());
				update.set("actions", confTemplate.getActions());
				update.set("badgeCollections", confTemplate.getBadgeCollections());
				update.set("challengeModels", confTemplate.getChallengeModels());
				update.set("points", confTemplate.getPoints());
				update.set("lastUpdate", now);
				mongoTemplate.updateFirst(query, update, PedibusGameConfTemplate.class);
			}
		} else {
			confTemplate.setObjectId(Utils.getUUID());
			confTemplate.setCreationDate(now);
			confTemplate.setLastUpdate(now);
			mongoTemplate.save(confTemplate);
		}
		return confTemplate;
	}

	public void updatePedibusGameConfParams(String ownerId, String pedibusGameId, Map<String, String> params) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(pedibusGameId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if(gameDB != null) {
			gameDB.getParams().putAll(params);
			Update update = new Update();
			update.set("lastUpdate", now);
			update.set("params", gameDB.getParams());
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		}
	}
	
	public void removeMultimediaContentByItineraryId(String ownerId, String instituteId,
			String schoolId, String itineraryId) {
		Query query = new Query(new Criteria("instituteId").is(instituteId)
				.and("schoolId").is(schoolId).and("itineraryId").is(itineraryId)
				.and("ownerId").is(ownerId));
		List<MultimediaContent> list = mongoTemplate.find(query, MultimediaContent.class);
		for(MultimediaContent mc : list) {
			removeMultimediaContent(ownerId, mc.getObjectId());
		}
	}
	
	public void removeMultimediaContentByLegId(String ownerId, String legId) {
		Query query = new Query(new Criteria("legId").is(legId)
				.and("ownerId").is(ownerId));
		List<MultimediaContent> list = mongoTemplate.find(query, MultimediaContent.class);
		for(MultimediaContent mc : list) {
			removeMultimediaContent(ownerId, mc.getObjectId());
		}
	}
	
	public void saveMultimediaContent(MultimediaContent content) {
		Query query = new Query(new Criteria("ownerId").is(content.getOwnerId())
				.and("objectId").is(content.getObjectId()));
		MultimediaContent contentDB = mongoTemplate.findOne(query, MultimediaContent.class);
		Date now = new Date();
		if(contentDB == null) {
			content.setCreationDate(now);
			content.setLastUpdate(now);
			if(Utils.isEmpty(content.getObjectId())) {
				content.setObjectId(Utils.getUUID());
			}
			mongoTemplate.save(content);
		} else {
			Update update = new Update();
			update.set("lastUpdate", now);
			update.set("name", content.getName());
			update.set("link", content.getLink());
			update.set("type", content.getType());
			update.set("geocoding", content.getGeocoding());
			update.set("subjects", content.getSubjects());
			update.set("schoolYears", content.getSchoolYears());
			update.set("classes", content.getClasses());
			update.set("sharable", content.isSharable());
			update.set("publicLink", content.isPublicLink());
			update.set("previewUrl", content.getPreviewUrl());
			update.set("position", content.getPosition());
			mongoTemplate.updateFirst(query, update, MultimediaContent.class);
		}
	}
	
	public void updateMultimediaContentPosition(MultimediaContent content) {
		Query query = new Query(new Criteria("ownerId").is(content.getOwnerId())
				.and("objectId").is(content.getObjectId()));
		MultimediaContent contentDB = mongoTemplate.findOne(query, MultimediaContent.class);
		Date now = new Date();
		if(contentDB != null) {
			Update update = new Update();
			update.set("lastUpdate", now);
			update.set("position", content.getPosition());
			mongoTemplate.updateFirst(query, update, MultimediaContent.class);
		}
	}
	
	public void removeMultimediaContent(String ownerId, String contentId) {
		Query query = new Query(new Criteria("objectId").is(contentId)
				.and("ownerId").is(ownerId));
		mongoTemplate.remove(query, MultimediaContent.class);
		query = new Query(new Criteria("contentReferenceId").is(contentId));
		Update update = new Update();
		update.set("contentReferenceId", null);
		mongoTemplate.updateMulti(query, update, MultimediaContent.class);
	}
	
	public MultimediaContent getMultimediaContent(String ownerId, String contentId) {
		Query query = new Query(new Criteria("objectId").is(contentId)
				.and("ownerId").is(ownerId));
		return mongoTemplate.findOne(query, MultimediaContent.class);
	}

	public MultimediaContent getMultimediaContent(String contentId) {
		Query query = new Query(new Criteria("objectId").is(contentId));
		return mongoTemplate.findOne(query, MultimediaContent.class);
	}
	
	public List<MultimediaContent> getMultimediaContentByLeg(String ownerId, String itineraryLegId) {
		Query query = new Query(Criteria.where("legId").is(itineraryLegId)
				.and("ownerId").is(ownerId));
		query.with(Sort.by(Sort.Direction.ASC, "position"));
		List<MultimediaContent> result = mongoTemplate.find(query, MultimediaContent.class);
		return result;
	}
	
	public long getMultimediaContentNumberByLeg(String ownerId, String itineraryLegId) {
		Query query = new Query(Criteria.where("legId").is(itineraryLegId)
				.and("ownerId").is(ownerId));
		return mongoTemplate.count(query, MultimediaContent.class);
	}

	public List<MultimediaContent> getMultimediaContentByReferenceId(String contentId) {
		Query query = new Query(Criteria.where("contentReferenceId").is(contentId));
		List<MultimediaContent> result = mongoTemplate.find(query, MultimediaContent.class);
		return result;
	}
	
	public List<MultimediaContent> searchMultimediaContent(String text, Double lat, Double lng,
			Double distance, List<String> types, List<String> subjects, List<String> schoolYears) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		if(Utils.isNotEmpty(text)) {
			TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matching(text);
			query = TextQuery.queryText(textCriteria).sortByScore();			
		} 
		if((lat != null) && (lng != null)) {
			Point center = new Point(lng, lat);
			if(distance == null) {
				distance = 0.1;
			}
			Distance geoDistance = new Distance(distance, Metrics.KILOMETERS);
			Circle circle = new Circle(center, geoDistance);
			criteria = criteria.and("geocoding").withinSphere(circle);
		}
		if((types != null) && (types.size() > 0)) {
			criteria = criteria.and("type").in(types);
		}
		if((subjects != null) && (subjects.size() > 0)) {
			criteria = criteria.and("subjects").in(subjects);
		}
		if((schoolYears != null) && (schoolYears.size() > 0)) {
			criteria = criteria.and("schoolYears").in(schoolYears);
		}
		criteria = criteria.and("sharable").is(true);
		criteria = criteria.and("disabled").is(false);
		query.addCriteria(criteria);
		query.limit(200);
		List<MultimediaContent> result = mongoTemplate.find(query, MultimediaContent.class);
		return result;
	}
	
	public Map<String, MonitoringPlay> getMonitoringPlayByGameId(String ownerId, String pedibusGameId) {
		Map<String, MonitoringPlay> result = new HashMap<String, MonitoringPlay>();
		//calendar stats
		Query query = new Query(new Criteria("ownerId").is(ownerId)
				.and("pedibusGameId").is(pedibusGameId));
		query.with(Sort.by(Direction.DESC, "day"));
		query.limit(1);
		long number = mongoTemplate.count(query, CalendarDay.class);
		CalendarDay findOne = mongoTemplate.findOne(query, CalendarDay.class);
		MonitoringPlay playCalendar = new MonitoringPlay();
		playCalendar.setNumber(number);
		if(findOne != null) {
			playCalendar.setLastPlay(findOne.getDay().getTime());
		}
		result.put("calendar", playCalendar);
		
		//pedibus stats
		PedibusGame game = getPedibusGame(ownerId, pedibusGameId);
		List<Route> routeList = getRouteBySchool(ownerId, game.getInstituteId(), game.getSchoolId());
		List<String> routeIds = new ArrayList<String>();
		for(Route route : routeList) {
			routeIds.add(route.getObjectId());
		}
		query = new Query(new Criteria("ownerId").is(ownerId)
				.and("routeId").in(routeIds));
		query.with(Sort.by(Direction.DESC, "timestamp"));
		query.limit(1);
		number = mongoTemplate.count(query, WsnEvent.class);
		WsnEvent event = mongoTemplate.findOne(query, WsnEvent.class);
		MonitoringPlay playPedibus = new MonitoringPlay();
		playPedibus.setNumber(number);
		if(event != null) {
			playPedibus.setLastPlay(event.getTimestamp().getTime());
		}
		result.put("pedibus", playPedibus);
		
		//excursion stats
		query = new Query(new Criteria("ownerId").is(ownerId)
				.and("pedibusGameId").is(pedibusGameId));
		query.with(Sort.by(Direction.DESC, "day"));
		query.limit(1);
		number = mongoTemplate.count(query, Excursion.class);
		Excursion excursion = mongoTemplate.findOne(query, Excursion.class);
		MonitoringPlay playExcursion = new MonitoringPlay();
		playExcursion.setNumber(number);
		if(excursion != null) {
			playExcursion.setLastPlay(excursion.getDay().getTime());
		}
		result.put("excursion", playExcursion);
		
		return result;
	}
	
	public void saveAvatar(Avatar avatar) {
		Query query = new Query(new Criteria("ownerId").is(avatar.getOwnerId()).and("objectId").is(avatar.getObjectId()));
		Avatar avatarDb = mongoTemplate.findOne(query, Avatar.class);
		Date now = new Date();
		if(avatarDb == null) {
			avatar.setCreationDate(now);
			avatar.setLastUpdate(now);
			mongoTemplate.save(avatar);
		} else {
			Update update = new Update();
			update.set("contentType", avatar.getContentType());
			update.set("image", avatar.getImage());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, Avatar.class);
		}
	}
	
	public ModalityMap getModalityMap() {
		return mongoTemplate.findOne(new Query(), ModalityMap.class);
	}
	
	public void saveModalityMap(ModalityMap modalityMap) {
		mongoTemplate.save(modalityMap);
	}
	
	public MultimediaContentTags getMultimediaContentTags() {
		return mongoTemplate.findOne(new Query(), MultimediaContentTags.class);
	}
	
	public void saveMultimediaContentTags(MultimediaContentTags mct) {
		mongoTemplate.save(mct);
	}
	
	public boolean existsPedibusGame(String id) {
		Query query = new Query(new Criteria("objectId").is(id));
		return mongoTemplate.exists(query, PedibusGame.class);
	}
	
	public boolean existsPedibusItinerary(String id) {
		Query query = new Query(new Criteria("objectId").is(id));
		return mongoTemplate.exists(query, PedibusItinerary.class);
	}

	public void updateLegPosition(PedibusItineraryLeg leg) {
		Query query = new Query(new Criteria("ownerId").is(leg.getOwnerId())
				.and("objectId").is(leg.getObjectId()));
		PedibusItineraryLeg legDb = mongoTemplate.findOne(query, PedibusItineraryLeg.class);
		Date now = new Date();
		if(legDb != null) {
			Update update = new Update();
			update.set("lastUpdate", now);
			update.set("position", leg.getPosition());
			mongoTemplate.updateFirst(query, update, PedibusItineraryLeg.class);
		}		
	}

}
