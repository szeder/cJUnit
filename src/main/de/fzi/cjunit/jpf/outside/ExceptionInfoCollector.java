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

import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.JVM;

import de.fzi.cjunit.jpf.exceptioninfo.ExceptionInfo;
import de.fzi.cjunit.jpf.exceptioninfo.ExceptionWrapper;


public class ExceptionInfoCollector {

	public ExceptionInfoCollector() { }

	public ExceptionInfo collectFromStack(JVM vm) throws Exception {
		ElementInfo ei = elementInfoFromStack(vm);

		return new ExceptionWrapper(ei);
	}

	protected ElementInfo elementInfoFromStack(JVM vm) throws Exception {
		int argRef = vm.getLastThreadInfo().peek();
		if (argRef == -1) {
			throw new Exception("Cannot examine stack: " +
					"cannot collect ExceptionInfo");
		}

		return elementInfoFromReference(argRef);
	}

	protected ElementInfo elementInfoFromReference(int argRef) {
		return DynamicArea.getHeap().get(argRef);
	}
}
