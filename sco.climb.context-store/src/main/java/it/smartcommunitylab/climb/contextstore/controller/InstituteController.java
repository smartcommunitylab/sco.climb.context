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
import it.smartcommunitylab.climb.contextstore.model.Institute;
import it.smartcommunitylab.climb.contextstore.storage.DataSetSetup;
import it.smartcommunitylab.climb.contextstore.storage.RepositoryManager;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
public class InstituteController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(InstituteController.class);
			
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/institute/{ownerId}", method = RequestMethod.GET)
	public @ResponseBody List<Institute> searchInstitute(
			@PathVariable String ownerId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorizationByExp(ownerId, null, null, null, "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Institute> result = (List<Institute>) storage.findData(Institute.class, null, null, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchInstitute[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/institute/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody Institute addInstitute(
			@RequestBody Institute institute, 
			@PathVariable String ownerId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorizationByExp(ownerId, null, null, null, "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		institute.setOwnerId(ownerId);
		institute.setObjectId(Utils.getUUID());
		storage.addInstitute(institute);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addInstitute[%s]:%s", ownerId, institute.getName()));
		}
		return institute;
	}

	@RequestMapping(value = "/api/institute/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody Institute updateInstitute(
			@RequestBody Institute institute, 
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorizationByExp(ownerId, null, null, null, "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		institute.setOwnerId(ownerId);
		institute.setObjectId(objectId);
		storage.updateInstitute(institute);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateSchool[%s]:%s", ownerId, institute.getName()));
		}
		return institute;
	}
	
	@RequestMapping(value = "/api/institute/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteInstitute(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!validateAuthorizationByExp(ownerId, null, null, null, "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeInstitute(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteInstitute[%s]:%s", ownerId, objectId));
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
