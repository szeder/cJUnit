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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import gov.nasa.jpf.Error;
import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.Property;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.NotDeadlockedProperty;
import gov.nasa.jpf.search.Search;

import de.fzi.cjunit.ConcurrentError;
import de.fzi.cjunit.DeadlockError;
import de.fzi.cjunit.JPFPropertyViolated;
import de.fzi.cjunit.testutils.OtherTestException;
import de.fzi.cjunit.testutils.TestException;
import de.fzi.cjunit.testutils.Counter;


public class ResultCollectorTest {

	@Test
	public void getTestResultOfSucceededTest() {
		ResultCollector rc = new ResultCollector(null, null);
		assertThat("test result", rc.getTestResult(), equalTo(true));
	}

	@Test
	public void getTestResultOfFailedTest() {
		ResultCollector rc = new ResultCollector(null, null);
		rc.exception = new TestException();
		assertThat("test result", rc.getTestResult(),
				equalTo(false));
	}

	@Test
	public void getExceptionFromPropertyWithProperty() {
		ResultCollector rc = new ResultCollector(null, null);
		Property property = new GenericProperty() {
			@Override
			public boolean check(Search search, JVM jvm) {
				return false;
			}
			@Override
			public String getErrorMessage() {
				return "something went wrong";
			}
		};

		assertThat(rc.getExceptionFromProperty(property),
				instanceOf(JPFPropertyViolated.class));
	}

	@Test
	public void getExceptionFromPropertyWithTestProperty() {
		final Throwable t = new TestException();
		ResultCollector rc = new ResultCollector(null, null);
		Property property = new TestFailedProperty() {
			@Override
			public boolean getTestResult() {
				return false;
			}
			@Override
			public Throwable getException() {
				return t;
			}
		};

		assertThat(rc.getExceptionFromProperty(property), equalTo(t));
	}

	protected ResultCollector createResultCollectorToTestErrorHandlers(
			TestFailedProperty tfp,
			final Counter invocationCounter) {
		return new ResultCollector(null, tfp) {
			@Override
			public void foundConcurrencyBug() {
				invocationCounter.increment();
			}
		};
	}

	@Test
	public void testHandleFirstError() {
		final Counter reportSuccessCounter = new Counter();
		TestFailedProperty tfp = new TestFailedProperty() {
			@Override
			public boolean foundSucceededPath() {
				return false;
			}
			@Override
			public void reportSuccessAsFailure() {
				reportSuccessCounter.increment();
			}
		};
		final Counter invocationCounter = new Counter();
		ResultCollector rc
				= createResultCollectorToTestErrorHandlers(
						tfp, invocationCounter);
		Error error = new Error(0, new PropertyListenerAdapter(), null,
				null);
		Throwable exception = new TestException();
		rc.handleFirstError(error, exception);
		assertThat("error stored", rc.error, equalTo(error));
		assertThat("exception stored", rc.exception,
				equalTo(exception));
		assertThat("foundConcurrencyBug() not invoked",
				invocationCounter.getValue(), equalTo(0));
		assertThat("tfp.reportSuccessAsFailure() invoked",
				reportSuccessCounter.getValue(), equalTo(1));
	}

	@Test
	public void testHandleFirstErrorWhenPreviouslySucceeded() {
		TestFailedProperty tfp = new TestFailedProperty() {
			@Override
			public boolean foundSucceededPath() {
				return true;
			}
		};
		final Counter invocationCounter = new Counter();
		ResultCollector rc
				= createResultCollectorToTestErrorHandlers(
						tfp, invocationCounter);
		Error error = new Error(0, new PropertyListenerAdapter(), null,
				null);
		Throwable exception = new TestException();
		rc.handleFirstError(error, exception);
		assertThat("error stored", rc.error, equalTo(error));
		assertThat("exception stored", rc.exception,
				equalTo(exception));
		assertThat("foundConcurrencyBug() invoked",
				invocationCounter.getValue(), equalTo(1));
	}

