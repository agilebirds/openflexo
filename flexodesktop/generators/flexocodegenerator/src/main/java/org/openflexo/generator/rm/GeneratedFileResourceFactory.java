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
package org.openflexo.generator.rm;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.CopyOfFileResource;
import org.openflexo.foundation.rm.cg.CopyOfFlexoResource;
import org.openflexo.generator.GeneratedResourceFileFactory;
import org.openflexo.generator.PackagedResourceToCopyGenerator;
import org.openflexo.generator.bpel.BPELFileGenerator;
import org.openflexo.generator.bpel.BPELFileResource;
import org.openflexo.generator.bpel.BPELWSDLFileGenerator;
import org.openflexo.generator.bpel.BPELWSDLFileResource;
import org.openflexo.generator.bpel.BPELXSDFileGenerator;
import org.openflexo.generator.bpel.BPELXSDFileResource;
import org.openflexo.generator.cg.CGAPIFile;
import org.openflexo.generator.cg.CGJavaFile;
import org.openflexo.generator.cg.CGPListFile;
import org.openflexo.generator.cg.CGTextFile;
import org.openflexo.generator.cg.CGWOFile;
import org.openflexo.generator.cg.CGWebServerImageFile;
import org.openflexo.generator.dm.EOEntityPListGenerator;
import org.openflexo.generator.dm.EOModelPListGenerator;
import org.openflexo.generator.dm.GenericRecordGenerator;
import org.openflexo.generator.ie.PageComponentGenerator;
import org.openflexo.generator.ie.PopupComponentGenerator;
import org.openflexo.generator.ie.PopupLinkComponentGenerator;
import org.openflexo.generator.ie.TabComponentGenerator;
import org.openflexo.generator.rm.FlexoCopyOfFileResource;
import org.openflexo.generator.utils.ApplicationConfProdGenerator;
import org.openflexo.generator.utils.BuildPropertiesGenerator;
import org.openflexo.generator.utils.HelpGenerator;
import org.openflexo.generator.utils.JavaClassGenerator;
import org.openflexo.generator.utils.LocalizedFileGenerator;
import org.openflexo.generator.utils.MetaFileGenerator;
import org.openflexo.generator.utils.MetaWOGenerator;
import org.openflexo.generator.utils.PrototypeProcessBusinessDataSamplesGenerator;
import org.openflexo.generator.utils.ResourceToCopyGenerator;
import org.openflexo.generator.wkf.ControlGraphGenerator;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;


public class GeneratedFileResourceFactory {

	
    static final Logger logger = FlexoLogger.getLogger(GeneratedFileResourceFactory.class.getPackage().getName());

    public static CGRepositoryFileResource resourceForKeyWithCGFile(FlexoProject project, ResourceType type, String resourceName)
    {
        CGRepositoryFileResource ret = (CGRepositoryFileResource) project.resourceForKey(type, resourceName);
        if (ret!=null && ret.getCGFile()==null) {
            ret.delete(false);
            ret = null;
        }
        return ret;
    }
    
	/**
	 * @param repository
	 * @param generator
	 * @return
	 */
	public static ApplicationConfProdResource createApplicationConfProdFileResource(CGRepository repository, ApplicationConfProdGenerator generator)
	{
	    ApplicationConfProdResource returned = new ApplicationConfProdResource(generator.getProject());
	    returned.setGenerator(generator);
	    returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
	    CGTextFile cgFile = new CGTextFile(repository, returned);
	    initCGFile(cgFile,repository.getProjectSymbolicDirectory(),returned);
	    return registerResource(returned, ApplicationConfProdResource.getDefaultFileName());
	}

	public static EOEntityJavaFileResource createNewEOEntityJavaFileResource(CGRepository repository, GenericRecordGenerator generator)
	{
	 	 EOEntityJavaFileResource returned = new EOEntityJavaFileResource(generator.getProject());
		 returned.setGenerator(generator);
		 returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		 CGJavaFile cgFile = new CGJavaFile(repository,returned);
		 initCGFile(cgFile,repository.getJavaSymbolicDirectory(),returned);
	     return registerResource(returned, generator.getEntityClassName()+".java", generator.getEntityFolderPath());
	}

