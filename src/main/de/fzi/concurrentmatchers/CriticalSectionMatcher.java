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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import de.fzi.concurrentmatchers.util.CriticalSection;

/**
 * Is a critical section accessed exclusively by the invoker thread?
 *
 * @see CriticalSection
 */
public class CriticalSectionMatcher extends TypeSafeMatcher<CriticalSection> {

	@Override
	public boolean matchesSafely(CriticalSection cs) {
		return cs.checkExclusiveAccess();
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(
				"<no other threads in critical section>");
	}

	public static Matcher<CriticalSection> accessedExclusively() {
		return new CriticalSectionMatcher();
	}
}
