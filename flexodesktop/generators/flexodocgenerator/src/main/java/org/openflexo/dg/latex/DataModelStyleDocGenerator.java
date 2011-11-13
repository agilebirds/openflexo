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

import org.openflexo.foundation.rm.FlexoProject;

/**
 * @author gpolet
 * 
 */
public class DataModelStyleDocGenerator extends StyleDocGenerator {

	private static final String TEMPLATE_NAME = "flexodm.sty.vm";

	/**
	 * @param projectGenerator
	 * @param source
	 * @param styleName
	 */
	public DataModelStyleDocGenerator(ProjectDocLatexGenerator projectGenerator, FlexoProject source) {
		super(projectGenerator, source, "flexodm.sty");
	}

	/**
	 * Overrides getTemplateName
	 * 
	 * @see org.openflexo.dg.DGGenerator#getTemplateName()
	 */
	@Override
	public String getTemplateName() {
		return TEMPLATE_NAME;
	}

}
