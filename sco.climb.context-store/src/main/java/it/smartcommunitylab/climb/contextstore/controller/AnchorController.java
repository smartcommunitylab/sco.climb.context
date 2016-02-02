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
import it.smartcommunitylab.climb.contextstore.model.Anchor;
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
public class AnchorController {
	private static final transient Logger logger = LoggerFactory.getLogger(AnchorController.class);
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/anchor/{ownerId}", method = RequestMethod.GET)
	public @ResponseBody List<Anchor> searchAnchor(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Anchor> result = (List<Anchor>) storage.findData(Anchor.class, null, null, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchAnchor[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/anchor/{ownerId}/{id}", method = RequestMethod.GET)
	public @ResponseBody Anchor searchAnchor(@PathVariable String ownerId, @PathVariable String id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("id").is(id);
		Anchor result = storage.findOneData(Anchor.class, criteria, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchAnchor[%s]:%s", ownerId, id));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/anchor/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody Anchor addAnchor(@RequestBody Anchor anchor, @PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		anchor.setOwnerId(ownerId);
		anchor.setId(Utils.getUUID());
		storage.addAnchor(anchor);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addAnchor[%s]:%s", ownerId, anchor.getName()));
		}
		return anchor;
	}

	@RequestMapping(value = "/api/anchor/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody Anchor updateAnchor(@RequestBody Anchor anchor, @PathVariable String ownerId, 
			@PathVariable String objectId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		anchor.setOwnerId(ownerId);
		anchor.setId(objectId);
		storage.updateAnchor(anchor);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateAnchor[%s]:%s", ownerId, anchor.getName()));
		}
		return anchor;
	}
	
	@RequestMapping(value = "/api/anchor/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteAnchor(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeAnchor(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteAnchor[%s]:%s", ownerId, objectId));
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
