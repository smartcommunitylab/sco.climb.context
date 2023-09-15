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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.security.DataSetInfo;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;


@Controller
public class ConsoleController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(ConsoleController.class);

	@Autowired
	private RepositoryManager storage;
		
	@RequestMapping(method = RequestMethod.GET, value = "/api/console/data")
	public @ResponseBody DataSetInfo data(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return getDataSetInfo(request, response);
	}
	
    @RequestMapping(method = RequestMethod.POST, value = "/api/console/user/accept-terms")
    public @ResponseBody void acceptTermUsage(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	User user = getUserByEmail(request);
      if (user != null) {
          user.acceptTerms();
          storage.updateUser(user);
      }
    }

	private DataSetInfo getDataSetInfo(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		User user = getUserByEmail(request);
		DataSetInfo dsInfo = new DataSetInfo();
		dsInfo.setCf(user.getCf());
		dsInfo.setEmail(user.getEmail());
		dsInfo.setName(user.getName());
		dsInfo.setSurname(user.getSurname());
		dsInfo.setSubject(user.getSubject());
		dsInfo.setOwnerIds(Utils.getUserOwnerIds(user));
		dsInfo.getOwnerIds().remove(Const.SYSTEM_DOMAIN);
		dsInfo.setRoles(Utils.getUserRoles(user));
        dsInfo.setTermUsage(user.getTermUsage());
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDataSetInfo:%s - %s", user.getEmail(), user.getCf()));
		}        
		return dsInfo;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ResponseBody
	public Map<String,String> handleUnauthrizedError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}

}
