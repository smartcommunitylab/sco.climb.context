package it.smartcommunitylab.climb.contextdashboard.rest;

import it.smartcommunitylab.climb.contextdashboard.common.Utils;
import it.smartcommunitylab.climb.contextdashboard.model.WsnEvent;
import it.smartcommunitylab.climb.contextdashboard.security.DataSetInfo;
import it.smartcommunitylab.climb.contextdashboard.storage.DataSetSetup;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class EventStoreManager {
	private static final transient Logger logger = LoggerFactory.getLogger(EventStoreManager.class);
	
	private HttpClient httpClient = null;
	private String eventApiUrl = null;
	
	public EventStoreManager(String eventApiUrl) {
		httpClient = new HttpClient();
		this.eventApiUrl = eventApiUrl;
	}
	
	public List<WsnEvent> searchEvents(String dateFrom, String dateTo, String routeId, 
			List<Integer> eventTypeList, DataSetSetup dataSetSetup, String ownerId) throws HttpException, IOException {
		List<WsnEvent> result = Lists.newArrayList();
		DataSetInfo dataSetInfo = dataSetSetup.findDataSetById(ownerId);
		if(dataSetInfo != null) {
			String url = eventApiUrl + "event/" + ownerId;
			GetMethod getMethod = new GetMethod(url);
			NameValuePair[] params = new NameValuePair[3 + eventTypeList.size()];
			params[0] = new NameValuePair("routeId", routeId);
			params[1] = new NameValuePair("dateFrom", dateFrom);
			params[2] = new NameValuePair("dateTo", dateTo);
			int count = 3;
			for(Integer eventType : eventTypeList) {
				params[count] = new NameValuePair("eventType[]", String.valueOf(eventType));
				count++;
			}
			getMethod.setQueryString(params);
			if(logger.isInfoEnabled()) {
				logger.info("request: searchEvents:" + getMethod.getURI());
			}
			getMethod.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
			getMethod.addRequestHeader("X-ACCESS-TOKEN", dataSetInfo.getToken());
			int statusCode = httpClient.executeMethod(getMethod);
			if((statusCode >= 200) && (statusCode < 300)) {
				String json = getMethod.getResponseBodyAsString();
				JsonNode rootNode = Utils.readJsonFromString(json);
				Iterator<JsonNode> elements = rootNode.elements();
				while(elements.hasNext()) {
					JsonNode node = elements.next();
					WsnEvent event = Utils.toObject(node, WsnEvent.class);
					if(event != null) {
						result.add(event);
					}
				}
			}
		} else {
			logger.warn("DataSetInfo not found for " + ownerId);
		}
		return result;
	}
}
