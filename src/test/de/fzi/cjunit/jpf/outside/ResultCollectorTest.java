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

import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.Property;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.Search;

import de.fzi.cjunit.JPFPropertyViolated;
import de.fzi.cjunit.testutils.TestException;


public class ResultCollectorTest {

	@Test
	public void getTestResultOfSucceededTest() {
		ResultCollector rc = new ResultCollector();
		assertThat("test result", rc.getTestResult(), equalTo(true));
	}

	@Test
	public void getTestResultOfFailedTest() {
		ResultCollector rc = new ResultCollector();
		rc.exception = new TestException();
		assertThat("test result", rc.getTestResult(),
				equalTo(false));
	}

	@Test
	public void getExceptionFromPropertyWithProperty() {
		ResultCollector rc = new ResultCollector();
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
		ResultCollector rc = new ResultCollector();
		Property property = new TestFailedProperty() {
			@Override
			public boolean getTestResult() {
				return false;
			}
			@Override
			public Throwable getException() {
				return new TestException();
			}
		};

		assertThat(rc.getExceptionFromProperty(property),
				instanceOf(TestException.class));
	}
}
