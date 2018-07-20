package it.smartcommunitylab.climb.domain.common;

import it.smartcommunitylab.climb.domain.exception.StorageException;
import it.smartcommunitylab.climb.domain.model.gamification.ChallengeConcept;
import it.smartcommunitylab.climb.domain.model.gamification.ChallengeModel;
import it.smartcommunitylab.climb.domain.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.domain.model.gamification.GameDTO;
import it.smartcommunitylab.climb.domain.model.gamification.IncrementalClassificationDTO;
import it.smartcommunitylab.climb.domain.model.gamification.Notification;
import it.smartcommunitylab.climb.domain.model.gamification.PlayerStateDTO;
import it.smartcommunitylab.climb.domain.model.gamification.PointConcept;
import it.smartcommunitylab.climb.domain.model.gamification.RuleDTO;
import it.smartcommunitylab.climb.domain.model.gamification.RuleValidateDTO;
import it.smartcommunitylab.climb.domain.model.gamification.TeamDTO;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class GEngineUtils {
	@Autowired
	@Value("${gamification.url}")
	private String gamificationURL;
	
	@Autowired
	@Value("${gamification.user}")
	private String gamificationUser;

	@Autowired
	@Value("${gamification.password}")
	private String gamificationPassword;
	
	private ObjectMapper mapper = null;
	
	public GEngineUtils() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
	
	@SuppressWarnings("rawtypes")
	public PointConcept getPointConcept(PlayerStateDTO state, String key) {
		PointConcept result = null;
		Set<?> pointConcept = (Set) state.getState().get("PointConcept");
		if(pointConcept != null) {
			Iterator<?> it = pointConcept.iterator();
			while(it.hasNext()) {
				PointConcept pc = mapper.convertValue(it.next(), PointConcept.class);
				if(pc.getName().equals(key)) {
					result = pc;
					break;
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	public List<ChallengeConcept> getChallengeConcept(PlayerStateDTO state) {
		List<ChallengeConcept> result = new ArrayList<ChallengeConcept>();
		Set<?> challengeConcept = (Set) state.getState().get("ChallengeConcept");
		if(challengeConcept != null) {
			Iterator<?> it = challengeConcept.iterator();
			while(it.hasNext()) {
				ChallengeConcept challange = mapper.convertValue(it.next(), ChallengeConcept.class);
				result.add(challange);
			}
		}
		return result;
	}

	public void executeAction(ExecutionDataDTO executionData) throws Exception {
		String address = gamificationURL + "/exec/game/" + executionData.getGameId() + "/action/" + executionData.getActionId();
		HTTPUtils.post(address, executionData, null, gamificationUser, gamificationPassword);
	}
	
	public List<Notification> getNotification(String gameId, String playerId, long timestamp) 
			throws Exception {
		String address = gamificationURL + "/notification/game/" + gameId + "/team/" 
			+ URLEncoder.encode(playerId, "UTF-8") + "?fromTs=" + timestamp + "&size=1000000";
		String json = HTTPUtils.get(address, null, gamificationUser, gamificationPassword);
		Notification[] notifications = mapper.readValue(json, Notification[].class);
		List<Notification> result = Arrays.asList(notifications);
		return result;
	}
	
	public void createPlayer(String gameId, PlayerStateDTO player) throws Exception {
		String address = gamificationURL + "/data/game/" + gameId + "/player/" + player.getPlayerId();
		HTTPUtils.post(address, player, null, gamificationUser, gamificationPassword);
	}
	
	public void createTeam(String gameId, TeamDTO team) throws Exception {
		String address = gamificationURL + "/data/game/" + gameId + "/team/" + team.getPlayerId();
		HTTPUtils.post(address, team, null, gamificationUser, gamificationPassword);
	}
	
	public PlayerStateDTO getPlayerStatus(String gameId, String playerId) throws Exception {
		String address = gamificationURL + "/data/game/" + gameId + "/player/" + URLEncoder.encode(playerId, "UTF-8");
		String json = HTTPUtils.get(address, null, gamificationUser, gamificationPassword);
		PlayerStateDTO result = mapper.readValue(json, PlayerStateDTO.class);
		return result;
	}
	
	public void deletePlayerState(String gameId, String playerId) throws Exception {
		String address = gamificationURL + "/data/game/" + gameId + "/player/" + playerId;
		HTTPUtils.delete(address, null, gamificationUser, gamificationPassword);
	}
	
	public String createGame(GameDTO game) throws Exception {
		String address = gamificationURL + "/model/game";
		String json = HTTPUtils.post(address, game, null, gamificationUser, gamificationPassword);
		GameDTO result = mapper.readValue(json, GameDTO.class);
		return result.getId();
	}
	
	public void createChallenge(String gameId, ChallengeModel challengeModel) throws Exception {
		String address = gamificationURL + "/model/game/" + gameId + "/challenge";
		HTTPUtils.post(address, challengeModel, null, gamificationUser, gamificationPassword);
	}
	
	public void createRule(String gameId, RuleDTO rule) throws Exception {
		String address = gamificationURL + "/model/game/" + gameId + "/rule";
		HTTPUtils.post(address, rule, null, gamificationUser, gamificationPassword);
	}
	
	public void validateRule(String gameId, RuleValidateDTO rule) throws Exception {
		//String address = gamificationURL + "/model/game/" + gameId + "/rule/validate";
		String address = gamificationURL + "/console/rule/validate";
		String json = HTTPUtils.post(address, rule.getRule(), null, gamificationUser, gamificationPassword);
		TypeReference<ArrayList<String>> typeRef = new TypeReference<ArrayList<String>>() {};
		ArrayList<String> value = mapper.readValue(json, typeRef);
		if(value.size() > 0) {
			throw new StorageException(value.toString());
		}
	}
	
	public void createPointConcept(String gameId, PointConcept pointConcept) throws Exception {
		String address = gamificationURL + "/model/game/" + gameId + "/point";
		HTTPUtils.post(address, pointConcept, null, gamificationUser, gamificationPassword);
	}
	
	public void createTask(String gameId, IncrementalClassificationDTO classification) throws Exception {
		String address = gamificationURL + "/model/game/" + gameId + "/incclassification";
		HTTPUtils.post(address, classification, null, gamificationUser, gamificationPassword);
	}
	
}
