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
package org.openflexo.foundation.dm.action;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.localization.FlexoLocalization;

public class CreateERDiagram extends FlexoAction<CreateERDiagram,DMObject,DMEntity>
{

	private static final Logger logger = Logger.getLogger(CreateERDiagram.class.getPackage().getName());

	public static FlexoActionType<CreateERDiagram,DMObject,DMEntity> actionType
	= new FlexoActionType<CreateERDiagram,DMObject,DMEntity>  (
			"create_entity_diagram",
			FlexoActionType.newMenu,
			FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateERDiagram makeNewAction(DMObject focusedObject, Vector<DMEntity> globalSelection, FlexoEditor editor)
		{
			return new CreateERDiagram(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(DMObject object, Vector<DMEntity> globalSelection)
		{
			return (object instanceof DMModel || object instanceof DMEntity);
		}

		@Override
		protected boolean isEnabledForSelection(DMObject object, Vector<DMEntity> globalSelection)
		{
			return isVisibleForSelection(object, globalSelection);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DMObject.class);
	}


	private String diagramName;
	private Vector<DMEntity> entitiesToPutInTheDiagram;
	private ERDiagram newDiagram;
	private DMRepository repository;

	CreateERDiagram (DMObject focusedObject, Vector<DMEntity> globalSelection, FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context)
	{
		logger.info ("CreateERDiagram "+diagramName);
		if (getFocusedObject() != null) {
			newDiagram = new ERDiagram(getFocusedObject().getDMModel());
			for (DMEntity e : getEntitiesToPutInTheDiagram()) {
				newDiagram.addToEntities(e);
			}
			newDiagram.setName(diagramName);
			newDiagram.setRepository(getRepository());
			getFocusedObject().getDMModel().addToDiagrams(newDiagram);
		}
	}

	public String getDiagramName() {
		if (diagramName==null) {
			String base = FlexoLocalization.localizedForKey("new_diagram");
			diagramName = base;
			int i=0;
			while(getFocusedObject().getDMModel().getDiagramWithName(diagramName,false)!=null) {
				diagramName=base+"-"+i++;
			}
		}
		return diagramName;
	}

	public void setDiagramName(String diagramName) {
		this.diagramName = diagramName;
	}

	public Vector<DMEntity> getEntitiesToPutInTheDiagram()
	{
		if (entitiesToPutInTheDiagram == null) {
			entitiesToPutInTheDiagram = new Vector<DMEntity>();
			for (FlexoModelObject o : getGlobalSelectionAndFocusedObject()) {
				if (o instanceof DMEntity) entitiesToPutInTheDiagram.add((DMEntity)o);
			}
		}
		return entitiesToPutInTheDiagram;
	}

	public void setEntitiesToPutInTheDiagram(
			Vector<DMEntity> entitiesToPutInTheDiagram) {
		this.entitiesToPutInTheDiagram = entitiesToPutInTheDiagram;
	}

	public DMRepository getRepository()
	{
		if (repository == null) {
			Hashtable<DMRepository,Integer> occurences = new Hashtable<DMRepository,Integer>();
			for (DMEntity e : getEntitiesToPutInTheDiagram()) {
				if (occurences.get(e.getRepository()) == null) occurences.put(e.getRepository(), 1);
				else occurences.put(e.getRepository(),occurences.get(e.getRepository())+1);
			}
			int maxOccurs = -1;
			for (DMRepository r : occurences.keySet()) {
				if (occurences.get(r) > maxOccurs) {
					repository = r;
					maxOccurs = occurences.get(r);
				}
			}
			if (repository == null) repository = getFocusedObject().getProject().getDataModel().getRepositories().values().iterator().next();
		}
		return repository;
	}

	public void setRepository(DMRepository repository)
	{
		this.repository = repository;
	}

	public ERDiagram getNewDiagram() {
		return newDiagram;
	}




}
