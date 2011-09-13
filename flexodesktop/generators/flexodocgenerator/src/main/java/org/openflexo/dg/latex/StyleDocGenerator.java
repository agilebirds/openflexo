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

import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.dg.rm.GeneratedFileResourceFactory;
import org.openflexo.dg.rm.LatexFileResource;
import org.openflexo.dg.rm.StyleLatexFileResource;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;


public abstract class StyleDocGenerator extends DGLatexGenerator<FlexoProject>
{
   
    private String styleName;
    
    public StyleDocGenerator(ProjectDocLatexGenerator projectGenerator, FlexoProject source, String styleName)
    {
        super(projectGenerator, source);
        this.styleName=styleName;
    }

    /**
     * Overrides buildResourcesAndSetGenerators
     * @see org.openflexo.dg.DGGenerator#buildResourcesAndSetGenerators(DGRepository, java.util.Vector)
     */
    @Override
    public void buildResourcesAndSetGenerators(DGRepository repository, Vector<CGRepositoryFileResource> resources)
    {
        // The style itself
        StyleLatexFileResource res = (StyleLatexFileResource) getProject().resourceForKey(ResourceType.LATEX_FILE,LatexFileResource.nameForRepositoryAndIdentifier(repository, getIdentifier()));
        if (res!=null && res.getCGFile()==null) {
            res.delete(false);
            res = null;
        }
        if (res == null) {
            res = GeneratedFileResourceFactory.createNewStyleLatexFileResource(repository, this, getFileName().endsWith(getFileExtension())?getFileName():getFileName()+getFileExtension());
            if (logger.isLoggable(Level.FINE))
                logger.fine("Created Style LATEX resource " + res.getName());
        } else {
            res.setGenerator(this);
            if (logger.isLoggable(Level.FINE))
                logger.fine("Successfully retrieved Style LATEX resource " + res.getName());
        }
        res.registerObserverWhenRequired();
        resources.add(res);
    }
    
    /**
     * Overrides generate
     * @see org.openflexo.dg.DGGenerator#generate(boolean)
     */
    @Override
    public void generate(boolean forceRegenerate)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("Called StyleDocGenerator.generate(forceRegenerate)");
        super.generate(forceRegenerate);
    }
    
    /**
     * Overrides getFileName
     * @see org.openflexo.dg.DGGenerator#getFileName()
     */
    @Override
    public String getFileName()
    {
        return styleName;
    }

    /**
     * Overrides getIdentifier
     * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#getIdentifier()
     */
    @Override
	public String getIdentifier()
    {
        return "Style-Latex-"+getStyleName();
    }

    public String getStyleName()
    {
        return styleName;
    }

    public void setStyleName(String styleName)
    {
        this.styleName = styleName;
    }
    
    /**
     * Overrides getFileExtension
     * @see org.openflexo.dg.latex.DGLatexGenerator#getFileExtension()
     */
    @Override
    public String getFileExtension()
    {
        return ".sty";
    }
}
