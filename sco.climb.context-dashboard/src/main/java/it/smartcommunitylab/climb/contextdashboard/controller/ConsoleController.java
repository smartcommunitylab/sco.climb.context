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

package it.smartcommunitylab.climb.contextdashboard.controller;

import it.smartcommunitylab.climb.contextdashboard.common.Utils;
import it.smartcommunitylab.climb.contextdashboard.security.DataSetDetails;
import it.smartcommunitylab.climb.contextdashboard.security.DataSetInfo;
import it.smartcommunitylab.climb.contextdashboard.storage.RepositoryManager;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;


@Controller
public class ConsoleController {

	@Autowired
	private ServletContext context;		
	
	@Autowired
	private RepositoryManager storage;
	
	@RequestMapping(value = "/")
	public View root() {
		return new RedirectView("console");
	}		
	
	@RequestMapping(value = "/upload")
	public String upload() {
		return "upload";
	}

	@RequestMapping(value = "/login")
	public String login() {
		return "login";
	}		
	
	@RequestMapping(value = "/console")
	public String console() {
		return "console";
	}
	
	@RequestMapping(value = "/console/data")
	public @ResponseBody DataSetInfo data(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return storage.getDataSetInfo(getOwnerId());
	}
	
	@RequestMapping(value = "/report-appello")
	public String reportAppello() {
		return "report-appello";
	}	
	
	private String getOwnerId() {
		DataSetDetails details = (DataSetDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = details.getUsername();
		return name;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
	
}
