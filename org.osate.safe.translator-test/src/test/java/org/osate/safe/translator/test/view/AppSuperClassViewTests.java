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

import org.osate.safe.translator.model.DeviceModel;
import org.osate.safe.translator.model.ProcessModel;
import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.test.AllTests;

public class AppSuperClassViewTests {

	// Enable to overwrite existing expected values
	// Note that doing so will cause all tests to fail until this value is
	// re-disabled.
	private final static boolean GENERATE_EXPECTED = false;
	private final static String EXPECTED_DIR = "java/app/process/";

	private static SystemModel systemModel;
	private static ProcessModel processModel;
	private static DeviceModel pseudoDeviceModel;
	private static STGroup appSuperClassSTG;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		systemModel = AllTests.runArchTransTest("PulseOx",
				"PulseOx_Forwarding_System");
		processModel = systemModel.getProcessByType("PulseOx_Logic_Process");
		pseudoDeviceModel = systemModel.getDeviceByType("ICEpoInterface");

		URL appSuperClassStgUrl = Platform.getBundle(MAIN_PLUGIN_BUNDLE_ID)
				.getEntry(TEMPLATE_DIR + "java-superclass.stg");
		appSuperClassSTG = new STGroupFile(appSuperClassStgUrl.getFile());
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testPackageAndImports() {
		runTest("packageAndImports", processModel);
	}

	@Test
	public void testHeaderAndFields() {
		runTest("headerAndFields", processModel);
	}

	@Test
	public void testPortDeclarations() {
		runTest("portDeclarations", processModel);
	}

	@Test
	public void testConstructor() {
		runTest("constructor", processModel);
	}

	@Test
	public void testInitAndGetters() {
		runTest("initAndGetters", processModel);
	}

	@Test
	public void testChannelAssignments() {
		runTest("subAndPubChannelAssignments", processModel);
	}

	@Test
	public void testForwardMethods() {
		runTest("forwardMethods", pseudoDeviceModel);
	}

	@Test
	public void testAbstractMethods() {
		runTest("abstractMethods", processModel);
	}

	@Test
	public void testConcreteMethods() {
		runTest("concreteMethods", processModel);
	}

	@Test
	public void testTasks() {
		runTest("tasks", processModel);
	}

	// Convenience method so everything doesn't get cluttered up by all the
	// required parameters
	private void runTest(String testName, Object model) {
		runWriterTest(testName, model, "model", appSuperClassSTG, GENERATE_EXPECTED,
				EXPECTED_DIR);
	}
}
