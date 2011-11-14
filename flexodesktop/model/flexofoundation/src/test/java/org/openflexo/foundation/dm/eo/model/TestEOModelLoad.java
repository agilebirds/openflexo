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
package org.openflexo.foundation.dm.eo.model;

import java.io.File;
import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoTestCase;

/**
 * @author gpolet
 * 
 */
public class TestEOModelLoad extends FlexoTestCase {

	public TestEOModelLoad() {
		super("TestEOModelLoad");
	}

	/**
	 * Overrides setUp
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void test0LoadmodelAndDependancies() {
		File fcModelFile = getResource("FCModel.eomodeld");
		if (fcModelFile == null) {
			fail();
			return;
		}
		EOModelGroup group = new EOModelGroup();
		EOModel fcModel;
		try {
			fcModel = group.addModel(fcModelFile);
			fcModel.loadAllModelObjects();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (PropertyListDeserializationException e) {
			e.printStackTrace();
			fail();
			return;
		}
		assertEquals(12, fcModel.getEntities().size());
		assertEquals(4, fcModel.getMissingEntities().size());

		File catModelFile = getResource("DLRCategoryModel.eomodeld");
		if (catModelFile == null) {
			fail();
			return;
		}
		EOModel catModel;
		try {
			catModel = group.addModel(catModelFile);
			catModel.loadAllModelObjects();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (PropertyListDeserializationException e) {
			e.printStackTrace();
			fail();
			return;
		}
		assertEquals(0, catModel.getMissingEntities().size());
		assertEquals(3, fcModel.getMissingEntities().size());

		EOEntity vc = fcModel._entityNamed("FCVerticalChannel");
		assertNotNull(vc);
		EORelationship catRel = vc.relationshipNamed("category");
		assertNotNull(catRel);
		assertNotNull(catRel.getDestinationEntity());
		assertNotNull(catRel.getJoins().get(0).getDestinationAttribute());
		assertNotNull(catRel.getJoins().get(0).getSourceAttribute());

		File protoFile = getResource("EOPrototypes.eomodeld");
		if (protoFile == null) {
			fail();
			return;
		}
		EOModel protoModel;
		try {
			protoModel = group.addModel(protoFile);
			protoModel.loadAllModelObjects();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (PropertyListDeserializationException e) {
			e.printStackTrace();
			fail();
			return;
		}
		EOAttribute a = vc.attributeNamed("categoryID");
		assertNotNull(a);
		assertNotNull(a.getPrototype());

	}
}
