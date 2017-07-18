package org.osate.safe.translator.test.view;

import static org.osate.safe.translator.test.AllTests.MAIN_PLUGIN_BUNDLE_ID;
import static org.osate.safe.translator.test.AllTests.TEMPLATE_DIR;
import static org.osate.safe.translator.test.AllTests.initComplete;
import static org.osate.safe.translator.test.AllTests.usedProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osate.core.test.BIRTTest;
import org.osate.safe.translator.model.ProcessModel;
import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.test.AllTests;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class AppHABIRTTests extends BIRTTest {

	// Enable to overwrite existing expected values
	// Note that doing so will cause all tests to fail until this value is
	// re-disabled.
	private final static boolean GENERATE_EXPECTED = false;
	private final static String ELEMENT_RPTDESIGN_URL = "platform:/plugin/org.osate.safe.translator/src/main/resources/templates/element-report.rptdesign";

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
	public void basicBIRTTest() throws IOException, URISyntaxException {
		URL path = Platform.getBundle(AllTests.TEST_PLUGIN_BUNDLE_ID)
				.getEntry(AllTests.TEST_DIR + "birt/report/" + "basicBIRTTest" + ".xml");
		// One liner adapted from https://stackoverflow.com/a/35446009
		String expectedReport = new BufferedReader(new InputStreamReader(path.openStream())).lines()
				.collect(Collectors.joining(System.lineSeparator()));
		Map<String, ?> runAndRenderParameters = Collections.emptyMap();
		assertReport(ELEMENT_RPTDESIGN_URL, expectedReport, runAndRenderParameters);
	}

	@Override
	public String getProjectName() {
		return "PulseOx_Forwarding";
	}

}
