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
package org.openflexo.dg.html;

import java.util.regex.Pattern;

import org.openflexo.dg.DGGenerator;
import org.openflexo.dg.file.DGTextFile;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.generator.GeneratedCodeResult;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.toolbox.HTMLUtils;


/**
 * @author ndaniels
 * 
 */
public class DGTextGenerator<T extends FlexoModelObject> extends DGGenerator<T> implements IFlexoResourceGenerator
{
    protected static final String UPPER_CASE_REGEXP = "[A-Z]+";

    protected static final Pattern UPPER_CASE_PATTERN = Pattern.compile(UPPER_CASE_REGEXP);

    protected static final String LATEX_BACKSLASH = HTMLUtils.LATEX_BACKSLASH;

    protected static final String JAVA_BACKSLASH = "\\";

    protected static final String LATEX_TAG_REGEXP = "^\\\\[^ {]+(\\{[^}]*\\}\\s*)*";

    protected static final Pattern LATEX_TAG_PATTERN = Pattern.compile(LATEX_TAG_REGEXP);

    protected static final String CHARS_TO_ESCAPE_REGEXP = "[\\\\{}_&%#~]";

    protected static final Pattern CHARS_TO_ESCAPE_PATTERN = Pattern.compile(CHARS_TO_ESCAPE_REGEXP);

	private TextFileResource<DGTextGenerator<T>, DGTextFile> textResource;

    public DGTextGenerator(ProjectDocHTMLGenerator projectGenerator, T source, String templateName, String fileName)
    {
    	super(projectGenerator, source, templateName, null, fileName, null);
    }

    @Override
	public boolean isCodeAlreadyGenerated()
    {
        return getGeneratedCode() != null;
    }
    
    /**
     * Overrides getSymbolicDirectory
     * @see org.openflexo.dg.FlexoLatexResourceGenerator#getSymbolicDirectory(org.openflexo.foundation.cg.GenerationRepository)
     */
    public CGSymbolicDirectory getSymbolicDirectory(DGRepository repository)
    {
        return repository.getHTMLSymbolicDirectory();
    }
    
    /**
     * Overrides getFileExtension
     * @see org.openflexo.dg.DGGenerator#getFileExtension()
     */
    @Override
    public String getFileExtension()
    {
        return "";
    }
    
	@Override
	public GeneratedCodeResult getGeneratedCode() {
		if (generatedCode==null && textResource!=null && textResource.getASCIIFile()!=null && textResource.getASCIIFile().hasLastAcceptedContent()) {
			generatedCode = new GeneratedTextResource(getFileName(),textResource.getASCIIFile().getLastAcceptedContent());
		}
		return super.getGeneratedCode();
	}
	
	public void setTextResource(TextFileResource<DGTextGenerator<T>, DGTextFile> textResource) {
		this.textResource = textResource;
	}
    
	@Override
	public boolean needsGeneration() {
		return super.needsGeneration();
	}
}
