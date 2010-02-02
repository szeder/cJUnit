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
import org.junit.internal.runners.model.MultipleFailureException;

import java.util.ArrayList;
import java.util.List;

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

	@Test
	public void trueWhenSameList() {
		List<Throwable> l = new ArrayList<Throwable>();
		assertThat(ExceptionComparator.equals(l, l), is(true));
	}

	@Test
	public void falseWhenFirstListIsNull() {
		List<Throwable> l = new ArrayList<Throwable>();
		assertThat(ExceptionComparator.equals(null, l), is(false));
	}

	@Test
	public void falseWhenSecondListIsNull() {
		List<Throwable> l = new ArrayList<Throwable>();
		assertThat(ExceptionComparator.equals(l, null), is(false));
	}

	@Test
	public void falseWhenDifferentSize() {
		List<Throwable> l1 = new ArrayList<Throwable>();
		l1.add(t);
		List<Throwable> l2 = new ArrayList<Throwable>();
		assertThat(ExceptionComparator.equals(l1, l2), is(false));
	}

	@Test
	public void falseWhenDifferentExceptionsInLists() {
		List<Throwable> l1 = new ArrayList<Throwable>();
		l1.add(t);
		List<Throwable> l2 = new ArrayList<Throwable>();
		l2.add(new OtherTestException());
		assertThat(ExceptionComparator.equals(l1, l2), is(false));
	}

	@Test
	public void trueWhenEqualExceptionsInLists() {
		List<Throwable> l1 = new ArrayList<Throwable>();
		l1.add(t);
		List<Throwable> l2 = new ArrayList<Throwable>();
		Throwable t2 = new TestException(t.getMessage());
		t2.setStackTrace(t.getStackTrace());
		l2.add(t2);
		assertThat(ExceptionComparator.equals(l1, l2), is(true));
	}

	@Test
	public void falseWhenOneMFE() {
		MultipleFailureException mfe
				= new MultipleFailureException(null);
		assertThat(ExceptionComparator.equals(mfe, t), is(false));
	}

	@Test
	public void falseWhenMFEsWithDifferentLists() {
		List<Throwable> l = new ArrayList<Throwable>();
		l.add(t);
		MultipleFailureException mfe1
				= new MultipleFailureException(l);
		MultipleFailureException mfe2
				= new MultipleFailureException(null);
		assertThat(ExceptionComparator.equals(mfe1, mfe2), is(false));
	}

	@Test
	public void trueWhenMFEsWithEqualLists() {
		List<Throwable> l1 = new ArrayList<Throwable>();
		l1.add(t);
		MultipleFailureException mfe1
				= new MultipleFailureException(l1);
		List<Throwable> l2 = new ArrayList<Throwable>();
		Throwable t2 = new TestException(t.getMessage());
		t2.setStackTrace(t.getStackTrace());
		l2.add(t2);
		MultipleFailureException mfe2
				= new MultipleFailureException(l2);
		assertThat(ExceptionComparator.equals(mfe1, mfe2), is(true));

	}
}
