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
package org.openflexo.foundation.cg.templates;

import java.util.Vector;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.rm.FlexoProject;

public class CGTemplateFolder extends CGTemplateObject {
	private CGTemplateSet templateSet;
	private String folderName;
	public Vector<CGTemplateFolder> dirs = new Vector<CGTemplateFolder>();
	public Vector<CGTemplate> templates = new Vector<CGTemplate>();

	public CGTemplateFolder(CGTemplateSet templateSet, String name) {
		this.templateSet = templateSet;
		folderName = name;
	}

	@Override
	public String getName() {
		return folderName;
	}

	public CGTemplateFolder getFolder(String name) {
		for (CGTemplateFolder dir : dirs) {
			if (name.equals(dir.getName())) {
				return dir;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlexoProject getProject() {
		return templateSet.getProject();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CGTemplates getTemplates() {
		return templateSet.getTemplates();
	}

	@Override
	public String getClassNameKey() {
		return "template_folder";
	}

	@Override
	public String getFullyQualifiedName() {
		return "CGTemplateFolder." + getName();
	}

	@Override
	public void update() {
	}

	@Override
	public String getInspectorName() {
		return Inspectors.GENERATORS.CG_FOLDER_INSPECTOR;
	}
}