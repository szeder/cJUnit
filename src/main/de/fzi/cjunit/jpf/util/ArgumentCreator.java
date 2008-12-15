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

import gov.nasa.jpf.PropertyListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArgumentCreator {

	private Class<?> appClass;
	private List<String> appArgs = new ArrayList<String>();
	private Class<?> propertyClass;
	private Class<?> publisherClass;
	private List<String> jpfargs = new ArrayList<String>();

	public ArgumentCreator() {
	}

	public ArgumentCreator(Class<?> applicationClass) {
		appClass = applicationClass;
	}

	public ArgumentCreator app(Class<?> applicationClass) {
		appClass = applicationClass;
		return this;
	}

	public ArgumentCreator appArgs(String... args) {
		appArgs = Arrays.asList(args);
		return this;
	}

	public ArgumentCreator appArgs(List<String> args) {
		appArgs = new ArrayList<String>(args);
		return this;
	}

	public ArgumentCreator property(Class<?> propertyClass) {
		this.propertyClass = propertyClass;
		return this;
	}

	public ArgumentCreator publisher(Class<?> publisherClass) {
		this.publisherClass = publisherClass;
		return this;
	}

	public ArgumentCreator jpfArgs(String... args) {
		jpfargs = Arrays.asList(args);
		return this;
	}

	public ArgumentCreator jpfArgs(List<String> args) {
		jpfargs = new ArrayList<String>(args);
		return this;
	}

	public String[] getArgs() {
		ArrayList<String> args = new ArrayList<String>();
		if (propertyClass != null) {
			args.add("+search.properties=" +
					propertyClass.getName());
		}
		if (publisherClass != null) {
			args.add("+jpf.report.publisher=" +
					publisherClass.getSimpleName());
			args.add("+jpf.report." +
					publisherClass.getSimpleName() +
					".class=" + publisherClass.getName());
		}
		if (jpfargs != null && !jpfargs.isEmpty()) {
			args.addAll(jpfargs);
		}
		if (appClass != null) {
			args.add(appClass.getName());
			if (appArgs != null && !appArgs.isEmpty()) {
				args.addAll(appArgs);
			}
		}
		return args.toArray(new String[args.size()]);
	}

	public ArgumentCreator defaultJPFTestArgs() {
		publisher(DumbPublisher.class);
		property(PropertyListenerAdapter.class);
		jpfArgs("+log.level=severe");
		return this;
	}
}
