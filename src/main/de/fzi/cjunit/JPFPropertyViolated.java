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

import static de.fzi.cjunit.internal.util.LineSeparator.lineSeparator;

import gov.nasa.jpf.Property;


/**
 * This exception is thrown to indicate the violation of an user-supplied
 * property.  The exception's message contains the name of the violated
 * property class and the property's error message.
 * <p>
 * Note: The violation of <code>NotDeadLockedProperty</code> is not reported
 * through this exception, but through <code>DeadlockError</code>.
 */
public class JPFPropertyViolated extends Error {

	private static final long serialVersionUID = 1L;

	private Property violatedProperty;

	/**
	 * @param violatedProperty	the property which was violated during
	 *				the test run
	 */
	public JPFPropertyViolated(Property violatedProperty) {
		super(new String("Property '"
				+ violatedProperty.getClass().getName()
				+ "' violated" + lineSeparator
				+ violatedProperty.getErrorMessage()));
		this.violatedProperty = violatedProperty;
	}

	/**
	 * @return	the property which was violated during the test run
	 */
	public Property getViolatedProperty() {
		return violatedProperty;
	}
}
