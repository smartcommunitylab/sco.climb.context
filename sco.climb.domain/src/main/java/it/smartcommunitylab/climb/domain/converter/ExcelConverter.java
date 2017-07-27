package it.smartcommunitylab.climb.domain.converter;

import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.model.WsnEvent;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class ExcelConverter {
	private static final transient Logger logger = LoggerFactory.getLogger(ExcelConverter.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static void writeAttendance(Date dateFrom, Date dateTo, List<WsnEvent> events, List<Child> childList, 
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
	}
}
