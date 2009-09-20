/*
 * This file is covered by the terms of the NASA Open Source Agreement v1.3.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.util;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.Error;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.report.Reporter;
import gov.nasa.jpf.search.Search;


public class TestReporter extends Reporter {

	List<Error> errors;

	public TestReporter(Config conf, JPF jpf) throws Config.Exception {
		super(conf, jpf);
		errors = new ArrayList<Error>(1);
	}

	public void publishError(Error error) {
		errors.clear();
		errors.add(error);
		super.propertyViolated(null);
	}

	@Override
	public void propertyViolated(Search search) {
		// do nothing
	}

	@Override
	public List<Error> getErrors() {
		return errors;
	}

	@Override
	public Error getLastError() {
		int i = errors.size();
		if (0 < i) {
			return errors.get(i-1);
		} else {
			return null;
		}
	}

	@Override
	public int getNumberOfErrors() {
		return errors.size();
	}
}
