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


public interface TestProperty {

	/**
	 * @return	the result of the test according to the property
	 *		implementing this interface: <tt>true</tt> if
	 *		succeeded, <tt>false</tt> if failed.
	 */
	public boolean getTestResult();

	/**
	 * @return	the exception instance indicating the cause of the
	 *		property violation.
	 */
	public Throwable getException();
}
