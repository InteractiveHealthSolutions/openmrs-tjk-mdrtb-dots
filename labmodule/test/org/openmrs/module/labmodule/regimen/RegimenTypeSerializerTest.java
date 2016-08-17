package org.openmrs.module.labmodule.regimen;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.labmodule.regimen.RegimenType;
import org.openmrs.module.labmodule.regimen.RegimenUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class RegimenTypeSerializerTest extends BaseModuleContextSensitiveTest {
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() throws Exception {
		authenticate();
	}

	/**
	 */
	@Test
	public void evaluate_shouldSerialize() throws Exception {
		
		List<RegimenType> allTypes = RegimenUtils.getRegimenTypes();
		System.out.println("allTypes: " + allTypes);
		
	}
}