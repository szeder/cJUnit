/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>ConcurrentTest</code> annotation tells cJUnit that the
 * <code>public void</code> method to which it is attached can be run as a
 * concurrent test case.  To check that the test method delivers the correct
 * results regardless of the scheduling of its threads, the test method is
 * executed in all possible thread interleavings.  Any exceptions thrown by
 * the test in any of the thread interleavings will be reported as failure.
 * If no exceptions are thrown in any thread interleavings, the test is
 * assumed to have succeeded.
 * <p>
 * The <code>ConcurrentTest</code> annotation supports one optional parameter.
 * <p>
 * The optional parameter <code>expected</code> declares that a test
 * method should throw an exception in all thread interleavings.  If it
 * doesn't throw an exception or if it throws a different exception than the
 * one declared in any of the thread interleavings, the test fails.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ConcurrentTest {

	/**
	 * Default empty exception
	 */
	static class None extends Throwable {
		private static final long serialVersionUID= 1L;
		private None() {
		}
	}

	/**
	 * Optionally specify <code>expected</code>, a Throwable, to cause a
	 * test method to succeed if an exception of the specified class is
	 * thrown by the method in all thread interleavings.
	 */
	Class<? extends Throwable> expected() default None.class;
}
