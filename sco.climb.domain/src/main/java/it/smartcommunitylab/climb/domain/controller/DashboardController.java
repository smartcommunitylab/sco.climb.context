package it.smartcommunitylab.climb.domain.controller;

import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.GEngineUtils;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.model.CalendarDay;
import it.smartcommunitylab.climb.domain.model.Excursion;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.model.PedibusPlayer;
import it.smartcommunitylab.climb.domain.model.PedibusTeam;
import it.smartcommunitylab.climb.domain.model.Stats;
import it.smartcommunitylab.climb.domain.model.gamification.Challenge;
import it.smartcommunitylab.climb.domain.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.domain.model.gamification.Notification;
import it.smartcommunitylab.climb.domain.model.gamification.PlayerStateDTO;
import it.smartcommunitylab.climb.domain.model.gamification.PointConcept;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

import com.google.common.collect.Maps;

@Controller
public class DashboardController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(DashboardController.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private GEngineUtils gengineUtils;

	@Autowired
	@Value("${action.calendar}")	
	private String actionCalendar;	

	@Autowired
	@Value("${action.trip}")	
	private String actionTrip;
	
	@Autowired
	@Value("${param.date}")	
	private String paramDate;	
	
	@Autowired
	@Value("${param.meteo}")	
	private String paramMeteo;
	
	@Autowired
	@Value("${param.mode}")	
	private String paramMode;
	
	@Autowired
	@Value("${param.participants}")	
	private String paramParticipants;
	
	@Autowired
	@Value("${param.class.distance}")	
	private String paramClassDistance;
	
	@RequestMapping(value = "/api/game/player/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.GET)
	public @ResponseBody List<PedibusPlayer> getPlayersByClassRoom(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PedibusPlayer> players = storage.getPedibusPlayersByClassRoom(ownerId, pedibusGameId, classRoom);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPlayersByClassRoom[%s]: %s - %s", ownerId, 
					pedibusGameId, players.size()));
		}
		return players; 
	}
	
	@RequestMapping(value = "/api/game/team/{ownerId}/{pedibusGameId}", method = RequestMethod.GET)
	public @ResponseBody List<PedibusTeam> getTeams(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PedibusTeam> teams = storage.getPedibusTeams(ownerId, pedibusGameId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getTeams[%s]: %s - %s", ownerId, pedibusGameId, teams.size()));
		}
		return teams; 
	}
	
	@RequestMapping(value = "/api/game/calendar/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.POST)
	public @ResponseBody Boolean saveCalendarDay(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom,
			@RequestBody CalendarDay calendarDay,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Map<String, Boolean> result = storage.saveCalendarDay(ownerId, pedibusGameId, 
				classRoom, calendarDay);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveCalendarDay[%s]: %s - %s - %s", ownerId, pedibusGameId, 
					classRoom, result.toString()));
		}
		if(!result.get(Const.CLOSED)) {
			for(String childId : calendarDay.getModeMap().keySet()) {
				ExecutionDataDTO ed = new ExecutionDataDTO();
				ed.setGameId(game.getGameId());
				ed.setPlayerId(childId);
				ed.setActionId(actionCalendar);
				
				Map<String, Object> data = Maps.newTreeMap();
				data.put(paramMode, calendarDay.getModeMap().get(childId));
				data.put(paramDate, System.currentTimeMillis());
				data.put(paramMeteo, calendarDay.getMeteo());
				ed.setData(data);
				
				try {
					gengineUtils.executeAction(ed);
				} catch (Exception e) {
					logger.warn(String.format("saveCalendarDay[%s]: error in GE excecute action %s - %s",
							ownerId, game.getGameId(), classRoom));
				}
			}			
		}
		return result.get(Const.MERGED);
	}
	
	@RequestMapping(value = "/api/game/calendar/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.GET)
	public @ResponseBody List<CalendarDay> getCalendarDays(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom,
			@RequestParam Long from, 
			@RequestParam Long to, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Date dateFrom = new Date(from);
		Date dateTo = new Date(to);
		List<CalendarDay> result = storage.getCalendarDays(ownerId, pedibusGameId, classRoom, 
				dateFrom, dateTo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCalendarDays[%s]: %s - %s - %s", ownerId, pedibusGameId, 
					classRoom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/excursion/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.POST)
	public @ResponseBody void saveExcursion(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom,
			@RequestParam String name, 
			@RequestParam String meteo, 
			@RequestParam Long date, 
			@RequestParam Integer children, 
			@RequestParam Double distance, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Date day = new Date(date);
		storage.saveExcursion(ownerId, pedibusGameId, classRoom, name, children, 
				distance, day, meteo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveExcursion[%s]: %s - %s - %s - %s", ownerId, 
					pedibusGameId, classRoom, children, distance));
		}
		ExecutionDataDTO ed = new ExecutionDataDTO();
		ed.setGameId(game.getGameId());
		ed.setPlayerId(classRoom);
		ed.setActionId(actionTrip);
		
		Map<String, Object> data = Maps.newTreeMap();
		data.put(paramParticipants, Double.valueOf(children.toString()));
		data.put(paramClassDistance, distance);
		data.put(paramDate, date);
		data.put(paramMeteo, meteo);
		ed.setData(data);
		
		try {
			gengineUtils.executeAction(ed);
		} catch (Exception e) {
			logger.warn(String.format("saveExcursion[%s]: error in GE excecute action %s - %s",
					ownerId, game.getGameId(), classRoom));
		}
	}	
	
	@RequestMapping(value = "/api/game/excursion/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.GET)
	public @ResponseBody List<Excursion> getExcursions(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom,
			@RequestParam Long from, 
			@RequestParam Long to,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Date dateFrom = new Date(from);
		Date dateTo = new Date(to);
		List<Excursion> result = storage.getExcursions(ownerId, pedibusGameId, classRoom, 
				dateFrom, dateTo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getExcursions[%s]: %s - %s - %s", ownerId, pedibusGameId, 
					classRoom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/notification/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.GET)
	public @ResponseBody List<Notification> getNotifications(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom, 
			@RequestParam Long timestamp,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Notification> result = new ArrayList<Notification>();
		if(game != null) {
			List<Notification> classNotifications = gengineUtils.getNotification(game.getGameId(), 
					classRoom, timestamp);
			List<Notification> schoolNotifications = gengineUtils.getNotification(game.getGameId(), 
					game.getGlobalTeam(), timestamp);
			result.addAll(classNotifications);
			result.addAll(schoolNotifications);
			Collections.sort(result, new Comparator<Notification>() {
				@Override
				public int compare(Notification o1, Notification o2) {
					if(o1.getTimestamp() > o2.getTimestamp()) {
						return -1;
					} else if(o1.getTimestamp() < o2.getTimestamp()) {
						return 1;
					} else { 
						return 0; 
					}
				}
			});
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getNotifications[%s]: %s - %s - %s", ownerId, pedibusGameId, 
					classRoom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/challenge/{ownerId}/{pedibusGameId}/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody List<Challenge> getChallenge(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Challenge> result = new ArrayList<Challenge>();
		if(game != null) {
			PlayerStateDTO playerStatus = gengineUtils.getPlayerStatus(game.getGameId(), classRoom);
			Challenge classChallenge = new Challenge();
			classChallenge.setGameId(game.getGameId());
			classChallenge.setPlayerId(classRoom);
			classChallenge.setState(playerStatus.getState().get(GEngineUtils.challengeConcept));
			result.add(classChallenge);
			
			playerStatus = gengineUtils.getPlayerStatus(game.getGameId(), game.getGlobalTeam());
			Challenge schoolChallenge = new Challenge();
			schoolChallenge.setGameId(game.getGameId());
			schoolChallenge.setPlayerId(game.getGlobalTeam());
			schoolChallenge.setState(playerStatus.getState().get(GEngineUtils.challengeConcept));
			result.add(schoolChallenge);
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getChallenge[%s]: %s - %s - %s", ownerId, pedibusGameId, 
					classRoom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/stat/{ownerId}/{pedibusGameId}", method = RequestMethod.GET)
	public @ResponseBody Stats getStats(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,  
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Stats result = new Stats();
		if(game != null) {
			PlayerStateDTO playerStatus = gengineUtils.getPlayerStatus(game.getGameId(), game.getGlobalTeam());
			PointConcept pointConcept = gengineUtils.getPointConcept(playerStatus, env.getProperty("score.name"));
			if(pointConcept != null) {
				result.setGameScore(pointConcept.getScore());
			}
			result.setMaxGameScore(Double.valueOf(env.getProperty("score.final")));
			
			String key = env.getProperty("stat." + Const.MODE_PIEDI_SOLO);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_PIEDI_SOLO, pointConcept.getScore());
			}
			
			key = env.getProperty("stat." + Const.MODE_PIEDI_ADULTO);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_PIEDI_ADULTO, pointConcept.getScore());
			}
			key = env.getProperty("stat." + Const.MODE_PEDIBUS);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				Double score = result.getScoreModeMap().get(Const.MODE_PIEDI_ADULTO);
				if(score != null) {
					score = score + pointConcept.getScore();
				} else {
					score = pointConcept.getScore();
				}
				result.getScoreModeMap().put(Const.MODE_PIEDI_ADULTO, score);
			}
			
			key = env.getProperty("stat." + Const.MODE_SCUOLABUS);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_SCUOLABUS, pointConcept.getScore());
			}
			
			key = env.getProperty("stat." + Const.MODE_PARK_RIDE);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_PARK_RIDE, pointConcept.getScore());
			}
			
			key = env.getProperty("stat." + Const.MODE_AUTO);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_AUTO, pointConcept.getScore());
			}
			
			key = env.getProperty("stat." + Const.MODE_BONUS);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_BONUS, pointConcept.getScore());
			}
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStats[%s]: %s", ownerId, pedibusGameId));
		}
		return result;
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