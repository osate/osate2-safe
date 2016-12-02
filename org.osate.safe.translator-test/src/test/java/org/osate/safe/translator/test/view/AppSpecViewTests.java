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

import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.test.AllTests;

public class AppSpecViewTests {

	// Enable to overwrite existing expected values
	// Note that doing so will cause all tests to fail until this value is
	// re-disabled.
	private final static boolean GENERATE_EXPECTED = false;
	private final static String EXPECTED_DIR = "xml/appspec/";

	private static SystemModel systemModel;
	private static STGroup appspecSTG;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		systemModel = AllTests.runArchTransTest("PulseOx",
				"PulseOx_Forwarding_System");

		URL appspecStgUrl = Platform.getBundle(MAIN_PLUGIN_BUNDLE_ID)
				.getEntry(TEMPLATE_DIR + "midas-appspec.stg");
		appspecSTG = new STGroupFile(appspecStgUrl.getFile());
		appspecSTG.delimiterStartChar = '#';
		appspecSTG.delimiterStopChar = '#';
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testAppName() {
		runTest("appName", systemModel);
	}

	@Test
	public void testChannels() {
		runTest("channels", systemModel);
	}

	@Test
	public void testComponents() {
		runTest("components", systemModel);
	}

	// Convenience method so everything doesn't get cluttered up by all the
	// required parameters
	private void runTest(String testName, Object model) {
		runWriterTest(testName, model, "model", appspecSTG, GENERATE_EXPECTED,
				EXPECTED_DIR);
	}
}
