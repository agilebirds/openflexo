package org.flexo.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.flexo.model2.TestContainerA;
import org.flexo.model2.TestContainerB;
import org.flexo.model2.TestEmbeddedA;
import org.flexo.model2.TestEmbeddedB;
import org.junit.Test;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.ModelEntity;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * Test PAMELA in method clash context
 * 
 * @author sylvain
 * 
 */
public class BasicTests3 {

	/**
	 * Test the diagram factory
	 */
	@Test
	public void testFactory() {

		try {
			ModelFactory factory = new ModelFactory(ModelContextLibrary.getCompoundModelContext(TestContainerA.class, TestContainerB.class,
					TestEmbeddedA.class, TestEmbeddedB.class));

			ModelEntity<TestContainerA> containerAEntity = factory.getModelContext().getModelEntity(TestContainerA.class);
			ModelEntity<TestContainerB> containerBEntity = factory.getModelContext().getModelEntity(TestContainerB.class);
			ModelEntity<TestEmbeddedA> embeddedAEntity = factory.getModelContext().getModelEntity(TestEmbeddedA.class);
			ModelEntity<TestEmbeddedB> embeddedBEntity = factory.getModelContext().getModelEntity(TestEmbeddedB.class);

			assertNotNull(containerAEntity);
			assertNotNull(containerBEntity);
			assertNotNull(embeddedAEntity);
			assertNotNull(embeddedBEntity);

		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test the diagram factory
	 */
	@Test
	public void testInstanciate() throws Exception {

		ModelFactory factory = new ModelFactory(ModelContextLibrary.getCompoundModelContext(TestContainerA.class, TestContainerB.class,
				TestEmbeddedA.class, TestEmbeddedB.class));

		TestContainerA containerA = factory.newInstance(TestContainerA.class);
		TestContainerB containerB = factory.newInstance(TestContainerB.class);
		TestEmbeddedA embeddedA = factory.newInstance(TestEmbeddedA.class);
		TestEmbeddedB embeddedB = factory.newInstance(TestEmbeddedB.class);

		try {
			containerA.setEmbedded(embeddedA);
			containerB.setEmbedded(embeddedB);
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
