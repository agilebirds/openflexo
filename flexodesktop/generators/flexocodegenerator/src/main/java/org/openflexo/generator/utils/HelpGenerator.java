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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.help.ApplicationHelpEntryPoint;
import org.openflexo.foundation.help.HelpElementBuilder;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.ScreenshotResource;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.rm.HelpFileResource;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;


public class HelpGenerator extends MetaFileGenerator
{

    private static final Logger logger = FlexoLogger.getLogger(LocalizedFileGenerator.class.getPackage().getName());
    public static final String IDENTIFIER = "HELPPROPERTIES";
    /**
     * @param projectGenerator
     * @param object
     */
    public HelpGenerator(ProjectGenerator projectGenerator)
    {
        super(projectGenerator,FileFormat.XML,ResourceType.TEXT_FILE,"help.properties",IDENTIFIER);
        generators = new Hashtable<ScreenshotResource, ResourceToCopyGenerator>();
    }

    @Override
    public ProjectGenerator getProjectGenerator() {
    	return super.getProjectGenerator();
    }
    
    private Hashtable<ScreenshotResource, ResourceToCopyGenerator> generators;
    
    /**
     * Overrides buildResourcesAndSetGenerators
     * 
     * @see org.openflexo.generator.CGGenerator#buildResourcesAndSetGenerators(org.openflexo.foundation.cg.CGRepository,
     *      Vector)
     */
    @Override
    public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources)
    {
        // PList file
    	textResource = (HelpFileResource) resourceForKeyWithCGFile(ResourceType.TEXT_FILE,
    			GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
        if (textResource == null) {
            textResource = GeneratedFileResourceFactory.createNewHelpFileResource(repository, this);
            textResource.setGenerator(this);
            logger.info("Created HELP resource " + textResource.getName());
        } else {
            textResource.setGenerator(this);
            logger.info("Successfully retrieved HELP FILE resource " + textResource.getName());
        }
        ((HelpFileResource)textResource).registerObserverWhenRequired();
        resources.add(textResource);
        Hashtable<ScreenshotResource, ResourceToCopyGenerator> newGenerators = new Hashtable<ScreenshotResource, ResourceToCopyGenerator>();
        Iterator<ApplicationHelpEntryPoint> it = getProject().getFlexoWorkflow().getAllHelpEntryPoints().iterator();
    	while(it.hasNext()){
    		ApplicationHelpEntryPoint item = it.next();
    		ScreenshotResource screenshotResource = item.getProject().getScreenshotResource(item instanceof OperationNode?((OperationNode)item).getAbstractActivityNode():(FlexoModelObject)item, true);
    		if (screenshotResource==null) {
				continue;
			}
    		ResourceToCopyGenerator generator = getGenerator(screenshotResource);
			generator.buildResourcesAndSetGenerators(repository, resources);
			newGenerators.put(screenshotResource, generator);
    	}
    	generators.clear(); // Frees memory!
    	generators = newGenerators;
    }

    private ResourceToCopyGenerator getGenerator(ScreenshotResource screenshotResource) {
    	ResourceToCopyGenerator gen = generators.get(screenshotResource);
    	if (gen==null) {
			generators.put(screenshotResource,gen = new ResourceToCopyGenerator(getProjectGenerator(),screenshotResource, getRepository().getWebResourcesSymbolicDirectory()));
		}
		return gen;
	}

	/**
     * Overrides generate
     * 
     * @see org.openflexo.generator.CGGenerator#generate(boolean)
     */
    @Override
    public void generate(boolean forceRegenerate)
    {
        startGeneration();
        VelocityContext vc = new VelocityContext();
        vc.put("project",getProject());
        vc.put("generator",this);
        vc.put("helpItems", getProject().getFlexoWorkflow().getAllHelpEntryPoints());
        try {
        	String mergeResult = merge("HelpProperties.vm", vc);
         	generatedCode = new GeneratedTextResource("help.properties", mergeResult);
         	for (ResourceToCopyGenerator g : generators.values()) {
				g.generate(forceRegenerate);
			}
           	stopGeneration();
        } catch (GenerationException e) {
    		setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e,getProjectGenerator()));
		}
    }
   /* private void copyScreen() throws PermissionDeniedException{
    	Iterator<ApplicationHelpEntryPoint> it = getProject().getFlexoWorkflow().getAllHelpEntryPoints().iterator();
    	while(it.hasNext()){
    		ApplicationHelpEntryPoint item = it.next();
    		ScreenshotResource screenshootResource = item.getProject().getScreenshotResource(item instanceof OperationNode?((OperationNode)item).getAbstractActivityNode():(FlexoModelObject)item, true);
    		if(screenshootResource!=null){
    			try {
					screenshootResource.update();
				} catch (LoadResourceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					continue;
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					continue;
				} catch (ProjectLoadingCancelledException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					continue;
				} catch (ResourceDependancyLoopException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					continue;
				} catch (FlexoException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					continue;
				}
    			File f = projectGenerator.getWebResourceOutputDirectory();
    	        if (!f.exists())
    	            f.mkdirs();
    	        if (!f.canWrite()) {
    	           	throw new PermissionDeniedException(f,getProjectGenerator());
    	        }
    	        try {
    	            FileUtils.copyFileToDir(screenshootResource.getFile(), f);
    	        } catch (IOException e) {
    	            e.printStackTrace();
    	        }
    		}
    	}
    }*/
    public long getID(ApplicationHelpEntryPoint helpEntryPoint){
    	if(helpEntryPoint instanceof OperationNode) {
			return ((OperationNode)helpEntryPoint).getComponentInstance().getFlexoID();
		}
    	return helpEntryPoint.getFlexoID();
    }
    public String generateHelpDescriptor(ApplicationHelpEntryPoint helpEntryPoint){
    	Element e = HelpElementBuilder.getHelpElement(helpEntryPoint);
    	e.setAttribute("helpID",String.valueOf(getID(helpEntryPoint)));
    	return new XMLOutputter().outputString(e);
    }
    /**
     * Overrides getGeneratorLogger
     * 
     * @see org.openflexo.generator.CGGenerator#getGeneratorLogger()
     */
    @Override
    public Logger getGeneratorLogger()
    {
        return logger;
    }

	@Override
	public String getRelativePath() {
		return "";
	}

	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository) {
		return repository.getResourcesSymbolicDirectory();
	}


}
