package org.pazu.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

public interface ExceptionAttributes {

	/**
	 * Returns a {@link Map} of exception attributes. The Map may be used to
	 * display an error page or serialized into a {@link ResponseBody}.
	 * 
	 * @param exception
	 *            The Exception reported.
	 * @param httpRequest
	 *            The HttpServletRequest in which the Exception occurred.
	 * @param httpStatus
	 *            The HttpStatus value that will be used in the
	 *            {@link HttpServletResponse}.
	 * @return A Map of exception attributes.
	 */
	Map<String, Object> getExceptionAttributes(Exception exception,
			HttpServletRequest httpRequest, HttpStatus httpStatus);

}
