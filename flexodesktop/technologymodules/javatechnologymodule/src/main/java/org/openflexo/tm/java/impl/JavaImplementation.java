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
package org.openflexo.tm.java.impl;

import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.toolbox.JavaUtils;

/**
 * This class defines properties related to a Java implementation.
 * 
 * @author Emmanuel Koch, Blue Pimento Services SPRL
 */
public class JavaImplementation extends TechnologyModuleImplementation {

	public static final String TECHNOLOGY_MODULE_NAME = "Java";
	private static final String DEFAULT_PACKAGE_NAME = "org.openflexo";

	/** The project name i.e. "Flexo" */
	private String projectName = getProject().getName();
	/** The root package, without the archive project name i.e. "org.openflexo" if you want to generates your sources in "org.openflexo" */
	private String rootPackage;
	/** The archive name i.e. we generate a flexo.war */
	private String archiveName = getProject().getName().replace(" ", "_").toLowerCase();

	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Java implementation for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during unserialization.
	 * 
	 * @param builder
	 *            the builder that will create this implementation
	 */
	public JavaImplementation(ImplementationModelBuilder builder) throws TechnologyModuleCompatibilityCheckException {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Java implementation for the specified implementation model.
	 * 
	 * @param implementationModel
	 *            the implementation model where to create this Java implementation
	 */
	public JavaImplementation(ImplementationModel implementationModel) throws TechnologyModuleCompatibilityCheckException {
		super(implementationModel);
		this.rootPackage = JavaUtils.getPackageName(DEFAULT_PACKAGE_NAME + "." + getProjectName());
	}

	// =========== //
	// = Methods = //
	// =========== //

	/**
	 * Returns the rootPackage with "/" in place of "."
	 */
	public String getRootPath() {
		return rootPackage.replace(".", "/");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TechnologyModuleDefinition getTechnologyModuleDefinition() {
		return TechnologyModuleDefinition.getTechnologyModuleDefinition(TECHNOLOGY_MODULE_NAME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getHasInspector() {
		return true;
	}

	// =================== //
	// = Getter / Setter = //
	// =================== //

	/**
	 * The project name i.e. "Flexo".<br/>
	 * Note : the project name is independent of the root folder, since you can have many implementations of the same project.<br/>
	 * This is namely used by the pom.xml
	 * 
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		if (requireChange(this.projectName, projectName)) {
			Object oldValue = this.projectName;
			this.projectName = projectName;
			setChanged();
			notifyObservers(new SGAttributeModification("projectName", oldValue, projectName));
		}
	}

	/**
	 * The root package, without the archive project name.<br/>
	 * i.e. use a root package with "org.openflexo" and an archive name with "flexo" if you want to generates your sources in
	 * "org.openflexo"
	 * 
	 * @return the rootPackage
	 */
	public String getRootPackage() {
		return JavaUtils.getPackageName(rootPackage);
	}

	public void setRootPackage(String rootPackage) {
		if (requireChange(this.rootPackage, rootPackage)) {
			Object oldValue = this.rootPackage;
			this.rootPackage = rootPackage;
			setChanged();
			notifyObservers(new SGAttributeModification("rootPackage", oldValue, rootPackage));
		}
	}

	/**
	 * The archive name i.e. we generate a flexo.war
	 * 
	 * @return the archiveName
	 */
	public String getArchiveName() {
		return archiveName;
	}

	public void setArchiveName(String archiveName) {
		if (requireChange(this.archiveName, archiveName)) {
			Object oldValue = this.archiveName;
			this.archiveName = archiveName;
			setChanged();
			notifyObservers(new SGAttributeModification("archiveName", oldValue, archiveName));
		}
	}

}
