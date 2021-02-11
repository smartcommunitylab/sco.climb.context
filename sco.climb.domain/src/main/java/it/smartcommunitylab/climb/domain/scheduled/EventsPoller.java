package it.smartcommunitylab.climb.domain.scheduled;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.HTTPUtils;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.model.PedibusPlayer;
import it.smartcommunitylab.climb.domain.model.WsnEvent;
import it.smartcommunitylab.climb.domain.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Component
public class EventsPoller {

	@Autowired
	@Value("${gamification.url}")
	private String gamificationURL;			
	
	@Autowired
	@Value("${gamification.user}")
	private String gamificationUser;

	@Autowired
	@Value("${gamification.password}")
	private String gamificationPassword;
	
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
	private RepositoryManager storage;

	private static final transient Logger logger = LoggerFactory.getLogger(EventsPoller.class);
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//	private static final SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");

	
	@Scheduled(cron = "0 5,15 0 * * *") // second, minute, hour, day, month, weekday
	public void resetPollingFlag() {
		List<PedibusGame> games = storage.getPedibusGames();
		Calendar cal = new GregorianCalendar(TimeZone.getDefault());
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
		String date = sdf.format(cal.getTime());
		for (PedibusGame game : games) {		
			storage.resetPollingFlag(game.getOwnerId(), game.getObjectId());
			storage.updatePedibusGameLastDaySeen(game.getOwnerId(), game.getObjectId(), date);
		}
	}
	
