package com.abcbank.accountmaintenance.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AppUtil {

	private static final Logger LOG = LoggerFactory.getLogger(AppUtil.class);

	public static boolean isNotNullObjects(Object... objects) {
		boolean isAnyNull = Arrays.stream(objects).anyMatch(x -> Objects.isNull(x));
		return !isAnyNull;
	}

	public static boolean isNullObject(Object object) {
		return !isNotNullObjects(object);
	}

	/**
	 * <pre>
	 * if <i>String</i>: 
	 *  null	@return false
	 *  ""	@return false
	 *  " "	@return false
	 *  if <i>Collection</i>:
	 *  null or empty	@return false
	 *  if <i>Map</i>:
	 *  null or empty	@return false
	 * </pre>
	 * 
	 * @param objects
	 * @return
	 */
	public static boolean isNotNullNotEmptyObjects(Object... objects) {
		Predicate<Object> predicateStr = (x -> String.class.isAssignableFrom(x.getClass())
				&& StringUtils.isEmpty(x.toString().trim()));
		Predicate<Object> predicateColl = (x -> Collection.class.isAssignableFrom(x.getClass())
				&& ((Collection<?>) x).isEmpty());
		Predicate<Object> predicateMap = (x -> Map.class.isAssignableFrom(x.getClass()) && ((Map<?, ?>) x).isEmpty());

		return isNotNullObjects(objects) && !Arrays.stream(objects)
				.filter(x -> predicateStr.or(predicateColl).or(predicateMap).test(x)).findAny().isPresent();
	}



	/**
	 * <p>
	 * Note: outClass object must have default constructor with no arguments
	 * </p>
	 *
	 * @param entityList list of entities that needs to be mapped
	 * @param outCLass class of result list element
	 * @param modelMapper ModelMapper instance
	 * @param <D> type of objects in result list
	 * @param <T> type of entity in <code>entityList</code>
	 * @return list of mapped object with <code><D></code> type.
	 */
	public static <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass, ModelMapper modelMapper) {
		return entityList.stream().map(entity -> modelMapper.map(entity, outCLass)).collect(Collectors.toList());
	}

	public static ObjectNode createJsonNode() {
		return JsonNodeFactory.instance.objectNode();
	}

	public static ObjectNode createSuccessJsonNode(HttpStatus status, String uri, String message) {
		ObjectNode jsonNode = createJsonNode();
		jsonNode.putPOJO("timestamp", new Date());
		jsonNode.put("status", status.value());
		jsonNode.putPOJO("reason", status.getReasonPhrase());
		jsonNode.put("path", uri);
		jsonNode.put("message", message);

		return jsonNode;
	}

	public static ObjectNode createErrorJsonNode(HttpStatus status, String uri, String message) {
		ObjectNode jsonNode = createJsonNode();
		jsonNode.putPOJO("timestamp", new Date());
		jsonNode.put("status", status.value());
		jsonNode.putPOJO("error", status.getReasonPhrase());
		jsonNode.put("path", uri);
		jsonNode.put("message", message);

		return jsonNode;
	}

	public static ObjectNode createErrorJsonNode(HttpStatus status, String uri, Map<String, ObjectNode> messageMap) {
		ObjectNode jsonNode = createJsonNode();
		jsonNode.putPOJO("timestamp", new Date());
		jsonNode.put("status", status.value());
		jsonNode.putPOJO("error", status.getReasonPhrase());
		jsonNode.put("path", uri);

		ArrayNode arrayJsonNode = jsonNode.putArray("messages");

		// Get all errors
		messageMap.forEach((k, v) -> arrayJsonNode.add(v));

		return jsonNode;
	}

	public static Date truncateDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND));
		return c.getTime();
	}

	public static <T> HttpEntity<T> getEnityWithHttpHeader(T requestObject) {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<T> request = new HttpEntity<>(requestObject, headers);

		return request;
	}

	public static HttpEntity<?> getHttpHeader() {
		HttpHeaders headers = new HttpHeaders();

		HttpEntity<?> request = new HttpEntity<>(headers);

		return request;
	}

}
