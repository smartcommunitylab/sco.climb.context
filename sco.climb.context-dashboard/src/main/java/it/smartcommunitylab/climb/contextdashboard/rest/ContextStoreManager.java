package it.smartcommunitylab.climb.contextdashboard.rest;

import it.smartcommunitylab.climb.contextdashboard.common.Utils;
import it.smartcommunitylab.climb.contextdashboard.security.DataSetInfo;
import it.smartcommunitylab.climb.contextdashboard.storage.DataSetSetup;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class ContextStoreManager {
	private static final transient Logger logger = LoggerFactory.getLogger(ContextStoreManager.class);
	
	private HttpClient httpClient = null;
	private String contextApiUrl = null;
	
	public ContextStoreManager(String contextApiUrl) {
		httpClient = new HttpClient();
		this.contextApiUrl = contextApiUrl;
	}
	
	public String getContextApiUrl() {
		return contextApiUrl;
	}
	
	public List<Child> getChildList(String schoolId, DataSetSetup dataSetSetup, String ownerId) 
			throws HttpException, IOException {
		List<Child> result = Lists.newArrayList();
		DataSetInfo dataSetInfo = dataSetSetup.findDataSetById(ownerId);
		if(dataSetInfo != null) {
			String url = contextApiUrl + "child/" + ownerId + "/" + schoolId;
			if(logger.isInfoEnabled()) {
				logger.info("request: getChildList:" + url);
			}
			GetMethod getMethod = new GetMethod(url);
			getMethod.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
			getMethod.addRequestHeader("X-ACCESS-TOKEN", dataSetInfo.getToken());
			int statusCode = httpClient.executeMethod(getMethod);
			if((statusCode >= 200) && (statusCode < 300)) {
				String json = getMethod.getResponseBodyAsString();
				JsonNode rootNode = Utils.readJsonFromString(json);
				Iterator<JsonNode> elements = rootNode.elements();
				while(elements.hasNext()) {
					JsonNode node = elements.next();
					Child child = Utils.toObject(node, Child.class);
					if(child != null) {
						result.add(child);
					}
				}
			}
		} else {
			logger.warn("DataSetInfo not found for " + ownerId);
		}
		return result;
	}
	
	public List<Volunteer> getVolunteerList(String schoolId, DataSetSetup dataSetSetup, String ownerId) 
			throws HttpException, IOException {
		List<Volunteer> result = Lists.newArrayList();
		DataSetInfo dataSetInfo = dataSetSetup.findDataSetById(ownerId);
		if(dataSetInfo != null) {
			String url = contextApiUrl + "volunteer/" + ownerId + "/" + schoolId;
			if(logger.isInfoEnabled()) {
				logger.info("request: getChildList:" + url);
			}
			GetMethod getMethod = new GetMethod(url);
			getMethod.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
			getMethod.addRequestHeader("X-ACCESS-TOKEN", dataSetInfo.getToken());
			int statusCode = httpClient.executeMethod(getMethod);
			if((statusCode >= 200) && (statusCode < 300)) {
				String json = getMethod.getResponseBodyAsString();
				JsonNode rootNode = Utils.readJsonFromString(json);
				Iterator<JsonNode> elements = rootNode.elements();
				while(elements.hasNext()) {
					JsonNode node = elements.next();
					Volunteer volunteer = Utils.toObject(node, Volunteer.class);
					if(volunteer != null) {
						result.add(volunteer);
					}
				}
			}
		} else {
			logger.warn("DataSetInfo not found for " + ownerId);
		}
		return result;
	}
	
}
