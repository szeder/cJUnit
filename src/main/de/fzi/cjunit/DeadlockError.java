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
 * Thrown to indicate a deadlock in a concurrent unit test.
 */
public class DeadlockError extends ConcurrentError {

	private static final long serialVersionUID = 1L;

	public DeadlockError(String msg) {
		super("Deadlock detected: " + msg);
	}
}
