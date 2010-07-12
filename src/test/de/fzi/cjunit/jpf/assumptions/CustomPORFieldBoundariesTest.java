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

import java.util.List;
import java.util.Properties;

import gov.nasa.jpf.Error;
import gov.nasa.jpf.jvm.NoUncaughtExceptionsProperty;

import de.fzi.cjunit.testutils.JPFForTesting;
import de.fzi.cjunit.jpf.assumptions.DefaultPORFieldBoundariesTest
		.UpdateJavaUtilCollectionConcurrently;


public class CustomPORFieldBoundariesTest extends JPFForTesting {

	@Test
	public void testCustomPORFieldBoundariesDetectsBug() {
		Properties jpfArgs = new Properties();
		jpfArgs.setProperty("vm.por.field_boundaries.never", "");
		createJPF(UpdateJavaUtilCollectionConcurrently.class, jpfArgs);
		jpf.addSearchProperty(new NoUncaughtExceptionsProperty(config));
		jpf.run();

		List<Error> errors = jpf.getSearchErrors();
		assertThat("number of errors (with custom POR boundaries)",
				errors.size(), equalTo(1));
		assertThat("error message", errors.get(0).getDetails(),
				containsString("AssertionError: list size"));
	}
}
