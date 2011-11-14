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
package org.openflexo.dg.rm;

import java.util.logging.Logger;

import org.openflexo.dg.latex.StyleDocGenerator;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class StyleLatexFileResource extends LatexFileResource<StyleDocGenerator> implements FlexoObserver {
	protected static final Logger logger = FlexoLogger.getLogger(StyleLatexFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public StyleLatexFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public StyleLatexFileResource(FlexoProject aProject) {
		super(aProject);
	}

	private boolean isObserverRegistered = false;

	@Override
	public String getName() {
		if (getCGFile() == null || getCGFile().getRepository() == null || getIdentifier() == null) {
			return super.getName();
		}
		registerObserverWhenRequired();
		if (super.getName() == null) {
			setName(nameForRepositoryAndIdentifier(getCGFile().getRepository(), getIdentifier()));
		}
		return nameForRepositoryAndIdentifier(getCGFile().getRepository(), getIdentifier());
	}

	public void registerObserverWhenRequired() {
		if (!isObserverRegistered) {
			isObserverRegistered = true;
		}
	}

	@Override
	protected LatexFile createGeneratedResourceData() {
		return new LatexFile(getFile(), this);
	}

}
