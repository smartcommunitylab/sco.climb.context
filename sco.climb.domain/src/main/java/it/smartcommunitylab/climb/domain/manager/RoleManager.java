package it.smartcommunitylab.climb.domain.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.climb.contextstore.model.Authorization;
import it.smartcommunitylab.climb.contextstore.model.Institute;
import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Service
public class RoleManager {
	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private MailManager mailManager;
	
	public List<Authorization> addOwner(String ownerId, String email) throws EntityNotFoundException {
		List<Authorization> auths = new ArrayList<Authorization>();
		
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_ADD);
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.getActions().add(Const.AUTH_ACTION_DELETE);
		auth.setRole(Const.ROLE_OWNER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId("*");
		auth.setSchoolId("*");
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add("*");
		auths.add(auth);
		
		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_OWNER), auths);
		return auths;
	}
	
	public List<Authorization> addSchoolOwner(String ownerId, String email, 
			Institute institute, School school) throws EntityNotFoundException {
		List<Authorization> auths = new ArrayList<Authorization>();
		
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_ADD);
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.getActions().add(Const.AUTH_ACTION_DELETE);
		auth.setRole(Const.ROLE_SCHOOL_OWNER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add("*");
		auths.add(auth);
		
		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_SCHOOL_OWNER, institute.getObjectId(), school.getObjectId()), auths);
		return auths;
	}
	
	public List<Authorization> addVolunteer(String ownerId, String email, 
			Institute institute, School school, boolean sendMail) throws EntityNotFoundException {
		List<Authorization> auths = new ArrayList<Authorization>();
		
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.setRole(Const.ROLE_VOLUNTEER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add(Const.AUTH_RES_Institute);
		auth.getResources().add(Const.AUTH_RES_School);
		auth.getResources().add(Const.AUTH_RES_Child);
		auth.getResources().add(Const.AUTH_RES_Volunteer);
		auth.getResources().add(Const.AUTH_RES_Stop);
		auth.getResources().add(Const.AUTH_RES_Route);
		auth.getResources().add(Const.AUTH_RES_Attendance);
		auth.getResources().add(Const.AUTH_RES_NodeState);
		auths.add(auth);
		
		auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_ADD);
		auth.setRole(Const.ROLE_VOLUNTEER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add(Const.AUTH_RES_WsnEvent);
		auth.getResources().add(Const.AUTH_RES_EventLogFile);
		auth.getResources().add(Const.AUTH_RES_Image);
		auths.add(auth);
		
		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_VOLUNTEER, institute.getObjectId(), school.getObjectId()), auths);
		
		if(sendMail) {
			mailManager.sendVolunteerRoleAssign(email, institute, school);
		}
		return auths;
	}

	public List<Authorization> addGameEditor(String ownerId, String email, 
			Institute institute, School school) throws EntityNotFoundException {
		List<Authorization> auths = new ArrayList<Authorization>();
		
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.setRole(Const.ROLE_GAME_EDITOR);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add(Const.AUTH_RES_Institute);
		auth.getResources().add(Const.AUTH_RES_School);
		auth.getResources().add(Const.AUTH_RES_Image);
		
		auths.add(auth);
		
		auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.getActions().add(Const.AUTH_ACTION_ADD);
		auth.getActions().add(Const.AUTH_ACTION_DELETE);
		auth.setRole(Const.ROLE_GAME_EDITOR);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add(Const.AUTH_RES_PedibusGame);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Player);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Itinerary);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Leg);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Link);
		auths.add(auth);

		auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.setRole(Const.ROLE_GAME_EDITOR);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Mobility);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Tuning);
		auths.add(auth);
		
		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_GAME_EDITOR, institute.getObjectId(), school.getObjectId()), auths);
		return auths;
	}
	
	public List<Authorization> addSuperTeacher(String ownerId, String email, 
			Institute institute, School school) throws EntityNotFoundException {
		List<Authorization> auths = new ArrayList<Authorization>();
		
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.setRole(Const.ROLE_SUPER_TEACHER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add(Const.AUTH_RES_Institute);
		auth.getResources().add(Const.AUTH_RES_School);
		auth.getResources().add(Const.AUTH_RES_Image);		
		auths.add(auth);
		
		auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_ADD);
		auth.setRole(Const.ROLE_SUPER_TEACHER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add(Const.AUTH_RES_PedibusGame);
		auths.add(auth);

		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_SUPER_TEACHER, institute.getObjectId(), school.getObjectId()), auths);
		return auths;
	}
	
	public List<Authorization> addSuperTeacherGame(String ownerId, String email, 
			Institute institute, School school, PedibusGame pedibusGame) throws EntityNotFoundException {
		List<Authorization> auths = new ArrayList<Authorization>();
		
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.setRole(Const.ROLE_SUPER_TEACHER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId(pedibusGame.getObjectId());
		auth.getResources().add(Const.AUTH_RES_Institute);
		auth.getResources().add(Const.AUTH_RES_School);
		auth.getResources().add(Const.AUTH_RES_Child);
		auth.getResources().add(Const.AUTH_RES_Image);
		auth.getResources().add(Const.AUTH_RES_Volunteer);
		auth.getResources().add(Const.AUTH_RES_Stop);
		auth.getResources().add(Const.AUTH_RES_Route);
		auth.getResources().add(Const.AUTH_RES_Player);
		auth.getResources().add(Const.AUTH_RES_PedibusGame);
		auths.add(auth);
		
		auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.setRole(Const.ROLE_SUPER_TEACHER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId(pedibusGame.getObjectId());
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Mobility);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Calendar);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Excursion);
		auths.add(auth);
		
		auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.getActions().add(Const.AUTH_ACTION_ADD);
		auth.getActions().add(Const.AUTH_ACTION_DELETE);
		auth.setRole(Const.ROLE_SUPER_TEACHER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId(pedibusGame.getObjectId());
		auth.getResources().add(Const.AUTH_RES_PedibusGame);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Itinerary);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Player);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Leg);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Link);
		auths.add(auth);
		
		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_TEACHER, institute.getObjectId(), school.getObjectId(), 
						pedibusGame.getObjectId()), auths);
		return auths;
	}
	
	public List<Authorization> addTeacher(String ownerId, String email, 
			Institute institute, School school, PedibusGame pedibusGame) throws EntityNotFoundException {
		List<Authorization> auths = new ArrayList<Authorization>();
		
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.setRole(Const.ROLE_TEACHER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId(pedibusGame.getObjectId());
		auth.getResources().add(Const.AUTH_RES_Institute);
		auth.getResources().add(Const.AUTH_RES_School);
		auth.getResources().add(Const.AUTH_RES_Child);
		auth.getResources().add(Const.AUTH_RES_Image);
		auth.getResources().add(Const.AUTH_RES_Volunteer);
		auth.getResources().add(Const.AUTH_RES_Stop);
		auth.getResources().add(Const.AUTH_RES_Route);
		auth.getResources().add(Const.AUTH_RES_Player);
		auth.getResources().add(Const.AUTH_RES_PedibusGame);
		auths.add(auth);
		
		auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.setRole(Const.ROLE_TEACHER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId(pedibusGame.getObjectId());
		auth.getResources().add(Const.AUTH_RES_PedibusGame);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Mobility);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Itinerary);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Calendar);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Excursion);
		auths.add(auth);
		
		auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.getActions().add(Const.AUTH_ACTION_ADD);
		auth.getActions().add(Const.AUTH_ACTION_DELETE);
		auth.setRole(Const.ROLE_TEACHER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(institute.getObjectId());
		auth.setSchoolId(school.getObjectId());
		auth.setRouteId("*");
		auth.setGameId(pedibusGame.getObjectId());
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Player);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Leg);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Link);
		auths.add(auth);
		

		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_TEACHER, institute.getObjectId(), school.getObjectId(), 
						pedibusGame.getObjectId()), auths);
		return auths;
	}

}
