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

	@RequestMapping(value = "/role/{ownerId}/owner", method = RequestMethod.POST)
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
		
		AuthorizationDTO auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		AuthorizationDTO authorizationDTO = authorizationManager.insertAuthorization(auth);
		result.add(authorizationDTO);
		List<String> authIds = new ArrayList<String>();
		authIds.add(authorizationDTO.getId());
		storage.addUserRole(email, Const.ROLE_OWNER, getAuthKey(ownerId, Const.ROLE_OWNER), authIds);
		return result;
	}
	
	@RequestMapping(value = "/role/{ownerId}/volunteer", method = RequestMethod.POST)
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
		List<String> authIds = new ArrayList<String>();
		AuthorizationDTO authorizationDTO;
		
		actions.add(Const.AUTH_ACTION_READ);
		String resourceName = "pedibus";
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Institute);
		AuthorizationDTO auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_School);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Child);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Image);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Volunteer);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);

		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Stop);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);

		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Route);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);

		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Attendance);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		attributes.clear();
		actions.add(Const.AUTH_ACTION_ADD);
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_WsnEvent);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_EventLogFile);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		
		storage.addUserRole(email, Const.ROLE_VOLUNTEER, getAuthKey(ownerId, Const.ROLE_VOLUNTEER), authIds);
		return result;
	}
	
	@RequestMapping(value = "/role/{ownerId}/teacher", method = RequestMethod.POST)
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
		List<String> authIds = new ArrayList<String>();
		AuthorizationDTO authorizationDTO;
		
		actions.add(Const.AUTH_ACTION_READ);
		String resourceName = "pedibus";
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Institute);
		AuthorizationDTO auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_School);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Child);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Image);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Volunteer);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);

		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Stop);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);

		attributes.clear();
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_Route);
		attributes.put("pedibus-instituteId", instituteId);
		attributes.put("pedibus-schoolId", schoolId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);

		attributes.clear();
		actions.add(Const.AUTH_ACTION_ADD);
		actions.add(Const.AUTH_ACTION_UPDATE);
		actions.add(Const.AUTH_ACTION_DELETE);
		attributes.put("pedibus-ownerId", ownerId);
		attributes.put("pedibus-resource", Const.AUTH_RES_PedibusGame);
		attributes.put("pedibus-gameId", gameId);
		auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		authorizationDTO = authorizationManager.insertAuthorization(auth);
		authIds.add(authorizationDTO.getId());
		result.add(authorizationDTO);
		
		storage.addUserRole(email, Const.ROLE_TEACHER, getAuthKey(ownerId, Const.ROLE_TEACHER), authIds);
		return result;
	}
	
	@RequestMapping(value = "/role/{ownerId}/parent", method = RequestMethod.POST)
	public @ResponseBody List<AuthorizationDTO> addParent(
			@PathVariable String ownerId,
			@RequestParam String email,
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
		attributes.put("pedibus-resource", Const.AUTH_RES_PedibusGame);
		attributes.put("pedibus-gameId", gameId);
		AuthorizationDTO auth = authorizationManager.getAuthorizationDTO(email, actions, 
				resourceName, attributes);
		AuthorizationDTO authorizationDTO = authorizationManager.insertAuthorization(auth);
		result.add(authorizationDTO);
		List<String> authIds = new ArrayList<String>();
		authIds.add(authorizationDTO.getId());
		storage.addUserRole(email, Const.ROLE_PARENT, getAuthKey(ownerId, Const.ROLE_PARENT), authIds);
		return result;
	}
	
	
	@RequestMapping(value = "/role/{ownerId}/{role}", method = RequestMethod.DELETE)
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
		String authKey = getAuthKey(ownerId, role);
		List<String> list = user.getAuthorizations().get(authKey);
		if(list != null) {
			for(String authId : list) {
				authorizationManager.deleteAuthorization(authId);
			}
		}
		storage.removeUserRole(email, role, authKey);
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
	
	private String getAuthKey(String ownerId, String role) {
		return ownerId + "-" + role;
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
