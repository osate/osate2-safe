package org.osate.safe.translator.test.arch;

import static org.osate.safe.translator.test.AllTests.errorSB;
import static org.osate.safe.translator.test.AllTests.initComplete;
import static org.osate.safe.translator.test.AllTests.runArchTransTest;
import static org.osate.safe.translator.test.AllTests.usedDevices;
import static org.osate.safe.translator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.osate.safe.translator.test.AllTests;

public class ControllerErrorTests {

	@Before
	public void setUp() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		// See http://stackoverflow.com/a/18766889/2001755
		errorSB.setLength(0);
		errorSB.trimToSize();
	}

	@After
	public void tearDown() {
		usedProperties.clear();
		usedDevices.clear();
	}

	@Test
	public void testNoChannelDelay() {
		usedProperties.add("PulseOx_ForwardingNoChannelDelay_Properties");
		runArchTransTest("PulseOxNoChannelDelay", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_System.aadl:24: Missing required property 'Default_Channel_Delay'",
				errorSB.toString().trim());
	}

	@Test
	public void testNoOutputRate() {
		usedProperties.add("PulseOx_ForwardingNoOutputRate_Properties");
		runArchTransTest("PulseOxNoOutputRate", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:7: Missing the required output rate specification.",
				errorSB.toString().trim());
	}

	@Test
	public void testNoThreadDeadline() {
		usedProperties.add("PulseOx_ForwardingNoThreadDeadline_Properties");
		runArchTransTest("PulseOxNoThreadDeadline", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:145: Thread deadline must either be set with Default_Thread_Deadline (at package level) or with Timing_Properties::Deadline (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testNoThreadDispatch() {
		usedProperties.add("PulseOx_ForwardingNoThreadDispatch_Properties");
		runArchTransTest("PulseOxNoThreadDispatch", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Display.aadl:37: Thread dispatch type must either be set with Default_Thread_Dispatch (at package level) or with Thread_Properties::Dispatch_Protocol (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testNoThreadPeriod() {
		usedProperties.add("PulseOx_ForwardingNoThreadPeriod_Properties");
		runArchTransTest("PulseOxNoThreadPeriod", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:145: Thread period must either be set with Default_Thread_Period (at package level) or with Timing_Properties::Period (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testNoWCET() {
		usedProperties.add("PulseOx_ForwardingNoWCET_Properties");
		runArchTransTest("PulseOxNoWCET", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:145: Thread WCET must either be set with Default_Thread_WCET (at package level) or with Timing_Properties::Compute_Execution_Time (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testDuplicateSystem() {
		// So eventually we want to support duplicate systems, and as a result
		// of some preliminary changes, the system name is not known until after
		// processing, so the error message looks a little weird here (null is
		// used as a system name).
		usedProperties.add("PulseOx_Forwarding_Properties");
		runArchTransTest("PulseOxDuplicateSystem", "PulseOx_Forwarding_Duplicate_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Duplicate_System.aadl:29: Got a system called Duplicate_System but I already have one called null",
				errorSB.toString().trim());
	}

	@Test
	public void testIntegerOverflow() {
		usedProperties.add("PulseOx_ForwardingIntegerOverflow_Properties");
		runArchTransTest("PulseOxIntegerOverflow", "PulseOx_Forwarding_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Logic.aadl:145: Property Default_Thread_Period on element CheckSpO2Thread converts to 2.5E9 ms, which cannot be converted to an integer\n"
						+ "Error at PulseOx_Forwarding_Logic.aadl:145: Property Default_Thread_Deadline on element CheckSpO2Thread converts to 2.5E9 ms, which cannot be converted to an integer\n"
						+ "Error at PulseOx_Forwarding_Logic.aadl:145: Thread period must either be set with Default_Thread_Period (at package level) or with Timing_Properties::Period (on individual thread)",
				errorSB.toString().trim());
	}

	@Test
	public void testBidirectionalPort() {
		usedProperties.add("PulseOx_Forwarding_Properties");
		runArchTransTest("PulseOxBidirectionalPortConnection",
				"PulseOx_Forwarding_Bidirectional_System");
		assertEquals(
				"Error at PulseOx_Forwarding_Bidirectional_System.aadl:18: Bidirectional ports are not yet allowed.",
				errorSB.toString().trim());
	}

	@Test
	public void testDevToDevConnection() {
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedDevices.add("PulseOx_UseSpO2_Interface");
		runArchTransTest("PulseOxDevToDevConnection",
				"PulseOx_Forwarding_DevToDev_System");
		assertEquals(
				"Error at PulseOx_Forwarding_DevToDev_System.aadl:28: Device to device connections are not yet allowed.",
				errorSB.toString().trim());
	}

	@Test
	public void testDevIncDataPort() {
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedDevices.add("PulseOx_IncDataPort_Interface");
		runArchTransTest("PulseOxDevIncDataPort",
				"PulseOx_IncDataPort_System");
		assertEquals(
				"Error at PulseOx_IncDataPort_Interface.aadl:6: Incoming device ports must be event or event data.",
				errorSB.toString().trim());
	}
}
