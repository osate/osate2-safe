package org.osate.safe.translator.test.hazard;

import static org.osate.safe.translator.test.AllTests.initComplete;
import static org.osate.safe.translator.test.AllTests.usedProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.osate.safe.translator.model.DeviceModel;
import org.osate.safe.translator.model.ProcessModel;
import org.osate.safe.translator.model.SystemModel;
import org.osate.safe.translator.model.hazardanalysis.ManifestationTypeModel;
import org.osate.safe.translator.test.AllTests;

public class PropagatableErrorTests {

	private static Set<ManifestationTypeModel> pmInPropErrors;
	private static Set<ManifestationTypeModel> pmOutPropErrors;
	private static Set<ManifestationTypeModel> dmPropErrors;
	private static Iterator<ManifestationTypeModel> pmInIter;
	private static Iterator<ManifestationTypeModel> pmOutIter;

	@BeforeClass
	public static void initialize() {
		if (!initComplete)
			AllTests.initialize();
		usedProperties.add("MAP_Properties");
		usedProperties.add("PulseOx_Forwarding_Properties");
		usedProperties.add("PulseOx_Forwarding_Error_Properties");

		SystemModel systemModel = AllTests.runHazardTransTest("PulseOx", "PulseOx_Forwarding_System");
		DeviceModel deviceModel = systemModel.getDeviceByType("ICEpoInterface");
		ProcessModel processModel = systemModel.getProcessByType("PulseOx_Logic_Process");

		dmPropErrors = new LinkedHashSet<>(deviceModel.getPortByName("SpO2Out").getPropagatableErrors().values());
		pmInPropErrors = new LinkedHashSet<>(processModel.getPortByName("SpO2").getPropagatableErrors().values());
		pmOutPropErrors = new LinkedHashSet<>(
				processModel.getPortByName("DerivedAlarm").getPropagatableErrors().values());
	}

	@Before
	public void setup() {
		pmInIter = pmInPropErrors.iterator();
		pmOutIter = pmOutPropErrors.iterator();
	}

	@AfterClass
	public static void dispose() {
		usedProperties.clear();
	}

	@Test
	public void testPropagatableErrorsExist() {
		assertNotNull(dmPropErrors);
		assertEquals(1, dmPropErrors.size());
		assertNotNull(pmInPropErrors);
		assertEquals(6, pmInPropErrors.size());
		assertNotNull(pmOutPropErrors);
		assertEquals(2, pmOutPropErrors.size());
	}

	@Test
	public void testErrorManifestationKinds() {
		assertEquals("HIGH", pmInIter.next().getManifestationName());
		assertEquals("LOW", pmInIter.next().getManifestationName());
		assertEquals("VIOLATEDCONSTRAINT", pmOutIter.next().getManifestationName());
		assertEquals("VIOLATEDCONSTRAINT", pmOutIter.next().getManifestationName());
		assertEquals("HIGH", dmPropErrors.iterator().next().getManifestationName());
	}

	@Test
	public void testErrorManifestations() {
		assertEquals("SpO2ValueHigh", pmInIter.next().getName());
		assertEquals("SpO2ValueLow", pmInIter.next().getName());
		assertEquals("MissedAlarm", pmOutIter.next().getName());
		assertEquals("BogusAlarm", pmOutIter.next().getName());
		assertEquals("SpO2ValueHigh", dmPropErrors.iterator().next().getName());
	}
}
