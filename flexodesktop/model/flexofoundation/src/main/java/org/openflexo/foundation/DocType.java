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
package org.openflexo.foundation;

import java.util.List;
import java.util.Vector;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.StringRepresentable;

/**
 * Represents type of generation target (Business Technical, UserManual)
 * 
 * @author sguerin
 * 
 */
public class DocType extends TargetType implements ChoiceList, StringRepresentable {

	public static enum DefaultDocType {
		Business, Technical, UserManual, Objectives;

		public static boolean isDefaultDocType(String docType, boolean ignoreCase) {
			for (DefaultDocType defaultDocType : values()) {
				if (defaultDocType.name().equals(docType) || ignoreCase && defaultDocType.name().equalsIgnoreCase(docType)) {
					return true;
				}
			}
			return false;
		}
	}

	public static class DocTypeList {

	}

	private String name;

	private FlexoProject project;

	private Vector<Format> availableFormats;

	public DocType(String name, FlexoProject project) {
		this(project);
		availableFormats = new Vector<Format>();
		availableFormats.add(Format.HTML);
		availableFormats.add(Format.LATEX);
		availableFormats.add(Format.DOCX);
		name = FileUtils.getValidFileName(name).replace(',', ' '); // Target name are used to build directory structure
		this.name = name;
	}

	/**
	 * @param project
	 */
	public DocType(FlexoProject project) {
		super(project);
		this.project = project;
	}

	public DocType(FlexoProjectBuilder builder) {
		this(builder.project);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getTemplateFolderName() {
		return getName();
	}

	/**
	 * Overrides getFullyQualifiedName
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "DOC_TYPE." + getName();
	}

	/**
	 * Overrides getXMLResourceData
	 * 
	 * @see org.openflexo.foundation.FlexoXMLSerializableObject#getXMLResourceData()
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return project;
	}

	/**
	 * Overrides getAvailableValues
	 * 
	 * @see org.openflexo.kvc.ChoiceList#getAvailableValues()
	 */
	@Override
	public List<DocType> getAvailableValues() {
		return getProject().getDocTypes();
	}

	@Override
	public Vector<Format> getAvailableFormats() {
		return availableFormats;
	}

}
