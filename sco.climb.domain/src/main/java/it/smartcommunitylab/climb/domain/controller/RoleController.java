package it.smartcommunitylab.climb.domain.controller;

import it.smartcommunitylab.aac.authorization.beans.AccountAttributeDTO;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationDTO;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.security.AuthorizationManager;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

@Controller
public class RoleController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(RoleController.class);
	
	@Autowired
	AuthorizationManager authorizationManager;
	
	@Autowired
	private RepositoryManager storage;

	@RequestMapping(value = "/api/role/{ownerId}/owner", method = RequestMethod.POST)
	public @ResponseBody List<AuthorizationDTO> addOwner(
			@PathVariable String ownerId,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<AuthorizationDTO> result = new ArrayList<AuthorizationDTO>();
		List<String> actions = new ArrayList<String>();
		actions.add(Const.AUTH_ACTION_READ);
		actions.add(Const.AUTH_ACTION_ADD);
		actions.add(Const.AUTH_ACTION_UPDATE);
		actions.add(Const.AUTH_ACTION_DELETE);
		
		String resourceName = "pedibus";
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", "*");
		attributes.put("pedibus-schoolId", "*");
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", "*");
		
		AuthorizationDTO auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		AuthorizationDTO authorizationDTO = authorizationManager.insertAuthorization(auth);
		result.add(authorizationDTO);
		List<AuthorizationDTO> auths = new ArrayList<AuthorizationDTO>();
		auths.add(authorizationDTO);
		
		storage.addUserRole(email, Const.ROLE_OWNER, Utils.getAuthKey(ownerId, Const.ROLE_OWNER), auths);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addOwner: %s - %s", ownerId, email));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/volunteer", method = RequestMethod.POST)
	public @ResponseBody List<AuthorizationDTO> addVolunteer(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<AuthorizationDTO> result = new ArrayList<AuthorizationDTO>();
		List<String> actions = new ArrayList<String>();
		Map<String, String> attributes = new HashMap<String, String>();
		List<AuthorizationDTO> auths = new ArrayList<AuthorizationDTO>();
		AuthorizationDTO authorizationDTO;
		
		actions.add(Const.AUTH_ACTION_READ);
		String resourceName = "pedibus";
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", "*");
		attributes.put("pedibus-schoolId", "*");
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Institute);
		AuthorizationDTO auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", "*");
		attributes.put("pedibus-schoolId", "*");
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_School);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Child);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Image);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Volunteer);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);

		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Stop);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);

		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Route);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);

		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Attendance);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		actions.add(Const.AUTH_ACTION_ADD);
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_WsnEvent);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_EventLogFile);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		storage.addUserRole(email, Const.ROLE_VOLUNTEER, 
				Utils.getAuthKey(ownerId, Const.ROLE_VOLUNTEER, instituteId, schoolId), auths);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addVolunteer: %s - %s - %s - %s", ownerId, email, 
					instituteId, schoolId));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/teacher", method = RequestMethod.POST)
	public @ResponseBody List<AuthorizationDTO> addTeacher(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			@RequestParam String gameId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<AuthorizationDTO> result = new ArrayList<AuthorizationDTO>();
		List<String> actions = new ArrayList<String>();
		Map<String, String> attributes = new HashMap<String, String>();
		List<AuthorizationDTO> auths = new ArrayList<AuthorizationDTO>();
		AuthorizationDTO authorizationDTO;
		
		actions.add(Const.AUTH_ACTION_READ);
		String resourceName = "pedibus";
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", "*");
		attributes.put("pedibus-schoolId", "*");
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Institute);
		AuthorizationDTO auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", "*");
		attributes.put("pedibus-schoolId", "*");
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_School);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Child);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Image);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Volunteer);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);

		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Stop);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);

		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_Route);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);

		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", "*");
		attributes.put("pedibus-resource", Const.AUTH_RES_PedibusGame);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		actions.add(Const.AUTH_ACTION_UPDATE);
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", gameId);
		attributes.put("pedibus-resource", Const.AUTH_RES_PedibusGame_Calendar);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", gameId);
		attributes.put("pedibus-resource", Const.AUTH_RES_PedibusGame_Excursion);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", gameId);
		attributes.put("pedibus-resource", Const.AUTH_RES_PedibusGame_Link);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		auths.add(authorizationDTO);
		result.add(authorizationDTO);
		
		storage.addUserRole(email, Const.ROLE_TEACHER, 
				Utils.getAuthKey(ownerId, Const.ROLE_TEACHER, instituteId, schoolId, gameId), auths);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addTeacher: %s - %s - %s - %s - %s", ownerId, email, 
					instituteId, schoolId, gameId));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/parent", method = RequestMethod.POST)
	public @ResponseBody List<AuthorizationDTO> addParent(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			@RequestParam String gameId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<AuthorizationDTO> result = new ArrayList<AuthorizationDTO>();
		String resourceName = "pedibus";
		List<String> actions = new ArrayList<String>();
		Map<String, String> attributes = new HashMap<String, String>();
		actions.add(Const.AUTH_ACTION_READ);
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		attributes.put("pedibus-routeId", "*");
		attributes.put("pedibus-gameId", gameId);
		attributes.put("pedibus-resource", Const.AUTH_RES_PedibusGame);
		AuthorizationDTO auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		AuthorizationDTO authorizationDTO = authorizationManager.insertAuthorization(auth);
		result.add(authorizationDTO);
		
		storage.addUserRole(email, Const.ROLE_PARENT, 
				Utils.getAuthKey(ownerId, Const.ROLE_PARENT, gameId), result);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addParent: %s - %s - %s", ownerId, email, gameId));
		}
		return result;
	}
	
	
	@RequestMapping(value = "/api/role/{ownerId}/{role}", method = RequestMethod.DELETE)
	public @ResponseBody void removeRole(
			@PathVariable String ownerId,
			@PathVariable String role,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User user = storage.getUserByEmail(email);
		if(user == null) {
			throw new EntityNotFoundException(String.format("user %s not found", email));
		}
  	if(!user.getOwnerIds().contains(ownerId)) {
  		throw new UnauthorizedException("Unauthorized Exception: dataset not allowed");
  	}
  	if((user.getOwnerIds().size() > 1) || (user.getRoles().size() > 1)) {
  		throw new UnauthorizedException("Unauthorized Exception: user has other roles and dataset");
  	}
		String authKey = Utils.getAuthKey(ownerId, role);
		for(String key : user.getAuthorizations().keySet()) {
			if(key.startsWith(authKey)) {
				List<Object> list = user.getAuthorizations().get(authKey);
				if(list != null) {
					for(Object obj : list) {
						if(obj instanceof AuthorizationDTO) {
							AuthorizationDTO auth = (AuthorizationDTO) obj;
							String authId = auth.getId();
							authorizationManager.deleteAuthorization(authId);
						}
					}
				}				
			}
		}
		storage.removeUserRole(email, role, authKey);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeRole: %s - %s - %s", ownerId, role, email));
		}
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/auth/{authKey}", method = RequestMethod.DELETE)
	public @ResponseBody void removeAuthKey(
			@PathVariable String ownerId,
			@PathVariable String authKey,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User user = storage.getUserByEmail(email);
		if(user == null) {
			throw new EntityNotFoundException(String.format("user %s not found", email));
		}
  	if(!user.getOwnerIds().contains(ownerId)) {
  		throw new UnauthorizedException("Unauthorized Exception: dataset not allowed");
  	}
		List<Object> list = user.getAuthorizations().get(authKey);
		if(list != null) {
			for(Object obj : list) {
				if(obj instanceof AuthorizationDTO) {
					AuthorizationDTO auth = (AuthorizationDTO) obj;
					String authId = auth.getId();
					authorizationManager.deleteAuthorization(authId);
				}
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
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<User> result = storage.getUsersByRole(ownerId, role);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getUsersByRole: %s - %s", ownerId, role));
		}
		return result;
	}
	
	/*@RequestMapping(value = "/api/role/{ownerId}/user", method = RequestMethod.GET)
	public @ResponseBody List<User> getUsersByDataset(
			@PathVariable String ownerId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<User> result = storage.getUsersByOwnerId(ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getUsersByDataset: %s", ownerId));
		}
		return result;
	}*/
	
	@RequestMapping(value = "/api/role/{ownerId}/user", method = RequestMethod.GET)
	public @ResponseBody User getUserByEmail(
			@PathVariable String ownerId,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User result = storage.getUserByEmail(email);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getUserByEmail: %s - %s", ownerId, email));
		}
		if(result != null) {
			if(result.getOwnerIds().contains(ownerId)) {
				return result;
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
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
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
			newUser.setOwnerIds(Arrays.asList(new String[] {ownerId}));
    	storage.addUser(newUser);
  		if(logger.isInfoEnabled()) {
  			logger.info(String.format("saveUser[new]: %s - %s", ownerId, user.getEmail()));
  		}
    	return newUser;
    } else {
    	if(!userDb.getOwnerIds().contains(ownerId)) {
    		throw new UnauthorizedException("Unauthorized Exception: ownerId not allowed");
    	}
    	user.setObjectId(userDb.getObjectId());
    	storage.updateUser(user);
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
  	if(!user.getOwnerIds().contains(ownerId)) {
  		throw new UnauthorizedException("Unauthorized Exception: dataset not allowed");
  	}
  	if(user.getRoles().contains(Const.ROLE_ADMIN)) {
  		throw new UnauthorizedException("Unauthorized Exception: unable to delete admin user");
  	}
  	if((user.getOwnerIds().size() > 1) || (user.getRoles().size() > 1)) {
  		throw new UnauthorizedException("Unauthorized Exception: user has other roles and dataset");
  	}
  	for(String key : user.getAuthorizations().keySet()) {
  		List<Object> list = user.getAuthorizations().get(key);
  		if(list != null) {
  			for(Object obj : list) {
  				if(obj instanceof AuthorizationDTO) {
  					AuthorizationDTO auth = (AuthorizationDTO) obj;
  					String authId = auth.getId();
  					authorizationManager.deleteAuthorization(authId);
  				}
  			}
  		}  		
  	}
		storage.removeUser(ownerId, user.getObjectId());
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeUser: %s - %s", ownerId, email));
		}
	}

	private boolean validateRole(String role, String ownerId, HttpServletRequest request) throws Exception {
		boolean result = false;
		AccountAttributeDTO accountByEmail = getAccountByEmail(getAccoutProfile(request));
		if(accountByEmail == null) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		String email = accountByEmail.getAttributeValue();
		User user = storage.getUserByEmail(email);
		if(user != null) {
			result = user.getRoles().contains(role) && user.getOwnerIds().contains(ownerId);
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
