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
package org.openflexo.foundation.sg.implmodel.exception;

import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;

/**
 * @author Nicolas Daniels
 *
 */
public class TechnologyModuleCompatibilityCheckException extends ImplementationModelException {

	private StringBuilder message;
	private TechnologyModuleDefinition module;
	private TechnologyModuleDefinition moduleIncompatibleWith;

	public TechnologyModuleCompatibilityCheckException(TechnologyModuleDefinition module, TechnologyModuleDefinition moduleIncompatibleWith) {
		super();
		this.module = module;
		this.moduleIncompatibleWith = moduleIncompatibleWith;
		message = new StringBuilder("Module '" + module.getName() + "' is incompatible with module '" + moduleIncompatibleWith.getName() + "'");
	}

	public TechnologyModuleDefinition getModule() {
		return module;
	}

	public TechnologyModuleDefinition getModuleIncompatibleWith() {
		return moduleIncompatibleWith;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage() {
		return message.toString();
	}

	public void prependMessage(String pre) {
		message.insert(0, pre + "\n");
	}
}
