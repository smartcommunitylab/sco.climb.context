package it.smartcommunitylab.climb.domain.controller;

import it.smartcommunitylab.aac.authorization.beans.AuthorizationDTO;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.security.AuthorizationManager;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class AdminController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(AdminController.class);
	@Autowired
	AuthorizationManager authorizationManager;

	@RequestMapping(value = "/admin/auth/schema/upload", method = RequestMethod.POST)
	public @ResponseBody void uploadAuthSchema(
			@RequestBody String json,
			HttpServletRequest request) throws Exception {
//		if(!validateAuthorizationByExp(null, null, null, null, null,
//				Const.AUTH_RES_Auth, Const.AUTH_ACTION_ADD, request)) {
//			throw new UnauthorizedException("Unauthorized Exception: token not valid");
//		}
		authorizationManager.loadAuthSchema(json);
	}
	
	@RequestMapping(value = "/admin/auth", method = RequestMethod.POST)
	public @ResponseBody AuthorizationDTO addAuthorization(
			@RequestBody AuthorizationDTO auth,
			HttpServletRequest request) throws Exception {
//		if(!validateAuthorizationByExp(null, null, null, null, null,
//				Const.AUTH_RES_Auth, Const.AUTH_ACTION_ADD, request)) {
//			throw new UnauthorizedException("Unauthorized Exception: token not valid");
//		}
		return authorizationManager.insertAuthorization(auth);
	}
	
	@RequestMapping(value = "/admin/auth/{authId}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteAuthorization(
			@PathVariable String authId,
			HttpServletRequest request) throws Exception {
//		if(!validateAuthorizationByExp(null, null, null, null, null,
//				Const.AUTH_RES_Auth, Const.AUTH_ACTION_DELETE, request)) {
//			throw new UnauthorizedException("Unauthorized Exception: token not valid");
//		}
		authorizationManager.deleteAuthorization(authId);
	}
	
	@RequestMapping(value = "/admin/auth/validate", method = RequestMethod.POST)
	public @ResponseBody String validateAuth(
			@RequestBody AuthorizationDTO auth,
			HttpServletRequest request) throws Exception {
//		if(!validateAuthorizationByExp(null, null, null, null, null,
//				Const.AUTH_RES_Auth, Const.AUTH_ACTION_READ, request)) {
//			throw new UnauthorizedException("Unauthorized Exception: token not valid");
//		}
		return Boolean.toString(authorizationManager.validateAuthorization(auth));
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
