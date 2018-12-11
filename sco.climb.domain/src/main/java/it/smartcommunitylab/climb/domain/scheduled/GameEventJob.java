package it.smartcommunitylab.climb.domain.scheduled;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

public class GameEventJob implements Job {
	private static final transient Logger logger = LoggerFactory.getLogger(GameEventJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			String pedibusGameId = data.getString("pedibusGameId");
			RepositoryManager storage = (RepositoryManager) data.get("RepositoryManager");
			EventsPoller eventsPoller = (EventsPoller) data.get("EventsPoller");
			PedibusGame game = storage.getPedibusGame(pedibusGameId);
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
