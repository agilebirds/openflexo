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
package org.openflexo.dg.exception;

import org.openflexo.dg.latex.ProjectDocLatexGenerator;
import org.openflexo.generator.exception.GenerationException;


/**
 * @author gpolet
 *
 */
public class PDFGenerationFailedException extends GenerationException
{
	
	private String errorMessage;

    /**
	 * @param message
	 * @param projectGenerator
	 */
	public PDFGenerationFailedException(ProjectDocLatexGenerator projectGenerator)
	{
		this(projectGenerator, null);
	}

	/**
     * @param projectGenerator
     * @param errorMessage TODO
     * @param message
     */
    public PDFGenerationFailedException(ProjectDocLatexGenerator projectGenerator, String errorMessage)
    {
        super("pdf_generation_failed"+": "+errorMessage);
    }

}
