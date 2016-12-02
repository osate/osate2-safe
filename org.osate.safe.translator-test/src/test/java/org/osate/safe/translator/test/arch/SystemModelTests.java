package org.osate.safe.translator.test.arch;

import static org.osate.safe.translator.test.AllTests.initComplete;
import static org.osate.safe.translator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.test.AllTests;

public class SystemModelTests {
	private static SystemModel systemModel;

	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		systemModel = AllTests.runArchTransTest("PulseOx", "PulseOx_Forwarding_System");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testSystemExists() {
		assertNotNull(systemModel);
	}
	
	@Test
	public void testSystemLogicComponents() {
		assertEquals(2, systemModel.getLogicComponents().size());
		assertNotNull(systemModel.getProcessByType("PulseOx_Logic_Process"));
		assertNotNull(systemModel.getProcessByType("PulseOx_Display_Process"));
	}
	
	@Test
	public void testSystemDeviceComponents() {
		assertEquals(3, systemModel.getChildren().size());
		assertNotNull(systemModel.getDeviceByType("ICEpoInterface"));
	}
	
	@Test
	public void testSystemName() { 
		assertEquals("TestProject", systemModel.getName());
	}
}
