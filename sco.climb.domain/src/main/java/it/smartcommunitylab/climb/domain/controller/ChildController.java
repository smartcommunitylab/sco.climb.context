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

package it.smartcommunitylab.climb.domain.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.StorageException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.model.Avatar;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;


@Controller
public class ChildController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(ChildController.class);
	
	@Autowired
	@Value("${image.upload.dir}")
	private String imageUploadDir;
	
	@Autowired
	private RepositoryManager storage;
	
	private Avatar defaultAvatar;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
		Resource resource = new ClassPathResource("img/placeholder_child.png", 
				this.getClass().getClassLoader());
		try {
			byte[] imageByte = new byte[(int) resource.contentLength()];
			resource.getInputStream().read(imageByte, 0, imageByte.length);
			defaultAvatar = new Avatar();
			defaultAvatar.setContentType(MediaType.IMAGE_PNG_VALUE);
			defaultAvatar.setImage(new Binary(imageByte));
		} catch (IOException e) {
			logger.error(String.format("onApplicationEvent:%s", e.getMessage()));
		}
    }
    
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/child/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<Child> searchChild(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, null, null, 
				Const.AUTH_RES_Child, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("instituteId").is(instituteId).and("schoolId").is(schoolId);
		List<Child> result = (List<Child>) storage.findData(Child.class, criteria, null, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchChild[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/child/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody Child addChild(
			@RequestBody Child child, 
			@PathVariable String ownerId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		String instituteId = child.getInstituteId();
		String schoolId = child.getSchoolId();
		if(!validateAuthorization(ownerId, instituteId, schoolId, null, null, 
				Const.AUTH_RES_Child, Const.AUTH_ACTION_ADD, request)) {
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
	@RequestMapping(value = "/api/child/{ownerId}/{instituteId}/{schoolId}/classroom", method = RequestMethod.GET)
	public @ResponseBody List<Child> searchChildByClassroom(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId, 
			@RequestParam String classRoom, 
			HttpServletRequest request, 
			HttpServletResponse response)	throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, null, null, 
				Const.AUTH_RES_Child, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("instituteId").is(instituteId)
				.and("schoolId").is(schoolId).and("classRoom").is(classRoom);
		List<Child> result = (List<Child>) storage.findData(Child.class, criteria, null, ownerId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("searchChildByClassroom[%s]:%d", ownerId, result.size()));
		}
		return result;
	}	
	

	@RequestMapping(value = "/api/child/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody Child updateChild(
			@RequestBody Child child, 
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, child.getInstituteId(), child.getSchoolId(), 
				null, null, Const.AUTH_RES_Child, Const.AUTH_ACTION_UPDATE, request)) {
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
	public @ResponseBody String deleteChild(
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(objectId).and("ownerId").is(ownerId);
		Child child = storage.findOneData(Child.class, criteria, ownerId);
		if(child == null) {
			throw new EntityNotFoundException("child not found");
		}
		if(!validateAuthorization(ownerId, child.getInstituteId(), child.getSchoolId(), 
				null,	null, Const.AUTH_RES_Child, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeChild(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteChild[%s]:%s", ownerId, objectId));
		}
		return "{\"status\":\"OK\"}";
	}
	
	@RequestMapping(value = "/api/child/image/upload/png/{ownerId}/{objectId}", method = RequestMethod.POST)
	public @ResponseBody String uploadImage(
			@RequestParam("file") MultipartFile file,
			@PathVariable String ownerId, 
			@PathVariable String objectId,
			HttpServletRequest request) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(objectId);
		Child child = storage.findOneData(Child.class, criteria, ownerId);
		if(child == null) {
			throw new EntityNotFoundException("child not found");
		}
		if(!validateAuthorization(ownerId, child.getInstituteId(), child.getSchoolId(), 
				null,	null, Const.AUTH_RES_Image, Const.AUTH_ACTION_ADD, request)) {
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
	
	@RequestMapping(value = "/api/child/image/download/{imageType}/{ownerId}/{objectId}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<byte[]> downloadImage(
			@PathVariable String imageType, 
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(objectId);
		Child child = storage.findOneData(Child.class, criteria, ownerId);
		if(child == null) {
			throw new EntityNotFoundException("child not found");
		}
		if(!validateAuthorization(ownerId, child.getInstituteId(), child.getSchoolId(), 
				null,	null, Const.AUTH_RES_Image, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		String name = objectId + "." + imageType;
		String path = imageUploadDir + "/" + name;
		if(logger.isInfoEnabled()) {
			logger.info("downloadImage:" + name);
		}
		FileInputStream in = new FileInputStream(new File(path));
		byte[] image = IOUtils.toByteArray(in);
		HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.IMAGE_PNG);
		if(imageType.toLowerCase().equals("png")) {
			headers.setContentType(MediaType.IMAGE_PNG);
		} else if(imageType.toLowerCase().equals("gif")) {
			headers.setContentType(MediaType.IMAGE_GIF);
		} else if(imageType.toLowerCase().equals("jpg")) {
			headers.setContentType(MediaType.IMAGE_JPEG);
		} else if(imageType.toLowerCase().equals("jpeg")) {
			headers.setContentType(MediaType.IMAGE_JPEG);
		}
    headers.setContentLength(image.length);
    return new HttpEntity<byte[]>(image, headers);
	}
	
	@RequestMapping(value = "/api/child/image/upload/{ownerId}/{objectId}", method = RequestMethod.POST)
	public @ResponseBody void uploadAvatar(
			@PathVariable String ownerId, 
			@PathVariable String objectId,
			@RequestParam("data") MultipartFile data,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(objectId);
		Child child = storage.findOneData(Child.class, criteria, ownerId);
		if(child == null) {
			throw new EntityNotFoundException("child not found");
		}
		if(!validateAuthorization(ownerId, child.getInstituteId(), child.getSchoolId(), 
				null,	null, Const.AUTH_RES_Image, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		criteria = Criteria.where("resourceId").is(objectId).and("resourceType").is(Const.AUTH_RES_Child);
		Avatar avatar = storage.findOneData(Avatar.class, criteria, ownerId);
		if(avatar == null) {
			avatar = new Avatar();
			avatar.setOwnerId(ownerId);
			avatar.setObjectId(Utils.getUUID());
			avatar.setResourceId(objectId);
			avatar.setResourceType(Const.AUTH_RES_Child);
		}
		avatar.setContentType(data.getContentType());
		avatar.setImage(new Binary(data.getBytes()));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadAvatar[%s]:%s", ownerId, objectId));
		}		
		storage.saveAvatar(avatar);
	}
	
	@RequestMapping(value = "/api/child/image/info/{ownerId}/{objectId}", method = RequestMethod.GET)
	public @ResponseBody Avatar getAvatarInfo(
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(objectId);
		Child child = storage.findOneData(Child.class, criteria, ownerId);
		if(child == null) {
			throw new EntityNotFoundException("child not found");
		}
		if(!validateAuthorization(ownerId, child.getInstituteId(), child.getSchoolId(), 
				null,	null, Const.AUTH_RES_Image, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		criteria = Criteria.where("resourceId").is(objectId).and("resourceType").is(Const.AUTH_RES_Child);
		Avatar avatar = storage.findOneData(Avatar.class, criteria, ownerId);
		if(avatar == null) {
			throw new EntityNotFoundException("avatar not found");
		}
		avatar.setImage(null);
		return avatar;
	}
	
	@RequestMapping(value = "/api/child/image/download/{ownerId}/{objectId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<byte[]> downloadAvatar(
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(objectId);
		Child child = storage.findOneData(Child.class, criteria, ownerId);
		if(child == null) {
			throw new EntityNotFoundException("child not found");
		}
		if(!validateAuthorization(ownerId, child.getInstituteId(), child.getSchoolId(), 
				null,	null, Const.AUTH_RES_Image, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		criteria = Criteria.where("resourceId").is(objectId).and("resourceType").is(Const.AUTH_RES_Child);
		Avatar avatar = storage.findOneData(Avatar.class, criteria, ownerId);
		if(avatar == null) {
			avatar = defaultAvatar;
		}
		byte[] data = avatar.getImage().getData();
		if(logger.isInfoEnabled()) {
			logger.info(String.format("downloadAvatar[%s]:%s", ownerId, objectId));
		}
		response.setHeader("Cache-Control", "public, max-age=172800");
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(avatar.getContentType()))
				.contentLength(data.length)
				.body(data);
	}
	
	@ExceptionHandler({EntityNotFoundException.class, StorageException.class})
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String,String> handleEntityNotFoundError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ResponseBody
	public Map<String,String> handleUnauthorizedError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleGenericError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}		
	
}
