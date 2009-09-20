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

import java.util.Arrays;

public class ExceptionComparator {

	public static boolean equals(Throwable t1, Throwable t2) {
		if (t1 == t2) {
			return true;
		}
		if (t1 == null || t2 == null) {
			return false;
		}

		return eq(t1.getClass(), t2.getClass())
				&& eq(t1.getMessage(), t2.getMessage())
				&& Arrays.equals(t1.getStackTrace(),
						t2.getStackTrace())
				&& equals(t1.getCause(), t2.getCause());
	}

	private static boolean eq(Object a, Object b) {
		boolean result = a==b || (a != null && a.equals(b));
		return result;
	}
}
