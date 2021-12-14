package it.smartcommunitylab.climb.domain.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.smartcommunitylab.climb.contextstore.model.Authorization;
import it.smartcommunitylab.climb.contextstore.model.Institute;
import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.manager.RoleManager;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Controller
public class RoleController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(RoleController.class);
	
	@Autowired
	private RepositoryManager storage;
	@Autowired
	private RoleManager roleManager;

	@RequestMapping(value = "/api/role/{ownerId}/owner", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addOwner(
			@PathVariable String ownerId,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request) && 
				!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<Authorization> auths = roleManager.addOwner(ownerId, email);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addOwner: %s - %s", ownerId, email));
		}
		return auths;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/school", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addSchoolOwner(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		Institute institute = storage.getInstitute(ownerId, instituteId);
		School school = storage.getSchool(ownerId, instituteId, schoolId);
		if((institute == null) || (school == null)) {
			throw new EntityNotFoundException("institute or school not found");
		}
		List<Authorization> auths = roleManager.addSchoolOwner(ownerId, email, institute, school);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addSchoolOwner: %s - %s - %s - %s", ownerId, email, 
					instituteId, schoolId));
		}
		return auths;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/volunteer", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addVolunteer(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		Institute institute = storage.getInstitute(ownerId, instituteId);
		School school = storage.getSchool(ownerId, instituteId, schoolId);
		if((institute == null) || (school == null)) {
			throw new EntityNotFoundException("institute or school not found");
		}
		List<Authorization> auths = roleManager.addVolunteer(ownerId, email, institute, school, true);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addVolunteer: %s - %s - %s - %s", ownerId, email, 
					instituteId, schoolId));
		}
		return auths;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/editor", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addGameEditor(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<Authorization> auths = roleManager.addGameEditor(ownerId, email, instituteId, schoolId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addGameEditor: %s - %s - %s - %s", ownerId, email, 
					instituteId, schoolId));
		}
		return auths;
	}

	@RequestMapping(value = "/api/role/{ownerId}/teacher", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addTeacher(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			@RequestParam String pedibusGameId,
			HttpServletRequest request) throws Exception {
		User callerUser = getUserByEmail(request);
		boolean ownerRole = validateRole(Const.ROLE_OWNER, ownerId, callerUser);
		boolean editorRole = validateRole(Const.ROLE_GAME_EDITOR, ownerId, callerUser);
		if(!ownerRole && !editorRole) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		Institute institute = storage.getInstitute(ownerId, instituteId);
		School school = storage.getSchool(ownerId, instituteId, schoolId);
		PedibusGame pedibusGame = storage.getPedibusGame(ownerId, pedibusGameId);
		if((institute == null) || (school == null) || (pedibusGame == null)) {
			throw new EntityNotFoundException("institute or school or game not found");
		}
		List<Authorization> auths = roleManager.addTeacher(ownerId, email, institute, school, pedibusGame);		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addTeacher: %s - %s - %s - %s - %s", ownerId, email, 
					instituteId, schoolId, pedibusGameId));
		}
		return auths;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/parent", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addParent(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			@RequestParam String pedibusGameId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<Authorization> auths = new ArrayList<Authorization>();
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.setRole(Const.ROLE_PARENT);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(instituteId);
		auth.setSchoolId(schoolId);
		auth.setRouteId("*");
		auth.setGameId(pedibusGameId);
		auth.getResources().add(Const.AUTH_RES_PedibusGame);
		auths.add(auth);

		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_PARENT, pedibusGameId), auths);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addParent: %s - %s - %s", ownerId, email, pedibusGameId));
		}
		return auths;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/auth/{authKey}", method = RequestMethod.DELETE)
	public @ResponseBody void removeAuthKey(
			@PathVariable String ownerId,
			@PathVariable String authKey,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		User callerUser = getUserByEmail(request);
		boolean ownerRole = validateRole(Const.ROLE_OWNER, ownerId, callerUser);
		boolean editorRole = validateRole(Const.ROLE_GAME_EDITOR, ownerId, callerUser);
		boolean adminRole = validateRole(Const.ROLE_ADMIN, callerUser);
		if(!ownerRole &&  !editorRole && !adminRole) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User user = storage.getUserByEmail(email);
		if(user == null) {
			throw new EntityNotFoundException(String.format("user %s not found", email));
		}
		List<String> roles = Utils.getUserRoles(user);
		if(roles.contains(Const.ROLE_ADMIN)) {
			throw new UnauthorizedException("Unauthorized Exception: unable to delete admin role");
		}
		String ownerIdKey = Utils.getOwnerIdFromAuthKey(authKey);
		if(!ownerId.equals(ownerIdKey)) {
			throw new UnauthorizedException("Unauthorized Exception: domain not valid");
		}
		String roleKey = Utils.getRoleFromAuthKey(authKey);
		if(!ownerRole && !adminRole) {
			if(!Const.ROLE_TEACHER.equals(roleKey)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}
		}
		storage.removeUserAuthKey(email, authKey);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeAuthKey: %s - %s - %s", ownerId, email, authKey));
		}
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/users", method = RequestMethod.GET)
	public @ResponseBody List<User> getUsersByRole(
			@PathVariable String ownerId,
			@RequestParam(required=false) String role,
			HttpServletRequest request) throws Exception {
		User callerUser = getUserByEmail(request);
		boolean ownerRole = validateRole(Const.ROLE_OWNER, ownerId, callerUser);
		boolean editorRole = validateRole(Const.ROLE_GAME_EDITOR, ownerId, callerUser);
		if(!ownerRole && !editorRole) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<User> result = storage.getUsersByOwnerIdAndRole(callerUser, ownerId, role, ownerRole);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getUsersByRole: %s - %s", ownerId, role));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/user", method = RequestMethod.GET)
	public @ResponseBody User getUserByEmail(
			@PathVariable String ownerId,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		User callerUser = getUserByEmail(request);
		boolean ownerRole = validateRole(Const.ROLE_OWNER, ownerId, callerUser);
		boolean editorRole = validateRole(Const.ROLE_GAME_EDITOR, ownerId, callerUser);
		if(!ownerRole && !editorRole) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User user = storage.getUserByEmail(email);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getUserByEmail: %s - %s", ownerId, email));
		}
		if(user != null) {
			if(Utils.checkOwnerId(ownerId, user)) {
				return user;
			} else {
				throw new UnauthorizedException("Unauthorized Exception: ownerId not allowed");
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/user", method = RequestMethod.POST)
	public @ResponseBody User saveUser(
			@PathVariable String ownerId,
			@RequestBody User user,
			HttpServletRequest request) throws Exception {
		User callerUser = getUserByEmail(request);
		boolean ownerRole = validateRole(Const.ROLE_OWNER, ownerId, callerUser);
		boolean editorRole = validateRole(Const.ROLE_GAME_EDITOR, ownerId, callerUser);
		if(!ownerRole && !editorRole) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User userDb = null;
		if(Utils.isNotEmpty(user.getEmail())) {
			userDb = storage.getUserByEmail(user.getEmail());
		} else {
			throw new EntityNotFoundException("email must be present");
		}
		if(userDb == null) {
			User newUser = new User();
			newUser.setObjectId(Utils.getUUID());
			newUser.setName(user.getName());
			newUser.setSurname(user.getSurname());
			newUser.setEmail(user.getEmail());
			newUser.setCf(user.getCf());
    	storage.addUser(newUser);
    	
  		List<Authorization> auths = new ArrayList<Authorization>();
  		Authorization auth = new Authorization();
  		auth.setRole(Const.ROLE_USER);
  		auth.setOwnerId(ownerId);
  		auths.add(auth);
  		newUser = storage.addUserRole(user.getEmail(), 
  				Utils.getAuthKey(ownerId, Const.ROLE_USER), auths);
  		if(logger.isInfoEnabled()) {
  			logger.info(String.format("saveUser[new]: %s - %s", ownerId, user.getEmail()));
  		}
    	return newUser;
    } else {
    	user.setObjectId(userDb.getObjectId());
    	storage.updateUser(user);
    	if(!Utils.checkOwnerIdAndRole(ownerId, Const.ROLE_USER, userDb)) {
    		List<Authorization> auths = new ArrayList<Authorization>();
    		Authorization auth = new Authorization();
    		auth.setRole(Const.ROLE_USER);
    		auth.setOwnerId(ownerId);
    		auths.add(auth);
    		user = storage.addUserRole(user.getEmail(), 
    				Utils.getAuthKey(ownerId, Const.ROLE_USER), auths);
    	}
  		if(logger.isInfoEnabled()) {
  			logger.info(String.format("saveUser[update]: %s - %s", ownerId, user.getEmail()));
  		}
    	return user;
    }
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/user", method = RequestMethod.DELETE)
	public @ResponseBody void removeUser(
			@PathVariable String ownerId,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User user = storage.getUserByEmail(email);
		if(user == null) {
			throw new EntityNotFoundException(String.format("user %s not found", email));
		}
		List<String> userRoles = Utils.getUserRoles(user);
		List<String> userOwnerIds = Utils.getUserOwnerIds(user);
  	if(!userOwnerIds.contains(ownerId)) {
  		throw new UnauthorizedException("Unauthorized Exception: dataset not allowed");
  	}
  	if(userRoles.contains(Const.ROLE_ADMIN)) {
  		throw new UnauthorizedException("Unauthorized Exception: unable to delete admin user");
  	}
  	userRoles.remove(Const.ROLE_USER);
  	if((userOwnerIds.size() > 1) || (userRoles.size() > 1)) {
  		throw new UnauthorizedException("Unauthorized Exception: user has other roles and dataset");
  	}
		storage.removeUser(user.getObjectId());
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeUser: %s - %s", ownerId, email));
		}
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/domain", method = RequestMethod.DELETE)
	public @ResponseBody void removeUserFromDomain(
			@PathVariable String ownerId,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User user = storage.getUserByEmail(email);
		if(user == null) {
			throw new EntityNotFoundException(String.format("user %s not found", email));
		}
		for(String authKey : user.getRoles().keySet()) {
			if(authKey.startsWith(ownerId)) {
				storage.removeUserAuthKey(email, authKey);
			}
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeUserFromDomain: %s - %s", ownerId, email));
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
