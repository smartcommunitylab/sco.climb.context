package it.smartcommunitylab.climb.domain.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;

import it.smartcommunitylab.climb.contextstore.model.Authorization;
import it.smartcommunitylab.climb.contextstore.model.BaseObject;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.model.PedibusPlayer;
import it.smartcommunitylab.climb.domain.scheduled.ChildStatus;

public class Utils {
	private static ObjectMapper fullMapper = new ObjectMapper();
	static {
		Utils.fullMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Utils.fullMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		Utils.fullMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		Utils.fullMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Utils.fullMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
	}

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

	public static String getString(Map<String, String> data, String lang, String defaultLang) {
		String result = null;
		if(data.containsKey(lang)) {
			result = data.get(lang);
		} else {
			result = data.get(defaultLang);
		}
		return result;
	}
	
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
	
	public static JsonNode readJsonFromString(String json) throws JsonParseException, JsonMappingException, IOException {
		return Utils.fullMapper.readValue(json, JsonNode.class);
	}
	
	public static JsonNode readJsonFromReader(Reader reader) throws JsonProcessingException, IOException {
		return Utils.fullMapper.readTree(reader);
	}
	
	public static <T> List<T> readJSONListFromInputStream(InputStream in, Class<T> cls)
			throws IOException {
		CollectionType listType = Utils.fullMapper.getTypeFactory().constructCollectionType(ArrayList.class, cls);
		List<T> result = Utils.fullMapper.readValue(in, listType);
//		List<T> result = new ArrayList<T>();
//		for (Object o : list) {
//			result.add(Utils.fullMapper.convertValue(o, cls));
//		}
		return result;
	}
	
	public static <T> T readJSONFromInputStream(InputStream in, Class<T> cls)
			throws IOException {
		Object object = Utils.fullMapper.readValue(in, new TypeReference<T>() {});
		T result = Utils.fullMapper.convertValue(object, cls);
		return result;
	}
	
	public static <T> T toObject(Object in, Class<T> cls) {
		return Utils.fullMapper.convertValue(in, cls);
	}

	public static <T> T toObject(JsonNode in, Class<T> cls) throws JsonProcessingException {
		return Utils.fullMapper.treeToValue(in, cls);
	}

	public static Map<String,String> handleError(Exception exception) {
		Map<String,String> errorMap = new HashMap<String,String>();
		errorMap.put(Const.ERRORTYPE, exception.getClass().toString());
		errorMap.put(Const.ERRORMSG, exception.getMessage());
		return errorMap;
	}
	
	public static String getAuthKey(String ownerId, String role) {
		return ownerId + "__" + role;
	}

	public static String getAuthKey(String ownerId, String role, String... attributes) {
		String result = ownerId + "__" + role;
		for(String attribute : attributes) {
			if(!attribute.equals("*")) {
				result = result + "__" + attribute;
			}
		}
		return result;
	}
	
	public static String getOwnerIdFromAuthKey(String authKey) {
		String[] strings = authKey.split("__");
		if(strings.length >= 1) {
			return strings[0];
		}
		return null;
	}
	
	public static String getRoleFromAuthKey(String authKey) {
		String[] strings = authKey.split("__");
		if(strings.length >= 2) {
			return strings[1];
		}
		return null;
	}
	
//	public static String getBaseFromAuthKey(String authKey) {
//		String[] strings = authKey.split("__");
//		if(strings.length >= 2) {
//			return strings[0] + "__" + strings[1];
//		}
//		return null;
//	}
	
