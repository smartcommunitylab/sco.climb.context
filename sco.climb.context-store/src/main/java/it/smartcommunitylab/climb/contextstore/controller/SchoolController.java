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
import it.smartcommunitylab.climb.contextstore.model.School;
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
public class SchoolController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(SchoolController.class);
			
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/school/{ownerId}/{instituteId}", method = RequestMethod.GET)
	public @ResponseBody List<School> searchSchool(
			@PathVariable String ownerId, 
			@PathVariable String instituteId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorizationByExp(ownerId, instituteId, null, null, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("instituteId").is(instituteId);
		List<School> result = (List<School>) storage.findData(School.class, criteria, null, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchSchool[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/school/{ownerId}/{instituteId}", method = RequestMethod.POST)
	public @ResponseBody School addSchool(
			@RequestBody School school, 
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorizationByExp(ownerId, instituteId, null, null, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}		
		school.setOwnerId(ownerId);
		school.setInstituteId(instituteId);
		school.setObjectId(Utils.getUUID());
		storage.addSchool(school);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addSchool[%s]:%s", ownerId, school.getName()));
		}
		return school;
	}

	@RequestMapping(value = "/api/school/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody School updateSchool(@RequestBody School school, @PathVariable String ownerId, 
			@PathVariable String objectId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!validateAuthorizationByExp(ownerId, school.getInstituteId(), school.getObjectId(), null, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		school.setOwnerId(ownerId);
		school.setObjectId(objectId);
		storage.updateSchool(school);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateSchool[%s]:%s", ownerId, school.getName()));
		}
		return school;
	}
	
	@RequestMapping(value = "/api/school/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteSchool(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("ownerId").is(ownerId).and("objectId").is(objectId);
		School school = storage.findOneData(School.class, criteria, ownerId);
		if(!validateAuthorizationByExp(ownerId, school.getInstituteId(), school.getObjectId(), null, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeSchool(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteSchool[%s]:%s", ownerId, objectId));
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
