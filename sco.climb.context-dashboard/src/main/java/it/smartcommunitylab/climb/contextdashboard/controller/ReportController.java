/**
 *    Copyright 2015 Fondazione Bruno Kessler - Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package it.smartcommunitylab.climb.contextdashboard.controller;

import it.smartcommunitylab.climb.contextdashboard.common.Const;
import it.smartcommunitylab.climb.contextdashboard.common.Utils;
import it.smartcommunitylab.climb.contextdashboard.converter.ExcelConverter;
import it.smartcommunitylab.climb.contextdashboard.exception.InvalidParametersException;
import it.smartcommunitylab.climb.contextdashboard.model.WsnEvent;
import it.smartcommunitylab.climb.contextdashboard.rest.ContextStoreManager;
import it.smartcommunitylab.climb.contextdashboard.rest.EventStoreManager;
import it.smartcommunitylab.climb.contextdashboard.storage.DataSetSetup;
import it.smartcommunitylab.climb.contextdashboard.storage.RepositoryManager;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;


@Controller
public class ReportController {
	private static final transient Logger logger = LoggerFactory.getLogger(ReportController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@Autowired
	private EventStoreManager eventStoreManager;
	
	@Autowired
	private ContextStoreManager contextStoreManager;
	
	@RequestMapping(value = "/report/context/url", method = RequestMethod.GET)
	public @ResponseBody String getContextApiUrl() {
		return "{\"url\":\"" + contextStoreManager.getContextApiUrl() + "\"}";
	}
	
	@RequestMapping(value = "/report/attendance/{ownerId}", method = RequestMethod.GET)
	public void writeAttendance(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<WsnEvent> eventList = Lists.newArrayList();
		List<Child> childList = Lists.newArrayList();
		List<Volunteer> volunteerList = Lists.newArrayList();
		String schoolId = request.getParameter("schoolId");
		String routeId = request.getParameter("routeId");
		String dateFromString = request.getParameter("dateFrom");
		String dateToString = request.getParameter("dateTo");
		List<Integer> eventTypeList = Lists.newArrayList();
		eventTypeList.add(Const.NODE_AT_DESTINATION);
		eventTypeList.add(Const.SET_DRIVER);
		eventTypeList.add(Const.SET_HELPER);
 		try {
 			eventList= eventStoreManager.searchEvents(dateFromString, dateToString, routeId, eventTypeList, dataSetSetup, ownerId);
 			childList = contextStoreManager.getChildList(schoolId, dataSetSetup, ownerId);
 			volunteerList = contextStoreManager.getVolunteerList(schoolId, dataSetSetup, ownerId);
 			ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
 			Date dateFrom = sdf.parse(dateFromString);
 			Date dateTo = sdf.parse(dateToString);
			ExcelConverter.writeAttendance(dateFrom, dateTo, eventList, childList, volunteerList, outputBuffer);
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition", "attachment; filename=\"report-" + ownerId + ".xls\"");
			response.addHeader("Content-Transfer-Encoding", "binary");
			response.addHeader("Cache-control", "no-cache");
			response.getOutputStream().write(outputBuffer.toByteArray());
			response.getOutputStream().flush();
			outputBuffer.close();
		} catch (Exception e) {
			logger.error("writeAttendance:" + e.getMessage());
			throw new InvalidParametersException("Invalid query parameters:" + e.getMessage());
		}
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
	
}
