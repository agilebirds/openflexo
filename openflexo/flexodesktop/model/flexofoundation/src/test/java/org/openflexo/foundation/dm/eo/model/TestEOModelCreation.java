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
import java.io.IOException;

import org.openflexo.foundation.dm.eo.DMEOAdaptorType;
import org.openflexo.foundation.dm.eo.model.EOAttribute;
import org.openflexo.foundation.dm.eo.model.EOEntity;
import org.openflexo.foundation.dm.eo.model.EOJoin;
import org.openflexo.foundation.dm.eo.model.EOModel;
import org.openflexo.foundation.dm.eo.model.EOModelGroup;
import org.openflexo.foundation.dm.eo.model.EORelationship;
import org.openflexo.foundation.dm.eo.model.InvalidJoinException;
import org.openflexo.foundation.dm.eo.model.PropertyListDeserializationException;

import junit.framework.TestCase;

/**
 * @author gpolet
 * 
 */
public class TestEOModelCreation extends TestCase
{

    private EOModel m;

    private EOModel loaded;

    public TestEOModelCreation()
    {
		super("TestEOModelCreation");
	}

   /**
     * Overrides setUp
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        EOModelGroup group = new EOModelGroup();
        m = new EOModel();
        m.setName("TEST");
        m.setAdaptorName(DMEOAdaptorType.JDBC.getName());
        m.setConnectionDictionary(DMEOAdaptorType.JDBC.getDefaultConnectionDictionary("UserTest", "PasswordTest", "http://www.denali.be",
                null, null));
        group.addModel(m);
    }

    private void writeModelAndLoad()
    {
        File file;
        try {
            file = File.createTempFile("TEST", "");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return;
        }
        file = file.getParentFile();
        try {
            m.writeToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return;
        }
        try {
            loaded = EOModel.createEOModelFromFile(file, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
            return;
        } catch (PropertyListDeserializationException e) {
            e.printStackTrace();
            fail();
            return;
        }
    }

    public void test0CreateEOModel()
    {
        // Test the initialized model
        assertNotNull(m.getOriginalMap());
        assertEquals(0, m.getEntities().size());

        writeModelAndLoad();

        // Test the loaded model
        assertNotNull(loaded);
        assertEquals("TEST", loaded.getName());
        assertEquals(DMEOAdaptorType.JDBC.getName(), loaded.getAdaptorName());
    }

    public void test1CreateEntitiesAndProperties()
    {
        EOEntity entity = new EOEntity();
        entity.setName("TestEntity");
        entity.setClassName("TestEntityClass");
        entity.setExternalName("TEST_ENTITY");
        m.addEntity(entity);
        assertNotNull(m.entityNamed("TestEntity"));
        assertNotNull(entity.getModel());

        EOEntity entity2 = new EOEntity();
        entity2.setName("testentity");
        entity2.setClassName("TestEntityClass");
        entity2.setExternalName("TEST_ENTITY");
        try {
			m.addEntity(entity2);
			fail();
		} catch (RuntimeException e1) {
			//OK
		}
        EOAttribute a = new EOAttribute();
        a.setName("TestAttribute");
        a.setColumnName("TEST_ATTRIBUTE");
        entity.addAttribute(a);
        assertNotNull(a.getEntity());
        assertNotNull(entity.attributeNamed("TestAttribute"));
        a.setEntity(entity);
        assertNotNull(entity.attributeNamed("TestAttribute"));
        assertNotNull(entity.propertyNamed("TestAttribute"));

        EORelationship r = new EORelationship();
        r.setName("TestRelationShip");
        entity.addRelationship(r);
        assertNotNull(r.getEntity());
        assertNotNull(entity.relationshipNamed("TestRelationShip"));
        a.setEntity(entity);
        assertNotNull(entity.relationshipNamed("TestRelationShip"));
        assertNotNull(entity.propertyNamed("TestRelationShip"));

        // Attempt to add twice the same object should fail
        try {
            entity.addAttribute(a);
            fail();
        } catch (IllegalArgumentException e) {
            // OK
        }

        // Attempt to add an attribute with the same name should fail
        a = new EOAttribute();
        a.setName("TestAttribute");
        try {
            entity.addAttribute(a);
            fail();
        } catch (IllegalArgumentException e) {
            // OK
        }

        // Attempt to add an attribute with the same name should fail
        a = new EOAttribute();
        a.setName("TestRelationShip");
        try {
            entity.addAttribute(a);
            fail();
        } catch (IllegalArgumentException e) {
            // OK
        }

        writeModelAndLoad();
        assertNotNull(loaded);
        assertEquals(1, loaded.getEntities().size());
        EOEntity loadedEntity = loaded.entityNamed("TestEntity");
        assertNotNull(loadedEntity);
        assertEquals("TestEntity", loadedEntity.getName());
        assertEquals("TestEntityClass", loadedEntity.getClassName());
        assertEquals("TEST_ENTITY", loadedEntity.getExternalName());
        assertEquals(1, loadedEntity.getAttributes().size());
        assertEquals(1, loadedEntity.getRelationships().size());
        EOAttribute loadedAttribute = loadedEntity.attributeNamed("TestAttribute");
        assertNotNull(loadedAttribute);
        assertEquals("TestAttribute", loadedAttribute.getName());
        assertEquals("TEST_ATTRIBUTE", loadedAttribute.getColumnName());
        EORelationship loadedRel = loadedEntity.relationshipNamed("TestRelationShip");
        assertNotNull(loadedRel);
        assertEquals("TestRelationShip", loadedRel.getName());
    }

    public void test2ManipulateRelationships()
    {
        EOEntity entity = new EOEntity();
        entity.setName("TestEntity");
        m.addEntity(entity);

        EOAttribute a = new EOAttribute();
        a.setName("TestAttribute");
        a.setColumnName("TEST_ATTRIBUTE");
        entity.addAttribute(a);

        EOEntity destEntity = new EOEntity();
        entity.setName("TestDestEntity");
        m.addEntity(destEntity);

        EOAttribute destA = new EOAttribute();
        destA.setName("TestDestAttribute");
        destA.setColumnName("TEST_DEST_ATTRIBUTE");
        destEntity.addAttribute(destA);
        
        EORelationship r = new EORelationship();
        r.setName("TestRelationShip");
        entity.addRelationship(r);
        r.setDestinationEntity(destEntity);
        assertEquals(1, destEntity.getIncomingRelationships().size());
        assertEquals(r, destEntity.getIncomingRelationships().firstElement());
        
        EOJoin join = null;
		try {
			join = new EOJoin(r,a,destA);
		} catch (InvalidJoinException e1) {
			fail(e1.getMessage());
		}
        r.addJoin(join);
        assertEquals(1, a.getOutgoingRelationships().size());
        assertEquals(r, a.getOutgoingRelationships().firstElement());
        assertEquals(0, a.getIncomingRelationships().size());
        assertEquals(0, destA.getOutgoingRelationships().size());
        assertEquals(1, destA.getIncomingRelationships().size());
        assertEquals(r, destA.getIncomingRelationships().firstElement());
        
        assertEquals(1, r.getSourceAttributes().size());
        assertEquals(a, r.getSourceAttributes().get(0));
        assertEquals(1, r.getDestinationAttributes().size());
        assertEquals(destA, r.getDestinationAttributes().get(0));
        
        try {
            try {
				join.setSourceAttribute(destA);
			} catch (InvalidJoinException e) {
				fail(e.getMessage());
				e.printStackTrace();
			}
            fail();
        } catch (IllegalArgumentException e) {
            // OK - destA is not on the source entity of this relationship
        }
        
        try {
            try {
				join.setDestinationAttribute(a);
			} catch (InvalidJoinException e) {
				fail(e.getMessage());
				e.printStackTrace();
			}
            fail();
        } catch (IllegalArgumentException e) {
            // OK - a is not on the destination entity of this relationship
        }
        
        join.setRelationship(null);// This will generate a WARNING - it is normal
        assertEquals(0, a.getOutgoingRelationships().size());
        assertEquals(0, a.getIncomingRelationships().size());
        assertEquals(0, destA.getOutgoingRelationships().size());
        assertEquals(0, destA.getIncomingRelationships().size());

    }

}
