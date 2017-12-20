package it.smartcommunitylab.climb.domain.controller;

import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.GEngineUtils;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.model.Gamified;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.model.PedibusItinerary;
import it.smartcommunitylab.climb.domain.model.PedibusItineraryLeg;
import it.smartcommunitylab.climb.domain.model.PedibusPlayer;
import it.smartcommunitylab.climb.domain.model.PedibusTeam;
import it.smartcommunitylab.climb.domain.model.gamification.CustomData;
import it.smartcommunitylab.climb.domain.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.domain.model.gamification.PlayerStateDTO;
import it.smartcommunitylab.climb.domain.model.gamification.PointConcept;
import it.smartcommunitylab.climb.domain.model.gamification.TeamDTO;
import it.smartcommunitylab.climb.domain.scheduled.ChildStatus;
import it.smartcommunitylab.climb.domain.scheduled.EventsPoller;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
public class GamificationController extends AuthController {

	private static final transient Logger logger = LoggerFactory.getLogger(GamificationController.class);

	@Autowired
	@Value("${action.reset}")
	private String actionReset;	

	@Autowired
	@Value("${action.pedibus}")	
	private String actionPedibus;	

	@Autowired
	@Value("${param.kid.distance}")	
	private String paramDistance;	

	@Autowired
	@Value("${param.date}")	
	private String paramDate;	

	@Autowired
	@Value("${score.name}")	
	private String scoreName;	
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private EventsPoller eventsPoller;
	
	@Autowired
	private GEngineUtils gengineUtils;

	private ObjectMapper mapper = new ObjectMapper();

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/init", method = RequestMethod.GET)
	public @ResponseBody void initGame(
		@PathVariable String ownerId, 
		@PathVariable String pedibusGameId, 
		HttpServletRequest request, 
		HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<String> allChildrenId = Lists.newArrayList();
		List<String> allTeamsId = Lists.newArrayList();
		for (String classRoom : game.getClassRooms()) {
			Criteria criteria = Criteria.where("instituteId").is(game.getInstituteId())
					.and("schoolId").is(game.getSchoolId()).and("classRoom").is(classRoom);
			List<Child> childrenList = (List<Child>) storage.findData(Child.class, criteria, null, ownerId);
			List<String> childrenId = Lists.newArrayList();
			for (Child child : childrenList) {
				childrenId.add(child.getObjectId());
				allChildrenId.add(child.getObjectId());

				PedibusPlayer pp = new PedibusPlayer();
				pp.setChildId(child.getObjectId());
				pp.setWsnId(child.getWsnId());
				pp.setPedibusGameId(pedibusGameId);
				pp.setName(child.getName());
				pp.setSurname(child.getSurname());
				pp.setClassRoom(child.getClassRoom());
				storage.savePedibusPlayer(pp, ownerId, false);

				PlayerStateDTO player = new PlayerStateDTO();
				player.setPlayerId(child.getObjectId());
				player.setGameId(game.getGameId());
				CustomData cd = new CustomData();
				cd.put("name", child.getName());
				cd.put("surname", child.getSurname());
				player.setCustomData(cd);

				try {
					gengineUtils.createPlayer(game.getGameId(), player);
				} catch (Exception e) {
					logger.warn("Gamification engine player creation warning: " + e.getClass() + " " + e.getMessage());
				}
			}
			PedibusTeam pt = new PedibusTeam();
			pt.setChildrenId(childrenId);
			pt.setGameId(game.getGameId());
			pt.setPedibusGameId(pedibusGameId);
			pt.setClassRoom(classRoom);
			storage.savePedibusTeam(pt, ownerId, false);

			TeamDTO team = new TeamDTO();
			team.setName(classRoom);
			team.setMembers(childrenId);
			team.setPlayerId(classRoom);
			team.setGameId(game.getGameId());
			allTeamsId.add(classRoom);

			try {
				gengineUtils.createTeam(game.getGameId(), team);
			} catch (Exception e) {
				logger.warn("Gamification engine team creation warning: " + e.getClass() + " " + e.getMessage());
			}
		}	
		
		if (game.getGlobalTeam() != null && !game.getGlobalTeam().isEmpty()) {
			PedibusTeam pt = new PedibusTeam();
			pt.setChildrenId(allChildrenId);
			pt.setGameId(game.getGameId());
			pt.setPedibusGameId(pedibusGameId);
			pt.setClassRoom(game.getGlobalTeam());
			storage.savePedibusTeam(pt, ownerId, false);

			TeamDTO team = new TeamDTO();
			team.setName(game.getGlobalTeam());
			team.setMembers(allTeamsId);
			team.setPlayerId(game.getGlobalTeam());
			team.setGameId(game.getGameId());

			try {
				gengineUtils.createTeam(game.getGameId(), team);
			} catch (Exception e) {
				logger.warn("Gamification engine global team creation warning: " + e.getClass() + " " + e.getMessage());
			}				
		}		
		if (logger.isInfoEnabled()) {
			logger.info("initGame");
		}
	}
	
