package org.dei.perla.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.dei.perla.fpc.Attribute;
import org.dei.perla.fpc.Fpc;
import org.dei.perla.fpc.descriptor.DataType;
import org.dei.perla.rest.controller.PerLaController;
import org.dei.perla.rest.controller.PerLaException;
import org.dei.perla.rest.controller.RestTask;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/rest/v1")
public class RestV1 {

	@Resource
	private PerLaController ctrl;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<Object> startQuery(final WebRequest req) {
		Map<String, String[]> params = new HashMap<>(req.getParameterMap());

		if (params.size() == 0) {
			return new ResponseEntity<>(new Result("error",
					"missing query attributes"), HttpStatus.BAD_REQUEST);
		}

		if (!params.containsKey("period") || params.get("period").length == 0) {
			return new ResponseEntity<>(new Result("error", "missing period"),
					HttpStatus.BAD_REQUEST);
		}

		long period = 0;
		try {
			period = Long.parseLong(params.get("period")[0]);
		} catch (NumberFormatException e) {
			return new ResponseEntity<>(new Result("error",
					"wrong period value: " + e.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
		params.remove("period");

		Collection<Attribute> atts = null;
		try {
			atts = parseAttributes(params);
		} catch (PerLaException e) {
			return new ResponseEntity<>(new Result(e), HttpStatus.BAD_REQUEST);
		}

		try {
			RestTask t = ctrl.queryPeriodic(atts, period);
            return new ResponseEntity<>(t, HttpStatus.ACCEPTED);
		} catch (PerLaException e) {
			return new ResponseEntity<>(new Result(e), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/query/running", method = RequestMethod.GET)
	public ResponseEntity<Object> getAllRunningQueries() {
		Collection<RestTask> tasks = ctrl.getAllTasks();
		return new ResponseEntity<>(tasks, HttpStatus.OK);
	}

	@RequestMapping(value = "/query/running/{id}", method = RequestMethod.GET)
	public ResponseEntity<Object> getRunningQuery(@PathVariable("id") Integer id) {
		RestTask t = ctrl.getTask(id);
		if (t == null) {
			return new ResponseEntity<>(new Result("error", "not found"),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(t, HttpStatus.OK);
	}

	@RequestMapping(value = "/query/running/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Object> stopRunningQuery(
			@PathVariable("id") Integer id) {
		RestTask t = ctrl.getTask(id);
		if (t == null) {
			return new ResponseEntity<>(new Result("error", "not found"),
					HttpStatus.NOT_FOUND);
		}

		ctrl.stopTask(id);
		return new ResponseEntity<>(new Result("ok", "stopping task '" + id
				+ "'"), HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/fpc", method = RequestMethod.PUT, headers = "content-type=text/xml,application/xml")
	public ResponseEntity<Object> createFPC(@RequestBody byte[] descriptor) {
		InputStream is = new ByteArrayInputStream(descriptor);
		try {
			return new ResponseEntity<>(ctrl.createFpc(is), HttpStatus.CREATED);
		} catch (PerLaException e) {
			return new ResponseEntity<>(new Result(e), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/fpc", method = RequestMethod.GET)
	public ResponseEntity<Object> listFPC(final WebRequest req) {
		Map<String, String[]> params = req.getParameterMap();

		if (params.size() == 0) {
			return new ResponseEntity<>(ctrl.getAllFpcs(), HttpStatus.OK);
		}

		Collection<Attribute> atts = null;
		try {
			atts = parseAttributes(params);
		} catch (PerLaException e) {
			return new ResponseEntity<>(new Result(e), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(ctrl.getFpcByAttribute(atts), HttpStatus.OK);
	}

	@RequestMapping(value = "/fpc/{id}", method = RequestMethod.GET)
	public ResponseEntity<Object> getFPC(@PathVariable("id") Integer id) {
		Fpc fpc = ctrl.getFpc(id);
		if (fpc == null) {
			return new ResponseEntity<>(new Error("not found"),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(fpc, HttpStatus.OK);
	}

	public Collection<Attribute> parseAttributes(Map<String, String[]> query)
			throws PerLaException {
		List<Attribute> atts = new ArrayList<>();
		for (Map.Entry<String, String[]> e : query.entrySet()) {
			String n = e.getKey();
			if (e.getValue().length == 0) {
				throw new PerLaException("missing data type for attribute '"
						+ n + "'");
			}
			DataType t = parseDataType(e.getValue()[0]);
			atts.add(Attribute.create(n, t));
		}
		return atts;
	}

	public DataType parseDataType(String type) throws PerLaException {
		try {
			return DataType.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException exc) {
			throw new PerLaException("'" + type
					+ "' is not a valid PerLa data type");
		}
	}

}
