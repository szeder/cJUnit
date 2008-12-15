/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.ThreadInfo;


public class StackFrameConverterTestWithJPF {

	public class ToStackTraceTestListener extends ListenerAdapter {
		public StackTraceElement[] stackTrace;

		@Override
		public void exceptionThrown(JVM vm) {
			ThreadInfo ti = vm.getLastThreadInfo();
			stackTrace = StackFrameConverter.toStackTrace(
					ti.dumpStack());
		}
	}

	@Test
	public void toStackTraceTestWithJPF() {
		ToStackTraceTestListener listener
				= new ToStackTraceTestListener();
		String[] jpfArgs = new ArgumentCreator()
			.app(StackFrameConverterTestWithJPF.class)
			.defaultJPFTestArgs()
			.getArgs();

		Config conf = JPF.createConfig(jpfArgs);
		JPF jpf = new JPF(conf);
		jpf.addListener(listener);
		jpf.run();

		StackTraceElement[] expectedStackTrace = null;
		try {
			StackFrameConverterTestWithJPF.main((String[]) null);
		} catch (RuntimeException ex) {
			expectedStackTrace = ex.getStackTrace();
		}
		assertThat(listener.stackTrace[0],
				equalTo(expectedStackTrace[0]));
		assertThat(listener.stackTrace[1],
				equalTo(expectedStackTrace[1]));
		assertThat(listener.stackTrace[2],
				equalTo(expectedStackTrace[2]));
		assertThat(listener.stackTrace[3],
				equalTo(expectedStackTrace[3]));
	}

	// for testing the above
	public static void main(String... args) { methodA(); }
	public static void methodA() { methodB(); }
	public static void methodB() { methodC(); }
	public static void methodC() { throw new RuntimeException(); }
}