	@RequestMapping(value = "/api/game/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody PedibusGame createPedibusGame(
			@PathVariable String ownerId, 
			@RequestBody PedibusGame game, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, null, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		PedibusGame result = storage.savePedibusGame(game, ownerId, false);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createPedibusGame[%s]: %s", ownerId, game.getObjectId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}", method = RequestMethod.PUT)
	public @ResponseBody PedibusGame updatePedibusGame(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@RequestBody PedibusGame game, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, null, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		game.setOwnerId(ownerId);
		game.setObjectId(pedibusGameId);
		PedibusGame result = storage.savePedibusGame(game, ownerId, true);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updatePedibusGame[%s]: %s", ownerId, pedibusGameId));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}", method = RequestMethod.DELETE)
	public @ResponseBody PedibusGame deletePedibusGame(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		PedibusGame result = storage.removePedibusGame(ownerId, pedibusGameId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("deletePedibusGame[%s]: %s", ownerId, pedibusGameId));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}", method = RequestMethod.GET)
	public @ResponseBody PedibusGame getPedibusGame(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getPedibusGame[%s]: %s", ownerId, pedibusGameId));
		}
		return game;
	}

	@RequestMapping(value = "/api/game/{ownerId}/{instituteId}/{schoolId}/classes", method = RequestMethod.GET)
	public @ResponseBody List<String> getClassRoomBySchool(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,			
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorizationByExp(ownerId, instituteId, schoolId, null, null, 
				Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Child> children = storage.getChildrenBySchool(ownerId, instituteId, schoolId);
		List<String> result = new ArrayList<String>();
		for(Child child : children) {
			if(!result.contains(child.getClassRoom())) {
				result.add(child.getClassRoom());
			}
		}
		Collections.sort(result);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getClassRoomBySchool[%s]: %s", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<PedibusGame> getPedibusGames(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,			
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorizationByExp(ownerId, instituteId, schoolId, null, null, 
				Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		try {
			List<PedibusGame> result = storage.getPedibusGames(ownerId, instituteId, schoolId);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("getPedibusGames[%s]: %s", ownerId, result.size()));
			}
			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary", method = RequestMethod.POST)
	public @ResponseBody PedibusItinerary createItinerary(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@RequestBody PedibusItinerary itinerary, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		itinerary.setPedibusGameId(pedibusGameId);
		itinerary.setOwnerId(ownerId);
		itinerary.setObjectId(Utils.getUUID());
		storage.savePedibusItinerary(itinerary);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createItinerary[%s]: %s", ownerId, pedibusGameId));
		}
		return itinerary; 
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}", method = RequestMethod.PUT)
	public @ResponseBody PedibusItinerary updateItinerary(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@RequestBody PedibusItinerary itinerary, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		itinerary.setPedibusGameId(pedibusGameId);
		itinerary.setOwnerId(ownerId);
		itinerary.setObjectId(itineraryId);
		storage.savePedibusItinerary(itinerary);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updateItinerary[%s]: %s - %s", ownerId, pedibusGameId, itineraryId));
		}
		return itinerary; 
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteItinerary(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removePedibusItinerary(ownerId, pedibusGameId, itineraryId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updateItinerary[%s]: %s - %s", ownerId, pedibusGameId, itineraryId));
		}
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary", method = RequestMethod.GET)
	public @ResponseBody List<PedibusItinerary> getItinerary(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PedibusItinerary> result = storage.getPedibusItineraryByGameId(ownerId, pedibusGameId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getItinerary[%s]: %s - %s", ownerId, pedibusGameId, result.size()));
		}
		return result;
	}

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg", method = RequestMethod.POST)
	public @ResponseBody void createPedibusItineraryLeg(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@RequestBody PedibusItineraryLeg leg, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		leg.setPedibusGameId(pedibusGameId);
		leg.setItineraryId(itineraryId);
		leg.setOwnerId(ownerId);
		storage.savePedibusItineraryLeg(leg, ownerId, false);
		if (logger.isInfoEnabled()) {
			logger.info("add pedibusItineraryLeg");
		}
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/legs", method = RequestMethod.POST)
	public @ResponseBody void createPedibusItineraryLegs(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@RequestBody List<PedibusItineraryLeg> legs, 
			@RequestParam(required = false) Boolean sum, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Collections.sort(legs);
		int sumValue = 0;
		try {
			storage.removePedibusItineraryLegByItineraryId(ownerId, pedibusGameId, itineraryId);
			for (PedibusItineraryLeg leg: legs) {
				leg.setPedibusGameId(pedibusGameId);
				leg.setItineraryId(itineraryId);
				leg.setOwnerId(ownerId);
				if (sum != null && sum) {
					sumValue += leg.getScore();
					leg.setScore(sumValue);
				}
				storage.savePedibusItineraryLeg(leg, ownerId, false);
			}
			if (logger.isInfoEnabled()) {
				logger.info(String.format("createPedibusItineraryLegs[%s]: %s", ownerId, itineraryId));
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
	}	

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/legs", method = RequestMethod.PUT)
	public @ResponseBody List<PedibusItineraryLeg> updatePedibusItineraryLegs(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@RequestBody List<PedibusItineraryLeg> legs, 
			@RequestParam(required = false) Boolean sum, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Collections.sort(legs);
		int sumValue = 0;
		try {
			storage.removePedibusItineraryLegByItineraryId(ownerId, pedibusGameId, itineraryId);
			for (PedibusItineraryLeg leg: legs) {
				leg.setPedibusGameId(pedibusGameId);
				leg.setItineraryId(itineraryId);
				leg.setOwnerId(ownerId);
				if (sum != null && sum) {
					sumValue += leg.getScore();
					leg.setScore(sumValue);
				}
				storage.savePedibusItineraryLeg(leg, ownerId, false);
			}
			if (logger.isInfoEnabled()) {
				logger.info(String.format("updatePedibusItineraryLegs[%s]: %s", ownerId, itineraryId));
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
		return legs;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}", method = RequestMethod.GET)
	public @ResponseBody PedibusItineraryLeg getPedibusItineraryLeg(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@PathVariable String legId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		try {
			PedibusItineraryLeg result = storage.getPedibusItineraryLeg(ownerId, legId);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("getPedibusItineraryLeg[%s]: %s", ownerId, legId));
			}
			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/legs", 
			method = RequestMethod.GET)
	public @ResponseBody List<PedibusItineraryLeg> getPedibusItineraryLegs(
			@PathVariable String ownerId,
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		try {
			List<PedibusItineraryLeg> result = storage.getPedibusItineraryLegsByGameId(ownerId, 
					pedibusGameId, itineraryId);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("getPedibusItineraryLegs[%s]: %s", ownerId, result.size()));
			}
			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/game/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/status", 
			method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getGameStatus(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		PedibusItinerary itinerary = storage.getPedibusItinerary(ownerId, pedibusGameId, itineraryId);
		if(itinerary == null) {
			throw new EntityNotFoundException("itinerary not found");
		}
		/*if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}*/
		try {
			List<PedibusItineraryLeg> legs = storage.getPedibusItineraryLegsByGameId(ownerId, 
					pedibusGameId, itineraryId);
			PedibusItineraryLeg lastLeg = Collections.max(legs);
			
			// players score
			/**
			List<PedibusPlayer> players = storage.getPedibusPlayers(ownerId, gameId);

			for (PedibusPlayer player : players) {
				updateGamificationData(player, gameId, player.getChildId());
			}
			**/

			// teams score
			List<PedibusTeam> teams = storage.getPedibusTeams(ownerId, pedibusGameId);
			for (PedibusTeam team : teams) {
				updateGamificationData(team, pedibusGameId, game.getGameId(), team.getClassRoom());

				// find "current" leg
				for (PedibusItineraryLeg leg : legs) {
					if (team.getScore() >= leg.getScore()) {
						team.setPreviousLeg(leg);
					} else {
						team.setCurrentLeg(leg);
						break;
					}
				}

				if (team.getCurrentLeg() != null) {
					team.setScoreToNext(team.getCurrentLeg().getScore() - team.getScore());
				}
				team.setScoreToEnd(lastLeg.getScore() - team.getScore());
			}

			Map<String, Object> result = Maps.newTreeMap();
			result.put("game", game);
			result.put("itinerary", itinerary);
			result.put("legs", legs);
			//result.put("players", players);
			result.put("teams", teams);

			if (logger.isInfoEnabled()) {
				logger.info(String.format("getGameStatus[%s]: %s", ownerId, pedibusGameId));
			}

			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/game/events/{ownerId}/{pedibusGameId}", method = RequestMethod.PATCH)
	public @ResponseBody Map<String, Collection<ChildStatus>> pollEvents(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
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
		try {
			Map<String, Collection<ChildStatus>> childrenStatusMap = eventsPoller.pollGameEvents(game, false);
			for(String routeId : childrenStatusMap.keySet()) {
				Collection<ChildStatus> childrenStatus = childrenStatusMap.get(routeId);
				if(!eventsPoller.isEmptyResponse(childrenStatus)) {
					Map<String, Boolean> updateClassScores = 
							eventsPoller.updateCalendarDayFromPedibus(ownerId, pedibusGameId, childrenStatus); 
					eventsPoller.sendScores(childrenStatus, updateClassScores, game);
					storage.updatePollingFlag(ownerId, pedibusGameId, routeId, Boolean.FALSE);
				}
			}
			return childrenStatusMap;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/game/child/score/{ownerId}/{pedibusGameId}/{playerId}", method = RequestMethod.GET)
	public @ResponseBody void increaseChildScore(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String playerId, 
			@RequestParam Double score, 
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
		ExecutionDataDTO ed = new ExecutionDataDTO();
		ed.setGameId(game.getGameId());
		ed.setPlayerId(playerId);
		ed.setActionId(actionPedibus);

		Map<String, Object> data = Maps.newTreeMap();
		data.put(paramDistance, score);
		Date date = new Date();
		data.put(paramDate, date.getTime());
		ed.setData(data);
		
		gengineUtils.executeAction(ed);
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("increaseChildScore[%s]: increased game[%s] player[%s] score[%s]", ownerId,
					game.getGameId(), playerId, score));
		}			
	}
	
	@RequestMapping(value = "/api/game/reset/{ownerId}/{pedibusGameId}", method = RequestMethod.GET)
	public @ResponseBody void resetGame(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
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
		try {
			List<PedibusPlayer> players = storage.getPedibusPlayers(ownerId, pedibusGameId);
			for (PedibusPlayer player: players) {
				resetChild(game.getGameId(), player.getChildId());
			}
			
			List<PedibusTeam> teams = storage.getPedibusTeams(ownerId, pedibusGameId);
			for (PedibusTeam team: teams) {
				resetChild(game.getGameId(), team.getClassRoom());
			}			
			
			if (logger.isInfoEnabled()) {
				logger.info("reset game");
			}			
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
	}	
	
	@RequestMapping(value = "/api/game/child/reset/{ownerId}/{pedibusGameId}/{playerId}", method = RequestMethod.PATCH)
	public @ResponseBody void resetChild(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@RequestParam String playerId, 
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		try {
			resetChild(game.getGameId(), playerId);
			
			if (logger.isInfoEnabled()) {
				logger.info("reset player");
			}			
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
	}		
	
	private void resetChild(String gameId, String playerId) throws Exception {
//		ExecutionDataDTO ed = new ExecutionDataDTO();
//		ed.setGameId(gameId);
//		ed.setPlayerId(playerId);
//		ed.setActionId(actionReset);
//		Map<String, Object> data = Maps.newTreeMap();
//		ed.setData(data);
//		gengineUtils.executeAction(ed);
		gengineUtils.deletePlayerState(gameId, playerId);
	}
	

	@SuppressWarnings("rawtypes")
	private void updateGamificationData(Gamified entity, String pedibusGameId, String gameId, String id) throws Exception {
		
		PlayerStateDTO gamePlayer = gengineUtils.getPlayerStatus(gameId, id);
		
		entity.setGameStatus(gamePlayer);
		
		Set<?> pointConcept = (Set) gamePlayer.getState().get("PointConcept");
		
		if (pointConcept != null) {
			Iterator<?> it = pointConcept.iterator();
			while (it.hasNext()) {
				PointConcept pc = mapper.convertValue(it.next(), PointConcept.class);
				if (scoreName.equals(pc.getName())) {
					entity.setScore(pc.getScore());
				}
			}
		}

		/**
		Set<?> badgeCollectionConcept = (Set) gamePlayer.getState().get("BadgeCollectionConcept");
		if (badgeCollectionConcept != null) {
			Map<String, Collection> badges = Maps.newTreeMap();
			Iterator<?> it = badgeCollectionConcept.iterator();
			while (it.hasNext()) {
				BadgeCollectionConcept bcc = mapper.convertValue(it.next(), BadgeCollectionConcept.class);
				badges.put(bcc.getName(), bcc.getBadgeEarned());
			}
			entity.setBadges(badges);
		}
		**/

	}
	
	public static String getUUID() {
		return UUID.randomUUID().toString();
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
