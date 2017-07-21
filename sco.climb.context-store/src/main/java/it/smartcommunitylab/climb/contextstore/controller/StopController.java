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
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.contextstore.storage.DataSetSetup;
import it.smartcommunitylab.climb.contextstore.storage.RepositoryManager;

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


@Controller
public class StopController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(StopController.class);
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/stop/{ownerId}/{routeId}", method = RequestMethod.GET)
	public @ResponseBody List<Stop> searchStop(@PathVariable String ownerId,  @PathVariable String routeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(routeId);
		Route route = storage.findOneData(Route.class, criteria, ownerId);
		if(!validateAuthorizationByExp(ownerId, route.getInstituteId(), route.getSchoolId(), 
				routeId,	"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		criteria = Criteria.where("routeId").is(routeId);
		Sort sort = new Sort(Sort.Direction.ASC, "position");
		List<Stop> result = (List<Stop>) storage.findData(Stop.class, criteria, sort, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchStop[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/stop/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody Stop addStop(@RequestBody Stop stop, @PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(stop.getRouteId());
		Route route = storage.findOneData(Route.class, criteria, ownerId);
		if(!validateAuthorizationByExp(ownerId, route.getInstituteId(), route.getSchoolId(), 
				route.getObjectId(),	"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		stop.setOwnerId(ownerId);
		stop.setObjectId(Utils.getUUID());
		storage.addStop(stop);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addStop[%s]:%s", ownerId, stop.getName()));
		}
		return stop;
	}

	@RequestMapping(value = "/api/stop/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody Stop updateStop(@RequestBody Stop stop, @PathVariable String ownerId, 
			@PathVariable String objectId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(stop.getRouteId());
		Route route = storage.findOneData(Route.class, criteria, ownerId);
		if(!validateAuthorizationByExp(ownerId, route.getInstituteId(), route.getSchoolId(), 
				route.getObjectId(),	"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		stop.setOwnerId(ownerId);
		stop.setObjectId(objectId);
		storage.updateStop(stop);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateStop[%s]:%s", ownerId, stop.getName()));
		}
		return stop;
	}
	
	@RequestMapping(value = "/api/stop/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteStop(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(objectId);
		Stop stop = storage.findOneData(Stop.class, criteria, ownerId);
		criteria = Criteria.where("objectId").is(stop.getRouteId());
		Route route = storage.findOneData(Route.class, criteria, ownerId);
		if(!validateAuthorizationByExp(ownerId, route.getInstituteId(), route.getSchoolId(), 
				route.getObjectId(),	"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeStop(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteStop[%s]:%s", ownerId, objectId));
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
