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
package org.openflexo.foundation.cg.templates;


import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.cg.utils.TemplateRepositoryType;
import org.openflexo.foundation.rm.CustomTemplatesResource;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.logging.FlexoLogger;

public class CustomCGTemplateRepository extends CGTemplateRepository {
	
	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(CustomCGTemplateRepository.class.getPackage().getName());

	private CustomTemplatesResource _resource;
	
	private TemplateRepositoryType repositoryType;
	
	public CustomCGTemplateRepository (CGTemplates templates, CustomTemplatesResource resource, Vector<TargetType> availableTargets)
	{
		super(resource.getDirectory(),templates, availableTargets);
		_resource = resource;
	}
	
	@Override
	public String getFullyQualifiedName() 
	{
		return getTemplates().getFullyQualifiedName()+"."+_resource.getName();
	}

	public CustomTemplatesResource getResource() 
	{
		return _resource;
	}
	
	@Override
	public void delete() {
		if (_resource!=null)
			_resource.delete();
		super.delete();
		getTemplates().update();
	}

	@Override
	public String getName()
	{
		if (getResource() != null) return getResource().getName();
		return null;
	}
	
	@Override
	public void setName(String aName)
	{
		if (getResource() != null) {
			try {
				getResource().getProject().renameResource(getResource(), aName);
			} catch (DuplicateResourceException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean readOnly()
	{
		return false;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.GENERATORS.CG_CUSTOM_TEMPLATE_REPOSITORY;
	}

	public TemplateRepositoryType getRepositoryType() 
	{
		if (repositoryType==null) {
			Enumeration<TargetSpecificCGTemplateSet> en = getTargetSpecificTemplates(); 
			while (repositoryType==null && en.hasMoreElements()) {
				TargetSpecificCGTemplateSet set = en.nextElement();
				if (set.getTargetType() instanceof CodeType)
					repositoryType = TemplateRepositoryType.Code;
				else if (set.getTargetType() instanceof DocType)
					repositoryType = TemplateRepositoryType.Documentation;
			}
			if (repositoryType == null) {
				Enumeration<CGTemplate> en1 = getAllTemplateFiles().elements();
				while(en1.hasMoreElements() && repositoryType==null) {
					CGTemplate file = en1.nextElement();
					CGTemplate codeTemplate = getProject().getGeneratedCode().getTemplates().getApplicationRepository().getTemplateWithRelativePath(file.getRelativePath());
					if (codeTemplate==null)
						codeTemplate = getProject().getGeneratedCode().getTemplates().getApplicationRepository().getTemplateWithRelativePath(file.getTemplateName());
					CGTemplate docTemplate = getProject().getGeneratedDoc().getTemplates().getApplicationRepository().getTemplateWithRelativePath(file.getRelativePath());
					if (docTemplate==null)
						docTemplate = getProject().getGeneratedDoc().getTemplates().getApplicationRepository().getTemplateWithRelativePath(file.getTemplateName());
					if (codeTemplate==null && docTemplate!=null)
						repositoryType = TemplateRepositoryType.Documentation;
					else if (codeTemplate!=null && docTemplate==null)
						repositoryType = TemplateRepositoryType.Code;
				}
			}
		}
		return repositoryType;
	}
	
	public void setRepositoryType(TemplateRepositoryType repositoryType) {
		this.repositoryType = repositoryType;
		getTemplates().refresh();
	}
}
