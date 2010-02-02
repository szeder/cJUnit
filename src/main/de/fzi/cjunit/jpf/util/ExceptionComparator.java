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
import java.util.Iterator;
import java.util.List;

import org.junit.internal.runners.model.MultipleFailureException;

public class ExceptionComparator {

	public static boolean equals(Throwable t1, Throwable t2) {
		if (t1 == t2) {
			return true;
		}
		if (t1 == null || t2 == null) {
			return false;
		}

		if (!t1.getClass().equals(t2.getClass())) {
			return false;
		} else if (t1 instanceof MultipleFailureException) {
			return equals(((MultipleFailureException) t1).getFailures(),
					((MultipleFailureException) t2).getFailures());
		} else {
			return eq(t1.getMessage(), t2.getMessage())
				&& Arrays.equals(t1.getStackTrace(),
						t2.getStackTrace())
				&& equals(t1.getCause(), t2.getCause());
		}
	}

	public static boolean equals(List<Throwable> l1, List<Throwable> l2) {
		if (l1 == l2) {
			return true;
		}
		if (l1 == null || l2 == null) {
			return false;
		}
		if (l1.size() != l2.size()) {
			return false;
		}

		Iterator<Throwable> i1 = l1.iterator();
		Iterator<Throwable> i2 = l2.iterator();
		while (i1.hasNext() && i2.hasNext()) {
			if (equals(i1.next(), i2.next()) == false) {
				return false;
			}
		}
		return true;
	}

	private static boolean eq(Object a, Object b) {
		boolean result = a==b || (a != null && a.equals(b));
		return result;
	}
}
