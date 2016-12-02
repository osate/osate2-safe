package org.osate.safe.translator.test.arch;

import static org.osate.safe.translator.test.AllTests.initComplete;
import static org.osate.safe.translator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.osate.safe.translator.model.ProcessModel;
import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.model.ModelUtil.ComponentType;
import org.osate.safe.translator.test.AllTests;

public class ProcessModelTests {
	private static ProcessModel logicModel;
	private static ProcessModel displayModel;
//	private static ProcessModel processOnlyProcessModel;
	
	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		SystemModel systemModel = AllTests.runArchTransTest("PulseOx", "PulseOx_Forwarding_System");
//		SystemModel processOnlySystemModel = AllTests.runArchTransTest("PulseOxProcOnly", "PulseOx_Forwarding_Logic");
		logicModel = systemModel.getProcessByType("PulseOx_Logic_Process");
		displayModel = systemModel.getProcessByType("PulseOx_Display_Process");
//		processOnlyProcessModel = processOnlySystemModel.getProcessByType("PulseOx_Logic_Process");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testProcessExists() {
		assertNotNull(logicModel);
		assertNotNull(displayModel);
//		assertNotNull(processOnlyProcessModel);
	}
	
	@Test
	public void testProcessComponentType() {
		assertTrue(logicModel.isLogic());
		assertTrue(displayModel.isDisplay());
//		assertTrue(processOnlyProcessModel.isLogic());
	}
	
	@Test
	public void testProcessContainedPorts() {
		assertEquals(2, logicModel.getPorts().size());
//		assertEquals(2, processOnlyProcessModel.getPorts().size());
	}
	
	@Test
	public void testProcessContainedTasks() {
		// 1 explicit periodic task + 1 implicit sporadic task for the data port
		assertEquals(2, logicModel.getChildren().size()); 
		assertNotNull(logicModel.getChildren().get("CheckSpO2Thread"));
//		assertEquals(2, processOnlyProcessModel.getChildren().size());
//		assertNotNull(processOnlyProcessModel.getChildren().get("CheckSpO2Thread"));
	}
	
	@Test
	public void testProcessSporadicTasks() {
		assertEquals(1, logicModel.getSporadicTasks().size());
//		assertEquals(1, processOnlyProcessModel.getSporadicTasks().size());
	}
	
	@Test
	public void testProcessPeriodicTasks() {
		assertEquals(1, logicModel.getPeriodicTasks().size());
		assertNotNull(logicModel.getPeriodicTasks().get("CheckSpO2Thread"));
//		assertEquals(1, processOnlyProcessModel.getPeriodicTasks().size());
//		assertNotNull(processOnlyProcessModel.getPeriodicTasks().get("CheckSpO2Thread"));
	}
	
	@Test
	public void testProcessInDataPorts() {
		assertEquals(1, logicModel.getReceiveDataPorts().size());
		assertNotNull(logicModel.getReceiveDataPorts().get("SpO2"));
//		assertEquals(1, processOnlyProcessModel.getReceiveDataPorts().size());
//		assertNotNull(processOnlyProcessModel.getReceiveDataPorts().get("SpO2"));
	}
	
	@Test
	public void testProcessInEventPorts() {
		assertEquals(0, logicModel.getReceiveEventPorts().size());
//		assertEquals(0, processOnlyProcessModel.getReceiveEventPorts().size());
	}
	
	@Test
	public void testProcessInEventDataPorts() {
		assertEquals(0, logicModel.getReceiveEventDataPorts().size());
//		assertEquals(0, processOnlyProcessModel.getReceiveEventDataPorts().size());
	}
	
	@Test
	public void testProcessOutPorts() {
		assertEquals(1, logicModel.getSendPorts().size());
		assertNotNull(logicModel.getSendPorts().get("DerivedAlarm"));
//		assertEquals(1, processOnlyProcessModel.getSendPorts().size());
//		assertNotNull(processOnlyProcessModel.getSendPorts().get("DerivedAlarm"));
	}
	
	@Test
	public void testProcessIsPseudoDevice() {
		assertFalse(logicModel.isPseudoDevice());
//		assertFalse(processOnlyProcessModel.isPseudoDevice());
	}
	
	@Test
	public void testProcessSystemName() {
		assertEquals("TestProject", logicModel.getParentName());
//		assertEquals("TestProject", processOnlyProcessModel.getParentName());
	}
	
	@Test
	public void testProcessGlobals() {
		assertEquals(0, logicModel.getGlobals().size());
//		assertEquals(0, processOnlyProcessModel.getGlobals().size());
	}
	
	@Test
	public void testProcessComponentTypes() {
		assertEquals(ComponentType.CONTROLLER, logicModel.getComponentType());
//		assertEquals(ComponentType.CONTROLLER, processOnlyProcessModel.getComponentType());
		assertEquals(ComponentType.ACTUATOR, displayModel.getComponentType());
	}
}
