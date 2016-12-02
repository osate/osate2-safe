package org.osate.safe.translator.test.hazard;

import static org.osate.safe.translator.test.AllTests.initComplete;
import static org.osate.safe.translator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.osate.safe.translator.model.PortModel;
import org.osate.safe.translator.model.ProcessModel;
import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.model.hazardanalysis.NotDangerousDangerModel;
import org.osate.safe.translator.test.AllTests;

public class NotDangerousDangerModelTests {

	private static Map<String, NotDangerousDangerModel> sunkDangers;
	private static PortModel port;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");

		SystemModel systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
		ProcessModel processModel = systemModel.getProcessByType("PulseOx_Logic_Process");
		sunkDangers = processModel.getSunkDangers();
		port = processModel.getPortByName("SpO2");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testNDDMExist() {
		assertFalse(sunkDangers.isEmpty());
		assertEquals(3, sunkDangers.size());
	}

	@Test
	public void testNDDMName() {
		assertTrue(sunkDangers.keySet().contains("LowSpO2DoesNothing"));
	}

	@Test
	public void testNDDMSuccDangerExists() {
		assertEquals(1, sunkDangers.get("LowSpO2DoesNothing").getSuccessorDanger().getErrors().size());
	}
	
	@Test
	public void testNDDMSuccDangerErrorName() {
		assertEquals("SpO2ValueLow", sunkDangers.get("LowSpO2DoesNothing").getSuccessorDanger().getErrors().iterator().next().getName());
	}
	
	@Test
	public void testNDDMSuccDangerPortName() {
		assertEquals("SpO2", sunkDangers.get("LowSpO2DoesNothing").getSuccessorDanger().getPort().getName());
	}
	
	@Test
	public void testPortPropIsSunk() {
		assertTrue(port.getPropagatableErrorByName("LateSpO2").isSunk());
	}
	
	@Test
	public void testPortPropNotSunk() {
		assertFalse(port.getPropagatableErrorByName("SpO2ValueHigh").isSunk());
	}
}
