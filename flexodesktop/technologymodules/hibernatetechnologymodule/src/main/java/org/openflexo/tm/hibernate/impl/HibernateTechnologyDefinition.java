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
package org.openflexo.tm.hibernate.impl;

import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleInitializationException;
import org.openflexo.sgmodule.SGModule;
import org.openflexo.tm.hibernate.gui.HibernateGUIFactory;


/**
 * @author nid
 *
 */
public class HibernateTechnologyDefinition extends TechnologyModuleDefinition {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TechnologyModuleImplementation createNewImplementation(ImplementationModel implementationModel) throws TechnologyModuleCompatibilityCheckException {
		HibernateImplementation hibernateImplementation = new HibernateImplementation(implementationModel);

		try {
			HibernateModel.createNewHibernateModel("Model", hibernateImplementation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return hibernateImplementation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadModule() throws TechnologyModuleInitializationException {
		super.loadModule();
		SGModule.recordTechnologyModuleGUIFactory(HibernateImplementation.class, new HibernateGUIFactory());
	}
}
