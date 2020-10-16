package it.smartcommunitylab.climb.domain.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import it.smartcommunitylab.climb.contextstore.model.Authorization;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Institute;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.converter.ExcelConverter;
import it.smartcommunitylab.climb.domain.converter.ExcelError;
import it.smartcommunitylab.climb.domain.converter.JsonConverter;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.manager.RoleManager;
import it.smartcommunitylab.climb.domain.model.PedibusPlayer;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Controller
public class AdminController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private RoleManager roleManager;
	
	@Autowired
	private ExcelConverter excelConverter;
	
	@Autowired
	private JsonConverter jsonConverter;

	@RequestMapping(value = "/admin/user/csv", method = RequestMethod.POST)
	public @ResponseBody void uploadOwnerUserCsv(
			@RequestParam("file") MultipartFile file,
			@RequestParam(name="update", required=false) Boolean update,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		if(update == null) {
			update = Boolean.FALSE;
		}
		if (!file.isEmpty()) {
			Path tempFile = Files.createTempFile("climb-user", ".csv");
			tempFile.toFile().deleteOnExit();
			File outputFileCSV = tempFile.toFile();
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(outputFileCSV));
			FileCopyUtils.copy(file.getInputStream(), stream);
			stream.close();
			
			Reader in = new FileReader(outputFileCSV);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
			for (CSVRecord record : records) {
		    String ownerId = record.get("ownerId");
		    String subject = record.get("subject");
		    String name = record.get("name");
		    String surname = record.get("surname");
		    String email = record.get("email");
		    String cf = record.get("cf");
	    	
		    User user = new User();
	    	user.setCf(cf);
	    	user.setEmail(email);
	    	user.setName(name);
	    	user.setSurname(surname);
	    	user.setSubject(subject);
	    	
		    User userDb = storage.getUserByEmail(email);
		    if(userDb == null) {
		    	user.setObjectId(Utils.getUUID());
		    	storage.addUser(user);
		    } else if(update) {
		    	user.setObjectId(userDb.getObjectId());
		    	storage.updateUser(user);
		    }
		    
	  		List<Authorization> auths = new ArrayList<Authorization>();
	  		Authorization auth = new Authorization();
	  		auth.getActions().add(Const.AUTH_ACTION_READ);
	  		auth.getActions().add(Const.AUTH_ACTION_ADD);
	  		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
	  		auth.getActions().add(Const.AUTH_ACTION_DELETE);
	  		auth.setOwnerId(ownerId);
	  		auth.setInstituteId("*");
	  		auth.setSchoolId("*");
	  		auth.setRouteId("*");
	  		auth.setGameId("*");
	  		auth.getResources().add("*");
	  		auths.add(auth);
	  		
	  		storage.addUserRole(email, 
	  				Utils.getAuthKey(ownerId, Const.ROLE_OWNER), auths);
			}
			outputFileCSV.delete();
		}
	}

	@RequestMapping(value = "/admin/volunteer/csv/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.POST)
	public @ResponseBody List<ExcelError> uploadVolunteerUserCsv(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			@RequestParam("file") MultipartFile file,
			@RequestParam(name="update", required=false) Boolean update,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		Institute institute = storage.getInstitute(ownerId, instituteId);
		School school = storage.getSchool(ownerId, instituteId, schoolId);
		if((institute == null) || (school == null)) {
			throw new EntityNotFoundException("institute or school not found");
		}		
		List<ExcelError> errors = new ArrayList<ExcelError>();
		if (!file.isEmpty()) {
			Path tempFile = Files.createTempFile("climb-volunteer", ".csv");
			tempFile.toFile().deleteOnExit();
			File outputFileCSV = tempFile.toFile();
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(outputFileCSV));
			FileCopyUtils.copy(file.getInputStream(), stream);
			stream.close();
			
			Reader in = new FileReader(outputFileCSV);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
			List<User> users = new ArrayList<User>();
			int count = 1;
			for (CSVRecord record : records) {
				//extract fields
				try {
			    String name = record.get("name");
			    String surname = record.get("surname");
			    String email = record.get("email");
			    String cf = record.get("cf");
			    
			    //check data
			    if(Utils.isEmpty(name)) {
						ExcelError error = new ExcelError("Volunteers", count, "name mancante");
						errors.add(error);
			    }
			    if(Utils.isEmpty(surname)) {
						ExcelError error = new ExcelError("Volunteers", count, "surname mancante");
						errors.add(error);
			    }
			    if(Utils.isEmpty(email)) {
						ExcelError error = new ExcelError("Volunteers", count, "email mancante");
						errors.add(error);
			    }
			    if(Utils.isEmpty(cf)) {
						ExcelError error = new ExcelError("Volunteers", count, "cf mancante");
						errors.add(error);
			    }
			    
			    User user = new User();
		    	user.setCf(cf);
		    	user.setEmail(email);
		    	user.setName(name);
		    	user.setSurname(surname);
		    	users.add(user);					
				} catch (Exception e) {
					ExcelError error = new ExcelError("Volunteers", count, "errore parsing:" + e.getMessage());
					errors.add(error);
				}
		    count++;
			}
			outputFileCSV.delete();
			
			if(errors.size() == 0) {
				for(User user : users) {
					try {
						User userDb = storage.getUserByEmail(user.getEmail());
				    //create user
				    if(userDb == null) {
				    	user.setObjectId(Utils.getUUID());
				    	storage.addUser(user);
				    } else {
				    	String authKey = Utils.getAuthKey(ownerId, Const.ROLE_VOLUNTEER, instituteId, schoolId);
				    	if(userDb.getRoles().containsKey(authKey)) {
				    		continue;
				    	}
				    }
				    //add auths
						roleManager.addVolunteer(ownerId, user.getEmail(), institute, school, true);
					} catch (Exception e) {
						ExcelError error = new ExcelError("Volunteers", count, 
								"errore assegbnazo e ruolo a " + user.getEmail());
						errors.add(error);					
					}					
				}
			}
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadVolunteerUserCsv[%s]: %s %s", ownerId, instituteId, schoolId));
		}
		return errors;
	}

	@RequestMapping(value = "/admin/import/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.POST)
	public @ResponseBody List<ExcelError> uploadData(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			@RequestParam(name="players", required=false) Boolean onlyPlayers,
			@RequestParam(name="pedibusGameId", required=false) String pedibusGameId,
			@RequestParam("file") MultipartFile file,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<ExcelError> errors = new ArrayList<ExcelError>();
		if(onlyPlayers) {
			Map<String, PedibusPlayer> playersMap = excelConverter.readPlayers(file.getInputStream(), 
					ownerId, pedibusGameId, errors);
			if(errors.size() == 0) {
				for(PedibusPlayer player : playersMap.values()) {
					storePlayer(player);
				}
				if(logger.isInfoEnabled()) {
					logger.info(String.format("uploadData[game]: %s %s %s", ownerId, instituteId, schoolId));
				}
			}
		} else {
			Map<String, Route> routesMap = excelConverter.readRoutes(file.getInputStream(), 
					ownerId, instituteId, schoolId, errors);
			Map<String, Stop> stopsMap = excelConverter.readStops(file.getInputStream(), 
					ownerId, instituteId, schoolId, routesMap, errors);
			Map<String, Child> childrenMap = excelConverter.readChildren(file.getInputStream(), 
					ownerId, instituteId, schoolId, stopsMap, errors);
			Map<String, Volunteer> volunteersMap = excelConverter.readVolunteers(file.getInputStream(), 
					ownerId, instituteId, schoolId, routesMap, errors);
			if(errors.size() == 0) {
				for(Route route : routesMap.values()) {
					storeRoute(route);
				}
				for(Stop stop : stopsMap.values()) {
					storeStop(stop);
				}
				for(Child child : childrenMap.values()) {
					storeChild(child);
				}
				for(Volunteer volunteer : volunteersMap.values()) {
					storeVolunteer(volunteer);
				}			
				if(logger.isInfoEnabled()) {
					logger.info(String.format("uploadData[pedibus]: %s %s %s", ownerId, instituteId, schoolId));
				}
			}
		}
		return errors;
	}
	
	@RequestMapping(value = "/admin/import/{ownerId}/{instituteId}/{schoolId}/json", method = RequestMethod.GET)
	public @ResponseBody void convertJsonData(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			@RequestParam String path,
			HttpServletRequest request) throws Exception{
		if(!validateRole(Const.ROLE_ADMIN, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		try {
			FileReader gameReader = new FileReader(path + "/pedibusGame.json");
			if(jsonConverter.storePedibusGame(ownerId, instituteId, schoolId, gameReader)) {
				FileReader itineraryReader = new FileReader(path + "/pedibusItinerary.json");
				if(jsonConverter.storePedibusItinerary(ownerId, instituteId, schoolId, itineraryReader)) {
					FileReader legReader = new FileReader(path + "/pedibusItineraryLeg.json");
					jsonConverter.storePedibusItineraryLeg(ownerId, instituteId, schoolId, legReader);
				}
			}
		} catch (Exception e) {
			logger.error("convertJsonData error:{}", e.getMessage());
		}
	}
	
	private void storePlayer(PedibusPlayer player) throws Exception {
		storage.savePedibusPlayer(player, true);
	}

	private void storeVolunteer(Volunteer volunteer) throws Exception {
		if(Utils.isEmpty(volunteer.getCf())) {
			if(logger.isInfoEnabled()) {
				logger.info("Volunteer cf not found");
			}
			return;			
		}
		Criteria criteria = Criteria.where("schoolId").is(volunteer.getSchoolId())
				.and("instituteId").is(volunteer.getInstituteId())
				.and("cf").is(volunteer.getCf());
		Volunteer volunteerDb = storage.findOneData(Volunteer.class, criteria, 
				volunteer.getOwnerId());
		if(volunteerDb == null) {
			storage.addVolunteer(volunteer);
		} else {
			volunteerDb.setName(volunteer.getName());
			volunteerDb.setPhone(volunteer.getPhone());
			volunteerDb.setPassword(volunteer.getPassword());
			storage.updateVolunteer(volunteerDb);
		}
	}

	private void storeStop(Stop stop) throws Exception {
		if(Utils.isEmpty(stop.getName())) {
			if(logger.isInfoEnabled()) {
				logger.info("Stop name not found");
			}
			return;			
		}
		Criteria criteria = Criteria.where("routeId").is(stop.getRouteId())
				.and("name").is(stop.getName());
		Stop stopDb = storage.findOneData(Stop.class, criteria, stop.getOwnerId());
		if(stopDb == null) {
			storage.addStop(stop);
		} else {
			stopDb.setDepartureTime(stop.getDepartureTime());
			stopDb.setStart(stop.isStart());
			stopDb.setDestination(stop.isDestination());
			stopDb.setSchool(stop.isSchool());
			stopDb.setGeocoding(stop.getGeocoding());
			stopDb.setDistance(stop.getDistance());
			stopDb.setPosition(stop.getPosition());
			stopDb.setPassengerList(stop.getPassengerList());
			storage.updateStop(stopDb);
		}
	}

	private void storeRoute(Route route) throws Exception {
		if(Utils.isEmpty(route.getName())) {
			if(logger.isInfoEnabled()) {
				logger.info("Route name not found");
			}
			return;
		}
		Criteria criteria = Criteria.where("schoolId").is(route.getSchoolId())
				.and("instituteId").is(route.getInstituteId())
				.and("name").is(route.getName());
		Route routeDb = storage.findOneData(Route.class, criteria, route.getOwnerId());
		if(routeDb == null) {
			storage.addRoute(route);
		} else {
			if(logger.isInfoEnabled()) {
				logger.info(String.format("merge Route:%s", route.getName()));
			}
			routeDb.setFrom(route.getFrom());
			routeDb.setTo(route.getTo());
			routeDb.setVolunteerList(route.getVolunteerList());
			storage.updateRoute(routeDb);
		}
	}

	private void storeChild(Child child) throws Exception {
		Criteria criteriaBase = Criteria.where("schoolId").is(child.getSchoolId())
				.and("instituteId").is(child.getInstituteId());
		if(Utils.isNotEmpty(child.getCf())) {
			Criteria criteriaCf = criteriaBase.and("cf").is(child.getCf());
			Child childDb = storage.findOneData(Child.class, criteriaCf, child.getOwnerId());
			if(childDb == null) {
				storage.addChild(child);
			} else {
				if(logger.isInfoEnabled()) {
					logger.info(String.format("merge Child:%s", child.getCf()));
				}
				childDb.setName(child.getName());
				childDb.setSurname(child.getSurname());
				childDb.setParentName(child.getParentName());
				childDb.setNickname(child.getNickname());
				childDb.setPhone(child.getPhone());
				childDb.setWsnId(child.getWsnId());
				if(Utils.isNotEmpty(child.getClassRoom())) {
					childDb.setClassRoom(child.getClassRoom());
				}
				storage.updateChild(childDb);
			}
		} else {
			if(logger.isInfoEnabled()) {
				logger.info(String.format("Child cf not found: %s %s", 
						child.getName(), child.getSurname()));
			}
		}
	}
		
	@ExceptionHandler({EntityNotFoundException.class})
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String,String> handleEntityNotFoundError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ResponseBody
	public Map<String,String> handleUnauthorizedError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleGenericError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}	
	
}
