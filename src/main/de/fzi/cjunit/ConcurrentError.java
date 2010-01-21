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

/**
 * Thrown to indicate a concurrency bug in a concurrent unit test, i.e. when
 * the test produces different results in different thread interleavings.
 * It wraps the exception that caused the test to fail.
 */
public class ConcurrentError extends Error {

	private static final long serialVersionUID = 1L;

	public ConcurrentError(Throwable cause) {
		super(cause);
		setStackTrace(cause.getStackTrace());
	}

	public ConcurrentError(String message) {
		super(message);
	}
}
