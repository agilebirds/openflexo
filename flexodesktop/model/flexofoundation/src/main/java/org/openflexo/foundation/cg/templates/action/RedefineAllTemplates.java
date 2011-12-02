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
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateObject;
import org.openflexo.foundation.cg.templates.CGTemplateRepository;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.cg.templates.TargetSpecificCGTemplateSet;
import org.openflexo.toolbox.FileUtils;

public class RedefineAllTemplates extends FlexoAction<RedefineAllTemplates, CGTemplateRepository, CGTemplateObject> {

	private static final Logger logger = Logger.getLogger(RedefineAllTemplates.class.getPackage().getName());

	public static FlexoActionType<RedefineAllTemplates, CGTemplateRepository, CGTemplateObject> actionType = new FlexoActionType<RedefineAllTemplates, CGTemplateRepository, CGTemplateObject>(
			"redefine_all_template", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RedefineAllTemplates makeNewAction(CGTemplateRepository focusedObject, Vector<CGTemplateObject> globalSelection,
				FlexoEditor editor) {
			return new RedefineAllTemplates(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGTemplateRepository object, Vector<CGTemplateObject> globalSelection) {
			return object != null && !(object instanceof CustomCGTemplateRepository);
		}

		@Override
		protected boolean isEnabledForSelection(CGTemplateRepository object, Vector<CGTemplateObject> globalSelection) {
			return object != null && !(object instanceof CustomCGTemplateRepository);
		}

	};

	static {
		FlexoModelObject.addActionForClass(RedefineAllTemplates.actionType, CGTemplateRepository.class);
	}

	private CustomCGTemplateRepository _repository;

	RedefineAllTemplates(CGTemplateRepository focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException {
		logger.info("RedefineAllTemplates from " + getFocusedObject().getName() + " to " + getRepository().getName());
		CGTemplateRepository source = getFocusedObject();
		CustomCGTemplateRepository dest = getRepository();
		Enumeration<CGTemplate> en = source.getCommonTemplates().getAllTemplates();
		while (en.hasMoreElements()) {
			CGTemplate candidate = en.nextElement();
			if (candidate.getTemplateName().equals("VM_global_library.vm")) {
				continue;
			}
			CGTemplate existingRedefinedTemplate = dest.getCommonTemplates().getTemplate(candidate.getRelativePath());
			if (existingRedefinedTemplate != null) {
				logger.info("Template '" + existingRedefinedTemplate.getTemplateName() + "' is already redefined in the repository "
						+ dest.getName());
			} else {
				redefineTemplate(candidate, null);
			}
		}
		Enumeration<TargetSpecificCGTemplateSet> en2 = source.getTargetSpecificTemplates();
		while (en2.hasMoreElements()) {
			TargetSpecificCGTemplateSet targetTemplateSet = en2.nextElement();
			TargetType targetType = targetTemplateSet.getTargetType();
			en = targetTemplateSet.getAllTemplates();
			while (en.hasMoreElements()) {
				CGTemplate candidate = en.nextElement();
				CGTemplate existingRedefinedTemplate = null;
				if (dest.getTemplateSetForTarget(targetType) != null) {
					existingRedefinedTemplate = dest.getTemplateSetForTarget(targetType).getTemplate(candidate.getRelativePath());
				}
				if (existingRedefinedTemplate != null) {
					logger.info("Template '" + existingRedefinedTemplate.getTemplateName() + "' is already redefined in the repository "
							+ dest.getName());
				} else {
					redefineTemplate(candidate, targetType);
				}
			}
		}
		dest.refresh();
	}

	private void redefineTemplate(CGTemplate src, TargetType target) throws IOFlexoException {
		File createdFile = null;
		if (target == null) {
			createdFile = new File(_repository.getDirectory(), src.getRelativePath());
		} else {
			createdFile = new File(new File(_repository.getDirectory(), target.getTemplateFolderName()), src.getRelativePath());
		}
		try {
			FileUtils.saveToFile(createdFile, src.getContent());
		} catch (IOException e) {
			throw new IOFlexoException(e);
		}
	}

	public CustomCGTemplateRepository getRepository() {
		return _repository;
	}

	public void setRepository(CustomCGTemplateRepository repository) {
		_repository = repository;
	}

}
