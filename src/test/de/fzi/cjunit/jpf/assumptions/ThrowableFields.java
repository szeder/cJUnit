/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.assumptions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import de.fzi.cjunit.jpf.util.ElementInfoWrapper;
import de.fzi.cjunit.testutils.JPFForTesting;
import de.fzi.cjunit.testutils.TestException;

import gov.nasa.jpf.Property;
import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.NoUncaughtExceptionsProperty;


public class ThrowableFields extends JPFForTesting {

	public static class ThrowException {
		public static void main(String[] args) {
			// stackTrace field is initialized lazily, so it
			// remains uninitialized
			throw new TestException("Greetings humans");
		}
	}

	public static class ThrowExceptionWithInitializedStackTrace {
		public static void main(String[] args) {
			try {
				throw new TestException("I am Ziltiod");
			} catch (TestException t) {
				// initialize stackTrace field
				t.getStackTrace();
				throw t;
			}
		}
	}

	public ElementInfoWrapper initRunAndGetElementInfo(Class<?> appClass) {
		createJPF(appClass);
		NoUncaughtExceptionsProperty p
				= new NoUncaughtExceptionsProperty(config);
		jpf.addSearchProperty(p);
		jpf.run();

		assertThat("a property was violated",
				jpf.getSearchErrors().size(), equalTo(1));
		assertThat("our property was violated",
				jpf.getSearchErrors().get(0).getProperty(),
				equalTo((Property) p));

		ElementInfo ei = DynamicArea.getHeap().get(
				p.getUncaughtExceptionInfo()
					.getExceptionReference());
		return new ElementInfoWrapper(ei, Throwable.class);
	}

	@Test
	public void checkThrowableFields() {
		ElementInfoWrapper eiw = initRunAndGetElementInfo(
				ThrowException.class);

		assertThat("message",
				eiw.getElementInfoForField("detailMessage"),
				notNullValue());
		// cause == this means no cause; see Throwable.getCause()
		assertThat("cause",
				eiw.getElementInfoForField("cause"),
				equalTo(eiw.getWrappedElementInfo()));
		assertThat("stackTrace",
				eiw.getElementInfoForField("stackTrace"),
				nullValue());
		assertThat("snapshot",
				eiw.getElementInfoForField("snapshot"),
				notNullValue());
	}

	@Test
	public void checkThrowableFieldsWithStackTrace() {
		ElementInfoWrapper eiw = initRunAndGetElementInfo(
				ThrowExceptionWithInitializedStackTrace.class);

		assertThat("message",
				eiw.getElementInfoForField("detailMessage"),
				notNullValue());
		assertThat("cause",
				eiw.getElementInfoForField("cause"),
				equalTo(eiw.getWrappedElementInfo()));
		assertThat("stackTrace",
				eiw.getElementInfoForField("stackTrace"),
				notNullValue());
		assertThat("snapshot",
				eiw.getElementInfoForField("snapshot"),
				notNullValue());
	}

	@Test
	public void checkStackTraceElementFields() {
		ElementInfoWrapper eiw = initRunAndGetElementInfo(
				ThrowExceptionWithInitializedStackTrace.class);
		ElementInfo[] stackTrace = eiw.getReferenceArray("stackTrace");
		ElementInfoWrapper stackTraceEIW = new ElementInfoWrapper(
				stackTrace[0], StackTraceElement.class);

		assertThat("clsName",
				stackTraceEIW.getElementInfoForField("clsName"),
				notNullValue());
		assertThat("mthName",
				stackTraceEIW.getElementInfoForField("mthName"),
				notNullValue());
		assertThat("fileName",
				stackTraceEIW.getElementInfoForField("fileName"),
				notNullValue());
		assertThat("line",
				stackTraceEIW.getElementInfoForField("line"),
				notNullValue());
	}
}
