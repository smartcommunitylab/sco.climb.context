package it.smartcommunitylab.climb.domain.controller;

import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.GEngineUtils;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.model.gameconf.PedibusGameConf;
import it.smartcommunitylab.climb.domain.model.gameconf.PedibusGameConfSummary;
import it.smartcommunitylab.climb.domain.model.gameconf.PedibusGameConfTemplate;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
public class GameConfController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(GameConfController.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private GEngineUtils gengineUtils;

	@RequestMapping(value = "/api/game/conf/template", method = RequestMethod.GET)
	public @ResponseBody List<PedibusGameConfTemplate> getConfTemplates(
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		List<PedibusGameConfTemplate> result = storage.getPedibusGameConfTemplates();
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getConfTemplates: %s", result.size()));
		}
		return result; 
	}
	
	@RequestMapping(value = "/api/game/conf/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<PedibusGameConfSummary> getConfSummaryBySchool(
			@PathVariable String ownerId, 
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorizationByExp(ownerId, instituteId, schoolId, null, null, 
				Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PedibusGameConfSummary> result = storage.getPedibusGameConfSummary(ownerId, instituteId, schoolId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getConfSummaryBySchool: %s - %s", ownerId, result.size()));
		}
		return result; 
	}
	
	@RequestMapping(value = "/api/game/conf/{ownerId}/{confId}", method = RequestMethod.GET)
	public @ResponseBody PedibusGameConf getConfById(
			@PathVariable String ownerId, 
			@PathVariable String confId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGameConf gameConf = storage.getPedibusGameConf(ownerId, confId);
		if(gameConf == null) {
			throw new EntityNotFoundException("game conf not found");
		}
		if(!validateAuthorizationByExp(ownerId, gameConf.getInstituteId(), gameConf.getSchoolId(), 
				null, gameConf.getPedibusGameId(), Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getConfById: %s - %s", ownerId, confId));
		}
		return gameConf; 
	}
	
	@RequestMapping(value = "/api/game/conf/{ownerId}/{pedibusGameId}", method = RequestMethod.POST)
	public @ResponseBody PedibusGameConf saveConf(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@RequestBody PedibusGameConf gameConf, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		gameConf.setOwnerId(ownerId);
		gameConf.setInstituteId(game.getInstituteId());
		gameConf.setSchoolId(game.getSchoolId());
		gameConf.setPedibusGameId(pedibusGameId);
		storage.savePedibusGameConf(gameConf);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveConf: %s - %s", ownerId, gameConf.getObjectId()));
		}
		return gameConf; 
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
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
