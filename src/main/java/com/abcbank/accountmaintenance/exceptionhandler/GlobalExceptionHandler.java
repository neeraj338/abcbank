package com.abcbank.accountmaintenance.exceptionhandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.abcbank.accountmaintenance.util.AppUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ControllerAdvice(basePackages = { "com.abcbank.accountmaintenance" })
public class GlobalExceptionHandler extends ExceptionHandlerExceptionResolver {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


	@ExceptionHandler({ javax.validation.ConstraintViolationException.class,
			javax.validation.ValidationException.class,
			org.hibernate.exception.ConstraintViolationException.class,
			org.springframework.dao.DataIntegrityViolationException.class })
	public void constraintViolationException(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.CONFLICT.value());
	}

	@ExceptionHandler({ org.springframework.web.HttpMediaTypeNotSupportedException.class,
			HttpMessageNotReadableException.class })
	public ResponseEntity<?> handleControllerException(HttpMediaTypeNotSupportedException ex, WebRequest req) {

		String message = ExceptionUtils.getRootCauseMessage(ex);
		ObjectNode errorJsonNode = AppUtil.createErrorJsonNode(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
				((ServletWebRequest) req).getRequest().getRequestURI().toString(), message);
		logger.error(ExceptionUtils.getStackTrace(ex));
		return new ResponseEntity<>(errorJsonNode, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}
	
	/**
     * Handles MissingServletRequestParameterExceptions from the rest controller.
     * 
     * @param ex MissingServletRequestParameterException
     * @return error response POJO
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException( HttpServletRequest request, 
                                                                        MissingServletRequestParameterException ex) {
    	String message = ExceptionUtils.getRootCauseMessage(ex);
		ObjectNode errorJsonNode = AppUtil.createErrorJsonNode(HttpStatus.BAD_REQUEST,
				request.getRequestURI().toString(), message);

		logger.error(ExceptionUtils.getStackTrace(ex));

		return new ResponseEntity<>(errorJsonNode, HttpStatus.BAD_REQUEST);
    }
    
}