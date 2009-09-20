/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.outside;

import gov.nasa.jpf.Error;
import gov.nasa.jpf.Property;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.search.SearchListener;

import de.fzi.cjunit.JPFPropertyViolated;


public class ResultCollector implements TestProperty, SearchListener {

	protected Throwable exception;

	// from TestProperty
	@Override
	public boolean getTestResult() {
		return exception == null;
	}

	@Override
	public Throwable getException() {
		return exception;
	}

	// from SearchListener
	@Override
	public void propertyViolated(Search search) {
		exception = getExceptionFromProperty(
				getLastSearchError(search).getProperty());
	}

	protected Throwable getExceptionFromProperty(Property property) {
		if (property instanceof TestProperty) {
			return ((TestProperty) property).getException();
		} else {
			return new JPFPropertyViolated(property);
		}
	}

	protected Error getLastSearchError(Search search) {
		return search.getLastError();
	}

	@Override
	public void searchFinished(Search search) {}

	@Override
	public void searchStarted(Search search) {}

	@Override
	public void searchConstraintHit(Search search) {}

	@Override
	public void stateAdvanced(Search search) {}

	@Override
	public void stateBacktracked(Search search) {}

	@Override
	public void stateProcessed(Search search) {}

	@Override
	public void stateRestored(Search search) {}

	@Override
	public void stateStored(Search search) {}
}
