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
package org.openflexo.generator.exception;

import java.io.IOException;

import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.generator.AbstractProjectGenerator;

public class TemplateNotFoundException extends TemplateReplacementException {
	public TemplateNotFoundException(String templateName, String searchedDirectories,
			AbstractProjectGenerator<? extends GenerationRepository> projectGenerator) {
		super("Template not found: " + templateName, "template_not_found", "template name : " + templateName + "\n" + searchedDirectories,
				new Exception(), projectGenerator);
	}

	public TemplateNotFoundException(String templateName, IOException ioException,
			AbstractProjectGenerator<? extends GenerationRepository> projectGenerator) {
		super("Template not found: " + templateName, "template_not_found", "template name : " + templateName + "\nIOException occured.",
				ioException, projectGenerator);
	}
}
