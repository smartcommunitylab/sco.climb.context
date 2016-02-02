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
import it.smartcommunitylab.climb.contextstore.model.Route;
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


@Controller
public class RouteController {
	private static final transient Logger logger = LoggerFactory.getLogger(RouteController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/route/{ownerId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<Route> searchRoute(@PathVariable String ownerId,  @PathVariable String schoolId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("schoolId").is(schoolId);
		String dateString = request.getParameter("date");
		if(Utils.isNotEmpty(dateString)) {
			Date date = sdf.parse(dateString);
			criteria = criteria.andOperator(
					Criteria.where("from").lte(date), 
					Criteria.where("to").gte(date));
		}
		List<Route> result = (List<Route>) storage.findData(Route.class, criteria, null, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchRoute[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/route/{ownerId}/{routeId}", method = RequestMethod.GET)
	public @ResponseBody Route searchRouteById(@PathVariable String ownerId,  @PathVariable String routeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("id").is(routeId);
		Route result = storage.findOneData(Route.class, criteria, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchRouteById[%s]:%s", ownerId, routeId));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/route/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody Route addRoute(@RequestBody Route route, @PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		route.setOwnerId(ownerId);
		route.setId(Utils.getUUID());
		storage.addRoute(route);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addRoute[%s]:%s", ownerId, route.getName()));
		}
		return route;
	}

	@RequestMapping(value = "/api/route/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody Route updateRoute(@RequestBody Route route, @PathVariable String ownerId, 
			@PathVariable String objectId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		route.setOwnerId(ownerId);
		route.setId(objectId);
		storage.updateRoute(route);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateRoute[%s]:%s", ownerId, route.getName()));
		}
		return route;
	}
	
	@RequestMapping(value = "/api/route/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteRoute(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeRoute(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteRoute[%s]:%s", ownerId, objectId));
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
