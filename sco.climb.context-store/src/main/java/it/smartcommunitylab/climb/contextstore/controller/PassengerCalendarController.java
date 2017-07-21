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
import org.springframework.data.domain.Sort;
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
public class PassengerCalendarController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(PassengerCalendarController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/passengercal/{ownerId}/{routeId}", method = RequestMethod.GET)
	public @ResponseBody List<PassengerCalendar> searchPassengerCalendar(@PathVariable String ownerId, 
			@PathVariable String routeId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PassengerCalendar> result = Lists.newArrayList();
		Criteria criteria = Criteria.where("routeId").is(routeId);
		String dateFromString = request.getParameter("dateFrom");
		String dateToString = request.getParameter("dateTo");
		if(Utils.isNotEmpty(dateFromString) && Utils.isNotEmpty(dateToString)) {
			Date dateFrom = sdf.parse(dateFromString);
			Date dateTo = sdf.parse(dateToString);
			criteria = criteria.andOperator(
					Criteria.where("date").lte(dateTo),
					Criteria.where("date").gte(dateFrom));
			Sort sort = new Sort(Sort.Direction.ASC, "date");
			result = (List<PassengerCalendar>) storage.findData(PassengerCalendar.class, criteria, sort, ownerId);
		}		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchPassengerCalendar[%s]:%s - %d", ownerId, routeId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/passengercal/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody PassengerCalendar addPassengerCalendar(@RequestBody PassengerCalendar calendar, 
			@PathVariable String ownerId,	HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		calendar.setOwnerId(ownerId);
		calendar.setObjectId(Utils.getUUID());
		storage.addPassengerCalendar(calendar);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addPassengerCalendar[%s]:%s - %s", ownerId, calendar.getRouteId(), 
					sdf.format(calendar.getDate())));
		}
		return calendar;
	}

	@RequestMapping(value = "/api/passengercal/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody PassengerCalendar updatePassengerCalendar(@RequestBody PassengerCalendar calendar, 
			@PathVariable String ownerId,	@PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		calendar.setOwnerId(ownerId);
		calendar.setObjectId(objectId);
		storage.updatePassengerCalendar(calendar);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updatePassengerCalendar[%s]:%s - %s", ownerId, calendar.getRouteId(), 
					sdf.format(calendar.getDate())));
		}
		return calendar;
	}
	
	@RequestMapping(value = "/api/passengercal/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deletePassengerCalendar(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removePassengerCalendar(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deletePassengerCalendar[%s]:%s", ownerId, objectId));
		}
		return "{\"status\":\"OK\"}";
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
	
}
