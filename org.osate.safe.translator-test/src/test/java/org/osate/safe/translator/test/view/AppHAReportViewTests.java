package org.osate.safe.translator.test.view;

import static org.osate.safe.translator.test.AllTests.MAIN_PLUGIN_BUNDLE_ID;
import static org.osate.safe.translator.test.AllTests.TEMPLATE_DIR;
import static org.osate.safe.translator.test.AllTests.initComplete;
import static org.osate.safe.translator.test.AllTests.runWriterTest;
import static org.osate.safe.translator.test.AllTests.usedProperties;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import org.osate.safe.translator.model.ProcessModel;
import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.test.AllTests;

public class AppHAReportViewTests {

	// Enable to overwrite existing expected values
	// Note that doing so will cause all tests to fail until this value is
	// re-disabled.
	private final static boolean GENERATE_EXPECTED = false;
	private final static String EXPECTED_DIR = "markdown/app/report/";

	private static ProcessModel processModel;
	private static STGroup reportElementSTG;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("MAP_Error_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");

		SystemModel systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
		processModel = systemModel.getProcessByType("PulseOx_Logic_Process");

		URL reportElementStgUrl = Platform.getBundle(MAIN_PLUGIN_BUNDLE_ID)
				.getEntry(TEMPLATE_DIR + "report-element.stg");
		reportElementSTG = new STGroupFile(reportElementStgUrl.getFile());
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testDecomposition() {
		runTest("decomposition", processModel);
	}
	
	@Test
	public void testExternalInteractions() {
		runTest("externalInteractions", processModel);
	}
	
	@Test
	public void testInternalFaults() {
		runTest("internalFaults", processModel);
	}
	
	@Test
	public void testProcessModel() {
		runTest("processModel", processModel);
	}

	// Convenience method so everything doesn't get cluttered up by all the
	// required parameters
	private void runTest(String testName, Object model) {
		runWriterTest(testName, model, "model", reportElementSTG, GENERATE_EXPECTED,
				EXPECTED_DIR);
	}
}
