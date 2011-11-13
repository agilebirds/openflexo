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
package org.openflexo.generator.utils;

import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.ie.ComponentGenerator;

/**
 * @author nid
 * 
 */
public class StaticComponentGenerator extends ComponentGenerator {
	private String templateNamePrefix;

	public StaticComponentGenerator(ProjectGenerator projectGenerator, String templateNamePrefix, String componentGeneratedName) {
		super(projectGenerator, null, componentGeneratedName);
		this.templateNamePrefix = templateNamePrefix;
	}

	/**
	 * @see org.openflexo.generator.ie.ComponentGenerator#getJavaTemplate()
	 */
	@Override
	public String getJavaTemplate() {
		return templateNamePrefix + ".java.vm";
	}

	/**
	 * @see org.openflexo.generator.ie.ComponentGenerator#getWodTemplate()
	 */
	@Override
	public String getWodTemplate() {
		return templateNamePrefix + ".wod.vm";
	}

	/**
	 * @see org.openflexo.generator.ie.ComponentGenerator#getHtmlTemplate()
	 */
	@Override
	public String getHtmlTemplate() {
		return templateNamePrefix + ".html.vm";
	}

	/**
	 * @see org.openflexo.generator.ie.ComponentGenerator#getApiTemplate()
	 */
	@Override
	public String getApiTemplate() {
		return templateNamePrefix + ".api.vm";
	}
}
