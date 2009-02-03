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

import de.fzi.cjunit.testexceptions.TestException;

public class JPFInvokerTest {

	@Test
	public void createJPFArgs() throws Throwable {
		String[] args = new JPFInvoker().createJPFArgs(new String(),
				String.class.getMethod("toString"),
				RuntimeException.class);
		assertThat(args, hasItemInArray(
				"de.fzi.cjunit.jpf.inside.TestWrapper"));
		assertThat(args, hasItemInArray(
				"--testclass=java.lang.String"));
		assertThat(args, hasItemInArray("--testmethod=toString"));
		assertThat(args, hasItemInArray("--expectedexception=java.lang.RuntimeException"));
	}

	@Test
	public void checkResultOfSucceededTest() throws Throwable {
		JPFInvoker jpfInvoker = new JPFInvoker();
		jpfInvoker.testObserver = new TestObserver() {
			@Override
			public boolean getTestResult() {
				return true;
			}
		};

		jpfInvoker.checkResult();
	}

	@Test(expected=TestException.class)
	public void checkResultOfFailedTest() throws Throwable {
		JPFInvoker jpfInvoker = new JPFInvoker();
		jpfInvoker.testObserver = new TestObserver() {
			@Override
			public boolean getTestResult() {
				return false;
			}
			@Override
			public Throwable getException() {
				return new TestException();
			}
		};

		jpfInvoker.checkResult();
	}
}
