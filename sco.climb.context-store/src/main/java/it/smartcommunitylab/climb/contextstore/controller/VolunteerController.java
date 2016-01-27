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
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.contextstore.storage.DataSetSetup;
import it.smartcommunitylab.climb.contextstore.storage.RepositoryManager;

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
public class VolunteerController {
	private static final transient Logger logger = LoggerFactory.getLogger(VolunteerController.class);
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/volunteer/{ownerId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<Volunteer> searchVolunteer(@PathVariable String ownerId, @PathVariable String schoolId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("schoolId").is(schoolId);
		List<Volunteer> result = (List<Volunteer>) storage.findData(Volunteer.class, criteria, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchVolunteer[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/volunteer/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody Volunteer addVolunteer(@RequestBody Volunteer volunteer, @PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		volunteer.setOwnerId(ownerId);
		volunteer.setId(Utils.getUUID());
		storage.addVolunteer(volunteer);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addVolunteer[%s]:%s", ownerId, volunteer.getName()));
		}
		return volunteer;
	}

	@RequestMapping(value = "/api/volunteer/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody Volunteer updateVolunteer(@RequestBody Volunteer volunteer, @PathVariable String ownerId, 
			@PathVariable String objectId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		volunteer.setOwnerId(ownerId);
		volunteer.setId(objectId);
		storage.updateVolunteer(volunteer);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateVolunteer[%s]:%s", ownerId, volunteer.getName()));
		}
		return volunteer;
	}
	
	@RequestMapping(value = "/api/volunteer/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteVolunteer(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeVolunteer(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteVolunteer[%s]:%s", ownerId, objectId));
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