	public static boolean checkOwnerId(String ownerId, User user) {
		for(String authKey : user.getRoles().keySet()) {
			List<Authorization> authList = user.getRoles().get(authKey);
			for(Authorization auth : authList) {
				if(auth.getOwnerId().equals(ownerId)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean checkOwnerId(String ownerId, User user, String authKey) {
		List<Authorization> authList = user.getRoles().get(authKey);
		if((authList != null) && (authList.size() > 0)) {
			Authorization auth = authList.get(0);
			if(auth.getOwnerId().equals(ownerId)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkRole(String role, User user) {
		for(String authKey : user.getRoles().keySet()) {
			List<Authorization> authList = user.getRoles().get(authKey);
			for(Authorization auth : authList) {
				if(auth.getRole().equals(role)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean checkOwnerIdAndRole(String ownerId, String role, User user) {
		for(String authKey : user.getRoles().keySet()) {
			List<Authorization> authList = user.getRoles().get(authKey);
			for(Authorization auth : authList) {
				if(auth.getOwnerId().equals(ownerId)) {
					if(auth.getRole().equals(role)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static List<String> getUserRoles(User user) {
		List<String> result = new ArrayList<>();
		for(String authKey : user.getRoles().keySet()) {
			List<Authorization> authList = user.getRoles().get(authKey);
			for(Authorization auth : authList) {
				if(!result.contains(auth.getRole())) {
					result.add(auth.getRole());
				}
			}
		}
		return result;
	}
	
	public static List<String> getUserOwnerIds(User user) {
		List<String> result = new ArrayList<>();
		for(String authKey : user.getRoles().keySet()) {
			List<Authorization> authList = user.getRoles().get(authKey);
			for(Authorization auth : authList) {
				if(!result.contains(auth.getOwnerId())) {
					result.add(auth.getOwnerId());
				}
			}
		}
		return result;
	}
	
	public static boolean validateAuthorizationByRole(String authKeyToValidate, String ownerId, String role, 
			User user, boolean ownerRole) {
		if(user != null) {
			String[] strings = authKeyToValidate.split("__");
			if(strings.length < 2) {
				return false;
			}
			String ownerIdAuth = strings[0];
			String roleAuth = strings[1];
			String instituteIdAuth = "*";
			if(strings.length > 2) {
				instituteIdAuth = strings[2];
			}
			String schoolIdAuth = "*";
			if(strings.length > 3) {
				schoolIdAuth = strings[3];
			}			
			if(!ownerIdAuth.equals(ownerId)) {
				return false;
			}
			if(Utils.isNotEmpty(role)) {
				if(!roleAuth.equals(role)) {
					return false;
				}
			}
			if(ownerRole) {
				return true;
			}
			if(Const.ROLE_USER.equals(roleAuth)) {
				return true;
			}
			for(String authKey : user.getRoles().keySet()) {
				String[] tokens = authKey.split("__");
				if(tokens.length >= 2) {
					String ownerIdUser = tokens[0];
					String roleUser = tokens[1];
					String instituteIdUser = "*";
					if(tokens.length > 2) {
						instituteIdUser = tokens[2];
					}
					String schoolIdUser = "*";
					if(tokens.length > 3) {
						schoolIdUser = tokens[3];
					}
					if(ownerIdUser.equals(ownerId)) {
						if(Const.ROLE_GAME_EDITOR.equals(roleUser)) {
							if(Const.ROLE_GAME_EDITOR.equals(roleAuth) || 
									Const.ROLE_TEACHER.equals(roleAuth)) {
								if(instituteIdUser.equals("*") || 
										instituteIdUser.equals(instituteIdAuth)) {
									if(schoolIdUser.equals("*") || 
											schoolIdUser.equals(schoolIdAuth)) {
										return true;
									}
								}
							}
						}		
					}					
				}
			}
		}
		return false;
	}
	
	public static String getNormalizeLegName(String name) {
		return name.trim().replace(" ", "").replaceAll("\\W", "X");
	}
	
	public static String getEscapedLegName(String name) {
		return name.trim().replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"");
	}
	
	public static Date getStartOfTheDay(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
	}
	
	public static Date getEndOfTheDay(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    return cal.getTime();
	}
	
	public static String getPlayerKey(ChildStatus status) {
		return status.getClassRoom() + "-" + status.getNickname();
	}
	
	public static String getPlayerKey(Child child) {
		return child.getClassRoom() + "-" + child.getNickname();
	}
	
	public static String getPlayerKey(String classRoom, String nickname) {
		return classRoom + "-" + nickname;
	}
	
	public static boolean containsId(String id, List<PedibusPlayer> players) {
		boolean result = false;
		for(BaseObject obj : players) {
			if(id.equals(obj.getObjectId())) {
				result = true;
				break;
			}
		}
		return result;
	}
	
}
