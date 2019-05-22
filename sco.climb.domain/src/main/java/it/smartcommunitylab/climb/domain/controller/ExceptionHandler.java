package it.smartcommunitylab.climb.domain.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.smartcommunitylab.climb.domain.common.Utils;

@ControllerAdvice
public class ExceptionHandler {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgumentException(HttpServletRequest request, Exception exception) {
        return Utils.handleError(exception);
    }
}