	public Map<String, Collection<ChildStatus>> pollEvents(String pedibusGameId,
			boolean checkDate) throws Exception {
		Date actualTime = new Date();		
		Map<String, Collection<ChildStatus>> results = Maps.newTreeMap();
		PedibusGame game = storage.getPedibusGame(pedibusGameId);
		logger.info("Reading game " + game.getGameId() + " events.");
		if(!game.isUsingPedibusData()) {
			logger.info("Game " + game.getGameId() + " skip is not using pedibus data.");
		} else {
			try {
				sdf.parse(game.getLastDaySeen());
			} catch (Exception e) {
				logger.info("pollEvents: lastDaySeen not well formed for game " + game.getObjectId());
				game.setLastDaySeen(sdf.format(Utils.getStartOfTheDay(actualTime)));
			}			
			Map<String, Collection<ChildStatus>> childrenStatusMap = pollGameEvents(game, actualTime, checkDate);
			for(String routeId : childrenStatusMap.keySet()) {
				Collection<ChildStatus> childrenStatus = childrenStatusMap.get(routeId); 
				if(!isEmptyResponse(childrenStatus)) {
					Map<String, Boolean> updateClassScores = updateCalendarDayFromPedibus(game, routeId, childrenStatus);
					if(!game.isUseCalendar()) {
						sendScores(childrenStatus, updateClassScores, game);
					}
					storage.updatePollingFlag(game.getOwnerId(), game.getObjectId(), routeId, Boolean.TRUE);
				}
			}
			results.putAll(childrenStatusMap);
			storage.updatePedibusGameLastDaySeen(game.getOwnerId(), game.getObjectId(), sdf.format(actualTime));
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Collection<ChildStatus>> pollGameEvents(PedibusGame game, 
			Date actualTime, boolean checkDate) {
		Map<String, Collection<ChildStatus>> results = Maps.newTreeMap();
		if(game != null) {
			Date date = new Date();
			if(checkDate) {
				if(game.getFrom().compareTo(date) > 0 || game.getTo().compareTo(date) < 0) {
					logger.info("pollGameEvents: Skipping game " + game.getGameId() + ", date out of range.");
					return results;
				}
			}
			try {
				Criteria criteria = Criteria.where("instituteId").is(game.getInstituteId())
						.and("schoolId").is(game.getSchoolId());
				List<Route> routesList = (List<Route>) storage.findData(Route.class, criteria, 
						null, game.getOwnerId());

				Calendar cal = new GregorianCalendar(TimeZone.getDefault());

				if (game.getLastDaySeen() != null) {
					try {
						cal.setTime(sdf.parse(game.getLastDaySeen()));
					} catch (ParseException e) {
						logger.info("pollGameEvents: lastDaySeen not well formed for game " + game.getObjectId());
					}
				} 
				Date dateFrom = cal.getTime();
				
				cal.setTime(actualTime);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.MILLISECOND, 0);
				Date dateTo = cal.getTime();

				for (Route route : routesList) {
					String routeId = route.getObjectId();
					logger.info("pollGameEvents: Reading route " + routeId + " events.");
					
					Boolean pollingFlag = game.getPollingFlagMap().get(routeId);
					if((pollingFlag != null) && (pollingFlag == Boolean.FALSE)) {
						logger.info("pollGameEvents: Events already managed for route " + routeId);
						continue;
					}
					
					List<Integer> eventTypeList = Lists.newArrayList();
					List<String> nodeIdList = Lists.newArrayList();
					List<WsnEvent> eventsList = storage.searchEventsByLastUpdate(game.getOwnerId(), 
							routeId, dateFrom, dateTo, eventTypeList, nodeIdList);
					
					//remove events that are not in this day
					Date startOfTheDay = Utils.getStartOfTheDay(actualTime);
					Date endOfTheDay = Utils.getEndOfTheDay(actualTime);
					List<WsnEvent> eventsListCleaned = new ArrayList<>();
					for(WsnEvent event : eventsList) {
						if(event.getTimestamp().before(startOfTheDay) 
								|| event.getTimestamp().after(endOfTheDay)) {
							continue;
						}	
						eventsListCleaned.add(event);
					}
					if (!eventsListCleaned.isEmpty()) {
						Criteria routeCriteria = Criteria.where("routeId").is(routeId);
						Sort sort = new Sort(Sort.Direction.ASC, "position");
						List<Stop> stopList = (List<Stop>) storage.findData(Stop.class, routeCriteria, sort, 
								game.getOwnerId());

						Map<String, Stop> stopsMap = Maps.newTreeMap();
						for (Stop stop : stopList) {
							stopsMap.put(stop.getObjectId(), stop);
						}
						
						Criteria childCriteria = Criteria.where("instituteId").is(game.getInstituteId())
								.and("schoolId").is(game.getSchoolId());
						List<Child> childenList = (List<Child>) storage.findData(Child.class, childCriteria, 
								null, game.getOwnerId());
						Map<String, Child> childrenMap = Maps.newTreeMap();
						for(Child child : childenList) {
							childrenMap.put(child.getObjectId(), child);
						}
						
						logger.info("pollGameEvents: Computing scores for route " + routeId);
						EventsProcessor ep = new EventsProcessor(stopsMap, childrenMap);
						Collection<ChildStatus> status = ep.process(eventsListCleaned);

						results.put(routeId, status);
						logger.info("pollGameEvents: Computed scores for route " + routeId + " = " + status);
					} else {
						results.put(routeId, null);
						logger.info("pollGameEvents: No recent events for route " + routeId);
					}
				}					
			} catch (Exception e) {
				logger.warn(String.format("pollGameEvents[%s]:%s", game.getGameId(), e.getMessage()));
			}
		}
		return results;
	}
	
	private void sendScores(Collection<ChildStatus> childrenStatus, 
			Map<String, Boolean> updateClassScores, PedibusGame game) {
		String address = gamificationURL + "/gengine/execute";
		if(childrenStatus == null) { 
			return;
		}
		// <class, total distance>
		Map<String, Double> actionParamsDistance = new HashMap<String, Double>();
		// <class, total number>
		Map<String, Integer> actionParamsNumber = new HashMap<String, Integer>();
		for (ChildStatus childStatus: childrenStatus) {
			if(childStatus.isArrived()) {
				//check if is a right classroom
				Criteria childCriteria = Criteria.where("objectId").is(childStatus.getChildId());
				try {
					Child child = storage.findOneData(Child.class, childCriteria, game.getOwnerId());
					if(!game.getClassRooms().contains(child.getClassRoom())) {
						continue;
					}
					Boolean updateClassScore = updateClassScores.get(child.getClassRoom());
					if((updateClassScore == null) || (!updateClassScore)) {
						continue;
					}
				} catch (ClassNotFoundException e) {
					logger.warn("sendScores:" + e.getMessage());
					continue;
				}
				PedibusPlayer player = storage.getPedibusPlayer(game.getOwnerId(), game.getObjectId(),
						childStatus.getNickname(), childStatus.getClassRoom());
				if(player == null) {
					continue;
				}
				String playerId = childStatus.getClassRoom();
				Double score = childStatus.getScore();
				if(actionParamsDistance.containsKey(playerId)) {
					actionParamsDistance.put(playerId, actionParamsDistance.get(playerId) + score);
					actionParamsNumber.put(playerId, actionParamsNumber.get(playerId) + 1);
				} else {
					actionParamsDistance.put(playerId, score);
					actionParamsNumber.put(playerId, 1);
				}
			}
		}
		Date date = new Date();
		try {
			date = Utils.getStartOfTheDay(sdf.parse(game.getLastDaySeen()));
		} catch (ParseException e) {
			logger.warn("sendScores:" + e.getMessage());
			return;
		}		
		for(String playerId : actionParamsDistance.keySet()) {
			ExecutionDataDTO ed = new ExecutionDataDTO();
			ed.setGameId(game.getGameId());
			ed.setPlayerId(playerId);
			ed.setActionId(actionPedibus);
			ed.setExecutionMoment(date);
			
			Map<String, Object> data = Maps.newTreeMap();
			data.put(paramDistance, actionParamsDistance.get(playerId));
			data.put("kids", actionParamsNumber.get(playerId));
			ed.setData(data);
			
			try {
				if(logger.isInfoEnabled()) {
					logger.info(String.format("increased game[%s] player[%s] score[%s]", game.getGameId(), 
							playerId, actionParamsDistance.get(playerId)));
				}
				HTTPUtils.post(address, ed, null, gamificationUser, gamificationPassword);
			} catch (Exception e) {
				logger.warn("sendScores error:" + e.getMessage());
				continue;
			}									
		}		
	}
	
	private Map<String, Boolean> updateCalendarDayFromPedibus(PedibusGame game, 
			String routeId, Collection<ChildStatus> childrenStatus) {
		
		Map<String, Map<String, String>> classModeMap = new HashMap<String, Map<String,String>>();
		Map<String, Boolean> classUpdateScoreMap = new HashMap<String, Boolean>();
		
		if(childrenStatus == null) {
			return classUpdateScoreMap;
		}
		for(ChildStatus childStatus : childrenStatus) {
			if(childStatus.isArrived() && Utils.isNotEmpty(childStatus.getNickname()) 
					&& Utils.isNotEmpty(childStatus.getClassRoom())) {
				PedibusPlayer player = storage.getPedibusPlayer(game.getOwnerId(), game.getObjectId(),
						childStatus.getNickname(), childStatus.getClassRoom());
				if(player != null) {
					Map<String, String> modeMap = classModeMap.get(player.getClassRoom());
					if(modeMap == null) {
						modeMap = new HashMap<String, String>();
						classModeMap.put(player.getClassRoom(), modeMap);
					}
					modeMap.put(player.getObjectId(), Const.MODE_PEDIBUS);
				}
			}
		}
		
		if(game != null) {
			try {
				Date day = Utils.getStartOfTheDay(sdf.parse(game.getLastDaySeen()));
				for(String classRoom : classModeMap.keySet()) {
					Map<String, String> modeMap = classModeMap.get(classRoom);
					Boolean update = storage.updateCalendarDayFromPedibus(game.getOwnerId(), game.getObjectId(), 
							routeId, classRoom, day, modeMap);
					classUpdateScoreMap.put(classRoom, update);
				}
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		return classUpdateScoreMap;
	}
	
	private boolean isEmptyResponse(Collection<ChildStatus> childrenStatus) {
		boolean result = true;
		if((childrenStatus != null) && !childrenStatus.isEmpty()) {
			result = false;
		}
		return result;
	}	
}
