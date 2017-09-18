package it.smartcommunitylab.climb.contextstore.controller;

import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationDTO;
import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.climb.contextstore.common.Utils;
import it.smartcommunitylab.climb.contextstore.exception.UnauthorizedException;
import it.smartcommunitylab.climb.contextstore.security.AuthorizationManager;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	@Value("${authorization.clientId}")	
	private String clientId;

	@Autowired
	@Value("${authorization.clientSecret}")	
	private String clientSecret;
	
	@Autowired
	@Value("${profile.serverUrl}")
	private String profileServerUrl;

	@Autowired
	@Value("${profile.account}")
	private String profileAccount;

	@Autowired
	@Value("${profile.attribute}")
	private String profileAttribute;
	
	@Autowired
	AuthorizationManager authorizationManager;

	private AACProfileService profileConnector;

	@PostConstruct
	public void init() throws Exception {
		profileConnector = new AACProfileService(profileServerUrl);
	}

	protected String getCF(AccountProfile accountProfile) {
		String result = null;
		if(accountProfile != null) {
			result = accountProfile.getAttribute(profileAccount, profileAttribute);
		}
		return "1122334455";
		//return result;
	}
	
	protected String getSubject(AccountProfile accountProfile) {
		String result = null;
		if(accountProfile != null) {
			result = accountProfile.getUserId();
		}
		return "429";
		//return result;
	}
	
	protected AccountProfile getAccoutProfile(HttpServletRequest request) {
		AccountProfile result = null;
		String token = request.getHeader("Authorization");
		if (Utils.isNotEmpty(token)) {
			token = token.replace("Bearer ", "");
			try {
				result = profileConnector.findAccountProfile(token);
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(String.format("getAccoutProfile[%s]: %s", token, e.getMessage()));
				}
			} 
		}
		return result;
	}
	
	public boolean validateAuthorizationByExp(String ownerId, String instituteId, 
			String schoolId, String routeId, String action,	
			HttpServletRequest request) throws Exception {
		String subject = getSubject(getAccoutProfile(request));
		String resourceName = "pedibus";
		Map<String, String> attributes = new HashMap<String, String>();
		if(Utils.isNotEmpty(ownerId)) {
			attributes.put("pedibus-ownerId", ownerId);
		}
		if(Utils.isNotEmpty(instituteId)) {
			attributes.put("pedibus-instituteId", instituteId);
		}
		if(Utils.isNotEmpty(schoolId)) {
			attributes.put("pedibus-schoolId", schoolId);
		}
		if(Utils.isNotEmpty(routeId)) {
			attributes.put("pedibus-routeId", routeId);
		}
		AuthorizationDTO authorization = authorizationManager.getAuthorization(subject, action, 
				resourceName, attributes);
		if(!authorizationManager.validateAuthorization(authorization)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		return true;
	}
	
}
