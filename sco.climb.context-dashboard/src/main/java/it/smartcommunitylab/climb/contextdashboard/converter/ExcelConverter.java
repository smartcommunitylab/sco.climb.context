package it.smartcommunitylab.climb.contextdashboard.converter;

import it.smartcommunitylab.climb.contextdashboard.common.Const;
import it.smartcommunitylab.climb.contextdashboard.model.WsnEvent;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;

import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class ExcelConverter {
	private static final transient Logger logger = LoggerFactory.getLogger(ExcelConverter.class);
	
	public static void writeAttendance(Date date, List<WsnEvent> events, List<Child> childList, 
		List<Volunteer> volunteerList, OutputStream outputStream) throws Exception {
		Map<String, Child> childMap = new HashMap<String, Child>();
		Map<String, Volunteer> volunteerMap = new HashMap<String, Volunteer>();
		
		for(Child child : childList) {
			childMap.put(child.getObjectId(), child);
		}
		
		for(Volunteer volunteer : volunteerList) {
			volunteerMap.put(volunteer.getObjectId(), volunteer);
		}
		
		Workbook wb = new HSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		CellStyle cellStyleDate = wb.createCellStyle();
		cellStyleDate.setDataFormat(
        createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
		
		Sheet sheetVolunteers = wb.createSheet("VOLONTARI");
		Row rowDate = sheetVolunteers.createRow(0);
		Cell cellDateLabel = rowDate.createCell(0);
		Cell cellDateValue = rowDate.createCell(1);
		cellDateLabel.setCellValue("Giorno");
		cellDateValue.setCellValue(date);
		cellDateValue.setCellStyle(cellStyleDate);
		int rowCounter = 1;
		for(WsnEvent event : events) {
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
    
		Sheet sheetChildren = wb.createSheet("BAMBINI");
		rowDate = sheetChildren.createRow(0);
		cellDateLabel = rowDate.createCell(0);
		cellDateValue = rowDate.createCell(1);
		cellDateLabel.setCellValue("Giorno");
		cellDateValue.setCellValue(date);
		cellDateValue.setCellStyle(cellStyleDate);
		
		List<String> valueList = Lists.newArrayList();
		for(WsnEvent event : events) {
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
		
		rowCounter = 1;
		for(String value : valueList) {
			Row rowData = sheetChildren.createRow(rowCounter);
			cellDateLabel = rowData.createCell(0);
			cellDateLabel.setCellValue("Presente");
			cellDateValue = rowData.createCell(1);
			cellDateValue.setCellValue(value);
			rowCounter++;
		}
		
    wb.write(outputStream);
	}
}
