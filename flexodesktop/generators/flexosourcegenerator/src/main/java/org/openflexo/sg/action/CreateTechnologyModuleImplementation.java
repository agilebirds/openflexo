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
package org.openflexo.sg.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;


public class CreateTechnologyModuleImplementation extends FlexoAction<CreateTechnologyModuleImplementation, ImplementationModel, ImplementationModel> {

	private static final Logger logger = Logger.getLogger(CreateTechnologyModuleImplementation.class.getPackage().getName());

	public static FlexoActionType<CreateTechnologyModuleImplementation, ImplementationModel, ImplementationModel> actionType = new FlexoActionType<CreateTechnologyModuleImplementation, ImplementationModel, ImplementationModel>(
			"create_new_technology_module_implementation", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateTechnologyModuleImplementation makeNewAction(ImplementationModel focusedObject, Vector<ImplementationModel> globalSelection, FlexoEditor editor) {
			return new CreateTechnologyModuleImplementation(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ImplementationModel object, Vector<ImplementationModel> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ImplementationModel object, Vector<ImplementationModel> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateTechnologyModuleImplementation.actionType, ImplementationModel.class);
	}

	public TechnologyModuleDefinition technologyModuleDefinition;
	public TechnologyModuleImplementation newTechnologyModuleImplementation;
	public String errorMessage;

	CreateTechnologyModuleImplementation(ImplementationModel focusedObject, Vector<ImplementationModel> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidParametersException, TechnologyModuleCompatibilityCheckException {
		logger.info("Add technology module implementation");

		if (technologyModuleDefinition == null) {
			throw new InvalidParametersException("no_selected_technology_module");
		}

		newTechnologyModuleImplementation = technologyModuleDefinition.createNewImplementation(getFocusedObject());
		logger.info("Created technology module implementation " + newTechnologyModuleImplementation + " for model " + newTechnologyModuleImplementation.getImplementationModel());
	}

	public TechnologyModuleImplementation getNewTechnologyModuleImplementation() {
		return newTechnologyModuleImplementation;
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null)
			return getFocusedObject().getProject();
		return null;
	}

	/**
	 * Return all technology module definition not used yet in the implementation model.
	 * 
	 * @return all technology module definition not used yet in the implementation model.
	 */
	public List<TechnologyModuleDefinition> getUnusedTechnologyModules() {
		List<TechnologyModuleDefinition> result = new ArrayList<TechnologyModuleDefinition>();
		for (TechnologyModuleDefinition technologyModuleDefinition : TechnologyModuleDefinition.getAllTechnologyModuleDefinitions()) {
			if (!getFocusedObject().containsTechnologyModule(technologyModuleDefinition))
				result.add(technologyModuleDefinition);
		}

		Collections.sort(result, new Comparator<TechnologyModuleDefinition>() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compare(TechnologyModuleDefinition o1, TechnologyModuleDefinition o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});

		return result;
	}
}