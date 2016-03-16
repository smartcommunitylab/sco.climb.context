package it.smartcommunitylab.climb.contextdashboard.converter;

import it.smartcommunitylab.climb.contextdashboard.common.Const;
import it.smartcommunitylab.climb.contextdashboard.model.WsnEvent;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelConverter {
	private static final transient Logger logger = LoggerFactory.getLogger(ExcelConverter.class);
	
	public static void writeAttendance(Date date, List<WsnEvent> events, Route route, 
			List<Child> childList, List<Volunteer> volunteerList) {
		try {
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
					cellDateValue.setCellValue((String)event.getPayload().get("volunteerId"));
					rowCounter++;
				} else if(event.getEventType() == Const.SET_HELPER) {
					Row rowData = sheetVolunteers.createRow(rowCounter);
					cellDateLabel = rowData.createCell(0);
					cellDateLabel.setCellValue("Aiutante");
					cellDateValue = rowData.createCell(1);
					cellDateValue.setCellValue((String)event.getPayload().get("volunteerId"));
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
			
			FileOutputStream fileOut = new FileOutputStream("C:\\Users\\micnori\\Documents\\Tmp\\workbook.xls");
	    wb.write(fileOut);
	    fileOut.close();
			
		} catch (Exception e) {
			logger.error("error", e);
		}
	}
}
