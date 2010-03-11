/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.integration.testclasses;

import org.junit.Test;

import de.fzi.cjunit.testutils.TestException;


public class SequentialTestWithFailure {

	public static boolean invoked = false;

	@Test
	public void testMethod() {
		invoked = true;
		throw new TestException();
	}
}
