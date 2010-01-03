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
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.jvm.NoUncaughtExceptionsProperty;

import de.fzi.cjunit.testutils.JPFForTesting;


public class DefaultPORFieldBoundariesTest extends JPFForTesting {

	public static class UpdateJavaUtilCollectionConcurrently {
		public static void main(String[] args) throws Throwable {
			final List<Object> list = new ArrayList<Object>();
			Thread t = new Thread() {
				@Override
				public void run() {
					list.add(new Object());
				}
			};
			t.start();
			list.add(new Object());
			t.join();

			assertThat("list size", list.size(), equalTo(2));
		}
	}

	@Test
	public void testDefaultPORFieldBoundariesDoesNotDetectBug() {
		createJPF(UpdateJavaUtilCollectionConcurrently.class);
		jpf.addSearchProperty(new NoUncaughtExceptionsProperty(config));
		jpf.run();

		assertThat("number of errors", jpf.getSearchErrors().size(),
				equalTo(0));
	}
}