	/**
	 * @param repository
	 * @param generator
	 * @return
	 */
	public static EOModelPListFileResource createNewModelPlistFileResource(CGRepository repository, EOModelPListGenerator generator)
	{
        EOModelPListFileResource pListResource = (EOModelPListFileResource) resourceForKeyWithCGFile(generator.getProject(), ResourceType.PLIST_FILE,
        		GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
        if (pListResource == null) {
            pListResource = new EOModelPListFileResource(generator.getProject());
            pListResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
            pListResource.setGenerator(generator);
    	    CGPListFile cgFile = new CGPListFile(repository, pListResource);
    	    initCGFile(cgFile,repository.getResourcesSymbolicDirectory(),pListResource);
    	    DMEOModel model = generator.getModel();
    	    String folderPath = model.getDMEOModel().getName();
    	    registerResource(pListResource, EOModelPListFileResource.getDefaultFileName(), folderPath);
            logger.info("Created DMEOMODEL PLIST resource " + pListResource.getName());
        } else {
            pListResource.setGenerator(generator);
            logger.info("Successfully retrieved DMEOMODEL PLIST resource " + pListResource.getName());
        }
        pListResource.registerObserverWhenRequired();
        return pListResource;
	}

	/**
	 * @param repository
	 * @param generator
	 * @return
	 */
	public static HelpFileResource createNewHelpFileResource(CGRepository repository, HelpGenerator generator)
	{
	    HelpFileResource returned = new HelpFileResource(generator.getProject());
	    returned.setGenerator(generator);
	    returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
	    CGTextFile cgFile = new CGTextFile(repository, returned);
	    initCGFile(cgFile,repository.getResourcesSymbolicDirectory(),returned);
	    return registerResource(returned, HelpFileResource.getDefaultFileName());
	}

	/**
	 * @param repository
	 * @param generator
	 * @return
	 */
	public static LocalizationFileResource createNewLocalizedFileResource(CGRepository repository, LocalizedFileGenerator generator)
	{
	    LocalizationFileResource returned = new LocalizationFileResource(generator.getProject());
	    returned.setGenerator(generator);
	    returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
	    CGTextFile cgFile = new CGTextFile(repository, returned);
	    initCGFile(cgFile,repository.getResourcesSymbolicDirectory(),returned);
	    return registerResource(returned, LocalizationFileResource.getDefaultFileName(generator.getLanguage()));
	}

	public static OperationComponentWOFileResource createNewOperationComponentWOFileResource(CGRepository repository, PageComponentGenerator generator)
	{
		OperationComponentWOFileResource operationWOResource = (OperationComponentWOFileResource) resourceForKeyWithCGFile(repository
				.getProject(), ResourceType.WO_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (operationWOResource == null) {
			operationWOResource = new OperationComponentWOFileResource(generator.getProject());
			operationWOResource.setGenerator(generator);
			operationWOResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGWOFile cgFile = new CGWOFile(repository, operationWOResource);
			initCGFile(cgFile, repository.getComponentsSymbolicDirectory(), operationWOResource);
			operationWOResource = registerResource(operationWOResource, generator.getComponentClassName() + ".wo");
		} else {
			operationWOResource.setGenerator(generator);
		}
		operationWOResource.registerObserverWhenRequired();
		return operationWOResource;
	}

	public static OperationComponentAPIFileResource createNewOperationComponentAPIFileResource(CGRepository repository, PageComponentGenerator generator)
	{
		OperationComponentAPIFileResource operationAPIResource = (OperationComponentAPIFileResource) resourceForKeyWithCGFile(repository
				.getProject(), ResourceType.API_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (operationAPIResource == null) {
			operationAPIResource = new OperationComponentAPIFileResource(generator.getProject());
			operationAPIResource.setGenerator(generator);
			operationAPIResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGAPIFile cgFile = new CGAPIFile(repository, operationAPIResource);
			initCGFile(cgFile, repository.getComponentsSymbolicDirectory(), operationAPIResource);
			registerResource(operationAPIResource, generator.getComponentClassName() + ".api");
		} else {
			operationAPIResource.setGenerator(generator);
		}
		operationAPIResource.registerObserverWhenRequired();
		return operationAPIResource;
	}

	public static OperationComponentJavaFileResource createNewOperationComponentJavaFileResource(CGRepository repository,
			PageComponentGenerator generator) {
		OperationComponentJavaFileResource javaResource = (OperationComponentJavaFileResource)resourceForKeyWithCGFile(repository.getProject(),ResourceType.JAVA_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (javaResource == null) {
			javaResource = new OperationComponentJavaFileResource(generator.getProject());
			javaResource.setGenerator(generator);
			javaResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGJavaFile cgFile = new CGJavaFile(repository, javaResource);
			initCGFile(cgFile, repository.getJavaSymbolicDirectory(), javaResource);
			javaResource = registerResource(javaResource, generator.getComponentClassName() + ".java", generator.getComponentFolderPath());
		}
		else {
			javaResource.setGenerator(generator);
		}
		javaResource.registerObserverWhenRequired();
		return javaResource;
	}

	public static PopupComponentAPIFileResource createNewPopupComponentAPIFileResource(CGRepository repository,
			PopupComponentGenerator generator) {
		PopupComponentAPIFileResource popupAPIResource = (PopupComponentAPIFileResource) resourceForKeyWithCGFile(repository.getProject(),
				ResourceType.API_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (popupAPIResource == null) {
			popupAPIResource = new PopupComponentAPIFileResource(generator.getProject());
			popupAPIResource.setGenerator(generator);
			popupAPIResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGAPIFile cgFile = new CGAPIFile(repository, popupAPIResource);
			initCGFile(cgFile, repository.getComponentsSymbolicDirectory(), popupAPIResource);
			registerResource(popupAPIResource, generator.getComponentClassName() + ".api");
			logger.info("Created POPUP API resource " + popupAPIResource.getName());
		} else {
			popupAPIResource.setGenerator(generator);
			logger.info("Successfully retrieved POPUP API resource " + popupAPIResource.getName());
		}
		popupAPIResource.registerObserverWhenRequired();
		return popupAPIResource;
	}

	public static PopupComponentJavaFileResource createNewPopupComponentJavaFileResource(CGRepository repository, PopupComponentGenerator generator)
	{
		PopupComponentJavaFileResource javaResource = (PopupComponentJavaFileResource) resourceForKeyWithCGFile(repository.getProject(),
				ResourceType.JAVA_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (javaResource == null) {
			javaResource = new PopupComponentJavaFileResource(generator.getProject());
			javaResource.setGenerator(generator);
			javaResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGJavaFile cgFile = new CGJavaFile(repository, javaResource);
			initCGFile(cgFile, repository.getJavaSymbolicDirectory(), javaResource);
			registerResource(javaResource, generator.getComponentClassName() + ".java", generator.getComponentFolderPath());
		} else {
			javaResource.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved POPUP JAVA resource " + javaResource.getName());
		}
		javaResource.registerObserverWhenRequired();
		return javaResource;
	}

	public static PopupComponentWOFileResource createNewPopupComponentWOFileResource(CGRepository repository,
			PopupComponentGenerator generator) {
		PopupComponentWOFileResource popupWOResource = (PopupComponentWOFileResource) resourceForKeyWithCGFile(repository.getProject(),
				ResourceType.WO_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (popupWOResource == null) {
			popupWOResource = new PopupComponentWOFileResource(generator.getProject());
			popupWOResource.setGenerator(generator);
			popupWOResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGWOFile cgFile = new CGWOFile(repository, popupWOResource);
			initCGFile(cgFile, repository.getComponentsSymbolicDirectory(), popupWOResource);
			registerResource(popupWOResource, generator.getComponentClassName() + ".wo");
			logger.info("Created POPUP WO resource " + popupWOResource.getName());
		} else {
			popupWOResource.setGenerator(generator);
			logger.info("Successfully retrieved POPUP WO resource " + popupWOResource.getName());
		}
		popupWOResource.registerObserverWhenRequired();
		return popupWOResource;
	}

	public static PopupLinkComponentAPIFileResource createNewPopupLinkComponentAPIFileResource(CGRepository repository, PopupLinkComponentGenerator generator)
	{
		PopupLinkComponentAPIFileResource popupLinkAPIResource = (PopupLinkComponentAPIFileResource) resourceForKeyWithCGFile(repository
				.getProject(), ResourceType.API_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (popupLinkAPIResource == null) {
			popupLinkAPIResource = new PopupLinkComponentAPIFileResource(generator.getProject());
			popupLinkAPIResource.setGenerator(generator);
			popupLinkAPIResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGAPIFile cgFile = new CGAPIFile(repository, popupLinkAPIResource);
			initCGFile(cgFile, repository.getComponentsSymbolicDirectory(), popupLinkAPIResource);
			registerResource(popupLinkAPIResource, generator.getComponentClassName() + ".api");
			logger.info("Created PopupLink API resource " + popupLinkAPIResource.getName());
		} else {
			popupLinkAPIResource.setGenerator(generator);
			logger.info("Successfully retrieved PopupLink API resource " + popupLinkAPIResource.getName());
		}
		popupLinkAPIResource.registerObserverWhenRequired();
		return popupLinkAPIResource;
	}

	public static PopupLinkComponentJavaFileResource createNewPopupLinkComponentJavaFileResource(CGRepository repository,
			PopupLinkComponentGenerator generator) {
		PopupLinkComponentJavaFileResource javaResource = (PopupLinkComponentJavaFileResource) resourceForKeyWithCGFile(repository
				.getProject(), ResourceType.JAVA_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (javaResource == null) {
			javaResource = new PopupLinkComponentJavaFileResource(generator.getProject());
			javaResource.setGenerator(generator);
			javaResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGJavaFile cgFile = new CGJavaFile(repository, javaResource);
			initCGFile(cgFile, repository.getJavaSymbolicDirectory(), javaResource);
			registerResource(javaResource, generator.getComponentClassName() + ".java", generator.getComponentFolderPath());
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created PopupLink JAVA resource " + javaResource.getName());
		} else {
			javaResource.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved PopupLink JAVA resource " + javaResource.getName());
		}
		javaResource.registerObserverWhenRequired();
		return javaResource;
	}

	public static PopupLinkComponentWOFileResource createNewPopupLinkComponentWOFileResource(CGRepository repository,
			PopupLinkComponentGenerator generator) {
		PopupLinkComponentWOFileResource popupLinkWOResource = (PopupLinkComponentWOFileResource) resourceForKeyWithCGFile(repository
				.getProject(), ResourceType.WO_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (popupLinkWOResource == null) {
			popupLinkWOResource = new PopupLinkComponentWOFileResource(generator.getProject());
			popupLinkWOResource.setGenerator(generator);
			popupLinkWOResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGWOFile cgFile = new CGWOFile(repository, popupLinkWOResource);
			initCGFile(cgFile, repository.getComponentsSymbolicDirectory(), popupLinkWOResource);
			registerResource(popupLinkWOResource, generator.getComponentClassName() + ".wo");
			logger.info("Created PopupLink WO resource " + popupLinkWOResource.getName());
		} else {
			popupLinkWOResource.setGenerator(generator);
			logger.info("Successfully retrieved PopupLink WO resource " + popupLinkWOResource.getName());
		}
		popupLinkWOResource.registerObserverWhenRequired();
		return popupLinkWOResource;
	}

	public static ProcessorJavaFileResource createNewProcessorJavaFileResourceForProcess(CGRepository repository,
			ControlGraphGenerator generator) {
		ProcessorJavaFileResource returned = new ProcessorJavaFileResource(generator.getProject());
		returned.setGenerator(generator);
		returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		CGJavaFile cgFile = new CGJavaFile(repository, returned);
		initCGFile(cgFile, repository.getJavaSymbolicDirectory(), returned);
		return registerResource(returned, generator.getEntityClassName() + ".java", generator.getEntityFolderPath());
	}

	public static ProjectTextFileResource createNewProjectTextFileResource(CGRepository repository, MetaFileGenerator generator)
	{
		ProjectTextFileResource returned = new ProjectTextFileResource(generator.getProject());
		returned.setGenerator(generator);
		returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		returned.setFileType(generator.getFileType());
		returned.setResourceFormat(generator.getFileFormat());
		CGTextFile cgFile = new CGTextFile(repository,returned);
		initCGFile(cgFile,generator.getSymbolicDirectory(repository),returned);
		String folderPath = generator.getRelativePath();
        return registerResource(returned, returned.getFileName(), folderPath);
	}
	/**
	 * @param repository
	 * @param generator
	 * @return
	 */
	public static BuildPropertiesResource createBuildPropertiesFileResource(CGRepository repository, BuildPropertiesGenerator generator)
	{
	    BuildPropertiesResource returned = new BuildPropertiesResource(generator.getProject());
	    returned.setGenerator(generator);
	    returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
	    CGTextFile cgFile = new CGTextFile(repository, returned);
	    initCGFile(cgFile,repository.getProjectSymbolicDirectory(),returned);
	    return registerResource(returned, BuildPropertiesResource.getDefaultFileName());
	}
	
	public static TabComponentAPIFileResource createNewTabComponentAPIFileResource(CGRepository repository, TabComponentGenerator generator)
	{
		TabComponentAPIFileResource tabAPIResource = (TabComponentAPIFileResource) resourceForKeyWithCGFile(repository.getProject(),
				ResourceType.API_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (tabAPIResource == null) {
			tabAPIResource = new TabComponentAPIFileResource(generator.getProject());
			tabAPIResource.setGenerator(generator);
			tabAPIResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGAPIFile cgFile = new CGAPIFile(repository, tabAPIResource);
			initCGFile(cgFile, repository.getComponentsSymbolicDirectory(), tabAPIResource);
			registerResource(tabAPIResource, generator.getComponentClassName() + ".api");
		} else {
			tabAPIResource.setGenerator(generator);
		}
		tabAPIResource.registerObserverWhenRequired();
		return tabAPIResource;
	}

	public static TabComponentJavaFileResource createNewTabComponentJavaFileResource(CGRepository repository,
			TabComponentGenerator generator) {
		TabComponentJavaFileResource javaResource = (TabComponentJavaFileResource) resourceForKeyWithCGFile(repository.getProject(),
				ResourceType.JAVA_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (javaResource == null) {
			javaResource = new TabComponentJavaFileResource(generator.getProject());
			javaResource.setGenerator(generator);
			javaResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGJavaFile cgFile = new CGJavaFile(repository, javaResource);
			initCGFile(cgFile, repository.getJavaSymbolicDirectory(), javaResource);
			registerResource(javaResource, generator.getComponentClassName() + ".java", generator.getComponentFolderPath());
		} else {
			javaResource.setGenerator(generator);
		}
		javaResource.registerObserverWhenRequired();
		return javaResource;
	}

	public static TabComponentWOFileResource createNewTabComponentWOFileResource(CGRepository repository, TabComponentGenerator generator) {
		TabComponentWOFileResource tabWOResource = (TabComponentWOFileResource) resourceForKeyWithCGFile(repository.getProject(),
				ResourceType.WO_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (tabWOResource == null) {
			tabWOResource = new TabComponentWOFileResource(generator.getProject());
			tabWOResource.setGenerator(generator);
			tabWOResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
			CGWOFile cgFile = new CGWOFile(repository, tabWOResource);
			initCGFile(cgFile, repository.getComponentsSymbolicDirectory(), tabWOResource);
			registerResource(tabWOResource, generator.getComponentClassName() + ".wo");
		} else {
			tabWOResource.setGenerator(generator);
		}
		tabWOResource.registerObserverWhenRequired();
		return tabWOResource;
	}

	public static UtilComponentAPIFileResource createNewUtilComponentAPIFileResource(CGRepository repository, MetaWOGenerator generator)
	{
		 UtilComponentAPIFileResource returned = new UtilComponentAPIFileResource(generator.getProject());
		 returned.setGenerator(generator);
		 returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		 CGAPIFile cgFile = new CGAPIFile(repository,returned);
		 initCGFile(cgFile,repository.getComponentsSymbolicDirectory(),returned);
	     return registerResource(returned, generator.getComponentClassName()+".api");
	}

	public static UtilComponentJavaFileResource createNewUtilComponentJavaFileResource(CGRepository repository, MetaWOGenerator generator)
	{
		 UtilComponentJavaFileResource returned = new UtilComponentJavaFileResource(generator.getProject());
		 returned.setGenerator(generator);
		 returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		 CGJavaFile cgFile = new CGJavaFile(repository,returned);
		 initCGFile(cgFile,repository.getJavaSymbolicDirectory(),returned);
	     return registerResource(returned, generator.getComponentClassName()+".java", generator.getComponentFolderPath());
	}

	public static UtilComponentWOFileResource createNewUtilComponentWOFileResource(CGRepository repository, MetaWOGenerator generator)
	{
		 UtilComponentWOFileResource returned = new UtilComponentWOFileResource(generator.getProject());
		 returned.setGenerator(generator);
		 returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		 CGWOFile cgFile = new CGWOFile(repository,returned);
		 initCGFile(cgFile,repository.getComponentsSymbolicDirectory(),returned);
	     return registerResource(returned, generator.getComponentClassName()+".wo");
	}

	public static UtilJavaFileResource createNewUtilJavaFileResource(CGRepository repository, JavaClassGenerator generator)
	{
	 	 UtilJavaFileResource returned = new UtilJavaFileResource(generator.getProject());
		 returned.setGenerator(generator);
		 returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		 CGJavaFile cgFile = new CGJavaFile(repository,returned);
		 initCGFile(cgFile, repository.getJavaSymbolicDirectory(), returned);
	     return registerResource(returned, generator.getEntityClassName()+".java", generator.getEntityFolderPath());
	}

	private static void initCGFile(CGFile cgFile, CGSymbolicDirectory symbDir, CGRepositoryFileResource returned){
		GeneratedResourceFileFactory.initCGFile(cgFile, symbDir, returned);
	}
	
	private static <FR extends CGRepositoryFileResource>  FR registerResource(FR returned, String fileName) {
		return GeneratedResourceFileFactory.registerResource(returned, fileName);
	}
	
	private static <FR extends CGRepositoryFileResource>  FR registerResource(FR returned, String fileName, String folderPath) {
		return GeneratedResourceFileFactory.registerResource(returned, fileName, folderPath);
	}

	/**
	 * @param repository
	 * @param generator
	 * @return
	 */
	public static EOEntityPListFileResource createNewEntityPlistFileResource(CGRepository repository, EOEntityPListGenerator generator)
	{
		EOEntityPListFileResource pListResource = (EOEntityPListFileResource) resourceForKeyWithCGFile(generator.getProject(), ResourceType.PLIST_FILE,
                GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
        if (pListResource == null) {
            pListResource = new EOEntityPListFileResource(generator.getProject());
            pListResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
            pListResource.setGenerator(generator);
    	    CGPListFile cgFile = new CGPListFile(repository, pListResource);
    	    initCGFile(cgFile,repository.getResourcesSymbolicDirectory(),pListResource);
    	    DMEOEntity entity = generator.getEntity();
    	    String folderPath = entity.getDMEOModel().getName() + '/';
    	    registerResource(pListResource, EOEntityPListFileResource.getDefaultFileName(entity),folderPath);
            logger.info("Created DMEOENTITY PLIST resource " + pListResource.getName());
        } else {
            pListResource.setGenerator(generator);
            logger.info("Successfully retrieved DMEOENTITY PLIST resource " + pListResource.getName());
        }
        pListResource.registerObserverWhenRequired();
        return pListResource;
	}

	public static BPELFileResource createBPELFileResource(CGRepository repository, BPELFileGenerator generator)
	{
	 	 FlexoProject project = generator.getProject();
	 	 BPELFileResource returned = new BPELFileResource(project);
		 returned.setGenerator(generator);
	     returned.setName(BPELFileResource.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
	     returned.setFileType(ResourceType.BPEL);
	     CGTextFile cgFile = new CGTextFile(repository,returned);
	     initCGFile(cgFile,repository.getResourcesSymbolicDirectory(),returned);
	     return registerResource(returned, generator.getFileName());
	}

	public static BPELWSDLFileResource createWSDLFileResource(CGRepository repository, BPELWSDLFileGenerator generator)
	{
	 	 FlexoProject project = generator.getProject();
	 	 BPELWSDLFileResource returned = new BPELWSDLFileResource(project);
		 returned.setGenerator(generator);
	     returned.setName(BPELWSDLFileResource.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
	     returned.setFileType(ResourceType.WSDL);
	     CGTextFile cgFile = new CGTextFile(repository,returned);
	     initCGFile(cgFile,repository.getResourcesSymbolicDirectory(),returned);
	     return registerResource(returned, generator.getFileName());
	}

	public static BPELXSDFileResource createXSDFileResource(CGRepository repository, BPELXSDFileGenerator generator)
	{
	 	 FlexoProject project = generator.getProject();
	 	 BPELXSDFileResource returned = new BPELXSDFileResource(project);
		 returned.setGenerator(generator);
	     returned.setName(BPELXSDFileResource.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
	     returned.setFileType(ResourceType.XSD);
	     CGTextFile cgFile = new CGTextFile(repository,returned);
	     initCGFile(cgFile,repository.getResourcesSymbolicDirectory(),returned);
	     return registerResource(returned, generator.getFileName());
	}

	public static FlexoCopiedResource createNewCopiedFileResource(CGRepository repository, CGFile cgFile,
	        CGSymbolicDirectory symbolicDirectory, FlexoFileResource resourceToCopy)
	{
	    FlexoProject project = resourceToCopy.getProject();
	    FlexoCopiedResource returned = new FlexoCopiedResource(project, resourceToCopy);
	    if (repository.getSymbolicDirectories().get(symbolicDirectory.getName()) != symbolicDirectory) {
	        if (logger.isLoggable(Level.SEVERE))
	            logger.severe("Hu oh!!! you added a file to a repository but you passed a symbolic directory that is not in it? I will continue, but I would expect major failures later");
	    }
	    cgFile.setResource(returned);
	    cgFile.setSymbolicDirectory(symbolicDirectory);
	    repository.addToFiles(cgFile);
	    returned.setCGFile(cgFile);
	    return registerResource(returned, resourceToCopy.getFileName());
	}

	public static FlexoCopyOfFlexoResource createNewFlexoCopyOfFlexoResource(CGRepository repository, ResourceToCopyGenerator  generator, CGSymbolicDirectory symbolicDirectory, FlexoFileResource _source) {
		String name = CopyOfFlexoResource.nameForRepositoryAndResource(repository, _source);
		FlexoCopyOfFlexoResource copiedFile = (FlexoCopyOfFlexoResource) resourceForKeyWithCGFile(repository.getProject(), ResourceType.COPIED_FILE ,name); 
		if (copiedFile==null) {
			copiedFile = new FlexoCopyOfFlexoResource(_source.getProject(), _source);
			copiedFile.setName(name);
	    	CGWebServerImageFile cgFile = new CGWebServerImageFile(repository,copiedFile); 
		    initCGFile(cgFile,symbolicDirectory,copiedFile);
		    registerResource(copiedFile, _source.getFileName(),generator.getRelativePath());
		}
	    copiedFile.setGenerator(generator);
	    return copiedFile;
	}
	
	public static FlexoCopyOfFileResource createNewFlexoCopyOfFileResource(CGRepository repository, PackagedResourceToCopyGenerator  generator, CGSymbolicDirectory symbolicDirectory, FileResource _source, String folderPath) {
		FlexoCopyOfFileResource returned = new FlexoCopyOfFileResource(repository.getProject(), _source);
	    returned.setGenerator(generator);
		returned.setName(CopyOfFileResource.nameForRepositoryAndFileToCopy(repository, _source));
    	CGWebServerImageFile cgFile = new CGWebServerImageFile(repository,returned); 
	    initCGFile(cgFile,symbolicDirectory,returned);
	    return registerResource(returned, _source.getName(), folderPath);
	}
	
	public static PrototypeProcessBusinessDataSamplesFileResource createNewPrototypeProcessInstanceSamplesResource(CGRepository repository, PrototypeProcessBusinessDataSamplesGenerator generator)
	{
		PrototypeProcessBusinessDataSamplesFileResource returned = new PrototypeProcessBusinessDataSamplesFileResource(generator.getProject());
	    returned.setGenerator(generator);
	    returned.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
	    CGTextFile cgFile = new CGTextFile(repository, returned);
	    initCGFile(cgFile,repository.getResourcesSymbolicDirectory(),returned);
	    return registerResource(returned, generator.getFileName(), generator.getDirectoryPath());
	}

}
