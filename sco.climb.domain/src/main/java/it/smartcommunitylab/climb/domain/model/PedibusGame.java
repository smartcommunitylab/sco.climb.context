package it.smartcommunitylab.climb.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

public class PedibusGame extends BaseObject {

	private String instituteId;
	private String schoolId;
	private List<String> classRooms;
	private String gameId;
	private String gameName;
	private String gameDescription;
	private Date from;
	private Date to;
	private String globalTeam;
	private String fromHour;
	private String toHour;
	private int interval;
	private String lastDaySeen;
	private boolean lateSchedule;
	private boolean usingPedibusData;
	private boolean deployed;
	private String confTemplateId;
	private Map<String, String> params = new HashMap<>();
	private Map<String, Map<String, Integer>> mobilityParams = new HashMap<>();
	private String shortName;
	private Map<String, Boolean> pollingFlagMap = new HashMap<>();
	/**
	 * from monday (index 0) to sunday (index 6)
	 */
	private List<Boolean> daysOfWeek = new ArrayList<>();
	private List<String> modalities = new ArrayList<>();
	private String sponsorTemplate;
	private boolean roundTrip = false;
	private boolean useCalendar = true;

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public String getGameDescription() {
		return gameDescription;
	}

	public void setGameDescription(String gameDescription) {
		this.gameDescription = gameDescription;
	}

	public List<String> getClassRooms() {
		return classRooms;
	}

	public void setClassRooms(List<String> classRooms) {
		this.classRooms = classRooms;
	}

	public String getGlobalTeam() {
		return globalTeam;
	}

	public void setGlobalTeam(String globalTeam) {
		this.globalTeam = globalTeam;
	}

	public String getFromHour() {
		return fromHour;
	}

	public void setFromHour(String fromHour) {
		this.fromHour = fromHour;
	}

	public String getToHour() {
		return toHour;
	}

	public void setToHour(String toHour) {
		this.toHour = toHour;
	}

	public String getLastDaySeen() {
		return lastDaySeen;
	}

	public void setLastDaySeen(String lastDaySeen) {
		this.lastDaySeen = lastDaySeen;
	}

	public Map<String, Boolean> getPollingFlagMap() {
		return pollingFlagMap;
	}

	public void setPollingFlagMap(Map<String, Boolean> pollingFlagMap) {
		this.pollingFlagMap = pollingFlagMap;
	}

	public String getInstituteId() {
		return instituteId;
	}

	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}

	public boolean isLateSchedule() {
		return lateSchedule;
	}

	public void setLateSchedule(boolean lateSchedule) {
		this.lateSchedule = lateSchedule;
	}

	public boolean isUsingPedibusData() {
		return usingPedibusData;
	}

	public void setUsingPedibusData(boolean usingPedibusData) {
		this.usingPedibusData = usingPedibusData;
	}

	public boolean isDeployed() {
		return deployed;
	}

	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
	}

	public String getConfTemplateId() {
		return confTemplateId;
	}

	public void setConfTemplateId(String confTemplateId) {
		this.confTemplateId = confTemplateId;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public List<Boolean> getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(List<Boolean> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	public List<String> getModalities() {
		return modalities;
	}

	public void setModalities(List<String> modalities) {
		this.modalities = modalities;
	}

	public String getSponsorTemplate() {
		return sponsorTemplate;
	}

	public void setSponsorTemplate(String sponsorTemplate) {
		this.sponsorTemplate = sponsorTemplate;
	}

	public boolean isRoundTrip() {
		return roundTrip;
	}

	public void setRoundTrip(boolean roundTrip) {
		this.roundTrip = roundTrip;
	}

	public boolean isUseCalendar() {
		return useCalendar;
	}

	public void setUseCalendar(boolean useCalendar) {
		this.useCalendar = useCalendar;
	}

	public Map<String, Map<String, Integer>> getMobilityParams() {
		return mobilityParams;
	}

	public void setMobilityParams(Map<String, Map<String, Integer>> mobilityParams) {
		this.mobilityParams = mobilityParams;
	}

}
