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
import java.util.Hashtable;
import java.util.Vector;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.cg.utils.TemplateRepositoryType;
import org.openflexo.foundation.rm.CustomTemplatesResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;

public abstract class CGTemplates extends CGTemplateObject {

	protected CGTemplateRepository _applicationRepository;
	private Hashtable<CustomTemplatesResource,CustomCGTemplateRepository> _customRepositories;

	private FlexoProject _project;
    private Vector<TargetType> availableTargets;

    @Override
    public FlexoProject getProject()
    {

       return _project;
    }

	public CGTemplates(FlexoProject project, Vector<TargetType> availableTargets)
	{
		super();
        this.availableTargets = availableTargets;
		_project = project;
		_customRepositories = new Hashtable<CustomTemplatesResource,CustomCGTemplateRepository>();
	}

	public CustomCGTemplateRepository createNewCustomTemplatesRepository(CustomTemplatesResource resource)
	{
		return new CustomCGTemplateRepository(this,resource,availableTargets);
	}
	
	@Override
    public void update()
	{
		boolean hasChanged = false;
		Vector<CustomTemplatesResource> previousKnownResources = new Vector<CustomTemplatesResource>(_customRepositories.keySet());
		for (CustomTemplatesResource resource : getProject().getCustomTemplatesResources()) {
			if (_customRepositories.get(resource) == null) {
				if (resource.getFile()!=null && resource.getFile().exists() && resource.getFile().isDirectory()) { 
					_customRepositories.put(resource,createNewCustomTemplatesRepository(resource));
					hasChanged = true;
				}
				else {
					resource.delete();
					continue;
				}
			}
			if (previousKnownResources.contains(resource)) {
				previousKnownResources.remove(resource);
			}
		}
		for (CustomTemplatesResource resource : previousKnownResources) {
			_customRepositories.remove(resource);
			hasChanged = true;
		}
		_applicationRepository.update();
		for (CustomCGTemplateRepository repository : _customRepositories.values()) {
			repository.update();
		}
		
		if(hasChanged)
		{
			setChanged();
			notifyObservers(new TemplatesChanged());
		}
	}


	@Override
    public String getFullyQualifiedName()
	{
		return "TEMPLATES";
	}

	@Override
    public String getClassNameKey()
	{
		return "templates";
	}

	public CGTemplateRepository getApplicationRepository()
	{
		return _applicationRepository;
	}

	public Enumeration<CustomCGTemplateRepository> getCustomRepositories()
	{
		return _customRepositories.elements();
	}

	public Vector<CustomCGTemplateRepository> getCustomCodeRepositoriesVector()
	{
		Vector<CustomCGTemplateRepository> v = new Vector<CustomCGTemplateRepository>();
		Enumeration<CustomCGTemplateRepository> en = getCustomRepositories();
		while (en.hasMoreElements()) {
			CustomCGTemplateRepository rep = en.nextElement();
			if (rep.getRepositoryType()==TemplateRepositoryType.Code)
				v.add(rep);
			
		}
		return v;
	}

	public Vector<CustomCGTemplateRepository> getCustomDocRepositoriesVector()
	{
		Vector<CustomCGTemplateRepository> v = new Vector<CustomCGTemplateRepository>();
		Enumeration<CustomCGTemplateRepository> en = getCustomRepositories();
		while (en.hasMoreElements()) {
			CustomCGTemplateRepository rep = en.nextElement();
			if (rep.getRepositoryType()==TemplateRepositoryType.Documentation)
				v.add(rep);
			
		}
		return v;
	}
	
	public CustomCGTemplateRepository getCustomCGTemplateRepositoryForName (String aName)
	{
		if (aName == null) return null;
		for (CustomCGTemplateRepository repository : _customRepositories.values()) {
			if (repository.getName().equals(aName)) return repository;
		}
		return null;
	}

	public CustomCGTemplateRepository getCustomCGTemplateRepository (CustomTemplatesResource resource)
	{
		return _customRepositories.get(resource);
	}

	@Override
    public CGTemplates getTemplates()
	{
		return this;
	}

	   /**
     * @param selectedDMPackage
     * @return
     */
    public String getNextGeneratedCodeRepositoryName()
    {
        String baseName = FlexoLocalization.localizedForKey("default_custom_template_repository_name");
        String testMe = baseName;
        int test = 0;
        while (getCustomCGTemplateRepositoryForName(testMe) != null) {
            test++;
            testMe = baseName + test;
        }
        return testMe;
    }

	@Override
	public String getInspectorName()
	{
		return Inspectors.GENERATORS.CG_TEMPLATES;
	}

	@Override
    public String getHelpText()
	{
		return FlexoLocalization.localizedForKey("contains_templates_used_for_code_generation");
	}

}
