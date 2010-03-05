/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.outside;

public class ExceptionReconstructionException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExceptionReconstructionException(Throwable cause) {
		super("could not reconstruct the exception thrown during the test",
				cause);
	}

	public ExceptionReconstructionException(String message,
			Throwable cause) {
		super(message, cause);
	}
}
