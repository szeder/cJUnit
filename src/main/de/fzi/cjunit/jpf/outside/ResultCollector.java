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
import gov.nasa.jpf.report.Reporter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.search.SearchListener;

import de.fzi.cjunit.ConcurrentError;
import de.fzi.cjunit.JPFPropertyViolated;
import de.fzi.cjunit.jpf.util.ExceptionComparator;
import de.fzi.cjunit.jpf.util.TestReporter;


public class ResultCollector implements TestProperty, SearchListener {

	protected Search search;

	protected TestReporter reporter;
	protected TestFailedProperty tfp;

	protected Error error;
	protected Throwable exception;

	public ResultCollector(Reporter reporter, TestFailedProperty tfp) {
		this.reporter = (TestReporter) reporter;
		this.tfp = tfp;
	}

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
		Error currentError = getLastSearchError();
		Throwable currentException = getExceptionFromProperty(
				currentError.getProperty());
		if (error == null) {
			handleFirstError(currentError, currentException);
		} else {
			handleFurtherError(currentError, currentException);
		}
	}

	protected void handleFirstError(Error currentError,
			Throwable currentException) {
		error = currentError;
		exception = currentException;
		if (tfp.foundSucceededPath()) {
			foundConcurrencyBug();
		} else {
			tfp.reportSuccessAsFailure();
		}
	}

	protected void handleFurtherError(Error currentError,
			Throwable currentException) {
		if (error.getProperty() != currentError.getProperty()
				|| !ExceptionComparator.equals(exception,
						currentException)) {
			foundConcurrencyBug();
		}
	}

	protected void foundConcurrencyBug() {
		exception = new ConcurrentError(exception);
		terminateSearch();
	}

	protected void terminateSearch() {
		search.terminate();
	}

	protected Throwable getExceptionFromProperty(Property property) {
		if (property instanceof TestProperty) {
			return ((TestProperty) property).getException();
		} else {
			return new JPFPropertyViolated(property);
		}
	}

	protected Error getLastSearchError() {
		return search.getLastError();
	}

	@Override
	public void searchFinished(Search search) {
		if (error != null) {
			publishError();
		}
	}

	public void publishError() {
		reporter.publishError(error);
	}

	@Override
	public void searchStarted(Search search) {
		this.search = search;
	}

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
