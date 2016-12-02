package org.osate.safe.translator.test.hazard;

import static org.osate.safe.translator.test.AllTests.initComplete;
import static org.osate.safe.translator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.osate.safe.translator.model.ModelUtil.DesignTimeFaultDetectionApproach;
import org.osate.safe.translator.model.ModelUtil.RuntimeErrorDetectionApproach;
import org.osate.safe.translator.model.ModelUtil.RuntimeErrorHandlingApproach;
import org.osate.safe.translator.model.ModelUtil.RuntimeFaultHandlingApproach;
import org.osate.safe.translator.model.ProcessModel;
import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.model.hazardanalysis.InternallyCausedDangerModel;
import org.osate.safe.translator.model.hazardanalysis.NotDangerousDangerModel;
import org.osate.safe.translator.test.AllTests;

public class DetectionAndHandlingTests {

	private static Map<String, NotDangerousDangerModel> sunkDangers;
	private static Map<String, InternallyCausedDangerModel> internalDangers;

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
		internalDangers = processModel.getInternallyCausedDangers();
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testRuntimeErrorDetectionsExist() {
		assertEquals("TimestampViolation",
				sunkDangers.get("LateSpO2DoesNothing").getRuntimeDetections().iterator().next().getName());
	}

	@Test
	public void testRuntimeHandlingsExist() {
		assertEquals("SwitchToNoOutput",
				sunkDangers.get("LateSpO2DoesNothing").getRuntimeHandlings().iterator().next().getName());
		assertEquals("SwitchToNoOutput",
				sunkDangers.get("EarlySpO2DoesNothing").getRuntimeHandlings().iterator().next().getName());
	}

	@Test
	public void testRuntimeErrorDetectionExplanation() {
		assertEquals("Messages should be timestamped so latency violations can be detected",
				sunkDangers.get("LateSpO2DoesNothing").getRuntimeDetections().iterator().next().getExplanation());
	}

	@Test
	public void testRuntimeErrorHandlingExplanation() {
		assertEquals("The pump switches into a fail-safe mode, ie, it runs at a minimal (KVO) rate",
				sunkDangers.get("LateSpO2DoesNothing").getRuntimeHandlings().iterator().next().getExplanation());
		assertEquals("The pump switches into a fail-safe mode, ie, it runs at a minimal (KVO) rate",
				sunkDangers.get("EarlySpO2DoesNothing").getRuntimeHandlings().iterator().next().getExplanation());
	}

	@Test
	public void testRuntimeErrorDetectionApproach() {
		assertEquals(RuntimeErrorDetectionApproach.CONCURRENT,
				sunkDangers.get("LateSpO2DoesNothing").getRuntimeDetections().iterator().next().getApproach());
	}

	@Test
	public void testRuntimeErrorHandlingApproach() {
		assertEquals(RuntimeErrorHandlingApproach.ROLLFORWARD,
				sunkDangers.get("LateSpO2DoesNothing").getRuntimeHandlings().iterator().next().getErrorHandlingApproach());
		assertEquals(RuntimeErrorHandlingApproach.ROLLFORWARD,
				sunkDangers.get("EarlySpO2DoesNothing").getRuntimeHandlings().iterator().next().getErrorHandlingApproach());
	}

	@Test
	public void testDesignTimeFaultDetectionExists() {
		assertEquals(1,	internalDangers.get("BogusAlarmsArePossible").getDesignTimeDetections().size());
	}

	@Test
	public void testDesignTimeFaultDetectionExplanation() {
		assertEquals("The PulseOx is poorly maintained, and reports bad values due to deterioration. It should be tested periodically to ensure proper functioning.",	internalDangers.get("BogusAlarmsArePossible").getDesignTimeDetections().iterator().next().getExplanation());
	}

	@Test
	public void testDesignTimeFaultDetectionApproach() {
		assertEquals(DesignTimeFaultDetectionApproach.TESTING,	internalDangers.get("BogusAlarmsArePossible").getDesignTimeDetections().iterator().next().getApproach());
		assertEquals("TESTING",	internalDangers.get("BogusAlarmsArePossible").getDesignTimeDetections().iterator().next().getApproachStr());
	}
	
	@Test
	public void testRuntimeFaultHandlingExists() {
		assertEquals(1,	internalDangers.get("BogusAlarmsArePossible").getRuntimeHandlings().size());
	}
	
	@Test
	public void testRuntimeFaultHandlingExplanation() {
		assertEquals("The nurse sees that the pulse oximeter has deteriorated and turns it off.",	internalDangers.get("BogusAlarmsArePossible").getRuntimeHandlings().iterator().next().getExplanation());
	}
	
	@Test
	public void testRuntimeFaultHandlingErrorApproach() {
		assertEquals(RuntimeErrorHandlingApproach.ROLLFORWARD, internalDangers.get("BogusAlarmsArePossible").getRuntimeHandlings().iterator().next().getErrorHandlingApproach());
	}
	
	@Test
	public void testRuntimeFaultHandlingFaultApproach() {
		assertEquals(RuntimeFaultHandlingApproach.DIAGNOSIS, internalDangers.get("BogusAlarmsArePossible").getRuntimeHandlings().iterator().next().getFaultHandlingApproach());
	}
	
	@Test
	public void testNonexistentRuntimeFaultHandlingFaultApproach() {
		assertEquals(RuntimeFaultHandlingApproach.NOTSPECIFIED, sunkDangers.get("LateSpO2DoesNothing").getRuntimeHandlings().iterator().next().getFaultHandlingApproach());
	}	
}
