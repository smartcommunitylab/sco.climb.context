package it.smartcommunitylab.climb.contextstore.storage;

import it.smartcommunitylab.climb.contextstore.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.contextstore.model.Anchor;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Pedibus;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.contextstore.security.DataSetInfo;
import it.smartcommunitylab.climb.contextstore.security.Token;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	public List<?> findData(Class<?> entityClass, Criteria criteria, String ownerId)
			throws ClassNotFoundException {
		Query query = null;
		if (criteria != null) {
			query = new Query(new Criteria("ownerId").is(ownerId).andOperator(criteria));
		} else {
			query = new Query(new Criteria("ownerId").is(ownerId));
		}
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
		Query query = new Query(new Criteria("ownerId").is(school.getOwnerId()).and("id").is(school.getId()));
		School schoolDB = mongoTemplate.findOne(query, School.class);
		if(schoolDB == null) {
			throw new EntityNotFoundException(String.format("School with id %s not found", school.getId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		update.set("name", school.getName());
		update.set("address", school.getAddress());
		mongoTemplate.updateFirst(query, update, School.class);
	}

	public void removeSchool(String ownerId, String objectId) throws EntityNotFoundException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("id").is(objectId));
		School schoolDB = mongoTemplate.findOne(query, School.class);
		if(schoolDB == null) {
			throw new EntityNotFoundException(String.format("School with id %s not found", objectId));
		}
		mongoTemplate.findAndRemove(query, School.class);
	}

	public void addPedibus(Pedibus pedibus) {
		// TODO Auto-generated method stub
		
	}

	public void updatePedibus(Pedibus pedibus) {
		// TODO Auto-generated method stub
		
	}

	public void removePedibus(String ownerId, String objectId) {
		// TODO Auto-generated method stub
		
	}

	public void addRoute(Route route) {
		// TODO Auto-generated method stub
		
	}

	public void updateRoute(Route route) {
		// TODO Auto-generated method stub
		
	}

	public void removeRoute(String ownerId, String objectId) {
		// TODO Auto-generated method stub
		
	}

	public void addStop(Stop stop) {
		// TODO Auto-generated method stub
		
	}

	public void updateStop(Stop stop) {
		// TODO Auto-generated method stub
		
	}

	public void removeStop(String ownerId, String objectId) {
		// TODO Auto-generated method stub
		
	}

	public void addChild(Child child) {
		// TODO Auto-generated method stub
		
	}

	public void updateChild(Child child) {
		// TODO Auto-generated method stub
		
	}

	public void removeChild(String ownerId, String objectId) {
		// TODO Auto-generated method stub
		
	}

	public void addAnchor(Anchor anchor) {
		// TODO Auto-generated method stub
		
	}

	public void updateAnchor(Anchor anchor) {
		// TODO Auto-generated method stub
		
	}

	public void removeAnchor(String ownerId, String objectId) {
		// TODO Auto-generated method stub
		
	}

	public void addVolunteer(Volunteer volunteer) {
		// TODO Auto-generated method stub
		
	}

	public void updateVolunteer(Volunteer volunteer) {
		// TODO Auto-generated method stub
		
	}

	public void removeVolunteer(String ownerId, String objectId) {
		// TODO Auto-generated method stub
		
	}	
}
