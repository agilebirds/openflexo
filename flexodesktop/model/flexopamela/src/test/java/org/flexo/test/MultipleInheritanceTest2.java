package org.flexo.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.ModelEntity;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model3.Calculator;

/**
 * Test PAMELA in multiple inheritance context<br>
 * Test the behaviour of Calculator, instance of a multiple inheritance class hierarchy combining multiple partial implemenations
 * 
 * @author sylvain
 * 
 */
public class MultipleInheritanceTest2 {

	/**
	 * Test a factory that should success
	 */
	@Test
	public void testFactory() {

		try {
			ModelFactory factory = new ModelFactory(ModelContextLibrary.getCompoundModelContext(Calculator.class));

			ModelEntity<Calculator> calculatorEntity = factory.getModelContext().getModelEntity(Calculator.class);

			assertNotNull(calculatorEntity);

		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test the behaviour of Calculator, instance of a multiple inheritance class hierarchy combining multiple partial implemenations
	 */
	@Test
	public void testInstanciate() throws Exception {

		ModelFactory factory = new ModelFactory(ModelContextLibrary.getCompoundModelContext(Calculator.class));

		Calculator calculator = factory.newInstance(Calculator.class);
		assertEquals(-1, calculator.getStoredValue());
		System.out.println("Created calculator with " + calculator.getStoredValue());

		calculator.reset();
		assertEquals(0, calculator.getStoredValue());
		System.out.println("Calculator has now value " + calculator.getStoredValue());

		calculator.processPlus(10);
		assertEquals(10, calculator.getStoredValue());
		System.out.println("Calculator has now value " + calculator.getStoredValue());

		calculator.processPlus(5);
		assertEquals(15, calculator.getStoredValue());
		System.out.println("Calculator has now value " + calculator.getStoredValue());

		calculator.processMinus(8);
		assertEquals(7, calculator.getStoredValue());
		System.out.println("Calculator has now value " + calculator.getStoredValue());

	}
}
