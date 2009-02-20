/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.exceptioninfo;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;

import static de.fzi.cjunit.util.LineSeparator.lineSeparator;


public class ElementInfoWrapper {

	ElementInfo elementInfo;

	public ElementInfoWrapper(ElementInfo elementInfo,
			Class<?> expectedClass) {
		String className = elementInfo.getClassInfo().getName();
		if (!className.equals(expectedClass.getName())) {
			String gripe = "ElementInfo type mismatch:"
				+ lineSeparator + "expected: "
				+ expectedClass.getName() + lineSeparator
				+ "got: " + className;
			throw new RuntimeException(gripe);
		}

		this.elementInfo = elementInfo;
	}

	public ElementInfo getWrappedElementInfo() {
		return elementInfo;
	}

	public FieldInfo getFieldInfo(String fieldName) {
		ClassInfo classInfo = elementInfo.getClassInfo();
		FieldInfo fieldInfo = classInfo.getDeclaredInstanceField(
				fieldName);
		if (fieldInfo == null) {
			throw new RuntimeException(
					"No such field: " + fieldName);
		}
		return fieldInfo;
	}

	public int getReferenceValueForField(String fieldName) {
		FieldInfo fieldInfo = getFieldInfo(fieldName);
		int refVal = elementInfo.getFields().getReferenceValue(
				fieldInfo);
		return refVal;
	}

	public ElementInfo getElementInfoForField(String fieldName) {
		int refVal = getReferenceValueForField(fieldName);
		return DynamicArea.getHeap().get(refVal);
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
		if (!arrayElementInfo.isReferenceArray()) {
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