	@Test
	public void testHandleFirstErrorWithDeadlock() {
		final Counter invocationCounter = new Counter();
		ResultCollector rc
				= createResultCollectorToTestErrorHandlers(
						null, invocationCounter);
		Property ndlp = new NotDeadlockedProperty(null, null) {
			@Override
			public String getErrorMessage() {
				return "asdf";
			}
		};
		Error error = new Error(0, ndlp, null, null);
		rc.handleFirstError(error, null);
		assertThat("error stored", rc.error, equalTo(error));
		assertThat("foundConcurrencyBug() invoked",
				invocationCounter.getValue(), equalTo(1));
	}

	@Test
	public void testHandleFurtherErrorWithDifferentError() {
		final Counter invocationCounter = new Counter();
		ResultCollector rc
				= createResultCollectorToTestErrorHandlers(
						null, invocationCounter);
		rc.error = new Error(0, new PropertyListenerAdapter(), null,
				null);
		rc.exception = new TestException();
		Error error = new Error(0, new TestFailedProperty(), null,
				null);
		rc.handleFurtherError(error, rc.exception);
		assertThat("foundConcurrencyBug() invoked",
				invocationCounter.getValue(), equalTo(1));
	}

	@Test
	public void testHandleFurtherErrorWithDifferentException() {
		final Counter invocationCounter = new Counter();
		ResultCollector rc
				= createResultCollectorToTestErrorHandlers(
						null, invocationCounter);
		rc.error = new Error(0, new PropertyListenerAdapter(), null,
				null);
		rc.exception = new TestException();
		rc.handleFurtherError(rc.error, new OtherTestException());
		assertThat("foundConcurrencyBug() invoked",
				invocationCounter.getValue(), equalTo(1));
	}

	@Test
	public void testHandleFurtherErrorWithSameViolation() {
		final Counter invocationCounter = new Counter();
		ResultCollector rc
				= createResultCollectorToTestErrorHandlers(
						null, invocationCounter);
		rc.error = new Error(0, new PropertyListenerAdapter(), null,
				null);
		rc.exception = new TestException();
		rc.handleFurtherError(rc.error, rc.exception);
		assertThat("foundConcurrencyBug() not invoked",
				invocationCounter.getValue(), equalTo(0));
	}

	@Test
	public void testFoundConcurrencyBug() {
		ResultCollector rc = new ResultCollector(null, null) {
			@Override
			public void terminateSearch() {}
		};
		Throwable t = new TestException();
		rc.exception = t;
		rc.error = new Error(0, new PropertyListenerAdapter(), null,
				null);
		rc.foundConcurrencyBug();
		assertThat("wrapped into ConcurrentError", rc.exception,
				instanceOf(ConcurrentError.class));
		assertThat("cause", rc.exception.getCause(), equalTo(t));
	}

	@Test
	public void testFoundConcurrencyBugWithDeadlock() {
		ResultCollector rc = new ResultCollector(null, null) {
			@Override
			public void terminateSearch() {}
		};
		Throwable t = new TestException();
		rc.exception = t;
		Property ndlp = new NotDeadlockedProperty(null, null) {
			@Override
			public String getErrorMessage() {
				return "asdf";
			}
		};
		rc.error = new Error(0, ndlp, null, null);
		rc.foundConcurrencyBug();
		assertThat(rc.exception, instanceOf(DeadlockError.class));
		assertThat("no cause", rc.exception.getCause(), nullValue());
	}

	protected ResultCollector createResultCollectorToTestSearchFinished(
			final Counter invocationCounter) {
		return new ResultCollector(null, null) {
			@Override
			public void publishError() {
				invocationCounter.increment();
			}
		};
	}

	@Test
	public void searchFinishedPublishesOnError() {
		final Counter invocationCounter = new Counter();
		ResultCollector rc
				= createResultCollectorToTestSearchFinished(
						invocationCounter);
		rc.error = new Error(0, new PropertyListenerAdapter(), null,
				null);
		rc.searchFinished(null);
		assertThat("publishError() invoked once",
				invocationCounter.getValue(), equalTo(1));
	}

	@Test
	public void searchFinishedDoesNotPublishWithoutError() {
		final Counter invocationCounter = new Counter();
		ResultCollector rc
				= createResultCollectorToTestSearchFinished(
						invocationCounter);
		rc.searchFinished(null);
		assertThat("publishError() is not invoked",
				invocationCounter.getValue(), equalTo(0));
	}
}
