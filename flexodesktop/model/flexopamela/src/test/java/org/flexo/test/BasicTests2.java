package org.flexo.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.model.AbstractPAMELATest;
import org.openflexo.model.ModelContext;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.resource.PAMFlexoResource;

public class BasicTests2 extends AbstractPAMELATest {

	private ModelFactory factory;
	private ModelContext modelContext;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Override
	@Before
	public void setUp() throws Exception {
		modelContext = new ModelContext(PAMFlexoResource.class);
		factory = new ModelFactory(modelContext);
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testResources() throws Exception {
		PAMFlexoResource<?> container = factory.newInstance(PAMFlexoResource.class);
		PAMFlexoResource<?> content1 = factory.newInstance(PAMFlexoResource.class);
		PAMFlexoResource<?> content2 = factory.newInstance(PAMFlexoResource.class);
		container.addToContents(content1);
		content2.setContainer(container);
		assertEquals(container, content1.getContainer());
		assertEquals(container, content2.getContainer());
		assertTrue(container.getContents().contains(content1));
		assertTrue(container.getContents().contains(content2));
	}
}
