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

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.templates.CGDirectoryTemplateSet;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.toolbox.FileUtils;

public class RedefineCustomTemplateFile extends FlexoAction<RedefineCustomTemplateFile, CGTemplate, CGTemplate> {

	private static final Logger logger = Logger.getLogger(RedefineCustomTemplateFile.class.getPackage().getName());

	public static FlexoActionType<RedefineCustomTemplateFile, CGTemplate, CGTemplate> actionType = new FlexoActionType<RedefineCustomTemplateFile, CGTemplate, CGTemplate>(
			"redefine_template", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RedefineCustomTemplateFile makeNewAction(CGTemplate focusedObject, Vector<CGTemplate> globalSelection, FlexoEditor editor) {
			return new RedefineCustomTemplateFile(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGTemplate object, Vector<CGTemplate> globalSelection) {
			return object != null && object.isApplicationTemplate();
		}

		@Override
		protected boolean isEnabledForSelection(CGTemplate object, Vector<CGTemplate> globalSelection) {
			return object != null && object.isApplicationTemplate();
		}

	};

	static {
		FlexoModelObject.addActionForClass(RedefineCustomTemplateFile.actionType, CGTemplate.class);
	}

	private CustomCGTemplateRepository _repository;
	private TargetType _target;
	private CGTemplateFile _newTemplateFile;

	RedefineCustomTemplateFile(CGTemplate focusedObject, Vector<CGTemplate> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException {
		logger.info("Redefine CustomTemplateFile " + getFocusedObject());
		if (getFocusedObject() != null && _repository != null) {
			_newTemplateFile = redefineTemplate(getFocusedObject());
		}
		if (getGlobalSelection() != null) {
			for (CGTemplate t : getGlobalSelection()) {
				if (t != getFocusedObject()) {
					redefineTemplate(t);
				}
			}
		}
		_repository.refresh();
	}

	protected CGTemplateFile redefineTemplate(CGTemplate template) throws IOFlexoException {
		File createdFile;
		CGDirectoryTemplateSet set;
		if (getTarget() == null) {
			logger.info("Ici1");
			set = _repository.getCommonTemplates();
		} else {
			logger.info("Ici2");
			set = _repository.getTemplateSetForTarget(getTarget(), true);
		}
		String relativePath = template.getRelativePathWithoutSetPrefix();
		createdFile = new File(set.getDirectory(), relativePath);
		logger.info("CreatedFile=" + createdFile.getAbsolutePath());
		try {
			FileUtils.saveToFile(createdFile, template.getContent());
		} catch (IOException e) {
			throw new IOFlexoException(e);
		}
		return set.getTemplate(relativePath);
	}

	public CustomCGTemplateRepository getRepository() {
		return _repository;
	}

	public void setRepository(CustomCGTemplateRepository repository) {
		_repository = repository;
	}

	public TargetType getTarget() {
		return _target;
	}

	public void setTarget(TargetType target) {
		_target = target;
	}

	public CGTemplateFile getNewTemplateFile() {
		return _newTemplateFile;
	}

}
