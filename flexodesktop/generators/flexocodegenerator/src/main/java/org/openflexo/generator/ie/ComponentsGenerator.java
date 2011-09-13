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
package org.openflexo.generator.ie;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.MetaGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.utils.StaticComponentGenerator;
import org.openflexo.localization.FlexoLocalization;


/**
 * @author gpolet
 * 
 */
public class ComponentsGenerator extends MetaGenerator<FlexoModelObject, CGRepository>
{
	private static final Logger logger = Logger.getLogger(ComponentsGenerator.class.getPackage().getName());
	
	private Hashtable<ComponentDefinition,ComponentGenerator> generators;
	private Hashtable<PopupComponentDefinition, PopupLinkComponentGenerator> popupLinkGenerators;
	private Hashtable<String, StaticComponentGenerator> staticComponentsGenerators;
	
    public ComponentsGenerator(ProjectGenerator projectGenerator)
    {
        super(projectGenerator,null);
        generators = new Hashtable<ComponentDefinition,ComponentGenerator>();
        popupLinkGenerators = new Hashtable<PopupComponentDefinition, PopupLinkComponentGenerator>();
        staticComponentsGenerators = new Hashtable<String, StaticComponentGenerator>();
   }
    
    @Override
    public ProjectGenerator getProjectGenerator() {
    	return (ProjectGenerator) super.getProjectGenerator();
    }
    
	@Override
	public Logger getGeneratorLogger()
	{
		return logger;
	}

    @Override
	public void generate(boolean forceRegenerate) throws GenerationException
    {
    	if (logger.isLoggable(Level.FINE))
    		logger.fine("Called ComponentsGenerator.generate(forceRegenerate)");
    	resetSecondaryProgressWindow(generators.values().size()+popupLinkGenerators.size());
    	startGeneration();
    	for (PopupLinkComponentGenerator generator : popupLinkGenerators.values()) {
    		refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating")+ " "+generator.getGeneratedComponentName(),false);
    		generator.generate(forceRegenerate);
    	}
    	for (ComponentGenerator generator : generators.values()) {
    		refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating")+ " "+generator.getGeneratedComponentName(),false);
    		generator.generate(forceRegenerate);
    	}
    	stopGeneration();
    }
    
	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) 
	{
		Hashtable<ComponentDefinition, ComponentGenerator> hash = new Hashtable<ComponentDefinition, ComponentGenerator>();
		Hashtable<PopupComponentDefinition, PopupLinkComponentGenerator> links = new Hashtable<PopupComponentDefinition, PopupLinkComponentGenerator>();
		for(TabComponentDefinition tcd:getProject().getFlexoComponentLibrary().getTabComponentList()) {
			ComponentGenerator generator = getGenerator(tcd);
			if (generator != null) {
				hash.put(tcd,generator);
				generator.buildResourcesAndSetGenerators(repository,resources);
			}
			else {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Could not instanciate ComponentGenerator for "+tcd);
			}
		}
		for(PopupComponentDefinition pcd:getProject().getFlexoComponentLibrary().getPopupsComponentList()) {
			if (pcd.isHelper())
				continue;
			PopupLinkComponentGenerator linkGenerator = getPopupLinkGenerator(pcd);
			if (linkGenerator != null) {
				links.put(pcd,linkGenerator);
				linkGenerator.buildResourcesAndSetGenerators(repository,resources);
			}
			else {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Could not instanciate ComponentGenerator for "+pcd);
			}
			ComponentGenerator generator = getGenerator(pcd);
			if (generator != null) {
				hash.put(pcd,generator);
				generator.buildResourcesAndSetGenerators(repository,resources);
			}
			else {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Could not instanciate ComponentGenerator for "+pcd);
			}
		}
		for(OperationComponentDefinition ocd:getProject().getFlexoComponentLibrary().getOperationsComponentList()) {
			ComponentGenerator generator = getGenerator(ocd);
			if (generator != null) {
				hash.put(ocd,generator);
				generator.buildResourcesAndSetGenerators(repository,resources);
			}
			else {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Could not instanciate ComponentGenerator for "+ocd);
			}
		}
		
		if (getTarget() == CodeType.PROTOTYPE) {
			//Add page to manage samples
			StaticComponentGenerator generator = getStaticComponentGenerator("PrototypeSamplesAdminPage", "PrototypeSamplesAdminPage");
			if (generator != null)
				generator.buildResourcesAndSetGenerators(repository,resources);
			else {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Could not instanciate StaticComponentGenerator for PrototypeSamplesAdminPage");
			}
		}
		
		generators.clear();
		popupLinkGenerators.clear();
		generators = hash;
		popupLinkGenerators = links;
	}

	protected ComponentGenerator getGenerator(ComponentDefinition def)
	{
		ComponentGenerator returned = generators.get(def);
		if (returned == null) {
			if (def instanceof OperationComponentDefinition)
				generators.put(def,returned=new PageComponentGenerator(getProjectGenerator(),(OperationComponentDefinition) def));
			else if (def instanceof PopupComponentDefinition)
				generators.put(def,returned=new PopupComponentGenerator(getProjectGenerator(),(PopupComponentDefinition) def));
			else if (def instanceof TabComponentDefinition)
				generators.put(def,returned=new TabComponentGenerator(getProjectGenerator(),(TabComponentDefinition) def));
		}
		return returned;
	}
	
	protected PopupLinkComponentGenerator getPopupLinkGenerator(PopupComponentDefinition def)
	{
		PopupLinkComponentGenerator returned = popupLinkGenerators.get(def);
		if (returned == null) {
			popupLinkGenerators.put(def,returned=new PopupLinkComponentGenerator(getProjectGenerator(),def));
		}
		return returned;
	}
	
	protected StaticComponentGenerator getStaticComponentGenerator(String templateNamePrefix, String componentName)
	{
		StaticComponentGenerator returned = staticComponentsGenerators.get(componentName);
		if(returned == null)
		{
			returned = new StaticComponentGenerator(getProjectGenerator(), templateNamePrefix, componentName);
			staticComponentsGenerators.put(componentName, returned);
		}
		
		return returned;
	}
}
