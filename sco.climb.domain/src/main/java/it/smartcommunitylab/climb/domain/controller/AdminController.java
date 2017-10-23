package it.smartcommunitylab.climb.domain.controller;

import it.smartcommunitylab.aac.authorization.beans.AccountAttributeDTO;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationDTO;
import it.smartcommunitylab.aac.authorization.beans.RequestedAuthorizationDTO;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.converter.ExcelConverter;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AdminController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private RepositoryManager storage;

	@RequestMapping(value = "/admin/auth/schema/upload", method = RequestMethod.POST)
	public @ResponseBody void uploadAuthSchema(
			@RequestBody String json,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		authorizationManager.loadAuthSchema(json);
	}
	
	@RequestMapping(value = "/admin/auth", method = RequestMethod.POST)
	public @ResponseBody AuthorizationDTO addAuthorization(
			@RequestBody AuthorizationDTO auth,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		return authorizationManager.insertAuthorization(auth);
	}
	
	@RequestMapping(value = "/admin/auth/{authId}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteAuthorization(
			@PathVariable String authId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		authorizationManager.deleteAuthorization(authId);
	}
	
	@RequestMapping(value = "/admin/auth/validate", method = RequestMethod.POST)
	public @ResponseBody String validateAuth(
			@RequestBody RequestedAuthorizationDTO auth,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		return Boolean.toString(authorizationManager.validateAuthorization(auth));
	}
	
	@RequestMapping(value = "/admin/user/csv", method = RequestMethod.POST)
	public @ResponseBody void uploadUserCsv(
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
		    String ownerIds = record.get("ownerIds");
		    String roles = record.get("roles");
		    String subject = record.get("subject");
		    String name = record.get("name");
		    String surname = record.get("surname");
		    String email = record.get("email");
		    String cf = record.get("cf");
	    	
		    User user = new User();
	    	user.setCf(cf);
	    	user.setEmail(email);
	    	user.setName(name);
	    	user.setOwnerIds(Arrays.asList(ownerIds.split(",")));
	    	user.setRoles(Arrays.asList(roles.split(",")));
	    	user.setSubject(subject);
	    	user.setSurname(surname);
		    
		    User userDb = storage.getUserByCf(cf);
		    if(userDb == null) {
		    	user.setObjectId(Utils.getUUID());
		    	storage.addUser(user);
		    } else if(update) {
		    	user.setObjectId(userDb.getObjectId());
		    	storage.updateUser(user);
		    }
			}
		}
	}
	
	@RequestMapping(value = "/admin/import/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.POST)
	public @ResponseBody void uploadData(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			@RequestParam("file") MultipartFile file,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		Map<String, Route> routesMap = ExcelConverter.readRoutes(file.getInputStream(), 
				ownerId, instituteId, schoolId);
		Map<String, Stop> stopsMap = ExcelConverter.readStops(file.getInputStream(), 
				ownerId, instituteId, schoolId, routesMap);
		Map<String, Child> childrenMap = ExcelConverter.readChildren(file.getInputStream(), 
				ownerId, instituteId, schoolId, stopsMap);
		Map<String, Volunteer> volunteersMap = ExcelConverter.readVolunteers(file.getInputStream(), 
				ownerId, instituteId, schoolId);
		for(Route route : routesMap.values()) {
			storage.addRoute(route);
		}
		for(Stop stop : stopsMap.values()) {
			storage.addStop(stop);
		}
		for(Child child : childrenMap.values()) {
			storage.addChild(child);
		}
		for(Volunteer volunteer : volunteersMap.values()) {
			storage.addVolunteer(volunteer);
		}
	}
	
	@RequestMapping(value = "/admin/import/{ownerId}/{instituteId}/{schoolId}/child", method = RequestMethod.POST)
	public @ResponseBody void addChildren(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			@RequestParam("file") MultipartFile file,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addChildren: %s %s %s", ownerId, instituteId, schoolId));
		}
		Map<String, Child> childrenMap = ExcelConverter.readSimpleChildren(file.getInputStream(), 
				ownerId, instituteId, schoolId);
		for(Child child : childrenMap.values()) {
			Criteria criteria = Criteria.where("schoolId").is(schoolId)
					.and("instituteId").is(instituteId)
					.and("name").is(child.getName())
					.and("surname").is(child.getSurname());
			Child childDb = storage.findOneData(Child.class, criteria, ownerId);
			if(childDb != null) {
				if(logger.isInfoEnabled()) {
					logger.info(String.format("Child already exists: %s %s", child.getName(), child.getSurname()));
				}
				continue;
			}
			storage.addChild(child);
		}
	}
	
	private boolean validateRole(String role, HttpServletRequest request) throws Exception {
		boolean result = false;
		AccountAttributeDTO accountByEmail = getAccountByEmail(getAccoutProfile(request));
		if(accountByEmail == null) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		String email = accountByEmail.getAttributeValue();
		User user = storage.getUserByEmail(email);
		if(user != null) {
			result = user.getRoles().contains(role);
		}
		return result;
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
