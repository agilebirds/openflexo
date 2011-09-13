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
package org.openflexo.tm.bpslib.impl;

import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.foundation.xml.ImplementationModelBuilder;

/**
 * @author Nicolas Daniels
 */
public class BpsLibImplementation extends TechnologyModuleImplementation {

	public static final String TECHNOLOGY_MODULE_NAME = "BpsLib";

	protected boolean includeMails = true;
	protected boolean includeLocalization = true;

	// ================ //
	// = Constructors = //
	// ================ //

	public BpsLibImplementation(ImplementationModelBuilder builder) throws TechnologyModuleCompatibilityCheckException {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	public BpsLibImplementation(ImplementationModel implementationModel) throws TechnologyModuleCompatibilityCheckException {
		super(implementationModel);
	}

	// =========== //
	// = Methods = //
	// =========== //

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

	/* ===================== */
	/* ====== Actions ====== */
	/* ===================== */

	// =================== //
	// = Getter / Setter = //
	// =================== //

	public boolean getIncludeMails() {
		return includeMails;
	}

	public void setIncludeMails(boolean includeMails) {
		if (requireChange(this.includeMails, includeMails)) {
			Object oldValue = this.includeMails;
			this.includeMails = includeMails;
			setChanged();
			notifyObservers(new SGAttributeModification("includeMails", oldValue, includeMails));
		}
	}

	public boolean getIncludeLocalization() {
		return includeLocalization;
	}

	public void setIncludeLocalization(boolean includeLocalization) {
		if (requireChange(this.includeLocalization, includeLocalization)) {
			Object oldValue = this.includeLocalization;
			this.includeLocalization = includeLocalization;
			setChanged();
			notifyObservers(new SGAttributeModification("includeLocalization", oldValue, includeLocalization));
		}
	}
}
