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

import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.model.multimedia.MultimediaContent;
import it.smartcommunitylab.climb.domain.model.multimedia.MultimediaResult;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
public class MultimediaController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(MultimediaController.class);
			
	@Autowired
	private RepositoryManager storage;

	@RequestMapping(value = "/api/multimedia", method = RequestMethod.GET)
	public @ResponseBody List<MultimediaResult> searchContent(
			@RequestParam (required=false) String text,
			@RequestParam (required=false) Double lat,
			@RequestParam (required=false) Double lng,
			@RequestParam (required=false) String schoolId,
			@RequestParam (required=false) String type,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		List<MultimediaResult> result = new ArrayList<MultimediaResult>();
		List<MultimediaContent> list = storage.searchMultimediaContent(text, lat, lng, schoolId, type);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchContent:%s", result.size()));
		}
		return result;
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
	
}
