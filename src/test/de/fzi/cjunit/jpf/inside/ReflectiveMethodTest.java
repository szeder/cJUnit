/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.inside;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import de.fzi.cjunit.testutils.TestException;


public class ReflectiveMethodTest {

	@Test
	public void testCreateMethod() throws Throwable {
		ReflectiveMethod rm = new ReflectiveMethod("toString");
		Object target = new String();

		rm.createMethod(target);

		assertThat(rm.target, equalTo(target));
		assertThat(rm.method.getName(), equalTo("toString"));
	}

	@Test(expected=TestException.class)
	public void testInvokeUnchainsException()
			throws Throwable {
		ReflectiveMethod rm = new ReflectiveMethod() {
			@Override
			protected void invokeMethod()
					throws InvocationTargetException {
				throw new InvocationTargetException(
						new TestException());
			}
		};

		rm.invoke();
	}
}
