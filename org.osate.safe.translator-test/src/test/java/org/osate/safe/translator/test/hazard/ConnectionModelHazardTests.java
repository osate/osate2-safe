package org.osate.safe.translator.test.hazard;
//package org.osate.safe.translator.test.hazard;
//
//import static org.osate.safe.translator.test.AllTests.initComplete;
//import static org.osate.safe.translator.test.AllTests.usedProperties;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import org.osate.safe.translator.model.ConnectionModel;
//import org.osate.safe.translator.model.ModelUtil.Keyword;
//import org.osate.safe.translator.model.hazardanalysis.OccurrenceModel;
//import org.osate.safe.translator.model.SystemModel;
//import org.osate.safe.translator.test.AllTests;
//
//public class ConnectionModelHazardTests {
//
//	private static OccurrenceModel occurrence;
//	private static ConnectionModel channel;
//	private static SystemModel systemModel;
//
//	@BeforeClass
//	public static void initialize() {
//		if(!initComplete)
//			AllTests.initialize();
//		usedProperties.add("MAP_Properties");
//		usedProperties.add("PulseOx_Forwarding_Error_Properties");
//		usedProperties.add("PulseOx_Forwarding_Properties");
//		systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
//		channel = systemModel.getChannelByName("alarm_to_display");
//		occurrence = channel.getOccurrences().iterator().next();
//	}
//
//	@AfterClass
//	public static void dispose() {
//		usedProperties.clear();
//	}
//	
//	@Test
//	public void testOccurrenceExist(){
//		assertNotNull(occurrence);
//	}
//	
//	@Test
//	public void testOccurrenceCause() {
//		assertEquals("The SpO2 values from the pulse oximeter are too high, so the alarm is missed", occurrence.getCause());
//	}
//	
//	@Test
//	public void testOccurrenceConnectionErrorName() {
//		assertEquals("alarm_to_display", occurrence.getConnErrorName());
//	}
//	
//	@Test
//	public void testOccurrenceConstraint() {
//		assertEquals(systemModel.getConstraintByName("ShowAllAlarms"), occurrence.getConstraint());
//	}
//	
//	@Test
//	public void testOccurrenceTitle() {
//		assertEquals("Bad SpO2", occurrence.getTitle());
//	}
//	
//	@Test
//	public void testOccurrenceImpactName() {
//		assertEquals("SpO2ValueHigh", occurrence.getErrorType().getName());
//	}
//	
//	@Test
//	public void testOccurrenceKeyword() {
//		assertEquals(Keyword.NOTPROVIDING, occurrence.getKeyword());
//	}
//}
