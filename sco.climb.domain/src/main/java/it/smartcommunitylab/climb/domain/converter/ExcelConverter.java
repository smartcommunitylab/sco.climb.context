package it.smartcommunitylab.climb.domain.converter;

import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.InvalidParametersException;
import it.smartcommunitylab.climb.domain.model.PedibusPlayer;
import it.smartcommunitylab.climb.domain.model.WsnEvent;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class ExcelConverter {
	private static final transient Logger logger = LoggerFactory.getLogger(ExcelConverter.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private RepositoryManager storage;
	
	public void writeAttendance(Date dateFrom, Date dateTo, 
		List<WsnEvent> events, List<Child> childList, 
		List<Volunteer> volunteerList, OutputStream outputStream) throws Exception {
		Map<String, Child> childMap = new HashMap<String, Child>();
		Map<String, Volunteer> volunteerMap = new HashMap<String, Volunteer>();
		Map<String, List<WsnEvent>> eventMap = new TreeMap<String, List<WsnEvent>>();
		
		for(Child child : childList) {
			childMap.put(child.getObjectId(), child);
		}
		
		for(Volunteer volunteer : volunteerList) {
			volunteerMap.put(volunteer.getObjectId(), volunteer);
		}
		
		for(WsnEvent event : events) {
			String day = sdf.format(event.getTimestamp());
			List<WsnEvent> eventList = eventMap.get(day);
			if(eventList == null) {
				eventList = new ArrayList<WsnEvent>();
				eventMap.put(day, eventList);
			}
			eventList.add(event);
		}
		
		Workbook wb = new HSSFWorkbook();
		
		Sheet sheetVolunteers = wb.createSheet("VOLONTARI");
		int rowCounter = 0;
		for(String day : eventMap.keySet()) {
			if(logger.isInfoEnabled()) {
				logger.info("writeAttendance:" + day);
			}
			Row rowDate = sheetVolunteers.createRow(rowCounter);
			Cell cellDateLabel = rowDate.createCell(0);
			Cell cellDateValue = rowDate.createCell(1);
			cellDateLabel.setCellValue("Giorno");
			cellDateValue.setCellValue(day);
			rowCounter++;
			List<WsnEvent> eventList = eventMap.get(day);
			for(WsnEvent event : eventList) {
				if(event.getEventType() == Const.SET_DRIVER) {
					Row rowData = sheetVolunteers.createRow(rowCounter);
					cellDateLabel = rowData.createCell(0);
					cellDateLabel.setCellValue("Responsabile");
					cellDateValue = rowData.createCell(1);
					String volunteerId = (String)event.getPayload().get("volunteerId");
					String value = null;
					Volunteer volunteer = volunteerMap.get(volunteerId);
					if(volunteer != null) {
						value = volunteer.getName();
					} else {
						value = volunteerId;
					}
					cellDateValue.setCellValue(value);
					rowCounter++;
				} else if(event.getEventType() == Const.SET_HELPER) {
					Row rowData = sheetVolunteers.createRow(rowCounter);
					cellDateLabel = rowData.createCell(0);
					cellDateLabel.setCellValue("Aiutante");
					cellDateValue = rowData.createCell(1);
					String volunteerId = (String)event.getPayload().get("volunteerId");
					String value = null;
					Volunteer volunteer = volunteerMap.get(volunteerId);
					if(volunteer != null) {
						value = volunteer.getName();
					} else {
						value = volunteerId;
					}
					cellDateValue.setCellValue(value);
					rowCounter++;
				}
			}
		}
    
		Sheet sheetChildren = wb.createSheet("BAMBINI");
		rowCounter = 0;
		for(String day : eventMap.keySet()) {
			Row rowDate = sheetChildren.createRow(rowCounter);
			Cell cellDateLabel = rowDate.createCell(0);
			Cell cellDateValue = rowDate.createCell(1);
			cellDateLabel.setCellValue("Giorno");
			cellDateValue.setCellValue(day);
			rowCounter++;
			List<WsnEvent> eventList = eventMap.get(day);
			List<String> valueList = Lists.newArrayList();
			for(WsnEvent event : eventList) {
				if(event.getEventType() == Const.NODE_AT_DESTINATION) {
					String passengerId = (String)event.getPayload().get("passengerId");
					String value = null;
					Child child = childMap.get(passengerId);
					if(child != null) {
						value = child.getSurname() + " " + child.getName();
					} else {
						value = passengerId;
					}
					valueList.add(value);
				}
			}
			Collections.sort(valueList,	new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			for(String value : valueList) {
				Row rowData = sheetChildren.createRow(rowCounter);
				cellDateLabel = rowData.createCell(0);
				cellDateLabel.setCellValue("Presente");
				cellDateValue = rowData.createCell(1);
				cellDateValue.setCellValue(value);
				rowCounter++;
			}
		}
		
    wb.write(outputStream);
    wb.close();
	}
	
	public Map<String, Route> readRoutes(InputStream excel, 
			String ownerId, String instituteId, String schoolId, List<ExcelError> errors) throws Exception {
		Map<String, Route> result = new HashMap<String, Route>();
		XSSFWorkbook wb = new XSSFWorkbook(excel);
		try {
			XSSFSheet sheet = wb.getSheet("Pedibus");
			if(sheet == null) {
				throw new InvalidParametersException("Pedibus sheet not found");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			DataFormatter fmt = new DataFormatter();
			for(int i=1; i <= sheet.getLastRowNum(); i++) {
				try {
					Row row = sheet.getRow(i);
					String name = fmt.formatCellValue(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String dataInizio = fmt.formatCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String dataFine = fmt.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					
					if(Utils.isEmpty(name)) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "nome linea mancante");
						errors.add(error);
						continue;
					}
					
					Date from = null;
					try {
						from = sdf.parse(dataInizio);
					} catch (Exception e) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "data inizio non corretta");
						errors.add(error);
						continue;
					}
					
					Date to = null;
					try {
						to = sdf.parse(dataFine);
					} catch (Exception e) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "data fine non corretta");
						errors.add(error);
						continue;
					}
					
					Criteria criteria = Criteria.where("schoolId").is(schoolId)
							.and("instituteId").is(instituteId)
							.and("name").is(name);
					Route routeDb = storage.findOneData(Route.class, criteria, ownerId);
					
					if(routeDb == null) {
						Route route = new Route();
						route.setOwnerId(ownerId);
						route.setInstituteId(instituteId);
						route.setSchoolId(schoolId);
						route.setObjectId(Utils.getUUID());
						route.setName(name);
						route.setFrom(from);
						route.setTo(to);
						result.put(name, route);
					} else {
						routeDb.setFrom(from);
						routeDb.setTo(to);
						result.put(name, routeDb);
					}
				} catch (Exception e) {
					ExcelError error = new ExcelError(sheet.getSheetName(), i, e.toString());
					errors.add(error);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			wb.close();
		}
		return result;
	}
	
	public Map<String, Stop> readStops(InputStream excel,
			String ownerId, String instituteId, String schoolId,
			Map<String, Route> routesMap, List<ExcelError> errors) throws Exception {
		Map<String, Stop> result = new HashMap<String, Stop>();
		XSSFWorkbook wb = new XSSFWorkbook(excel);
		try {
			XSSFSheet sheet = wb.getSheet("Fermate");
			if(sheet == null) {
				throw new InvalidParametersException("Fermate sheet not found");
			}
			DataFormatter fmt = new DataFormatter();
			for(int i=1; i <= sheet.getLastRowNum(); i++) {
				try {
					Row row = sheet.getRow(i);
					String pedibus = fmt.formatCellValue(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String name = fmt.formatCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String oraPartenza = fmt.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String posizione = fmt.formatCellValue(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String distanzaStr = fmt.formatCellValue(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim(); 
					String partenza = fmt.formatCellValue(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String arrivo = fmt.formatCellValue(row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String ordineStr = fmt.formatCellValue(row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim(); 
										
					if(Utils.isEmpty(name)) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "nome fermata mancante");
						errors.add(error);
						continue;
					}
					
					double[] geocoding = new double[2];
					String[] split = posizione.split(",");
					try {
						geocoding[0] = Double.valueOf(split[1]);
						geocoding[1] = Double.valueOf(split[0]);
					} catch (Exception e) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "posizione non valida");
						errors.add(error);
						continue;
					}
					
					double distanza = 0;
					try {
						distanza = Double.valueOf(distanzaStr);
					} catch (Exception e) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "distanza non valida");
						errors.add(error);
						continue;
					}
					
					int ordine = 0;
					try {
						ordine = Integer.valueOf(ordineStr);
					} catch (Exception  e) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "ordine non valido");
						errors.add(error);
						continue;
					}
					
					boolean start = false;
					try {
						start = Boolean.valueOf(partenza.toLowerCase());
					} catch (Exception e) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "valore partenza non valido");
						errors.add(error);
						continue;
					}
					
					boolean destination = false;
					try {
						destination = Boolean.valueOf(arrivo.toLowerCase());
					} catch (Exception e) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "valore arrivo non valido");
						errors.add(error);
						continue;
					}
					
					boolean school = destination ? true : false;
					
					Route route = routesMap.get(pedibus);
					if(route == null) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "linea non trovata");
						errors.add(error);
						continue;
					}
					
					Criteria criteria = Criteria.where("routeId").is(route.getObjectId())
							.and("name").is(name);
					Stop stopDb = storage.findOneData(Stop.class, criteria, ownerId);
					
					if(stopDb == null) {
						Stop stop = new Stop();
						stop.setOwnerId(ownerId);
						stop.setRouteId(route.getObjectId());
						stop.setObjectId(Utils.getUUID());
						stop.setName(name);
						stop.setDepartureTime(oraPartenza);
						stop.setStart(start);
						stop.setDestination(destination);
						stop.setSchool(school);
						stop.setGeocoding(geocoding);
						stop.setDistance(distanza);
						stop.setPosition(ordine);						
						result.put(route.getName() + "," + name, stop);
					} else {
						stopDb.setDepartureTime(oraPartenza);
						stopDb.setStart(start);
						stopDb.setDestination(destination);
						stopDb.setSchool(school);
						stopDb.setGeocoding(geocoding);
						stopDb.setDistance(distanza);
						stopDb.setPosition(ordine);
						result.put(route.getName() + "," + name, stopDb);
					}
				} catch (Exception e) {
					ExcelError error = new ExcelError(sheet.getSheetName(), i, e.toString());
					errors.add(error);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			wb.close();
		}
		return result;
	}
	
	public Map<String, Child> readChildren(InputStream excel,	
			String ownerId, String instituteId, String schoolId, 
			Map<String, Stop> stopsMap, 
			List<ExcelError> errors) throws Exception {
		Map<String, Child> result = new HashMap<>();
		XSSFWorkbook wb = new XSSFWorkbook(excel);
		try {
			XSSFSheet sheet = wb.getSheet("Bambini");
			if(sheet == null) {
				throw new InvalidParametersException("Bambini sheet not found");
			}
			DataFormatter fmt = new DataFormatter();
			for(int i=1; i <= sheet.getLastRowNum(); i++) {
				try {
					Row row = sheet.getRow(i);
					
					String cognome = fmt.formatCellValue(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String nome = fmt.formatCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String nickname = fmt.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String genitore = fmt.formatCellValue(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String telefono = fmt.formatCellValue(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String classe = fmt.formatCellValue(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String fermata = fmt.formatCellValue(row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String cf = fmt.formatCellValue(row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim().toUpperCase();
					String nodo = fmt.formatCellValue(row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					
					if(Utils.isEmpty(cf)) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "codice fiscale mancante");
						errors.add(error);
						continue;
					}
					
					if(Utils.isEmpty(cognome)) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "cognome mancante");
						errors.add(error);
						continue;
					}
					
					if(Utils.isEmpty(nome)) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "nome mancante");
						errors.add(error);
						continue;
					}
					
					Stop stop = null;
					if(Utils.isNotEmpty(fermata)) {
						stop = stopsMap.get(fermata);
						if(stop == null) {
							ExcelError error = new ExcelError(sheet.getSheetName(), i, "fermata non trovata");
							errors.add(error);
							continue;
						}
					}
					
					Criteria criteria = Criteria.where("schoolId").is(schoolId)
							.and("instituteId").is(instituteId)
							.and("cf").is(cf);
					Child childDb = storage.findOneData(Child.class, criteria, ownerId);
					
					if(childDb == null) {
						Child child = new Child();
						child.setOwnerId(ownerId);
						child.setInstituteId(instituteId);
						child.setSchoolId(schoolId);
						child.setObjectId(Utils.getUUID());
						child.setCf(cf);
						child.setName(nome);
						child.setSurname(cognome);
						child.setNickname(nickname);
						child.setParentName(genitore);
						child.setPhone(telefono);
						child.setClassRoom(classe);
						child.setWsnId(nodo);
						if(stop != null) {
							if(!stop.getPassengerList().contains(child.getObjectId())) {
								stop.getPassengerList().add(child.getObjectId());
							}
						}
						result.put(child.getObjectId(), child);
					} else {
						childDb.setName(nome);
						childDb.setSurname(cognome);
						childDb.setNickname(nickname);
						childDb.setParentName(genitore);
						childDb.setPhone(telefono);
						childDb.setWsnId(nodo);
						if(Utils.isNotEmpty(classe)) {
							childDb.setClassRoom(classe);
						}
						if(stop != null) {
							if(!stop.getPassengerList().contains(childDb.getObjectId())) {
								stop.getPassengerList().add(childDb.getObjectId());
							}
						}
						result.put(childDb.getObjectId(), childDb);
					}
				} catch (Exception e) {
					ExcelError error = new ExcelError(sheet.getSheetName(), i, e.toString());
					errors.add(error);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			wb.close();
		}
		return result;
	}
	
	public Map<String, PedibusPlayer> readPlayers(InputStream excel,
			String ownerId, String pedibusGameId, List<ExcelError> errors) throws Exception {
		Map<String, PedibusPlayer> result = new HashMap<>();
		List<String> playerKeys = new ArrayList<>();
		XSSFWorkbook wb = new XSSFWorkbook(excel);
		try {
			XSSFSheet sheet = wb.getSheet("Bambini");
			if(sheet == null) {
				throw new InvalidParametersException("Bambini sheet not found");
			}
			DataFormatter fmt = new DataFormatter();
			for(int i=1; i <= sheet.getLastRowNum(); i++) {
				try {
					Row row = sheet.getRow(i);
					
					String nickname = fmt.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String classe = fmt.formatCellValue(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					
					if(Utils.isEmpty(nickname)) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "nickname mancante");
						errors.add(error);
						continue;
					}
					
					if(Utils.isEmpty(classe)) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "classe mancante");
						errors.add(error);
						continue;
					}
					
					if(playerKeys.contains(Utils.getPlayerKey(classe, nickname))) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "nickname già presente in classe");
						errors.add(error);
						continue;
					} else {
						playerKeys.add(Utils.getPlayerKey(classe, nickname));
					}
					
					PedibusPlayer playerDb = storage.getPedibusPlayer(ownerId, pedibusGameId, nickname, classe);
					
					if(playerDb == null) {
						PedibusPlayer player = new PedibusPlayer();
						player.setOwnerId(ownerId);
						player.setPedibusGameId(pedibusGameId);
						player.setObjectId(Utils.getUUID());
						player.setNickname(nickname);
						player.setClassRoom(classe);
						result.put(player.getObjectId(), player);
					} else {
						playerDb.setNickname(nickname);
						playerDb.setClassRoom(classe);
						result.put(playerDb.getObjectId(), playerDb);
					}
				} catch (Exception e) {
					ExcelError error = new ExcelError(sheet.getSheetName(), i, e.toString());
					errors.add(error);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			wb.close();
		}
		return result;
	}
	
	public Map<String, Volunteer> readVolunteers(InputStream excel,
			String ownerId, String instituteId, String schoolId, 
			Map<String, Route> routesMap, List<ExcelError> errors) throws Exception {
		Map<String, Volunteer> result = new HashMap<String, Volunteer>();
		XSSFWorkbook wb = new XSSFWorkbook(excel);
		try {
			XSSFSheet sheet = wb.getSheet("Volontari");
			if(sheet == null) {
				throw new InvalidParametersException("Volontari sheet not found");
			}
			DataFormatter fmt = new DataFormatter();
			for(int i=1; i <= sheet.getLastRowNum(); i++) {
				try {
					Row row = sheet.getRow(i);
					String cognome = fmt.formatCellValue(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String nome = fmt.formatCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String telefono = fmt.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String linea = fmt.formatCellValue(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String email = fmt.formatCellValue(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
					String cf = fmt.formatCellValue(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim().toUpperCase();
					
					if(Utils.isEmpty(cf)) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "codice fiscale mancante");
						errors.add(error);
						continue;						
					}
					
					if(Utils.isEmpty(cognome)) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "cognome mancante");
						errors.add(error);
						continue;						
					}
					
					if(Utils.isEmpty(nome)) {
						ExcelError error = new ExcelError(sheet.getSheetName(), i, "nome mancante");
						errors.add(error);
						continue;						
					}
					
					Route route = null;
					if(Utils.isNotEmpty(linea)) {
						route = routesMap.get(linea);
						if(route == null) {
							logger.warn(String.format("Route '%s' not found", route));
						}
					}
					
					Criteria criteria = Criteria.where("schoolId").is(schoolId)
							.and("instituteId").is(instituteId).and("cf").is(cf);
					Volunteer volunteerDb = storage.findOneData(Volunteer.class, criteria, ownerId);
					
					if(volunteerDb == null) {
						Volunteer volunteer = new Volunteer();
						volunteer.setOwnerId(ownerId);
						volunteer.setInstituteId(instituteId);
						volunteer.setSchoolId(schoolId);
						volunteer.setObjectId(Utils.getUUID());
						volunteer.setName(cognome + " " + nome);
						volunteer.setPhone(telefono);
						volunteer.setEmail(email);
						volunteer.setCf(cf);
						if(route != null) {
							if(!route.getVolunteerList().contains(volunteer.getObjectId())) {
								route.getVolunteerList().add(volunteer.getObjectId());
							}
						}
						result.put(volunteer.getObjectId(), volunteer);
					} else {
						volunteerDb.setName(cognome + " " + nome);
						volunteerDb.setPhone(telefono);
						volunteerDb.setEmail(email);
						if(route != null) {
							if(!route.getVolunteerList().contains(volunteerDb.getObjectId())) {
								route.getVolunteerList().add(volunteerDb.getObjectId());
							}
						}
						result.put(volunteerDb.getObjectId(), volunteerDb);
					}					
				} catch (Exception e) {
					ExcelError error = new ExcelError(sheet.getSheetName(), i, e.toString());
					errors.add(error);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			wb.close();
		}
		return result;
	}
	
}
