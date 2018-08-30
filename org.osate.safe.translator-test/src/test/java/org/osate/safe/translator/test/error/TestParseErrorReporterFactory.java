package org.osate.safe.translator.test.error;

import org.eclipse.emf.ecore.resource.Resource;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterFactory;

/*
 * Adapted from http://www.javaworld.com/article/2073352
 */

public class TestParseErrorReporterFactory implements ParseErrorReporterFactory {

	public final static TestParseErrorReporterFactory INSTANCE = new TestParseErrorReporterFactory();

	private final TestParseErrorReporter reporter = new TestParseErrorReporter();

	private TestParseErrorReporterFactory() {
		// Private to prevent instantiation...

	}

	@Override
	public ParseErrorReporter getReporterFor(Resource aadlRsrc) {
		// TODO Auto-generated method stub
		return reporter;
	}

}
