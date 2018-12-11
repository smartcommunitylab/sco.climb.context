package it.smartcommunitylab.climb.domain.scheduled;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private Map<String, JobKey> jobMaps = new HashMap<>();
	
	private Scheduler scheduler;
	
	@PostConstruct
	public void init() throws Exception {
		// init scheduler
		Properties properties = new Properties();
		properties.setProperty("org.quartz.scheduler.instanceName", "ClimbScheduler");
		properties.setProperty("org.quartz.threadPool.threadCount", "25");
		SchedulerFactory schedFact = new StdSchedulerFactory(properties);
	  scheduler = schedFact.getScheduler();
	  scheduler.start();
	  
		// set task for specific game
		List<PedibusGame> games = storage.getPedibusGames();
		for (PedibusGame game : games) {
			addJob(game);
		}
	}

	private void addJob(PedibusGame game) throws InvalidParametersException {
		if((game != null) && game.isDeployed() && game.isUsingPedibusData()) {
			try {
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
					job.getJobDataMap().put("RepositoryManager", storage);
					job.getJobDataMap().put("EventsPoller", eventsPoller);
					job.getJobDataMap().put("pedibusGameId", game.getObjectId());
					scheduler.scheduleJob(job, trigger);
					jobMaps.put(game.getObjectId(), job.getKey());
				}
			} catch (Exception e) {
				throw new InvalidParametersException(e.getMessage());
			}
		}
	}
	
	public void resetJob(String pedibusGameId) throws InvalidParametersException {
		removeJob(pedibusGameId);
		PedibusGame game = storage.getPedibusGame(pedibusGameId);
		addJob(game);
	}
	
	private void removeJob(String pedibusGameId) {
		JobKey jobKey = jobMaps.get(pedibusGameId);
		if(jobKey != null) {
			try {
				scheduler.deleteJob(jobKey);
				jobMaps.remove(pedibusGameId);
			} catch (SchedulerException e) {
				logger.warn(String.format("removeTask[%s]:%s", pedibusGameId, e.getMessage()));
			}
		}
	}
}
