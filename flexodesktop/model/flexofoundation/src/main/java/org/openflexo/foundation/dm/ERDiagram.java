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
package org.openflexo.foundation.dm;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.EntityAddedToDiagram;
import org.openflexo.foundation.dm.dm.EntityRemovedFromDiagram;
import org.openflexo.foundation.xml.FlexoDMBuilder;


public class ERDiagram extends DMObject {

	protected static final Logger logger = Logger.getLogger(ERDiagram.class.getPackage().getName());

	private String name;
	private Vector<DMEntity> entities;
	private DMRepository repository;

	/**
	 * Constructor used during deserialization
     */
    public ERDiagram(FlexoDMBuilder builder)
    {
        this(builder.dmModel);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public ERDiagram(DMModel dmModel)
    {
        super(dmModel);
        entities = new Vector<DMEntity>();
    }

    @Override
    public final void delete() {
    	DMModel model = getDMModel();
    	super.delete();
    	// This removes the only ref
    	model.removeFromDiagrams(this);

    	// this method is final because of this call
    	deleteObservers();
    }

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String aName)
	{
		if (name == null || !name.equals(aName)) {
			String oldName = name;
			name = aName;
			setChanged();
			notifyObservers(new DMAttributeDataModification("name",oldName,aName));
		}
	}

	public Vector<DMEntity> getEntities()
	{
		return entities;
	}

	public void setEntities(Vector<DMEntity> someEntities)
	{
		Vector<DMEntity> entitiesToRemove = new Vector<DMEntity>();
		entitiesToRemove.addAll(entities);
		for (DMEntity entity : someEntities) {
			if (entities.contains(entity)) {
				entitiesToRemove.remove(entity);
			}
			else {
				addToEntities(entity);
			}
		}
		for (DMEntity entity : entitiesToRemove) {
			removeFromEntities(entity);
		}
	}

	public void addToEntities(DMEntity entity)
	{
		//logger.info("**** addToEntities() "+entity);
		entities.add(entity);
		setChanged();
		notifyObservers(new EntityAddedToDiagram(entity));
	}

	public void removeFromEntities(DMEntity entity)
	{
		//logger.info("**** removeFromEntities() "+entity);
		entities.remove(entity);
		setChanged();
		notifyObservers(new EntityRemovedFromDiagram(entity));
	}


	@Override
	public boolean getAllowsChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Vector<? extends DMObject> getEmbeddedDMObjects()
	{
		return entities;
	}

	@Override
	public Vector<? extends DMObject> getOrderedChildren()
	{
		return entities;
	}

	@Override
	public TreeNode getParent()
	{
		return getDMModel();
	}

	@Override
	public boolean isDeletable()
	{
		return true;
	}

	@Override
	public String getClassNameKey()
	{
		return "er_diagram";
	}

	@Override
	public String getFullyQualifiedName()
	{
		return getDMModel().getFullyQualifiedName()+"."+getName();
	}

	@Override
	public String getInspectorName()
	{
		return Inspectors.DM.ER_DIAGRAM_INSPECTOR;
	}

	public DMRepository getRepository()
	{
		return repository;
	}

	public void setRepository(DMRepository aRepository)
	{
		repository = aRepository;
	}


}
