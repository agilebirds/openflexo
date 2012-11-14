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
package org.openflexo.foundation.sg;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.InvalidReaderRepositoryException;
import org.openflexo.foundation.cg.MissingReaderRepositoryException;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.sg.implmodel.CreateImplementationModel;
import org.openflexo.foundation.sg.implmodel.ImplementationModelDefinition;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class CreateSourceRepository extends AbstractGCAction<CreateSourceRepository, CGObject> {

	private static final Logger logger = Logger.getLogger(CreateSourceRepository.class.getPackage().getName());

	public static FlexoActionType<CreateSourceRepository, CGObject, CGObject> actionType = new FlexoActionType<CreateSourceRepository, CGObject, CGObject>(
			"create_source_repository", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateSourceRepository makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new CreateSourceRepository(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CGObject object, Vector<CGObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(CGObject object, Vector<CGObject> globalSelection) {
			return object != null && object.getGeneratedCode() instanceof GeneratedSources;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, GeneratedSources.class);
		FlexoModelObject.addActionForClass(actionType, SourceRepository.class);
	}

	private SourceRepository _newSourceRepository;

	public String newSourceRepositoryName;
	public File newSourceRepositoryDirectory;
	public ImplementationModelDefinition implementationModel;
	public boolean createNewImplementationModel;
	public String newImplementationModelName;
	public String newImplementationModelDescription;

	CreateSourceRepository(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateCodeRepositoryNameException, MissingReaderRepositoryException,
			InvalidReaderRepositoryException {
		logger.info("Add SourceRepository " + getFocusedObject());
		if (getFocusedObject().getGeneratedCode() != null) {
			GeneratedOutput gc = getFocusedObject().getGeneratedCode();
			if (gc instanceof GeneratedSources) {
				if (createNewImplementationModel) {
					CreateImplementationModel createImplementationModel = CreateImplementationModel.actionType.makeNewEmbeddedAction(
							(GeneratedSources) gc, null, this);
					createImplementationModel.newModelName = newImplementationModelName;
					createImplementationModel.newModelDescription = newImplementationModelDescription;
					createImplementationModel.skipDialog = true;
					createImplementationModel.doAction();
					implementationModel = createImplementationModel.getNewImplementationModelDefinition();
				}

				_newSourceRepository = new SourceRepository((GeneratedSources) gc, newSourceRepositoryName, newSourceRepositoryDirectory);
				_newSourceRepository.setImplementationModel(implementationModel.getImplementationModel());
				getFocusedObject().getGeneratedCode().addToGeneratedRepositories(_newSourceRepository);

			}
		}
	}

	public Vector<ImplementationModelDefinition> getImplementationModels() {
		return getFocusedObject().getProject().getGeneratedSources().getImplementationModels();
	}

	public SourceRepository getNewSourceRepository() {
		return _newSourceRepository;
	}

	public String errorMessage;

	public boolean isValid() {
		if (StringUtils.isEmpty(newSourceRepositoryName)) {
			errorMessage = FlexoLocalization.localizedForKey("no_source_repository_name_defined");
			return false;
		}

		if (getFocusedObject().getProject().getExternalRepositoryWithKey(newSourceRepositoryName) != null) {
			errorMessage = FlexoLocalization.localizedForKey("a_source_repository_with_that_name_already_exists");
			return false;
		}

		if (newSourceRepositoryDirectory == null) {
			errorMessage = FlexoLocalization.localizedForKey("no_directory_defined");
			return false;
		}

		if (implementationModel == null) {
			if (createNewImplementationModel) {
				if (StringUtils.isEmpty(newImplementationModelName)) {
					errorMessage = FlexoLocalization.localizedForKey("no_implementation_model_name_defined");
					return false;
				}
				return true;
			}
			errorMessage = FlexoLocalization.localizedForKey("no_implementation_model_defined");
			return false;
		}

		return true;
	}

}
