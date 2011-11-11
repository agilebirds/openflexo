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
package org.openflexo.dg.latex;

import org.openflexo.dg.DGGenerator;
import org.openflexo.dg.ProjectDocGenerator;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.toc.TOCEntry;

/**
 * 
 * This class is intended for generators that don't generate any physical file on the disk but initiates multiple generators. Generators
 * that generate a physical file and also initiates multiple generators should not inherit from this class because file generation is
 * prevented.
 * 
 * @author gpolet
 * 
 */
public abstract class AbstractCompoundGenerator<T extends FlexoModelObject> extends DGGenerator<T> {

	/**
	 * @param projectGenerator
	 * @param source
	 */
	public AbstractCompoundGenerator(ProjectDocGenerator projectGenerator, T source) {
		super(projectGenerator, source);
	}

	/**
	 * Overrides generate
	 * 
	 * @see org.openflexo.dg.DGGenerator#generate(boolean)
	 */
	@Override
	public void generate(boolean forceRegenerate) {
		// We prevent compound generators with no physical file generated from
		// attempting to generate a file (it would crash of course)
	}

	/**
	 * Overrides getFileName
	 * 
	 * @see org.openflexo.dg.DGGenerator#getFileName()
	 */
	@Override
	final public String getFileName() {
		return null;
	}

	@Override
	final public String getFileExtension() {
		return null;
	}

	/**
	 * Overrides getTemplateName
	 * 
	 * @see org.openflexo.dg.DGGenerator#getTemplateName()
	 */
	@Override
	final public String getTemplateName() {
		return null;
	}

	@Override
	final public TOCEntry getTOCEntry() {
		return null;
	}
}
