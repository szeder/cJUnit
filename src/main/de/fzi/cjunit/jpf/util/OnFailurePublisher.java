/*
 * This file is covered by the terms of the NASA Open Source Agreement v1.3.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.util;

import java.io.PrintWriter;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.Path;
import gov.nasa.jpf.jvm.Step;
import gov.nasa.jpf.jvm.Transition;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.report.Reporter;
import gov.nasa.jpf.util.Left;

import de.fzi.cjunit.jpf.outside.JPFInvoker;


public class OnFailurePublisher extends ConsolePublisher {

	JPFInvoker jpfInvoker;

	public OnFailurePublisher(Config conf, Reporter reporter) {
		super(conf, reporter);

		initCopiedConsolePublisherVariables(conf);
	}

	public void setJPFInvoker(JPFInvoker jpfInvoker) {
		this.jpfInvoker = jpfInvoker;
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	protected void openChannel() {
		out = new PrintWriter(System.out, false);
	}

	@Override
	public void publishFinished() {
		super.publishFinished();
		if (jpfInvoker.getTestResult() == false) {
			out.flush();
		}
	}

	protected boolean showCG;
	protected boolean showSteps;
	protected boolean showLocation;
	protected boolean showSource;
	protected boolean showMethod;
	protected boolean showCode;

	protected void initCopiedConsolePublisherVariables(Config conf) {
		showCG = conf.getBoolean(
				"jpf.report.console.show_cg", true);
		showSteps = conf.getBoolean(
				"jpf.report.console.show_steps", true);
		showLocation = conf.getBoolean(
				"jpf.report.console.show_location", true);
		showSource = conf.getBoolean(
				"jpf.report.console.show_source", true);
		showMethod = conf.getBoolean(
				"jpf.report.console.show_method", false);
		showCode = conf.getBoolean(
				"jpf.report.console.show_code", false);
	}

	int prevThreadIndex = -1;

	@Override
	protected void publishTrace() {
		Path path = reporter.getPath();
		int i=0;

		if (path.size() == 0) {
		  return; // nothing to publish
		}

		publishTopicStart("trace " + reporter.getLastErrorId());

		for (Transition t : path) {
		  int threadIndex = t.getThreadIndex();
		  if (prevThreadIndex != threadIndex) {
		    out.print("------------------------------------------------------ ");
		    out.println("transition #" + i++ + " thread: " + t.getThreadIndex());

		    if (showCG){
		      ChoiceGenerator<?> cg = t.getChoiceGenerator();
		      String cgDescription = cg.toString();
		      String strippedCGDescription = cgDescription.replace(
				      cg.getClass().getName() + " ", "");
		      out.println(strippedCGDescription);
		    }
		    prevThreadIndex = threadIndex;
		  }

		  if (showSteps) {
		    String lastLine = null;
		    MethodInfo lastMi = null;
		    int nNoSrc=0;

		    for (Step s : t) {
		      if (showSource) {
		        String line = s.getLineString();
		        if (line != null) {
		          if (!line.equals(lastLine)) {
		            if (nNoSrc > 0){
		              out.println("      [" + nNoSrc + " insn w/o sources]");
		            }

		            out.print("  ");
		            if (showLocation) {
		              out.print(Left.format(s.getLocationString(),30));
		              out.print(" : ");
		            }
		            out.println(line.trim());
		            nNoSrc = 0;

		          }
		        } else { // no source
		          nNoSrc++;
		        }

		        lastLine = line;
		      }

		      if (showCode) {
		        Instruction insn = s.getInstruction();
		        if (showMethod){
		          MethodInfo mi = insn.getMethodInfo();
		          if (mi != lastMi) {
		            ClassInfo mci = mi.getClassInfo();
		            out.print("    ");
		            if (mci != null) {
		              out.print(mci.getName());
		              out.print(".");
		            }
		            out.println(mi.getUniqueName());
		            lastMi = mi;
		          }
		        }
		        out.print("      ");
		        out.println(insn);
		      }
		    }

		    if (showSource && !showCode && (nNoSrc > 0)) {
		      out.println("      [" + nNoSrc + " insn w/o sources]");
		    }
		  }
		}
	}
}
