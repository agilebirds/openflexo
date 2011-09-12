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

import java.util.Date;
import java.util.logging.Level;

import org.openflexo.dg.docx.DGDocxXMLGenerator;
import org.openflexo.dg.docx.DocxTemplatesEnum;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoTOCResource;
import org.openflexo.foundation.rm.GeneratedDocResource;
import org.openflexo.foundation.wkf.FlexoProcess;


public class ProjectDocxXmlFileResource extends DocxXmlFileResource<DGDocxXMLGenerator<FlexoProject>> implements FlexoObserver
{
	private static final String RELS_SUFFIX = ".FILERELS";

	private boolean isObserverRegistered = false;

	public ProjectDocxXmlFileResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }

	public ProjectDocxXmlFileResource(DGDocxXMLGenerator<FlexoProject> generator, DocxTemplatesEnum docxTemplate)
	{
		super(generator.getProject());
		setGenerator(generator);
		generator.addDocxResource(this, docxTemplate);
		registerObserverWhenRequired();
	}

	@Override
	public String getName()
	{
		if(super.getName() == null && getCGFile()!=null && getCGFile().getRepository() != null && getGenerator() != null && getGenerator().getDocxTemplateForResource(this) != null)
			setName(nameForRepositoryAndDocxTemplate(getCGFile().getRepository(), getGenerator().getDocxTemplateForResource(this)));

		return super.getName();
	}

	public void registerObserverWhenRequired()
	{
		if ((!isObserverRegistered) && (getProject() != null))
		{
			isObserverRegistered = true;
			if (logger.isLoggable(Level.FINE))
				logger.fine("*** addObserver " + getFileName() + " for " + getProject());
			getProject().addObserver(this);
		}
	}

	public static String nameForRepositoryAndDocxTemplate(GenerationRepository repository, DocxTemplatesEnum docxTemplate)
	{
		return repository.getName() + ".PROJECT_DOCX." + docxTemplate;
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies()
	{
		super.rebuildDependancies();
		if(getGenerator()!=null && getGenerator().getDocxTemplateForResource(this).getIsFullProjectDependent())
		{
			for(FlexoProcess process : getProject().getAllLocalFlexoProcesses())
				addToDependantResources(process.getFlexoResource());
			addToDependantResources(getProject().getWorkflow().getFlexoResource());
			addToDependantResources(getProject().getDataModel().getFlexoResource());
			for(ComponentDefinition comp:getProject().getFlexoComponentLibrary().getAllComponentList())
				addToDependantResources(comp.getComponentResource());
			addToDependantResources(getProject().getFlexoComponentLibraryResource().getFlexoResource());
			addToDependantResources(getProject().getFlexoDKVResource());
		}
		addToDependantResources(getProject().getTOCResource());
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{

	}

	/**
	 * Return dependancy computing between this resource, and an other resource,
	 * asserting that this resource is contained in this resource's dependant
	 * resources
	 *
	 * @param resource
	 * @param dependancyScheme
	 * @return
	 */
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate)
	{
		if (resource instanceof GeneratedDocResource)
		{
			if (getGenerator() != null)
			{
				if (!requestDate.before(getGenerator().getRepository().getLastUpdateDate()))
				{
					if (logger.isLoggable(Level.FINER))
						logger.finer("OPTIMIST DEPENDANCY CHECKING for DGRepository " + getRepository());
					return false;
				}
			}
		}
		else if (resource instanceof FlexoTOCResource)
		{
			if (getGenerator() != null && getGenerator().getRepository().getTocRepository() != null)
			{
				if (!requestDate.before(getGenerator().getRepository().getTocRepository().getLastUpdateDate()))
				{
					if (logger.isLoggable(Level.FINER))
						logger.finer("OPTIMIST DEPENDANCY CHECKING for TOC ENTRY " + getRepository());
					return false;
				}
			}
			return true;
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

	@Override
	public String getFileName()
	{
		if(getFile() != null)
			return getFile().getName();

    	if (getGenerator() != null)
            return getGenerator().getFileNameForResource(this);

    	return null;
	}

	@Override
	public String getGenerationResultKey()
	{
		if(getGenerator() != null)
			return getGenerator().getGenerationResultKeyForResource(this);
		logger.warning("Attempt to get a GenerationResultKey on a resource without generator !");
		return null;
	}
}
