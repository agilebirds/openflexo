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
package org.openflexo.foundation.cg.generator;

import java.util.Date;
import java.util.Vector;

import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;


public interface IFlexoResourceGenerator {

	public FlexoProject getProject();
	
	public String getIdentifier();
	
    public GeneratedCodeResult getGeneratedCode();
    
    // No more exception thrown because GenerationExceptions are handled in FlexoResourceGenerators
	public void generate(boolean forceRegenerate);

	public IGenerationException getGenerationException();
	
	public boolean isCodeAlreadyGenerated();
	
	public boolean needsGeneration();
	
	public boolean needsRegenerationBecauseOfTemplateUpdated();
	
	public boolean needsRegenerationBecauseOfTemplateUpdated(Date diskLastGenerationDate);

	public Vector<CGRepositoryFileResource> refreshConcernedResources();
	
    public void addObserver(FlexoObserver o);
    
    public void deleteObserver(FlexoObserver o);
    
	public Vector<CGTemplate> getUsedTemplates();
	
	public void setUsedTemplates(Vector<CGTemplate> templates);

	public Date getMemoryLastGenerationDate();
	
	public boolean hasFormattingException();
	
	public boolean hasAppendingException();
	
	public FlexoResource getTemplateLocator();
	
	public void addToGeneratedResourcesGeneratedByThisGenerator(CGRepositoryFileResource<?,?,? extends CGFile> resource);

	public void removeFromGeneratedResourcesGeneratedByThisGenerator(CGRepositoryFileResource<?,?,? extends CGFile> resource);
	
	public void silentlyGenerateCode();

}
