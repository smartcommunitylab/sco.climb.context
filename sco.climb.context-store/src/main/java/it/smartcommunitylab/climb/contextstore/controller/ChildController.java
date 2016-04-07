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
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.storage.DataSetSetup;
import it.smartcommunitylab.climb.contextstore.storage.RepositoryManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class ChildController {
	private static final transient Logger logger = LoggerFactory.getLogger(ChildController.class);
	
	@Autowired
	@Value("${image.upload.dir}")
	private String imageUploadDir;
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/child/{ownerId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<Child> searchChild(@PathVariable String ownerId,  @PathVariable String schoolId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("schoolId").is(schoolId);
		List<Child> result = (List<Child>) storage.findData(Child.class, criteria, null, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchChild[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/child/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody Child addChild(@RequestBody Child child, @PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		child.setOwnerId(ownerId);
		child.setObjectId(Utils.getUUID());
		storage.addChild(child);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addChild[%s]:%s", ownerId, child.getName()));
		}
		return child;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/child/{ownerId}/{schoolId}/classroom", method = RequestMethod.GET)
	public @ResponseBody List<Child> searchChild(@PathVariable String ownerId, @PathVariable String schoolId, 
			@RequestParam String classRoom, HttpServletRequest request, HttpServletResponse response)	throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("schoolId").is(schoolId).and("classRoom").is(classRoom);
		List<Child> result = (List<Child>) storage.findData(Child.class, criteria, null, ownerId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("searchChild[%s]:%d", ownerId, result.size()));
		}
		return result;
	}	
	

	@RequestMapping(value = "/api/child/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody Child updateChild(@RequestBody Child child, @PathVariable String ownerId, 
			@PathVariable String objectId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		child.setOwnerId(ownerId);
		child.setObjectId(objectId);
		storage.updateChild(child);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateChild[%s]:%s", ownerId, child.getName()));
		}
		return child;
	}
	
	@RequestMapping(value = "/api/child/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteChild(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeChild(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteChild[%s]:%s", ownerId, objectId));
		}
		return "{\"status\":\"OK\"}";
	}
	
	@RequestMapping(value = "/api/image/upload/png/{ownerId}/{objectId}", method = RequestMethod.POST)
	public @ResponseBody String uploadImage(@RequestParam("file") MultipartFile file,
			@PathVariable String ownerId, @PathVariable String objectId,
			HttpServletRequest request) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		String name = objectId + ".png";
		if(logger.isInfoEnabled()) {
			logger.info("uploadImage:" + name);
		}
		if (!file.isEmpty()) {
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(
					new File(imageUploadDir + "/" + name)));
			FileCopyUtils.copy(file.getInputStream(), stream);
			stream.close();
		}
		return "{\"status\":\"OK\"}";
	}
	
	@RequestMapping(value = "/api/image/download/png/{ownerId}/{objectId}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<byte[]> downloadImage(@PathVariable String ownerId, @PathVariable String objectId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		String name = objectId + ".png";
		String path = imageUploadDir + "/" + name;
		if(logger.isInfoEnabled()) {
			logger.info("downloadImage:" + name);
		}
		FileInputStream in = new FileInputStream(new File(path));
		byte[] image = IOUtils.toByteArray(in);
		HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.IMAGE_PNG);
    headers.setContentLength(image.length);
    return new HttpEntity<byte[]>(image, headers);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
	
}
