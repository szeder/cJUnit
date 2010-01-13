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

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;


public class JPFWithDifferentConfigurationsTest {

	@Test
	public void testJPFWithDifferentConfigurationsHasIssues() {
		// the order of test cases is important here
		Class<?>[] classes = new Class<?>[] {
				DefaultPORFieldBoundariesTest.class,
				CustomPORFieldBoundariesTest.class };
		Result result = JUnitCore.runClasses(classes);

		assertThat("number of tests", result.getRunCount(), equalTo(2));
		assertThat("number of failed tests", result.getFailureCount(),
				equalTo(1));
		assertThat("error message",
				result.getFailures().get(0).getMessage(),
				containsString("(with custom POR boundaries)"));
	}
}
