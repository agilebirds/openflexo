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
package org.openflexo.foundation.cg;

import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.cg.templates.CGCodeTemplates;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.GeneratedCodeResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class GeneratedCode extends GeneratedOutput {

	private static final Logger logger = FlexoLogger.getLogger(GeneratedCode.class.getPackage().getName());
	private CGCodeTemplates _templates;

	/**
	 * Creates and returns a newly created workflow
	 * 
	 * @return a newly created workflow
	 */
	public static GeneratedOutput createNewGeneratedCode(FlexoProject project) {
		GeneratedOutput newCG = new GeneratedCode(project);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("createNewGeneratedCode(), project=" + project + " " + newCG);
		}

		File cgFile = ProjectRestructuration.getExpectedGeneratedCodeFile(project);
		FlexoProjectFile generatedCodeFile = new FlexoProjectFile(cgFile, project);
		GeneratedCodeResource cgRes;
		try {
			cgRes = new GeneratedCodeResource(project, newCG, generatedCodeFile);
		} catch (InvalidFileNameException e2) {
			e2.printStackTrace();
			generatedCodeFile = new FlexoProjectFile("GeneratedCode");
			generatedCodeFile.setProject(project);
			try {
				cgRes = new GeneratedCodeResource(project, newCG, generatedCodeFile);
			} catch (InvalidFileNameException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Could not create generated code.");
				}
				e.printStackTrace();
				return null;
			}
		}

		try {
			cgRes.saveResourceData();
			project.registerResource(cgRes);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		return newCG;
	}

	@Override
	public String getHelpText() {
		return FlexoLocalization.localizedForKey("generated_code_help_text");
	}

	/**
	 * @param project
	 */
	public GeneratedCode(FlexoProject project) {
		super(project);
	}

	/**
	 * @param project
	 */
	public GeneratedCode(GeneratedCodeBuilder builder) {
		this(builder.getProject());
		builder.generatedCode = this;
		_resource = builder.resource;
		initializeDeserialization(builder);
	}

	@Override
	public String getFullyQualifiedName() {
		return "GeneratedCode";
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "generated_code";
	}

	@Override
	public String getInspectorName() {
		return Inspectors.CG.GENERATED_CODE_INSPECTOR;
	}

	@Override
	public CGCodeTemplates getTemplates() {
		if (_templates == null) {
			Vector<TargetType> v = new Vector<TargetType>();
			v.addAll(CodeType.availableValues());
			_templates = new CGCodeTemplates(getProject(), v);
		}
		return _templates;
	}

	/**
	 * Overrides getDefaultRepositoryName
	 * 
	 * @see org.openflexo.foundation.cg.GeneratedOutput#getDefaultRepositoryName()
	 */
	@Override
	public String getDefaultRepositoryName() {
		return "default_cg_repository_name";
	}

}
