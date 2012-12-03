package org.openflexo.technologyadapter.emf.model;

import org.junit.Test;
import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.toolbox.FileResource;

public class TestLoadEMFMetaModel {

	@Test
	public void test() {
		ApplicationContext applicationContext = new TestApplicationContext(new FileResource("src/test/resources/EMF"));
	}

}