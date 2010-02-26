/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.exceptioninfo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.internal.runners.model.MultipleFailureException;

import de.fzi.cjunit.testutils.OtherTestException;
import de.fzi.cjunit.testutils.TestException;

import java.util.ArrayList;
import java.util.List;


public class MultipleFailureExceptionInfoTest {

	@Test
	public void testMultipleFailureExceptionInfoCtor() {
		Throwable t1 = new TestException();
		Throwable t2 = new OtherTestException();
		List<Throwable> failures = new ArrayList<Throwable>();
		failures.add(t1);
		failures.add(t2);
		MultipleFailureException mfe = new MultipleFailureException(
				failures);

		MultipleFailureExceptionInfo mfei
				= new MultipleFailureExceptionInfo(mfe);

		assertThat(mfei.failures.length, equalTo(2));
		assertThat(mfei.failures[0].getClassName(),
				equalTo(TestException.class.getName()));
		assertThat(mfei.failures[1].getClassName(),
				equalTo(OtherTestException.class.getName()));
	}

	@Test
	public void testMultipleFailureExceptionInfoCtorWithNullFailures() {
		MultipleFailureException mfe = new MultipleFailureException(
				null);

		MultipleFailureExceptionInfo mfei
				= new MultipleFailureExceptionInfo(mfe);

		assertThat(mfei.failures.length, equalTo(0));
	}
}
