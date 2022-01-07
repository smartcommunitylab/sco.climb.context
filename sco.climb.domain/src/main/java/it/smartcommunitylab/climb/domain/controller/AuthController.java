package it.smartcommunitylab.climb.domain.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Controller;

import it.smartcommunitylab.climb.contextstore.model.Authorization;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.security.AccountProfile;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Controller
public class AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	private RepositoryManager storage;
	
	protected AccountProfile getAccoutProfile(HttpServletRequest request) throws Exception {
		BearerTokenAuthentication authentication = (BearerTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
		OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
		AccountProfile result = new AccountProfile();
		result.setCf((String) principal.getAttributes().get("codicefiscale"));
		result.setEmail((String) principal.getAttributes().get("email"));
		return result;
	}
	
	public User getUserByEmail(HttpServletRequest request) throws Exception {
		AccountProfile account = getAccoutProfile(request);
		User user = null;
		if(Utils.isNotEmpty(account.getEmail())) {
			user = storage.getUserByEmail(account.getEmail());
		} else if(Utils.isNotEmpty(account.getCf())) {
			user = storage.getUserByCf(account.getCf());
		} else {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		if(user == null) {
			throw new UnauthorizedException("Unauthorized Exception: user email or cf not found");
		}
		return user;
	}
	
	public boolean validateAuthorization(String ownerId, String instituteId, 
			String schoolId, String routeId, String gameId, String resource, String action,	
			HttpServletRequest request) throws Exception {
		User user = getUserByEmail(request);
		if(!validateAuthorization(ownerId, instituteId, schoolId, routeId, gameId,
				resource, action, user)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		return true;
	}
	
	public boolean validateAuthorization(String ownerId, String instituteId, 
			String schoolId, String routeId, String gameId, String resource, String action,	
			User user) throws Exception {
		return validateAuthorization(ownerId, instituteId, schoolId, routeId, gameId,
				resource, action, user, true);
	}

	private boolean validateAuthorization(String ownerId, String instituteId, String schoolId, String routeId, 
			String gameId, String resource, String action, User user, boolean nullable) {
		if(user != null) {
			for(String authKey : user.getRoles().keySet()) {
				List<Authorization> authList = user.getRoles().get(authKey);
				for(Authorization auth : authList) {
					if(auth.getOwnerId().equals(ownerId)) {
						if(auth.getResources().contains("*") || auth.getResources().contains(resource)) {
							if(auth.getActions().contains(action)) {
								if(!Utils.isEmpty(instituteId) || !nullable) {
									if(Utils.isEmpty(instituteId) && !nullable) {
										instituteId = "*";
									}
									if(!auth.getInstituteId().equals(instituteId) && !auth.getInstituteId().equals("*")) {
										continue;
									}
								}
								if(!Utils.isEmpty(schoolId) || !nullable) {
									if(Utils.isEmpty(schoolId) && !nullable) {
										schoolId = "*";
									}
									if(!auth.getSchoolId().equals(schoolId) && !auth.getSchoolId().equals("*")) {
										continue;
									}
								}
								if(!Utils.isEmpty(routeId) || !nullable) {
									if(Utils.isEmpty(routeId) && !nullable) {
										routeId = "*";
									}
									if(!auth.getRouteId().equals(routeId) && !auth.getRouteId().equals("*")) {
										continue;
									}
								}
								if(!Utils.isEmpty(gameId) || !nullable) {
									if(Utils.isEmpty(gameId) && !nullable) {
										gameId = "*";
									}
									if(!auth.getGameId().equals(gameId) && !auth.getGameId().equals("*")) {
										continue;
									}
								}
								return true;
							}
						}						
					}
				}
			}
		}
		return false;
	}

	public boolean validateRole(String role, String ownerId, String instituteId, 
			String schoolId, HttpServletRequest request) throws Exception {
		User user = getUserByEmail(request);
		if(user != null) {
			for(String authKey : user.getRoles().keySet()) {
				List<Authorization> authList = user.getRoles().get(authKey);
				for(Authorization auth : authList) {
					if(auth.getRole().equals(role) && auth.getOwnerId().equals(ownerId) && 
							auth.getInstituteId().equals(instituteId) && auth.getSchoolId().equals(schoolId)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean validateRole(String role, String ownerId, HttpServletRequest request) throws Exception {
		User user = getUserByEmail(request);
		return validateRole(role, ownerId, user);
	}
	
	public boolean validateRole(String role, String ownerId, User user) {
		if(user != null) {
			for(String authKey : user.getRoles().keySet()) {
				List<Authorization> authList = user.getRoles().get(authKey);
				for(Authorization auth : authList) {
					if(auth.getRole().equals(role) && auth.getOwnerId().equals(ownerId)) {
						return true;
					}
				}
			}
		}
		return false;		
	}
	
	public boolean validateRole(String role, HttpServletRequest request) throws Exception {
		User user = getUserByEmail(request);
		return validateRole(role, user);
	}
	
	public boolean validateRole(String role, User user) {
		if(user != null) {
			for(String authKey : user.getRoles().keySet()) {
				List<Authorization> authList = user.getRoles().get(authKey);
				for(Authorization auth : authList) {
					if(auth.getRole().equals(role)) {
						return true;
					}
				}
			}
		}
		return false;				
	}
	
}
