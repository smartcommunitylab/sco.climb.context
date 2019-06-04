package it.smartcommunitylab.climb.domain.scheduled;

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
					Map<String, Boolean> updateClassScores = updateCalendarDayFromPedibus(game, childrenStatus);
					sendScores(childrenStatus, updateClassScores, game);
					storage.updatePollingFlag(game.getOwnerId(), game.getObjectId(), routeId, Boolean.TRUE);
				}
			}
			results.putAll(childrenStatusMap);
			storage.updatePedibusGameLastDaySeen(game.getOwnerId(), game.getObjectId(), sdf.format(actualTime));
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Collection<ChildStatus>> pollGameEvents(PedibusGame game, 
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

						logger.info("pollGameEvents: Computing scores for route " + routeId);
						EventsProcessor ep = new EventsProcessor(stopsMap);
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
	
	public void sendScores(Collection<ChildStatus> childrenStatus, 
			Map<String, Boolean> updateClassScores, PedibusGame game) {
		String address = gamificationURL + "/gengine/execute";
		if(childrenStatus == null) { 
			return;
		}
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
				
				String playerId = childStatus.getChildId();
				Double score = childStatus.getScore();
						
				ExecutionDataDTO ed = new ExecutionDataDTO();
				ed.setGameId(game.getGameId());
				ed.setPlayerId(playerId);
				ed.setActionId(actionPedibus);
				
				Map<String, Object> data = Maps.newTreeMap();
				data.put(paramDistance, score);
				Date date = new Date();
				try {
					date = Utils.getStartOfTheDay(sdf.parse(game.getLastDaySeen()));
				} catch (ParseException e) {
					logger.warn("sendScores:" + e.getMessage());
					continue;
				}
				data.put(paramDate, date.getTime());
				ed.setData(data);
				
				try {
					if(logger.isInfoEnabled()) {
						logger.info(String.format("increased game[%s] player[%s] score[%s]", game.getGameId(), playerId, score));
					}
					HTTPUtils.post(address, ed, null, gamificationUser, gamificationPassword);
				} catch (Exception e) {
					logger.warn("sendScores error:" + e.getMessage());
					continue;
				}				
			}
		}
	}
	
	public Map<String, Boolean> updateCalendarDayFromPedibus(PedibusGame game, 
			Collection<ChildStatus> childrenStatus) {
		
		Map<String, Map<String, String>> classModeMap = new HashMap<String, Map<String,String>>();
		Map<String, Boolean> classUpdateScoreMap = new HashMap<String, Boolean>();
		
		if(childrenStatus == null) {
			return classUpdateScoreMap;
		}
		for(ChildStatus childStatus : childrenStatus) {
			if(childStatus.isArrived()) {
				//TODO new pedibus -> game interaction
//				PedibusPlayer player = storage.getPedibusPlayerByChildId(game.getOwnerId(), game.getObjectId(), 
//						childStatus.getChildId());
//				if(player != null) {
//					String classRoom = player.getClassRoom();
//					Map<String, String> modeMap = classModeMap.get(classRoom);
//					if(modeMap == null) {
//						modeMap = new HashMap<String, String>();
//						classModeMap.put(classRoom, modeMap);
//					}
//					modeMap.put(player.getChildId(), Const.MODE_PEDIBUS);
//				}
			}
		}
		
		if(game != null) {
			try {
				Date day = Utils.getStartOfTheDay(sdf.parse(game.getLastDaySeen()));
				for(String classRoom : classModeMap.keySet()) {
					Map<String, String> modeMap = classModeMap.get(classRoom);
					Boolean update = storage.updateCalendarDayFromPedibus(game.getOwnerId(), game.getObjectId(), 
							classRoom, day, modeMap);
					classUpdateScoreMap.put(classRoom, update);
				}
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		return classUpdateScoreMap;
	}
	
	public boolean isEmptyResponse(Collection<ChildStatus> childrenStatus) {
		boolean result = true;
		if((childrenStatus != null) && !childrenStatus.isEmpty()) {
			result = false;
		}
		return result;
	}	
}
