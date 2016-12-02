package org.osate.safe.translator.test.arch;

import static org.osate.safe.translator.test.AllTests.initComplete;
import static org.osate.safe.translator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.osate.safe.translator.model.ProcessConnectionModel;
import org.osate.safe.translator.model.ProcessModel;
import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.test.AllTests;

public class ProcessConnectionModelTests {
	private static ProcessModel systemLogicModel;
	private static ProcessModel isolatedLogicModel;
	private static ProcessConnectionModel systemChannel;
	private static ProcessConnectionModel isolatedChannel;
	
	@BeforeClass
	public static void initialize() {
		if(!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		SystemModel systemModel = AllTests.runArchTransTest("PulseOx", "PulseOx_Forwarding_System");
//		SystemModel processOnlySystemModel = AllTests.runArchTransTest("PulseOxProcOnly", "PulseOx_Forwarding_Logic");
		systemLogicModel = systemModel.getProcessByType("PulseOx_Logic_Process");
//		isolatedLogicModel = processOnlySystemModel.getProcessByType("PulseOx_Logic_Process");
		systemChannel = systemLogicModel.getChannelByName("outgoing_alarm");
//		isolatedChannel = isolatedLogicModel.getChannelByName("outgoing_alarm");
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}
	
	@Test
	public void testPortConnectionsExist(){
		assertEquals(systemLogicModel.getChannels().size(), 1);
//		assertEquals(isolatedLogicModel.getChannels().size(), 1);
	}
	
	@Test
	public void testPortConnectionPublisherInfo() {
		assertEquals(systemChannel.getPubName(), "CheckSpO2Thread");
//		assertEquals(isolatedChannel.getPubName(), "CheckSpO2Thread");
		assertEquals(systemChannel.getPublisher().getName(), "CheckSpO2Thread");
//		assertEquals(isolatedChannel.getPublisher().getName(), "CheckSpO2Thread");
		assertEquals(systemChannel.getPubPortName(), "Alarm");
//		assertEquals(isolatedChannel.getPubPortName(), "Alarm");
	}
	
	@Test
	public void testPortConnectionSubscriberInfo() {
		assertEquals("PulseOx_Logic_Process", systemChannel.getSubName());
//		assertEquals("PulseOx_Logic_Process", isolatedChannel.getSubName());
		assertEquals("PulseOx_Logic_Process", systemChannel.getSubscriber().getName());
//		assertEquals("PulseOx_Logic_Process", isolatedChannel.getSubscriber().getName());
		assertEquals("DerivedAlarm", systemChannel.getSubPortName());
//		assertEquals("DerivedAlarm", isolatedChannel.getSubPortName());
	}
	
	@Test
	public void testPortConnectionDirections() {
//		assertFalse(isolatedChannel.isProcessToThread());
		assertFalse(systemChannel.isProcessToThread());
	}
}
