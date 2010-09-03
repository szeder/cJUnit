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

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;

import static de.fzi.cjunit.internal.util.LineSeparator.lineSeparator;


public class ElementInfoWrapper {

	ElementInfo elementInfo;

	public ElementInfoWrapper(ElementInfo elementInfo,
			Class<?> expectedClass) {
		if (!elementInfo.getClassInfo().isInstanceOf(expectedClass.getName())) {
			String gripe = "ElementInfo type mismatch:"
				+ lineSeparator + "expected: "
				+ expectedClass.getName() + lineSeparator
				+ "got: " + elementInfo.getClassInfo().getName();
			throw new RuntimeException(gripe);
		}

		this.elementInfo = elementInfo;
	}

	public ElementInfo getWrappedElementInfo() {
		return elementInfo;
	}

	public String getClassName() {
		return elementInfo.getClassInfo().getName();
	}

	public FieldInfo getFieldInfo(String fieldName) {
		ClassInfo classInfo = elementInfo.getClassInfo();
		while (classInfo != null) {
			FieldInfo fieldInfo = classInfo.getDeclaredInstanceField(
					fieldName);
			if (fieldInfo != null) {
				return fieldInfo;
			}
			classInfo = classInfo.getSuperClass();
		}
		throw new RuntimeException(
				"No such field: " + fieldName);
	}

	public int getReferenceValueForField(String fieldName) {
		FieldInfo fieldInfo = getFieldInfo(fieldName);
		final int[] values = elementInfo.getFields().dumpRawValues();
		return values[fieldInfo.getStorageOffset()];
	}

	public ElementInfo getElementInfoForField(String fieldName) {
		int refVal = getReferenceValueForField(fieldName);
		return DynamicArea.getHeap().get(refVal);
	}

	public int[] getIntArrayForField(String fieldName) {
		int refVal = getReferenceValueForField(fieldName);
		return DynamicArea.getHeap().get(refVal).asIntArray();
	}

	public String getStringField(String fieldName) {
		return elementInfo.getStringField(fieldName);
	}

	public int getIntField(String fieldName) {
		return elementInfo.getIntField(fieldName);
	}

	public int getArrayLength(String fieldName) {
		ElementInfo arrayElementInfo
				= getElementInfoForField(fieldName);
		if (!arrayElementInfo.isArray()) {
			throw new RuntimeException(
					"Not an array: " + fieldName);
		}
		return arrayElementInfo.arrayLength();
	}

	public ElementInfo[] getReferenceArray(String fieldName) {
		ElementInfo arrayElementInfo
				= getElementInfoForField(fieldName);
		if (!arrayElementInfo.getClassInfo().isReferenceArray()) {
			throw new RuntimeException(
					"Not a reference array: " + fieldName);
		}

		ElementInfo[] array = new ElementInfo[
				arrayElementInfo.arrayLength()];
		for (int i = 0; i < array.length; i++) {
			int ref = arrayElementInfo.getFields().dumpRawValues()[i];
			array[i] = DynamicArea.getHeap().get(ref);
		}

		return array;
	}
}
