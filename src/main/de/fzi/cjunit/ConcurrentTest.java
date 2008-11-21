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
	 * thrown by the method.
	 */
	Class<? extends Throwable> expected() default None.class;
}
