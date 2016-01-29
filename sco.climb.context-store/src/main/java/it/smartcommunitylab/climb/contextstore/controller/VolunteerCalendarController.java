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

package it.smartcommunitylab.climb.contextstore.controller;

import it.smartcommunitylab.climb.contextstore.common.Utils;
import it.smartcommunitylab.climb.contextstore.exception.UnauthorizedException;
import it.smartcommunitylab.climb.contextstore.model.PassengerCalendar;
import it.smartcommunitylab.climb.contextstore.model.VolunteerCalendar;
import it.smartcommunitylab.climb.contextstore.storage.DataSetSetup;
import it.smartcommunitylab.climb.contextstore.storage.RepositoryManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;


@Controller
public class VolunteerCalendarController {
	private static final transient Logger logger = LoggerFactory.getLogger(VolunteerCalendarController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/volunteercal/{ownerId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<VolunteerCalendar> searchVolunteerCalendar(@PathVariable String ownerId, 
			@PathVariable String schoolId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<VolunteerCalendar> result = Lists.newArrayList();
		Criteria criteria = Criteria.where("schoolId").is(schoolId);
		String dateFromString = request.getParameter("dateFrom");
		String dateToString = request.getParameter("dateTo");
		if(Utils.isNotEmpty(dateFromString) && Utils.isNotEmpty(dateToString)) {
			Date dateFrom = sdf.parse(dateFromString);
			Date dateTo = sdf.parse(dateToString);
			criteria = criteria.andOperator(
					Criteria.where("date").lte(dateTo),
					Criteria.where("date").gte(dateFrom));
			result = (List<VolunteerCalendar>) storage.findData(VolunteerCalendar.class, criteria, ownerId);
		}		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchVolunteerCalendar[%s]:%s - %d", ownerId, schoolId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/volunteercal/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody VolunteerCalendar addVolunteerCalendar(@RequestBody VolunteerCalendar calendar, 
			@PathVariable String ownerId,	HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		calendar.setOwnerId(ownerId);
		calendar.setId(Utils.getUUID());
		storage.addVolunteerCalendar(calendar);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addVolunteerCalendar[%s]:%s - %s", ownerId, calendar.getRouteId(), 
					sdf.format(calendar.getDate())));
		}
		return calendar;
	}

	@RequestMapping(value = "/api/volunteercal/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody VolunteerCalendar updateVolunteerCalendar(@RequestBody VolunteerCalendar calendar, 
			@PathVariable String ownerId,	@PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		calendar.setOwnerId(ownerId);
		calendar.setId(objectId);
		storage.updateVolunteerCalendar(calendar);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateVolunteerCalendar[%s]:%s - %s", ownerId, calendar.getRouteId(), 
					sdf.format(calendar.getDate())));
		}
		return calendar;
	}
	
	@RequestMapping(value = "/api/volunteercal/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteVolunteerCalendar(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeVolunteerCalendar(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteVolunteerCalendar[%s]:%s", ownerId, objectId));
		}
		return "OK";
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
	
}
