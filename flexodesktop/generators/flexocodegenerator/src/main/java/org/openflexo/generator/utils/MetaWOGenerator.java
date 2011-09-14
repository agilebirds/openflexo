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

import java.util.Vector;

import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.generator.GeneratedComponent;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.APIFileResource;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.foundation.rm.cg.WOFileResource;
import org.openflexo.generator.FlexoComponentResourceGenerator;
import org.openflexo.generator.FlexoResourceGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.rm.UtilComponentAPIFileResource;
import org.openflexo.generator.rm.UtilComponentJavaFileResource;
import org.openflexo.generator.rm.UtilComponentWOFileResource;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
public abstract class MetaWOGenerator extends FlexoResourceGenerator<ComponentDefinition, GeneratedComponent> implements FlexoComponentResourceGenerator
{
    private String _componentPackageName;
    private DMEntity _entity;
    protected String generatedComponentName;
    protected ComponentDefinition component;
    protected JavaFileResource javaResource;
    protected WOFileResource woResource;
    protected APIFileResource apiResource;

    public MetaWOGenerator(ProjectGenerator projectGenerator,ComponentDefinition componentDef, String generatedComponentName, String componentPackageName)
    {
        super(projectGenerator, componentDef);
        this.component = componentDef;
        this.generatedComponentName = generatedComponentName;
        if(componentDef != null)
        	_componentPackageName = componentDef.getComponentDMEntity().getPackage().getJavaStringRepresentation();
        else
        	_componentPackageName = componentPackageName != null ? componentPackageName : "";
        _entity = componentDef!=null?componentDef.getComponentDMEntity():null;
    }
    
    @Override
	public ComponentDefinition getComponentDefinition()
    {
        return component;
    }
    
    @Override
	public DMEntity getEntity() {
    	return _entity;
    }
    
    @Override
	public final String getIdentifier() {
    	return getComponentPackageName()+(getComponentPackageName().length()>0?".":"")+getComponentClassName();
    }
    
    public final String getComponentClassName(){
    	return generatedComponentName;
    }
    
    public final String getComponentPackageName(){
    	return _componentPackageName;
    }
    
    public final String getComponentFolderPath(){
    	if(getEntity()!=null){
    		return getEntity().getPathForPackage();
    	}
    	return getComponentPackageName().replace('.','/');
    }
    
    
    @Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) 
	{
		refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating")+ " "+getIdentifier(),false);

		// Java file 
	 	javaResource = (JavaFileResource)resourceForKeyWithCGFile(ResourceType.JAVA_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (javaResource == null) {
			javaResource = GeneratedFileResourceFactory.createNewUtilComponentJavaFileResource(repository, this);
		}
		else {
			javaResource.setGenerator(this);
		}
		resources.add(javaResource);		
		// WO file
		WOFileResource WOResource = (WOFileResource)resourceForKeyWithCGFile(ResourceType.WO_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (WOResource == null) {
			WOResource = GeneratedFileResourceFactory.createNewUtilComponentWOFileResource(repository, this);
		}
		else {
			WOResource.setGenerator(this);
		}
		resources.add(WOResource);
		woResource = WOResource;

		// API file
		APIFileResource APIResource = (APIFileResource)resourceForKeyWithCGFile(ResourceType.API_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (APIResource == null) {
			APIResource = GeneratedFileResourceFactory.createNewUtilComponentAPIFileResource(repository, this);
		}
		else {
			APIResource.setGenerator(this);
		}
		resources.add(APIResource);	
		apiResource = APIResource;
	}
    
    @Override
    public GeneratedComponent getGeneratedCode() {
    	if (generatedCode==null
    			&& javaResource!=null && javaResource.getJavaFile()!=null && javaResource.getJavaFile().hasLastAcceptedContent()
    			&& apiResource!=null && apiResource.getAPIFile()!=null && apiResource.getAPIFile().hasLastAcceptedContent()
    			&& woResource !=null && woResource.getWOFile()!=null && woResource.getWOFile().hasLastAcceptedContent()) {
    		generatedCode = new GeneratedComponent(generatedComponentName,
    				javaResource.getJavaFile().getLastAcceptedContent(),
    				apiResource.getAPIFile().getLastAcceptedContent(),
    				woResource.getWOFile().getHTMLFile().getLastAcceptedContent(),
    				woResource.getWOFile().getWODFile().getLastAcceptedContent(),
    				woResource.getWOFile().getWOOFile().getLastAcceptedContent());    				
    	}
    	return generatedCode;
    }
    
    public abstract void rebuildDependanciesForResource(UtilComponentJavaFileResource java);
    public abstract void rebuildDependanciesForResource(UtilComponentWOFileResource wo);
    public abstract void rebuildDependanciesForResource(UtilComponentAPIFileResource api);
}
