package it.smartcommunitylab.climb.domain.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Institute;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.manager.MailManager;
import it.smartcommunitylab.climb.domain.model.WsnEvent;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Component
public class LowBatteryManager {
	private static final transient Logger logger = LoggerFactory.getLogger(LowBatteryManager.class);
	
	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private MailManager mailManager;
	
	@Scheduled(cron = "0 55 23 * * *") // second, minute, hour, day, month, weekday
	//@Scheduled(fixedDelay = 1000000000, initialDelay = 5000)
	public void sendWarning() {
		logger.info("sendWarning");
		Map<Institute, List<String>> messageMap = new HashMap<>();  
		List<Route> routes = storage.getRoutes();
		Date today = new Date();
		Date startOfTheDay = Utils.getStartOfTheDay(today);
		Date endOfTheDay = Utils.getEndOfTheDay(today);
		List<Integer> eventTypeList = Lists.newArrayList();
		eventTypeList.add(Const.BATTERY_STATE);
		for(Route route : routes) {
			if(route.getFrom().before(today) && route.getTo().after(today)) {
				String instituteName = "";
				String schoolName = "";
				Institute institute = storage.getInstitute(route.getOwnerId(), route.getInstituteId());
				if(institute == null) {
					continue;
				}
				instituteName = institute.getName();
				School school = storage.getSchool(route.getOwnerId(), route.getInstituteId(), route.getSchoolId());
				if(school != null) {
					schoolName = school.getName();
				}
				List<WsnEvent> eventsList = storage.searchEventsByLastUpdate(route.getOwnerId(), 
						route.getObjectId(), startOfTheDay, endOfTheDay, eventTypeList, Lists.newArrayList());
				for(WsnEvent event : eventsList) {
					String passengerId = (String) event.getPayload().get("passengerId");
					Integer batteryLevel = (Integer) event.getPayload().get("batteryLevel");
					if(Utils.isNotEmpty(passengerId) && (batteryLevel != null)) {
						if(batteryLevel == 1) {
							List<String> messages = messageMap.get(institute);
							if(messages == null) {
								messages = Lists.newArrayList();
								messageMap.put(institute, messages);
							}
							String childInfo = "";
							Child child = storage.getChild(route.getOwnerId(), passengerId);
							if(child != null) {
								childInfo = child.getName() + " " + child.getSurname() + " " + child.getClassRoom();
							}
							String message = route.getOwnerId() + " - " + event.getWsnNodeId() + " - " 
								+ childInfo + " - " + schoolName + " - " + instituteName + "\n";
							messages.add(message);
						}
					}
				}
			}
		}
		for(Institute institute : messageMap.keySet()) {
			messageMap.get(institute).add(0, "owner - wsnNodeId - name surname classroom - schoolName - instituteName\n");
			mailManager.sendBatteryLowWarning(institute, messageMap.get(institute));			
		}
	}

}
