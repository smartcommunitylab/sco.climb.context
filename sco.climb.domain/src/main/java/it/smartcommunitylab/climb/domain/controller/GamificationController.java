package it.smartcommunitylab.climb.domain.controller;

import java.io.StringWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import it.smartcommunitylab.climb.contextstore.model.ClassRoom;
import it.smartcommunitylab.climb.contextstore.model.Institute;
import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.GEngineUtils;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.InvalidParametersException;
import it.smartcommunitylab.climb.domain.exception.StorageException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.model.Gamified;
import it.smartcommunitylab.climb.domain.model.MultimediaContentTags;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.model.PedibusGameReport;
import it.smartcommunitylab.climb.domain.model.PedibusItinerary;
import it.smartcommunitylab.climb.domain.model.PedibusItineraryLeg;
import it.smartcommunitylab.climb.domain.model.PedibusPlayer;
import it.smartcommunitylab.climb.domain.model.PedibusTeam;
import it.smartcommunitylab.climb.domain.model.gamification.BadgeCollectionConcept;
import it.smartcommunitylab.climb.domain.model.gamification.ChallengeModel;
import it.smartcommunitylab.climb.domain.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.domain.model.gamification.GameDTO;
import it.smartcommunitylab.climb.domain.model.gamification.IncrementalClassificationDTO;
import it.smartcommunitylab.climb.domain.model.gamification.PedibusGameConfTemplate;
import it.smartcommunitylab.climb.domain.model.gamification.PlayerStateDTO;
import it.smartcommunitylab.climb.domain.model.gamification.PointConcept;
import it.smartcommunitylab.climb.domain.model.gamification.PointConcept.PeriodInternal;
import it.smartcommunitylab.climb.domain.model.gamification.RuleDTO;
import it.smartcommunitylab.climb.domain.model.gamification.RuleValidateDTO;
import it.smartcommunitylab.climb.domain.model.gamification.TeamDTO;
import it.smartcommunitylab.climb.domain.model.multimedia.ContentOwner;
import it.smartcommunitylab.climb.domain.model.multimedia.MultimediaContent;
import it.smartcommunitylab.climb.domain.model.multimedia.MultimediaResult;
import it.smartcommunitylab.climb.domain.scheduled.SchedulerManager;
import it.smartcommunitylab.climb.domain.storage.DocumentManager;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

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
	private DocumentManager documentManager;

	@Autowired
	private GEngineUtils gengineUtils;
	
	@Autowired
	private SchedulerManager schedulerManager;

	private ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/deploy", method = RequestMethod.GET)
	public @ResponseBody PedibusGame deployGame(
		@PathVariable String ownerId, 
		@PathVariable String pedibusGameId, 
		HttpServletRequest request, 
		HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		if(game.isDeployed()) {
			throw new StorageException("game already deployed");
		}
		//create game
		if(Utils.isEmpty(game.getGameId())) {
			//read legs
			List<PedibusItineraryLeg> legs = new ArrayList<>();
			String finalDestination = "";
			List<PedibusItinerary> itineraryList = storage.getPedibusItineraryByGameId(ownerId, pedibusGameId);
			if(itineraryList.size() > 0) {
				List<PedibusItineraryLeg> legsByGameId = storage.getPedibusItineraryLegsByGameId(ownerId, 
						pedibusGameId, itineraryList.get(0).getObjectId());
				legs.addAll(legsByGameId);
				finalDestination = legsByGameId.get(legsByGameId.size() - 1).getName();	
			}
			//get game conf template
			PedibusGameConfTemplate gameConf = storage.getPedibusGameConfTemplate(game.getConfTemplateId());
			if(gameConf == null) {
				throw new EntityNotFoundException("game conf not found");
			}
			GameDTO gameDTO = new GameDTO();
			gameDTO.setName(game.getGameName());
			//set params
			game.getParams().put("const_school_name", game.getGlobalTeam());
			game.getParams().put("const_number_of_teams", String.valueOf(game.getClassRooms().size() + 1));
			long dailyNominalDistance = Long.valueOf(game.getParams().get("const_daily_nominal_distance"));
			long weeklyNominalDistance = dailyNominalDistance * 5;
			long minDistanceLegsAlmostReached = dailyNominalDistance * 15;
			long almostReachedNextLeg = dailyNominalDistance * 3;
			game.getParams().put("const_weekly_nominal_distance", String.valueOf(weeklyNominalDistance));
			game.getParams().put("const_almost_reached_next_leg", String.valueOf(almostReachedNextLeg));
			game.getParams().put("final_destination", finalDestination);
			//set almost reached rules
			Map<String, Boolean> almostReachedMap = new HashMap<>();
			for(int i = 0; i < legs.size(); i++) {
				PedibusItineraryLeg currentLeg = legs.get(i);
				if(i == 0) {
					almostReachedMap.put(currentLeg.getObjectId(), Boolean.FALSE);
				} else {
					PedibusItineraryLeg previousLeg = legs.get(i - 1);
					if(currentLeg.getScore() - previousLeg.getScore() >= minDistanceLegsAlmostReached) {
						almostReachedMap.put(currentLeg.getObjectId(), Boolean.TRUE);
					} else {
						almostReachedMap.put(currentLeg.getObjectId(), Boolean.FALSE);
					}
				}
			}
			//create actions
			gameDTO.getActions().addAll(gameConf.getActions());
			//create badgeCollections
			for(String badgeCollection : gameConf.getBadgeCollections()) {
				BadgeCollectionConcept badgeCollectionConcept = new BadgeCollectionConcept(badgeCollection);
				gameDTO.getBadgeCollectionConcept().add(badgeCollectionConcept);
			}
			//create point concepts
			for(String pointName : gameConf.getPoints().keySet()) {
				List<String> periods = gameConf.getPoints().get(pointName);
				PointConcept pointConcept = new PointConcept(pointName);
				Map<String, PeriodInternal> intervalMap = new HashMap<>();
				for(String period : periods) {
					if(period.equals("daily")) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(game.getFrom());
						LocalDate ld = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
						ld = ld.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
						Date start = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
						PeriodInternal periodInternal = pointConcept.new PeriodInternal(period, start, 86400000);
						intervalMap.put(period, periodInternal);
					} else if(period.equals("weekly")) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(game.getFrom());
						LocalDate ld = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
						ld = ld.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
						Date start = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
						PeriodInternal periodInternal = pointConcept.new PeriodInternal(period, start, 604800000);
						intervalMap.put(period, periodInternal);
					}
				}
				pointConcept.setPeriods(intervalMap);
				gameDTO.getPointConcept().add(pointConcept);
			}
			//create game
			String gameId = null;
			try {
				gameId = gengineUtils.createGame(gameDTO);
				game.setGameId(gameId);
				storage.updatePedibusGameGameId(ownerId, pedibusGameId, gameId);
				storage.updatePedibusGameDeployed(ownerId, pedibusGameId, true);
			} catch (Exception e) {
				logger.error("Gamification engine game creation error: " + e.getClass() + " " + e.getMessage());
				throw new StorageException("unable to create game:" + e.getMessage());
			}
			//create tasks
			try {
				for(String classificationName : gameConf.getTasks().keySet()) {
					Map<String, String> taskMap = gameConf.getTasks().get(classificationName);
					IncrementalClassificationDTO classificationDTO = new IncrementalClassificationDTO();
					classificationDTO.setClassificationName(classificationName);
					classificationDTO.setName(classificationName);
					classificationDTO.setItemType(taskMap.get("point"));
					classificationDTO.setPeriodName(taskMap.get("period"));
					classificationDTO.setItemsToNotificate(game.getClassRooms().size() + 1);
					classificationDTO.setType(taskMap.get("type"));
					gengineUtils.createTask(gameId, classificationDTO);
				}
			} catch (Exception e) {
				logger.error("Gamification engine task creation error: " + e.getClass() + " " + e.getMessage());
				throw new StorageException("unable to create task:" + e.getMessage());
			}
			//create rules
			VelocityEngine velocityEngine = new VelocityEngine();
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
			velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			velocityEngine.init();
			VelocityContext context = new VelocityContext();
			context.put("params", game.getParams());
			context.put("legList", legs);
			context.put("almostReachedMap", almostReachedMap);
			context.put("Utils", Utils.class); 
			for(String ruleFile : gameConf.getRuleFileTemplates()) {
				String ruleName = ruleFile.replace(".vm", ""); 
				try {
					Template t = velocityEngine.getTemplate("game-template/" + ruleFile);
					StringWriter writer = new StringWriter();
					t.merge(context, writer);
					String ruleContent = writer.toString();
					if(!ruleName.contains("constants")) {
						RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
						ruleValidateDTO.setRule(ruleContent);
						gengineUtils.validateRule(gameId, ruleValidateDTO);
					}
					RuleDTO ruleDTO = new RuleDTO();
					ruleDTO.setName(ruleName);
					ruleDTO.setContent(writer.toString());
					if(ruleName.contains("constants")) {
						int indexOf = ruleName.indexOf("_");
						if(indexOf > 0) {
							ruleDTO.setName(ruleName.substring(0, indexOf));
						}
					}
					gengineUtils.createRule(gameId, ruleDTO);
				} catch (Exception e) {
					logger.error("Gamification engine rule creation error: " + e.getClass() + " " + e.getMessage());
					throw new StorageException("unable to create rule:" + e.getMessage());
				}				
			}			
			//create challenges
			for(String model : gameConf.getChallengeModels().keySet()) {
				List<String> variables = gameConf.getChallengeModels().get(model);
				ChallengeModel challengeModel = new ChallengeModel();
				challengeModel.setName(model);
				challengeModel.setVariables(variables);
				try {
					gengineUtils.createChallenge(gameId, challengeModel);
				} catch (Exception e) {
					logger.error("Gamification engine challenge creation error: " + e.getClass() + " " + e.getMessage());
					throw new StorageException("unable to create challenge:" + e.getMessage());
				}
			}
		}		
		//create players
		for (String classRoom : game.getClassRooms()) {
			TeamDTO player = new TeamDTO();
			player.setName(classRoom);
			player.setPlayerId(classRoom);
			player.setGameId(game.getGameId());
			try {
				gengineUtils.createTeam(game.getGameId(), player);
			} catch (Exception e) {
				logger.warn("Gamification engine player creation warning: " + e.getClass() + " " + e.getMessage());
			}
		}	
		//create global team
		if (Utils.isNotEmpty(game.getGlobalTeam())) {
			TeamDTO team = new TeamDTO();
			team.setName(game.getGlobalTeam());
			team.setMembers(game.getClassRooms());
			team.setPlayerId(game.getGlobalTeam());
			team.setGameId(game.getGameId());
			try {
				gengineUtils.createTeam(game.getGameId(), team);
			} catch (Exception e) {
				logger.warn("Gamification engine global team creation warning: " + e.getClass() + " " + e.getMessage());
			}				
		}
		try {
			schedulerManager.resetJob(game.getObjectId());
		} catch (Exception e) {
			logger.warn(String.format("deployGame[%s]: error in reset job for %s", 
					ownerId, game.getObjectId()));
		}
		if (logger.isInfoEnabled()) {
			logger.info("deployGame:" + game.getObjectId());
		}
		return game;
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, null, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		PedibusGame result = storage.savePedibusGame(game, ownerId, false);
		PedibusItinerary itinerary = new PedibusItinerary();
		itinerary.setOwnerId(ownerId);
		itinerary.setPedibusGameId(result.getObjectId());
		itinerary.setName(result.getGameName() + " - itinerario");
		storage.savePedibusItinerary(itinerary);
		try {
			schedulerManager.resetJob(result.getObjectId());
		} catch (Exception e) {
			logger.warn(String.format("createPedibusGame[%s]: error in reset job for %s", 
					ownerId, result.getObjectId()));
		}
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, null, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		game.setOwnerId(ownerId);
		game.setObjectId(pedibusGameId);
		PedibusGame result = storage.savePedibusGame(game, ownerId, true);
		try {
			schedulerManager.resetJob(result.getObjectId());
		} catch (Exception e) {
			logger.warn(String.format("updatePedibusGame[%s]: error in reset job for %s", 
					ownerId, result.getObjectId()));
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updatePedibusGame[%s]: %s", ownerId, pedibusGameId));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/mobility", method = RequestMethod.PUT)
	public @ResponseBody PedibusGame updatePedibusGameMobility(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@RequestBody PedibusGame game, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame pedibusGameDb = storage.getPedibusGame(ownerId, pedibusGameId);
		if(pedibusGameDb == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, pedibusGameDb.getInstituteId(), pedibusGameDb.getSchoolId(), 
				null, null, Const.AUTH_RES_PedibusGame_Mobility, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		pedibusGameDb.getParams().putAll(game.getParams());
		PedibusGame result = storage.savePedibusGame(pedibusGameDb, ownerId, true);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updatePedibusGameMobility[%s]: %s", ownerId, pedibusGameId));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/tuning", method = RequestMethod.PUT)
	public @ResponseBody PedibusGame updatePedibusGameTuning(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@RequestBody PedibusGame game, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame pedibusGameDb = storage.getPedibusGame(ownerId, pedibusGameId);
		if(pedibusGameDb == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, pedibusGameDb.getInstituteId(), pedibusGameDb.getSchoolId(), 
				null, null, Const.AUTH_RES_PedibusGame_Tuning, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		pedibusGameDb.getParams().putAll(game.getParams());
		PedibusGame result = storage.savePedibusGame(pedibusGameDb, ownerId, true);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updatePedibusGameTuning[%s]: %s", ownerId, pedibusGameId));
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		PedibusGame result = storage.removePedibusGame(ownerId, pedibusGameId);
		try {
			schedulerManager.removeJob(pedibusGameId);
		} catch (Exception e) {
			logger.warn(String.format("deletePedibusGame[%s]: error in removing job for %s", ownerId, pedibusGameId));
		}		
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getPedibusGame[%s]: %s", ownerId, pedibusGameId));
		}
		return game;
	}

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/students", method = RequestMethod.GET)
	public @ResponseBody Integer getStudentByClasses(
			@PathVariable String ownerId,
			@PathVariable String pedibusGameId,
			@RequestParam List<String> classes,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}		
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		int result = 0;
		List<PedibusPlayer> pedibusPlayers = storage.getPedibusPlayers(ownerId, pedibusGameId);
		for(PedibusPlayer player : pedibusPlayers) {
			if(classes.contains(player.getClassRoom())) {
				result++;
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getStudentByClasses[%s]: %s - %s", ownerId, pedibusGameId, result));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{instituteId}/{schoolId}/classes", method = RequestMethod.GET)
	public @ResponseBody List<ClassRoom> getClasses(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, null, null, 
				Const.AUTH_RES_School, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		School school = storage.getSchool(ownerId, instituteId, schoolId);
		if(school == null) {
			throw new EntityNotFoundException("school not found");
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getClasses[%s]: %s - %s - %s", ownerId, instituteId, schoolId, school.getClasses()));
		}
		return school.getClasses();
	}
	
	@RequestMapping(value = "/api/game/report", method = RequestMethod.GET)
	public @ResponseBody List<PedibusGameReport> getPedibusGameReports(
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		List<PedibusGameReport> result = new ArrayList<>();
		List<PedibusGame> games = storage.getPedibusGames();
		for(PedibusGame game : games) {
			List<PedibusItinerary> itineraryList = storage.getPedibusItineraryByGameId(game.getOwnerId(), game.getObjectId());
			if(itineraryList.size() > 0) {
				PedibusItinerary pedibusItinerary = itineraryList.get(0);
				List<PedibusItineraryLeg> legs = storage.getPedibusItineraryLegsByGameId(game.getOwnerId(), game.getObjectId(), 
						pedibusItinerary.getObjectId());
				if(legs.size() > 0) {
					PedibusGameReport report = new PedibusGameReport(game);
					report.setItineraryId(pedibusItinerary.getObjectId());
					report.setLegs(legs.size());
					report.setFirstLeg(legs.get(0).getName());
					report.setFinalLeg(legs.get(legs.size() - 1).getName());
					report.setFinalScore(legs.get(legs.size() - 1).getScore());
					result.add(report);					
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getPedibusGameReports: %s", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<PedibusGame> getPedibusGamesBySchool(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,			
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, null, null, 
				Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		User user = getUserByEmail(request);
		List<PedibusGame> result = new ArrayList<>();
		List<PedibusGame> list = storage.getPedibusGames(ownerId, instituteId, schoolId);
		for(PedibusGame game : list) {
			if(validateAuthorization(ownerId, instituteId, schoolId, null, game.getObjectId(),
				Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, user)) {
				result.add(game);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getPedibusGamesBySchool[%s]: %s", ownerId, result.size()));
		}
		return result;
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Itinerary, Const.AUTH_ACTION_ADD, request)) {
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Itinerary, Const.AUTH_ACTION_UPDATE, request)) {
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
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
	public @ResponseBody PedibusItineraryLeg createPedibusItineraryLeg(
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Leg, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		leg.setPedibusGameId(pedibusGameId);
		leg.setItineraryId(itineraryId);
		leg.setOwnerId(ownerId);
		storage.savePedibusItineraryLeg(leg, ownerId, false, game.isDeployed());
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createPedibusItineraryLeg[%s]: %s", ownerId, itineraryId));
		}
		return leg;
	}

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}", method = RequestMethod.PUT)
	public @ResponseBody PedibusItineraryLeg updatePedibusItineraryLeg(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@PathVariable String legId,
			@RequestBody PedibusItineraryLeg leg, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		PedibusItineraryLeg legDb = storage.getPedibusItineraryLeg(ownerId, legId);
		if(legDb == null) {
			throw new EntityNotFoundException("pedibus itinerary leg not found");
		}		
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Leg, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		leg.setPedibusGameId(pedibusGameId);
		leg.setItineraryId(itineraryId);
		leg.setOwnerId(ownerId);
		leg.setObjectId(legId);
		storage.savePedibusItineraryLeg(leg, ownerId, true, game.isDeployed());
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updatePedibusItineraryLeg[%s]: %s", ownerId, legId));
		}
		return leg;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}", method = RequestMethod.DELETE)
	public @ResponseBody List<PedibusItineraryLeg> deletePedibusItineraryLeg(
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
		PedibusItineraryLeg legDb = storage.getPedibusItineraryLeg(ownerId, legId);
		if(legDb == null) {
			throw new EntityNotFoundException("pedibus itinerary leg not found");
		}		
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Leg, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removePedibusItineraryLeg(ownerId, legId);
		List<PedibusItineraryLeg> legs = storage.getPedibusItineraryLegsByItineraryId(itineraryId);
		Collections.sort(legs);
		for(int position = 0; position < legs.size(); position++) {
			PedibusItineraryLeg leg = legs.get(position);
			leg.setPosition(position);
			storage.updateLegPosition(leg);
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("deletePedibusItineraryLeg[%s]: %s", ownerId, legId));
		}
		return legs;
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Leg, Const.AUTH_ACTION_ADD, request)) {
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
				storage.savePedibusItineraryLeg(leg, ownerId, false, game.isDeployed());
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Leg, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Collections.sort(legs);
		int sumValue = 0;
		try {
			storage.removePedibusItineraryLegByItineraryId(ownerId, pedibusGameId, itineraryId);
//			storage.removeMultimediaContentByItineraryId(ownerId, game.getInstituteId(), 
//					game.getSchoolId(), itineraryId);
			for (PedibusItineraryLeg leg: legs) {
				leg.setPedibusGameId(pedibusGameId);
				leg.setItineraryId(itineraryId);
				leg.setOwnerId(ownerId);
				if (sum != null && sum) {
					sumValue += leg.getScore();
					leg.setScore(sumValue);
				}
				storage.savePedibusItineraryLeg(leg, ownerId, false, game.isDeployed());
			}
			if (logger.isInfoEnabled()) {
				logger.info(String.format("updatePedibusItineraryLegs[%s]: %s", ownerId, itineraryId));
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
		return legs;
	}

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/legs/positions", method = RequestMethod.PUT)
	public @ResponseBody List<PedibusItineraryLeg> updatePedibusItineraryLegsPositions(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@RequestBody List<PedibusItineraryLeg> legs, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Leg, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Collections.sort(legs);
		for(int position = 0; position < legs.size(); position++) {
			PedibusItineraryLeg leg = legs.get(position);
			leg.setPosition(position);
			storage.updateLegPosition(leg);
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updatePedibusItineraryLegsPositions[%s]: %s", ownerId, itineraryId));
		}
		return legs;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}/content", 
			method = RequestMethod.POST)
	public @ResponseBody MultimediaContent addMultimediaContent(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@PathVariable String legId,
			@RequestBody MultimediaContent content, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Link, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		PedibusItineraryLeg leg = storage.getPedibusItineraryLeg(ownerId, legId);
		if(leg == null) {
			throw new EntityNotFoundException("pedibus itinerary leg not found");
		}
		Institute institute = storage.getInstitute(ownerId, game.getInstituteId());
		School school = storage.getSchool(ownerId, game.getInstituteId(), game.getSchoolId());
		PedibusItinerary itinerary = storage.getPedibusItinerary(ownerId, pedibusGameId, itineraryId);
		User user = getUserByEmail(request);
		ContentOwner contentOwner = new ContentOwner();
		contentOwner.setName(user.getName() + " " + user.getSurname());
		contentOwner.setUserId(user.getObjectId());
		
		content.setObjectId(null);
		content.setOwnerId(ownerId);
		content.setInstituteId(institute.getObjectId());
		content.setInstituteName(institute.getName());
		content.setSchoolId(school.getObjectId());
		content.setSchoolName(school.getName());
		content.setItineraryId(itineraryId);
		content.setItineraryName(itinerary.getName());
		content.setLegId(legId);
		content.setLegName(leg.getName());
		content.setGeocoding(leg.getGeocoding());
		content.setContentOwner(contentOwner);
		storage.saveMultimediaContent(content);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("addMultimediaContent[%s]: %s", ownerId, legId));
		}
		return content;
	}

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}/content/{contentId}", 
			method = RequestMethod.PUT)
	public @ResponseBody MultimediaContent updateMultimediaContent(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@PathVariable String legId,
			@PathVariable String contentId,
			@RequestBody MultimediaContent content, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Link, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		MultimediaContent contentDb = storage.getMultimediaContent(ownerId, contentId);
		if(contentDb == null) {
			throw new EntityNotFoundException("multimedia content not found");
		}	
		content.setObjectId(contentId);
		storage.saveMultimediaContent(content);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updateMultimediaContent[%s]: %s", ownerId, contentId));
		}
		return content;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}/content/positions", 
			method = RequestMethod.PUT)
	public @ResponseBody List<MultimediaContent> updateMultimediaContentPositions(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@PathVariable String legId,
			@RequestBody List<MultimediaContent> contents, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Link, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		for(int position = 0; position < contents.size(); position++) {
			MultimediaContent content = contents.get(position);
			content.setPosition(position);
			storage.updateMultimediaContentPosition(content);
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updateMultimediaContentPositions[%s]: %s", ownerId, legId));
		}
		return contents;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}/content/{contentId}", 
			method = RequestMethod.DELETE)
	public @ResponseBody MultimediaContent deleteMultimediaContent(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@PathVariable String legId,
			@PathVariable String contentId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Link, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		MultimediaContent contentDb = storage.getMultimediaContent(ownerId, contentId);
		if(contentDb == null) {
			throw new EntityNotFoundException("multimedia content not found");
		}
		storage.removeMultimediaContent(ownerId, contentId);
		List<MultimediaContent> list = storage.getMultimediaContentByLeg(ownerId, legId);
		int position = 0;
		for(MultimediaContent content : list) {
			content.setPosition(position);
			storage.updateMultimediaContentPosition(content);
			position++;
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("deleteMultimediaContent[%s]: %s", ownerId, contentId));
		}
		return contentDb;
	}

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}/file", 
			method = RequestMethod.POST)
	public @ResponseBody String uploadPedibusItineraryLegLinkFile(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@PathVariable String legId,
			@RequestParam(required = false) MultipartFile file, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		PedibusItineraryLeg leg = storage.getPedibusItineraryLeg(ownerId, legId);
		if(leg == null) {
			throw new EntityNotFoundException("pedibus itinerary leg not found");
		}		
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Link, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		String url = documentManager.uploadFile(file);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadPedibusItineraryLegLinkFile: %s - %s - %s", ownerId, legId, url));
		}
		return "{\"link\":\"" + url + "\"}";
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		try {
			List<PedibusItineraryLeg> result = storage.getPedibusItineraryLegsByGameId(ownerId, 
					pedibusGameId, itineraryId);
			for(PedibusItineraryLeg leg : result) {
				leg.setMultimediaContents(storage.getMultimediaContentNumberByLeg(ownerId, leg.getObjectId()));
			}
			if (logger.isInfoEnabled()) {
				logger.info(String.format("getPedibusItineraryLegs[%s]: %s", ownerId, result.size()));
			}
			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}/content", 
			method = RequestMethod.GET)
	public @ResponseBody List<MultimediaContent> getMultimediaContent(
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Link, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<MultimediaContent> result = storage.getMultimediaContentByLeg(ownerId, legId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getMultimediaContent[%s]: %s", ownerId, result.size()));
		}		
		return result;
	}
	
	@RequestMapping(value = "/api/game/multimedia", method = RequestMethod.GET)
	public @ResponseBody List<MultimediaResult> searchMultimediaContent(
			@RequestParam (required=false) String text,
			@RequestParam (required=false) Double lat,
			@RequestParam (required=false) Double lng,
			@RequestParam (required=false) Double distance,
			@RequestParam (required=false) List<String> types,
			@RequestParam (required=false) List<String> subjects,
			@RequestParam (required=false) List<String> schoolYears,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		List<MultimediaResult> result = new ArrayList<MultimediaResult>();
		List<MultimediaContent> list = storage.searchMultimediaContent(text, lat, lng, distance, 
				types, subjects, schoolYears);
		Map<String, MultimediaContent> contentMap = new HashMap<>();
		Map<String, List<MultimediaContent>> contentMapRef = new HashMap<>();
		for(MultimediaContent content : list) {
			if(!contentMap.containsKey(content.getObjectId())) {
				contentMap.put(content.getObjectId(), content);
			}
			if(content.getContentReferenceId() != null) {
				if(!contentMapRef.containsKey(content.getContentReferenceId())) {
					contentMapRef.put(content.getContentReferenceId(), 
							new ArrayList<MultimediaContent>());
				}
				contentMapRef.get(content.getContentReferenceId()).add(content);
			} else {
				if(!contentMapRef.containsKey(content.getObjectId())) {
					contentMapRef.put(content.getObjectId(), 
							new ArrayList<MultimediaContent>());
				}
			}
		}
		for(String contentId : contentMapRef.keySet()) {
			MultimediaContent contentRef = contentMap.get(contentId);
			if(contentRef == null) {
				contentRef = storage.getMultimediaContent(contentId);
			}
			//union of subjects and schoolYears
			for(MultimediaContent content : contentMapRef.get(contentId)) {
				for(String subject : content.getSubjects()) {
					if(!contentRef.getSubjects().contains(subject)) {
						contentRef.getSubjects().add(subject);
					}
				}
				for(String schoolYear : content.getSchoolYears()) {
					if(!contentRef.getSchoolYears().contains(schoolYear)) {
						contentRef.getSchoolYears().add(schoolYear);
					}
				}
			}
			MultimediaResult multimediaResult = new MultimediaResult();
			multimediaResult.setReferenceContent(contentRef);
			multimediaResult.setContents(contentMapRef.get(contentId));
			result.add(multimediaResult);
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchMultimediaContent:%s", result.size()));
		}
		return result;
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
			
			// teams score
			List<PedibusTeam> teams = new ArrayList<>();
			List<String> classes = game.getClassRooms();
			classes.add(game.getGlobalTeam());
			for (String classRoom : classes) {
				PedibusTeam team = new PedibusTeam();
				team.setClassRoom(classRoom);
				team.setGameId(game.getGameId());
				team.setPedibusGameId(game.getObjectId());
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
				teams.add(team);
			}

			Map<String, Object> result = Maps.newTreeMap();
			result.put("game", game);
			result.put("itinerary", itinerary);
			result.put("legs", legs);
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

	@RequestMapping(value = "/api/game/player/score/{ownerId}/{pedibusGameId}/{playerId}", method = RequestMethod.GET)
	public @ResponseBody void increasePlayerScore(
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
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		ExecutionDataDTO ed = new ExecutionDataDTO();
		ed.setGameId(game.getGameId());
		ed.setPlayerId(playerId);
		ed.setActionId(actionPedibus);

		Map<String, Object> data = Maps.newTreeMap();
		data.put(paramDistance, score);
		ed.setData(data);
		
		gengineUtils.executeAction(ed);
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("increasePlayerScore[%s]: increased game[%s] player[%s] score[%s]", ownerId,
					game.getGameId(), playerId, score));
		}			
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/reset", method = RequestMethod.GET)
	public @ResponseBody PedibusGame resetGame(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			HttpServletRequest request,	
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Date now = new Date();
		if(now.after(game.getFrom()) && now.before(game.getTo())) {
			//TODO for testing only
//			throw new StorageException("game is in progress");
		}
		
		if(Utils.isNotEmpty(game.getGameId())) {
			try {
				for(String classRoom : game.getClassRooms()) {
					gengineUtils.deletePlayerState(game.getGameId(), classRoom);
				}
				
				gengineUtils.deleteChallenges(game.getGameId());
				
				gengineUtils.deleteRules(game.getGameId());
				
				gengineUtils.deleteGame(game.getGameId());
			} catch (Exception e) {
				throw e;
			}
		}
		
		storage.removeExcursionByGameId(ownerId, pedibusGameId);
		
		storage.removeCalendarDayByGameId(ownerId, pedibusGameId);
		
		storage.updatePedibusGameGameId(ownerId, pedibusGameId, null);
		
		storage.updatePedibusGameDeployed(ownerId, pedibusGameId, false);
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("resetGame[%s]: %s", ownerId, pedibusGameId));
		}	
		game = storage.getPedibusGame(ownerId, pedibusGameId);
		return game;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{instituteId}/{schoolId}/{pedibusGameId}/clone/{itineraryId}", 
			method = RequestMethod.GET)
	public @ResponseBody PedibusItinerary cloneItinerary(			
			@PathVariable String ownerId, 
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Itinerary, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		PedibusGame game = storage.getPedibusGame(pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		long count = storage.getPedibusItineraryNumByGameId(ownerId, pedibusGameId);
		if(count > 0) {
			throw new InvalidParametersException("game itinerary already exists");
		}
		PedibusItinerary itineraryToClone = storage.getPedibusItinerary(itineraryId);
		if(itineraryToClone == null) {
			throw new EntityNotFoundException("itinerary to clone not found");
		}
		Institute institute = storage.getInstitute(ownerId, game.getInstituteId());
		School school = storage.getSchool(ownerId, game.getInstituteId(), game.getSchoolId());
		User user = getUserByEmail(request);
		ContentOwner contentOwner = new ContentOwner();
		contentOwner.setName(user.getName() + " " + user.getSurname());
		contentOwner.setUserId(user.getObjectId());
		List<PedibusItineraryLeg> legsToClone = storage.getPedibusItineraryLegsByItineraryId(itineraryId);
		//create itinerary
		PedibusItinerary itinerary = new PedibusItinerary();
		itinerary.setOwnerId(ownerId);
		itinerary.setPedibusGameId(game.getObjectId());
		itinerary.setName(itineraryToClone.getName() + " - clone");
		itinerary.setDescription(itineraryToClone.getDescription());
		storage.savePedibusItinerary(itinerary);
		//create legs
		for(PedibusItineraryLeg legToClone : legsToClone) {
			PedibusItineraryLeg leg = new PedibusItineraryLeg();
			leg.setOwnerId(ownerId);
			leg.setPedibusGameId(game.getObjectId());
			leg.setItineraryId(itinerary.getObjectId());
			leg.setName(legToClone.getName());
			leg.setDescription(legToClone.getDescription());
			leg.setPosition(legToClone.getPosition());
			leg.setGeocoding(legToClone.getGeocoding());
			leg.setImageUrl(legToClone.getImageUrl());
			leg.setPolyline(legToClone.getPolyline());
			leg.setScore(legToClone.getScore());
			leg.setTransport(legToClone.getTransport());
			leg.setIcon(legToClone.getIcon());
			leg.setAdditionalPoints(legToClone.getAdditionalPoints());
			storage.savePedibusItineraryLeg(leg, ownerId, false, false);
			//clone multimedia content
			List<MultimediaContent> mcListToClone = storage.getMultimediaContentByLeg(
					legToClone.getOwnerId(), legToClone.getObjectId());
			for(MultimediaContent mcToClone : mcListToClone) {
				if(mcToClone.isDisabled()) {
					continue;
				}
				MultimediaContent content = new MultimediaContent();
				content.setOwnerId(ownerId);
				content.setInstituteId(instituteId);
				content.setInstituteName(institute.getName());
				content.setSchoolId(schoolId);
				content.setSchoolName(school.getName());
				content.setItineraryId(itineraryId);
				content.setItineraryName(itinerary.getName());
				content.setLegId(leg.getObjectId());
				content.setLegName(leg.getName());
				content.setName(mcToClone.getName());
				content.setType(mcToClone.getType());
				content.setLink(mcToClone.getLink());
				content.setGeocoding(mcToClone.getGeocoding());
				content.setClasses(game.getClassRooms());
				content.setSubjects(mcToClone.getSubjects());
				content.setSchoolYears(mcToClone.getSchoolYears());
				content.setPreviewUrl(mcToClone.getPreviewUrl());
				content.setPosition(mcToClone.getPosition());
				content.setContentOwner(contentOwner);
				content.setSharable(mcToClone.isSharable());
				content.setPublicLink(mcToClone.isPublicLink());
				if(Utils.isNotEmpty(mcToClone.getContentReferenceId())) {
					content.setContentReferenceId(mcToClone.getContentReferenceId());
				} else {
					content.setContentReferenceId(mcToClone.getObjectId());
				}
				storage.saveMultimediaContent(content);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("cloneItinerary[%s]: %s - %s", ownerId, pedibusGameId, itineraryId));
		}
		return itinerary;
	}
	
	@RequestMapping(value = "/api/game/{shortName}", method = RequestMethod.GET)
	public void redirectShortName(
			@PathVariable String shortName,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		List<PedibusGame> games = storage.getPedibusGamesByShortName(shortName);
		if(games.size() != 1) {
			throw new EntityNotFoundException("short name is not unique or not present");
		}
		PedibusGame game = games.get(0);
		StringBuilder redirectUrl = new StringBuilder(request.getContextPath());
		List<PedibusItinerary> itineraryList = storage.getPedibusItineraryByGameId(game.getOwnerId(), game.getObjectId());
		if(itineraryList.size() == 0) {
			throw new EntityNotFoundException("game has no itinerary");
		}
		redirectUrl.append("/game-public/index.html#/" + game.getOwnerId());
		redirectUrl.append("/" + game.getObjectId());
		redirectUrl.append("/" + itineraryList.get(0).getObjectId());
		redirectUrl.append("/map");
		response.sendRedirect(redirectUrl.toString());
	}
	
	/**
	@RequestMapping(value = "/api/game/{ownerId}/{instituteId}/{schoolId}/player/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody List<PedibusPlayer> getPedibusPlayerByClassRoom(
			@PathVariable String ownerId, 
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			@PathVariable String classRoom,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, null, 
				null, Const.AUTH_RES_Player, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PedibusPlayer> result = storage.getPedibusPlayersByClassRoom(ownerId, instituteId, schoolId, classRoom);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPedibusPlayerByClassRoom[%s]: %s", ownerId, result.size()));
		}
		return result;
	}
	*/
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/player", method = RequestMethod.GET)
	public @ResponseBody List<PedibusPlayer> getPedibusPlayerByGame(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@RequestParam List<String> classes,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}				
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Player, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PedibusPlayer> result = storage.getPedibusPlayersByClassRooms(ownerId, pedibusGameId, classes);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPedibusPlayerByGame[%s]: %s", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/players", method = RequestMethod.POST)
	public @ResponseBody List<PedibusPlayer> savePedibusPlayers(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@RequestBody List<PedibusPlayer> players,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}				
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Player, Const.AUTH_ACTION_ADD, request) || 
				!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
						pedibusGameId, Const.AUTH_RES_PedibusGame_Player, Const.AUTH_ACTION_UPDATE, request) ||
				!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
						pedibusGameId, Const.AUTH_RES_PedibusGame_Player, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PedibusPlayer> existingPlayers = storage.getPedibusPlayers(ownerId, pedibusGameId);
		for(PedibusPlayer player : existingPlayers) {
			if(!Utils.containsId(player.getObjectId(), players)) {
				storage.removePedibusPlayer(ownerId, player.getObjectId());
			}
		}
		for(PedibusPlayer player : players) {
			player.setOwnerId(ownerId);
			player.setPedibusGameId(pedibusGameId);
			if(Utils.isNotEmpty(player.getObjectId())) {
				storage.savePedibusPlayer(player, true);
			} else {
				storage.savePedibusPlayer(player, false);
			}
		}
		existingPlayers = storage.getPedibusPlayers(ownerId, pedibusGameId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("savePedibusPlayers[%s]: %s - %s", ownerId, pedibusGameId, 
					existingPlayers.size()));
		}
		return existingPlayers;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/mctags", method = RequestMethod.GET)
	public @ResponseBody MultimediaContentTags getMultimediaContentTags(
			@PathVariable String ownerId,
			@PathVariable String pedibusGameId,
			HttpServletRequest request) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		MultimediaContentTags contentTags = storage.getMultimediaContentTags();
		contentTags.setClasses(game.getClassRooms());
//		School school = storage.getSchool(ownerId, game.getInstituteId(), game.getSchoolId());
//		if(school != null) {
//			for(ClassRoom classRoom : school.getClasses()) {
//				String schoolYear = classRoom.getSchoolYear();
//				if(Utils.isNotEmpty(schoolYear) && 
//						!contentTags.getSchoolYears().contains(schoolYear)) {
//					contentTags.getSchoolYears().add(schoolYear);
//				}
//			}
//		}
		return contentTags;
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
	
	@ExceptionHandler({EntityNotFoundException.class, InvalidParametersException.class})
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
