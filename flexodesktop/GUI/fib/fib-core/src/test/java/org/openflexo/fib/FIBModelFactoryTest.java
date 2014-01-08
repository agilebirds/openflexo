/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib;

import static org.junit.Assert.fail;

import java.util.logging.Logger;

import org.junit.Test;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * Test instanciation of FIBModelFactory<br>
 * TODO: instanciate all widgets
 * 
 */
public class FIBModelFactoryTest {

	private static final Logger logger = FlexoLogger.getLogger(FIBModelFactoryTest.class.getPackage().getName());

	@Test
	public void testInstanciateFIBModelFactory() {
		try {
			FIBModelFactory factory = new FIBModelFactory();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			fail();
		}
	}
}
