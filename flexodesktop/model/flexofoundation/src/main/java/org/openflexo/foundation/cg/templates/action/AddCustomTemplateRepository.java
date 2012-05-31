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
package org.openflexo.foundation.cg.templates.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.templates.CGTemplateObject;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.cg.utils.TemplateRepositoryType;
import org.openflexo.foundation.rm.CustomTemplatesResource;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.utils.FlexoProjectFile;

public class AddCustomTemplateRepository extends FlexoAction<AddCustomTemplateRepository, CGTemplates, CGTemplateObject> {

	private static final Logger logger = Logger.getLogger(AddCustomTemplateRepository.class.getPackage().getName());

	public static FlexoActionType<AddCustomTemplateRepository, CGTemplates, CGTemplateObject> actionType = new FlexoActionType<AddCustomTemplateRepository, CGTemplates, CGTemplateObject>(
			"add_custom_templates_repository", FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddCustomTemplateRepository makeNewAction(CGTemplates focusedObject, Vector<CGTemplateObject> globalSelection,
				FlexoEditor editor) {
			return new AddCustomTemplateRepository(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGTemplates object, Vector<CGTemplateObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(CGTemplates object, Vector<CGTemplateObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddCustomTemplateRepository.actionType, CGTemplates.class);
	}

	private boolean associateTemplateRepository = true;

	private CustomCGTemplateRepository _newCustomTemplatesRepository;

	private String _newCustomTemplatesRepositoryName;

	private FlexoProjectFile _newCustomTemplatesRepositoryDirectory;

	private TemplateRepositoryType repositoryType;

	AddCustomTemplateRepository(CGTemplates focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateCodeRepositoryNameException, InvalidFileNameException,
			DuplicateResourceException {
		if (getFocusedObject().getTemplates().getCustomCGTemplateRepositoryForName(getNewCustomTemplatesRepositoryName()) != null
				|| getFocusedObject().getProject().resourceForFileName(getNewCustomTemplatesRepositoryDirectory()) != null) {
			throw new DuplicateResourceException(ResourceType.CUSTOM_TEMPLATES + "." + getNewCustomTemplatesRepositoryName());
		}

		logger.info("Add CustomTemplateRepository " + getFocusedObject());
		if (getFocusedObject() != null) {
			getNewCustomTemplatesRepositoryDirectory().getFile().mkdirs();
			CustomTemplatesResource newResource = new CustomTemplatesResource(getFocusedObject().getProject(),
					getNewCustomTemplatesRepositoryName(), getNewCustomTemplatesRepositoryDirectory());
			getFocusedObject().getProject().registerResource(newResource);
			getFocusedObject().getTemplates().refresh();
			_newCustomTemplatesRepository = getFocusedObject().getTemplates().getCustomCGTemplateRepositoryForName(
					getNewCustomTemplatesRepositoryName());
			_newCustomTemplatesRepository.setRepositoryType(getRepositoryType());
		}
	}

	public String getNewCustomTemplatesRepositoryName() {
		return _newCustomTemplatesRepositoryName;
	}

	public void setNewCustomTemplatesRepositoryName(String newCustomTemplatesRepositoryName) {
		_newCustomTemplatesRepositoryName = newCustomTemplatesRepositoryName;
	}

	public FlexoProjectFile getNewCustomTemplatesRepositoryDirectory() {
		return _newCustomTemplatesRepositoryDirectory;
	}

	public void setNewCustomTemplatesRepositoryDirectory(FlexoProjectFile newGeneratedCodeRepositoryDirectory) {
		_newCustomTemplatesRepositoryDirectory = newGeneratedCodeRepositoryDirectory;
	}

	public CustomCGTemplateRepository getNewCustomTemplatesRepository() {
		return _newCustomTemplatesRepository;
	}

	public TemplateRepositoryType getRepositoryType() {
		return repositoryType;
	}

	public void setRepositoryType(TemplateRepositoryType repositoryType) {
		this.repositoryType = repositoryType;
	}

	public boolean isAssociateTemplateRepository() {
		return associateTemplateRepository;
	}

	public void setAssociateTemplateRepository(boolean associateTemplateRepository) {
		this.associateTemplateRepository = associateTemplateRepository;
	}

}
