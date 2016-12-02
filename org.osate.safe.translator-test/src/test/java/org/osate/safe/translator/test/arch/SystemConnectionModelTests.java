package org.osate.safe.translator.test.arch;

import static org.osate.safe.translator.test.AllTests.initComplete;
import static org.osate.safe.translator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.osate.safe.translator.model.SystemConnectionModel;
import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.test.AllTests;

public class SystemConnectionModelTests {
	private static SystemModel systemModel;
	private static SystemConnectionModel devToProcessConn;
	private static SystemConnectionModel processToProcessConn;
	private static SystemConnectionModel specialConn;
	
	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		systemModel = AllTests.runArchTransTest("PulseOx", "PulseOx_Forwarding_System");
		devToProcessConn = systemModel.getChannelByName("spo2_to_logic");
		processToProcessConn = systemModel.getChannelByName("alarm_to_display");
		specialConn = systemModel.getChannelByName("spo2_to_display");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}
	
	@Test
	public void testPortConnectionsExist(){
		assertEquals(4, systemModel.getChannels().size());
	}
	
	@Test
	public void testDefaultPortConnectionChannelDelay() {
		assertEquals(100, devToProcessConn.getChannelDelay());
	}
	
	@Test
	public void testOverridePortConnectionChannelDelay() {
		assertEquals(150, specialConn.getChannelDelay());
	}
	
	@Test
	public void testPortConnectionPublisherInfo() {
		assertEquals(systemModel.getDeviceByType("ICEpoInterface"), devToProcessConn.getPublisher());
		assertEquals("pulseOx", devToProcessConn.getPubName());
		assertEquals("SpO2", devToProcessConn.getPubPortName());
	}
	
	@Test
	public void testPortConnectionSubscriberInfo() {
		assertEquals(devToProcessConn.getSubscriber(), systemModel.getProcessByType("PulseOx_Logic_Process"));
		assertEquals(devToProcessConn.getSubName(), "appLogic");
		assertEquals(devToProcessConn.getSubPortName(), "SpO2");
	}
	
	@Test
	public void testPortConnectionDirections() {
		assertTrue(devToProcessConn.isDevicePublished());
		assertFalse(devToProcessConn.isDeviceSubscribed());
		assertFalse(processToProcessConn.isDevicePublished());
		assertFalse(processToProcessConn.isDeviceSubscribed());
	}
}
