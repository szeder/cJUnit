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
 * The <code>ConcurrentTest</code> annotation supports two optional
 * parameters: <code>expected</code>, and <code>threadCount</code>.
 * <p>
 * The first optional parameter, <code>expected</code>, declares that a test
 * method should throw an exception in all thread interleavings.  If it
 * doesn't throw an exception or if it throws a different exception than the
 * one declared in any of the thread interleavings, the test fails.  The
 * <code>expected</code> annotation parameter can be combined with the
 * <code>threadCount</code> parameter.  When combined, the test method must
 * throw the declared exception in all threads, in all thread interleavings.
 * <p>
 * The second optional parameter, <code>threadCount</code>, specifies the
 * number of threads that will concurrently invoke the test method.  cJUnit
 * will automatically create the necessary number of threads and will invoke
 * the test method from all of them.  For example the following test case
 * <pre>
 *   int i = 0;
 *   &#064;ConcurrentTest(threadCount=2)
 *   public void incrementByTwoThreads() {
 *       synchronized (lock) { i++; }
 *       TestBarrier.await();
 *       assertThat(i, equalTo(2));
 *   }
 * </pre>
 * involves two threads, both of them concurrently invoking the
 * <code>incrementByTwoThreads()</code> method.  The
 * <code>threadCount</code> and <code>expected</code> annotation parameters
 * can be combined.  In this case the test method must throw the declared
 * exception in all threads in all thread interleavings to make the test
 * succeed.
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
	 * <p>
	 * Can be combined with <code>threadCount</code> parameter.
	 */
	Class<? extends Throwable> expected() default None.class;

	/**
	 * Optionally specifies the number of threads that should invoke the
	 * test method concurrently.
	 */
	int threadCount() default 1;
}
