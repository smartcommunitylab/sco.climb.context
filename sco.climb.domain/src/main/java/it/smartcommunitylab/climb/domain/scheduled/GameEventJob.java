package it.smartcommunitylab.climb.domain.scheduled;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

public class GameEventJob implements Job {
	private static final transient Logger logger = LoggerFactory.getLogger(GameEventJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			SchedulerContext schedulerContext = context.getScheduler().getContext();
			String pedibusGameId = schedulerContext.getString("pedibusGameId");
			RepositoryManager storage = (RepositoryManager) schedulerContext.get("RepositoryManager");
			EventsPoller eventsPoller = (EventsPoller) schedulerContext.get("EventsPoller");
			PedibusGame game = storage.getPedibusGame(pedibusGameId);
			logger.info("GameEventJob.execute:{}/{}/{}", pedibusGameId, eventsPoller, game);
			if((game != null) && game.isDeployed() && game.isUsingPedibusData()) {
				Date now = new Date();
				if(game.getFrom().before(now) && game.getTo().after(now)) {
					eventsPoller.pollEvents(pedibusGameId, true);
				}
			}
		} catch (Exception e) {
			logger.warn("execute error:" + e.getMessage());
		}
	}

}
