/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.builders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.runner.Runner;

import de.fzi.cjunit.ConcurrentTest;
import de.fzi.cjunit.runners.ConcurrentRunner;

public class ConcurrentBuilderTest {

	static public class TestClass {
		@Test public void testMethod() { }
	}

	@Test
	public void returnsNullOnTestClass() throws Throwable {
		Runner runner = new ConcurrentBuilder().runnerForClass(
				TestClass.class);
		assertThat(runner, nullValue());
	}

	static public class ConcurrentTestClass {
		@ConcurrentTest public void testMethod() { }
	}

	@Test
	public void returnsConcurrentBuilderOnConcurrentTestClass()
			throws Throwable {
		Runner runner = new ConcurrentBuilder().runnerForClass(
				ConcurrentTestClass.class);
		assertThat(runner, notNullValue());
		assertThat(runner, is(ConcurrentRunner.class));
	}
}
