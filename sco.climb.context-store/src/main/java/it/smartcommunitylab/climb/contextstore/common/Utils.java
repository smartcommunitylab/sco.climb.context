package it.smartcommunitylab.climb.contextstore.common;

import it.smartcommunitylab.climb.contextstore.security.DataSetInfo;
import it.smartcommunitylab.climb.contextstore.security.Token;
import it.smartcommunitylab.climb.contextstore.storage.DataSetSetup;
import it.smartcommunitylab.climb.contextstore.storage.RepositoryManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class Utils {

	public static boolean isNotEmpty(String value) {
		boolean result = false;
		if ((value != null) && (!value.isEmpty())) {
			result = true;
		}
		return result;
	}
	
	public static boolean isEmpty(String value) {
		boolean result = true;
		if ((value != null) && (!value.isEmpty())) {
			result = false;
		}
		return result;
	}
	
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static String getString(Map<String, String> data, String lang, String defaultLang) {
		String result = null;
		if(data.containsKey(lang)) {
			result = data.get(lang);
		} else {
			result = data.get(defaultLang);
		}
		return result;
	}

	public static boolean validateAPIRequest(ServletRequest req, DataSetSetup dataSetSetup, 
			RepositoryManager storage) {
		boolean result = false;
		HttpServletRequest request = (HttpServletRequest) req;
		String uriPath = request.getRequestURI();
		if (uriPath != null && !uriPath.isEmpty()) {
			String tokenArrived = request.getHeader("X-ACCESS-TOKEN");
			if (tokenArrived != null && !tokenArrived.isEmpty()) {
				Token matchedToken = storage.findTokenByToken(tokenArrived);
				if (matchedToken != null) {
					if (matchedToken.getExpiration() > 0) {
						//token exired
						if(matchedToken.getExpiration() < System.currentTimeMillis()) {
							return false;
						}
					}
					String ownerId = matchedToken.getName();
					DataSetInfo dataSetInfo = dataSetSetup.findDataSetById(ownerId);
					//dataset config not found
					if(dataSetInfo == null) {
						return false;
					}
					//wrong API path
					if(!uriPath.contains(ownerId)) {
						return false;
					}
					// delegate resources to controller via request attribute map.
					if (matchedToken.getResources() != null	&& !matchedToken.getResources().isEmpty()) {
						req.setAttribute("resources",	matchedToken.getResources());
					}
					// check ( resources *)
					if (matchedToken.getPaths().contains("*")) {
						result = true;
					} else {
						for(String resourcePath : matchedToken.getPaths()) {
							if(uriPath.contains(resourcePath)) {
								result = true;
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}

	public static Map<String,String> handleError(Exception exception) {
		Map<String,String> errorMap = new HashMap<String,String>();
		errorMap.put(Const.ERRORTYPE, exception.getClass().toString());
		errorMap.put(Const.ERRORMSG, exception.getMessage());
		return errorMap;
	}
}
