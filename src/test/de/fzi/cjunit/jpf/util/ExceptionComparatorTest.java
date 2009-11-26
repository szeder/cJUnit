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

import de.fzi.cjunit.testutils.OtherTestException;
import de.fzi.cjunit.testutils.TestException;


public class ExceptionComparatorTest {

	Throwable t;

	public ExceptionComparatorTest() {
		t = new TestException("message");
	}

	@Test
	public void trueWhenSameObject() {
		assertThat(ExceptionComparator.equals(t, t), is(true));
	}

	@Test
	public void firstIsNull() {
		assertThat(ExceptionComparator.equals(null, t), is(false));
	}

	@Test
	public void secondIsNull() {
		assertThat(ExceptionComparator.equals(t, null), is(false));
	}

	@Test
	public void falseWhenDifferentClass() {
		Throwable other = new OtherTestException(t.getMessage());
		other.setStackTrace(t.getStackTrace());

		assertThat(ExceptionComparator.equals(t, other), is(false));
	}

	@Test
	public void falseWhenDifferentMessage() {
		Throwable other = new TestException("other message");
		other.setStackTrace(t.getStackTrace());

		assertThat(ExceptionComparator.equals(t, other), is(false));
	}

	@Test
	public void falseWhenDifferentStackTrace() {
		Throwable other = new TestException(t.getMessage());

		assertThat(ExceptionComparator.equals(t, other), is(false));
	}

	@Test
	public void falseWhenDifferentCause() {
		Throwable other = new TestException(t.getMessage(),
				new OtherTestException());
		other.setStackTrace(t.getStackTrace());

		assertThat(ExceptionComparator.equals(t, other), is(false));
	}

	@Test
	public void trueWithSameCause() {
		Throwable cause1 = new OtherTestException("other message");
		t.initCause(cause1);
		Throwable cause2 = new OtherTestException(cause1.getMessage());
		cause2.setStackTrace(cause1.getStackTrace());
		Throwable other = new TestException(t.getMessage(), cause2);
		other.setStackTrace(t.getStackTrace());

		assertThat(ExceptionComparator.equals(t, other), is(true));
	}
}
