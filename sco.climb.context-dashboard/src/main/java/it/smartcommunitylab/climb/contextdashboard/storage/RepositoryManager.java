package it.smartcommunitylab.climb.contextdashboard.storage;

import it.smartcommunitylab.climb.contextdashboard.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.contextdashboard.security.DataSetInfo;
import it.smartcommunitylab.climb.contextdashboard.security.Token;
import it.smartcommunitylab.climb.contextstore.model.Anchor;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.PassengerCalendar;
import it.smartcommunitylab.climb.contextstore.model.Pedibus;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.contextstore.model.VolunteerCalendar;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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

	public Token findTokenByToken(String token) {
		Query query = new Query(new Criteria("token").is(token));
		Token result = mongoTemplate.findOne(query, Token.class);
		return result;
	}
	
	public List<DataSetInfo> getDataSetInfo() {
		List<DataSetInfo> result = mongoTemplate.findAll(DataSetInfo.class);
		return result;
	}
	
	public void saveDataSetInfo(DataSetInfo dataSetInfo) {
		Query query = new Query(new Criteria("ownerId").is(dataSetInfo.getOwnerId()));
		DataSetInfo appInfoDB = mongoTemplate.findOne(query, DataSetInfo.class);
		if (appInfoDB == null) {
			mongoTemplate.save(dataSetInfo);
		} else {
			Update update = new Update();
			update.set("password", dataSetInfo.getPassword());
			update.set("token", dataSetInfo.getToken());
			mongoTemplate.updateFirst(query, update, DataSetInfo.class);
		}
	}
	
	public void saveAppToken(String name, String token) {
		Query query = new Query(new Criteria("name").is(name));
		Token tokenDB = mongoTemplate.findOne(query, Token.class);
		if(tokenDB == null) {
			Token newToken = new Token();
			newToken.setToken(token);
			newToken.setName(name);
			newToken.getPaths().add("/api");
			mongoTemplate.save(newToken);
		} else {
			Update update = new Update();
			update.set("token", token);
			mongoTemplate.updateFirst(query, update, Token.class);
		}
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
		update.set("pedibusId", route.getPedibusId());
		update.set("schoolId", route.getSchoolId());
		update.set("from", route.getFrom());
		update.set("to", route.getTo());
		update.set("distance", route.getDistance());
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
		update.set("surname", child.getName());
		update.set("externalId", child.getExternalId());
		update.set("parentName", child.getParentName());
		update.set("phone", child.getPhone());
		update.set("schoolId", child.getSchoolId());
		update.set("classRoom", child.getClassRoom());
		update.set("wsnId", child.getWsnId());
		update.set("imageUrl", child.getImageUrl());
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
		update.set("password", volunteer.getPassword());
		update.set("wsnId", volunteer.getWsnId());
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
}
