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
import it.smartcommunitylab.climb.contextstore.model.Pedibus;
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
public class PedibusController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(PedibusController.class);
			
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/pedibus/{ownerId}", method = RequestMethod.GET)
	public @ResponseBody List<Pedibus> searchPedibus(
			@PathVariable String ownerId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Pedibus> result = (List<Pedibus>) storage.findData(Pedibus.class, null, null, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchPedibus[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/pedibus/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<Pedibus> searchPedibusBySchool(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("instituteId").is(instituteId)
				.and("schoolId").is(schoolId);
		List<Pedibus> result = (List<Pedibus>) storage.findData(Pedibus.class, criteria, null, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchPedibusBySchool[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/pedibus/{ownerId}/{instituteId}", method = RequestMethod.GET)
	public @ResponseBody List<Pedibus> searchPedibusByInstitute(
			@PathVariable String ownerId, 
			@PathVariable String instituteId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("instituteId").is(instituteId);
		List<Pedibus> result = (List<Pedibus>) storage.findData(Pedibus.class, criteria, null, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchPedibusByInstitute[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/pedibus/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody Pedibus addPedibus(@RequestBody Pedibus pedibus, @PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		pedibus.setOwnerId(ownerId);
		pedibus.setObjectId(Utils.getUUID());
		storage.addPedibus(pedibus);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addPedibus[%s]:%s", ownerId, pedibus.getObjectId()));
		}
		return pedibus;
	}

	@RequestMapping(value = "/api/pedibus/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody Pedibus updatePedibus(
			@RequestBody Pedibus pedibus, 
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		pedibus.setOwnerId(ownerId);
		pedibus.setObjectId(objectId);
		storage.updatePedibus(pedibus);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updatePedibus[%s]:%s", ownerId, pedibus.getObjectId()));
		}
		return pedibus;
	}
	
	@RequestMapping(value = "/api/pedibus/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deletePedibus(
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removePedibus(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deletePedibus[%s]:%s", ownerId, objectId));
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
