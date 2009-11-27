/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.concurrentmatchers;

import org.hamcrest.Matcher;

import de.fzi.concurrentmatchers.util.CriticalSection;

public class Matchers {

	/**
	 * Is a critical section accessed exclusively by the invoker thread?
	 *
	 * See {@link CriticalSection}.
	 */
	public static Matcher<CriticalSection> accessedExclusively() {
		return CriticalSectionMatcher.accessedExclusively();
	}
}
