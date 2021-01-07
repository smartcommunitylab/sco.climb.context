package it.smartcommunitylab.climb.domain.scheduled;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TimeOfDay;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.InvalidParametersException;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Component
public class SchedulerManager {
	private static final transient Logger logger = LoggerFactory.getLogger(SchedulerManager.class);
	
	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private EventsPoller eventsPoller;

	private List<String> jobList = new ArrayList<>();
	
	private Scheduler scheduler;
	
	@PostConstruct
	public void init() throws Exception {
		// init scheduler
		Properties properties = new Properties();
		properties.setProperty("org.quartz.scheduler.instanceName", "ClimbScheduler");
		properties.setProperty("org.quartz.threadPool.threadCount", "125");
		SchedulerFactory schedFact = new StdSchedulerFactory(properties);
	  scheduler = schedFact.getScheduler();
	  scheduler.start();
	  
		// set task for specific game
		List<PedibusGame> games = storage.getPedibusGames();
		for (PedibusGame game : games) {
			try {
				addJob(game);
			} catch (Exception e) {
				logger.warn(String.format("initJob[%s] error:%s", game.getObjectId(), e.getMessage()));
			}			
		}
	}

	private void addJob(PedibusGame game) throws InvalidParametersException {
		if((game != null) && game.isDeployed() && game.isUsingPedibusData()) {
			try {
				Date now = new Date();
				if(game.getTo().after(now)) {
					if(Utils.isNotEmpty(game.getFromHour()) 
							&& Utils.isNotEmpty(game.getToHour())
							&& (game.getInterval() > 0)) {
						String[] fromH = game.getFromHour().split(":");
						String[] toH = game.getToHour().split(":");
						Trigger trigger = TriggerBuilder.newTrigger()
								.withIdentity("gameId-" + game.getObjectId())
								.withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
									.startingDailyAt(new TimeOfDay(Integer.parseInt(fromH[0]), Integer.parseInt(fromH[1])))
									.endingDailyAt(new TimeOfDay(Integer.parseInt(toH[0]), Integer.parseInt(toH[1])))
									.withIntervalInMinutes(game.getInterval()))
								.build();
						JobDetail job = JobBuilder.newJob(GameEventJob.class)
								.withIdentity("gameId-" + game.getObjectId()).build();
						scheduler.getContext().put("RepositoryManager", storage);
						scheduler.getContext().put("EventsPoller", eventsPoller);
						scheduler.getContext().put("pedibusGameId", game.getObjectId());
						scheduler.scheduleJob(job, trigger);
						jobList.add(game.getObjectId());
						logger.info(String.format("addJob:%s", game.getObjectId()));
					}
				}
			} catch (Exception e) {
				logger.warn(String.format("addJob[%s] error:%s", game.getObjectId(), e.getMessage()));
				throw new InvalidParametersException(e.getMessage());
			}
		}
	}
	
	public void resetJob(String pedibusGameId) throws InvalidParametersException {
		removeJob(pedibusGameId);
		PedibusGame game = storage.getPedibusGame(pedibusGameId);
		addJob(game);
	}
	
	public void removeJob(String pedibusGameId) {
		if(jobList.contains(pedibusGameId)) {
			try {
				TriggerKey triggerKey = new TriggerKey("gameId-" +  pedibusGameId);
				scheduler.unscheduleJob(triggerKey);
				JobKey jobKey = new JobKey("gameId-" +  pedibusGameId);
				scheduler.deleteJob(jobKey);
				jobList.remove(pedibusGameId);
			} catch (SchedulerException e) {
				logger.warn(String.format("removeJob[%s] error:%s", pedibusGameId, e.getMessage()));
			}
		}
	}
}
