/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.testutils;

public class OtherTestException extends Throwable {

	private static final long serialVersionUID = 1L;

	public OtherTestException() { }

	public OtherTestException(String msg) {
		super(msg);
	}

	public OtherTestException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public OtherTestException(Throwable t) {
		super(t);
	}
}
