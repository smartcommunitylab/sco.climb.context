package it.smartcommunitylab.climb.domain.controller;

import it.smartcommunitylab.aac.AACException;
import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.AACService;
import it.smartcommunitylab.aac.authorization.beans.AccountAttributeDTO;
import it.smartcommunitylab.aac.authorization.beans.RequestedAuthorizationDTO;
import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.aac.model.TokenData;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.security.AuthorizationManager;

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
	@Value("${oauth.serverUrl}")	
	private String oauthServerUrl;
	
	@Autowired
	@Value("${security.oauth2.client.clientId}")	
	private String clientId;

	@Autowired
	@Value("${security.oauth2.client.clientSecret}")	
	private String clientSecret;
	
	@Autowired
	@Value("${profile.serverUrl}")
	private String profileServerUrl;

	@Autowired
	AuthorizationManager authorizationManager;

	private AACService aacService;
	
	private AACProfileService profileConnector;
	
	@PostConstruct
	public void init() throws Exception {
		aacService = new AACService(oauthServerUrl, clientId, clientSecret);
		profileConnector = new AACProfileService(profileServerUrl);
	}
	
	protected TokenData refreshToken(String refreshToken) throws SecurityException, AACException {
		return aacService.refreshToken(refreshToken);
	}
	
	protected AccountAttributeDTO getAccountByEmail(AccountProfile accountProfile) {
		String email = null;
		if(accountProfile == null) {
			return null;
		}
		if(Utils.isNotEmpty(
				accountProfile.getAttribute("adc", "pat_attribute_email"))) {
			email = accountProfile.getAttribute("adc", "pat_attribute_email");
		} else if(Utils.isNotEmpty(
				accountProfile.getAttribute("google", "email"))) {
			email = accountProfile.getAttribute("google", "email");
		} else if(Utils.isNotEmpty(
				accountProfile.getAttribute("facebook", "email"))) {
			email = accountProfile.getAttribute("facebook", "email");
		} else if(Utils.isNotEmpty(
				accountProfile.getAttribute("internal", "email"))) {
			email = accountProfile.getAttribute("internal", "email");
		}
		AccountAttributeDTO account = new AccountAttributeDTO();
		account.setAccountName(Const.AUTH_ACCOUNT_NAME);
		account.setAttributeName(Const.AUTH_ATTRIBUTE_NAME);
		//TODO TEST
		//account.setAttributeValue("smartcommunitytester@gmail.com");
		account.setAttributeValue(email);
		return account;
	}

	protected String getSubject(AccountProfile accountProfile) {
		String result = null;
		if(accountProfile != null) {
			result = accountProfile.getUserId();
		}
		return result;
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
			String schoolId, String routeId, String gameId, String resource, String action,	
			HttpServletRequest request) throws Exception {
		AccountAttributeDTO account = getAccountByEmail(getAccoutProfile(request));
		if(account == null) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		String resourceName = "pedibus";
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("pedibus-resource", resource);
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
		if(Utils.isNotEmpty(gameId)) {
			attributes.put("pedibus-gameId", gameId);
		}
		RequestedAuthorizationDTO authorization = authorizationManager.getAuthorization(account, action, 
				resourceName, attributes);
		if(!authorizationManager.validateAuthorization(authorization)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		return true;
	}
	
}
