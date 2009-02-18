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

import de.fzi.cjunit.jpf.exceptioninfo.ExceptionInfoDefaultImpl;
import de.fzi.cjunit.testexceptions.OtherTestException;
import de.fzi.cjunit.testexceptions.TestException;


public class ExceptionInfoDefaultImplTest {

	final String msg = "the exception's message";
	final String otherMsg = "other exception's message";

	@Test
	public void exceptionInfoAttributes() {
		ExceptionInfo ei = new ExceptionInfoDefaultImpl(
				new TestException(msg));

		assertThat("classname", ei.getClassName(),
				equalTo(TestException.class.getName()));
		assertThat("message", ei.getMessage(), equalTo(msg));
		assertThat("cause", ei.getCause(), equalTo(null));
		assertThat("hascause", ei.hasCause(), equalTo(false));
	}

	@Test
	public void chainedExceptionInfoAttributes() {
		OtherTestException ote = new OtherTestException(otherMsg);
		ExceptionInfo ei = new ExceptionInfoDefaultImpl(
				new TestException(msg, ote));

		assertThat("hascause", ei.hasCause(), equalTo(true));
		assertThat("cause classname", ei.getCause().getClassName(),
				equalTo(OtherTestException.class.getName()));
		assertThat("cause message", ei.getCause().getMessage(),
				equalTo(otherMsg));
		assertThat("cause hasCause", ei.getCause().hasCause(),
				equalTo(false));
	}
}
