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
package org.openflexo.dg.pptx;
/**
 * MOS
 * @author MOSTAFA
 * TODO_MOS
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.dg.ProjectDocGenerator;
import org.openflexo.dg.rm.GeneratedFileResourceFactory;
import org.openflexo.dg.rm.ProjectDocxXmlFileResource;
import org.openflexo.dg.rm.ProjectPptxXmlFileResource;
import org.openflexo.docxparser.flexotag.FlexoContentTag;
import org.openflexo.docxparser.flexotag.FlexoDescriptionTag;
import org.openflexo.docxparser.flexotag.FlexoNameTag;
import org.openflexo.docxparser.flexotag.FlexoTitleTag;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.PresentationRepository;
import org.openflexo.foundation.cg.dm.CustomTemplateRepositoryChanged;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.cg.templates.TemplateFileNotification;
import org.openflexo.foundation.ptoc.PSlide;
import org.openflexo.foundation.ptoc.PTOCRepository;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.generator.PackagedResourceToCopyGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.TemplateNotFoundException;
import org.openflexo.generator.rm.FlexoCopyOfFileResource;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;

public class ProjectDocPptxGenerator extends ProjectDocGenerator {
	private static final String PPTX_MACRO_LIBRARY_NAME = "pptx_macro_library.vm";
	public static final String PPTXEXTRA_DIRECTORY_NAME = "DocxExtras";

	protected static final Logger logger = FlexoLogger.getLogger(ProjectDocPptxGenerator.class.getPackage().getName());

	protected boolean hasBeenInitialized = false;

	private final Hashtable<String, DGPptxXMLGenerator<FlexoProject>> generators = new Hashtable<String, DGPptxXMLGenerator<FlexoProject>>();

	protected static final Vector<FileResource> fileResourceToCopy = new Vector<FileResource>();

	static {
		FileResource directory = new FileResource(PPTXEXTRA_DIRECTORY_NAME);
		if (directory != null) {
			for (String fileName : directory.list(FileUtils.CVSFileNameFilter)) {
				fileResourceToCopy.add(new FileResource(PPTXEXTRA_DIRECTORY_NAME + "/" + fileName));
			}
		}
	}

	public ProjectDocPptxGenerator(FlexoProject project, PresentationRepository repository) throws GenerationException {
		super(project, repository);
	}

	@Override
	public CGTemplates getDefaultTemplates() {
		
		return getProject().getGeneratedDoc().getTemplates();
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public String getFileExtension() {
		return "";
	}

	/**
	 * Overrides buildResourcesAndSetGenerators
	 * 
	 * @see org.openflexo.dg.DGGenerator#buildResourcesAndSetGenerators(DGRepository, java.util.Vector)
	 */
//	@Override
//	public void buildResourcesAndSetGenerators(DGRepository repository, Vector<CGRepositoryFileResource> resources) {
//		try{
//			PresentationRepository prep = (PresentationRepository) repository;
//			hasBeenInitialized = true;
//			// MARKER 1.2 getOrderedTemplateListGroupedPerGenerator
//			for (String nameGenerator : PptxTemplatesEnum
//					.getOrderedTemplateListGroupedPerGenerator().keySet()) {
//				/**
//				 * getGenerator method returns the corresponding
//				 * DGPptxXMLGenerator instance from the generators list
//				 * attribute, (it creates it if not created.)
//				 */
//				DGPptxXMLGenerator<FlexoProject> generator = getGenerator(nameGenerator);
//				for (PptxTemplatesEnum pptxTemplate : PptxTemplatesEnum.getOrderedTemplateListGroupedPerGenerator().get(nameGenerator)) {
//					refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating")+ " " + pptxTemplate.getFilePath(), false);
//					/**
//					 * GeneratedFileResourceFactory.
//					 * createNewProjectPptXmlFileResource method returns the
//					 * ProjectPptxXmlFileResource instance corresponding to
//					 * generator, repository and pptxTemplate. if not yet
//					 * created it creates it.
//					 * 
//					 */
//					// MARKER 1.3
//					// GeneratedFileResourceFactory.createNewProjectPptXmlFileResource
//					ProjectPptxXmlFileResource res = GeneratedFileResourceFactory.createNewProjectPptXmlFileResource(prep,generator, pptxTemplate);
//					resources.add(res);
//				}
//			}
//
//			buildResourcesAndSetGeneratorsForCopyOfPackagedResources(resources);
//			// Useless as they are copied in copyAdditionalFiles, should be used
//			// instead but the images imported in the wysiwyg are not in the
//			// resources currently (and thus not copied by
//			// buildResourcesAndSetGeneratorsForCopiedResources)
//			// buildResourcesAndSetGeneratorsForCopiedResources(resources);
//			// MARKER 1.4 screenshotsGenerator.buildResourcesAndSetGenerators
//			screenshotsGenerator.buildResourcesAndSetGenerators(repository,
//					resources);
//		}catch(Exception e ){
//			//TODO_MOS Inform about exception 
//		}
//	}
	
	
	public void buildResourcesAndSetGenerators(DGRepository repository, Vector<CGRepositoryFileResource> resources) {
		try{
			PresentationRepository prep = (PresentationRepository) repository;
			hasBeenInitialized = true;
			// MARKER 1.2 getOrderedTemplateListGroupedPerGenerator
			for (String nameGenerator : PptxTemplatesEnum
					.getOrderedTemplateListGroupedPerGenerator().keySet()) {
				if(nameGenerator.equals(PptxTemplatesEnum.SLIDE_XML.toString()))
				{
					/**
					 * if we use the slide template we must create a generator for every slide and slide_rels
					 * Because generators have a hashTable called generatedCodeReuslt with templates names as keys 
					 * and resulted code as values.
					 * If we use only one generator to generate all slides, the resulted generatedCodeReuslt hashTable
					 * will contain only the code of the last slide.
					 */
					buildSlideResourcesAndSetGenerators(nameGenerator,prep,resources);
				}else
					buildResourcesAndSetGenerators(nameGenerator,prep, resources);
				
			}

			buildResourcesAndSetGeneratorsForCopyOfPackagedResources(resources);
			// Useless as they are copied in copyAdditionalFiles, should be used
			// instead but the images imported in the wysiwyg are not in the
			// resources currently (and thus not copied by
			// buildResourcesAndSetGeneratorsForCopiedResources)
			// buildResourcesAndSetGeneratorsForCopiedResources(resources);
			screenshotsGenerator.buildResourcesAndSetGenerators(repository,
					resources);
		}catch(Exception e ){
			//TODO_MOS Inform about exception 
		}
	}
	

	
	private void buildResourcesAndSetGenerators(String nameGenerator , PresentationRepository prep , Vector<CGRepositoryFileResource> resources)
	{
		/**
		 * getGenerator method returns the corresponding
		 * DGPptxXMLGenerator instance from the generators list
		 * attribute, (it creates it if not created.)
		 */
		DGPptxXMLGenerator<FlexoProject> generator = getGenerator(nameGenerator);
		for (PptxTemplatesEnum pptxTemplate : PptxTemplatesEnum.getOrderedTemplateListGroupedPerGenerator().get(nameGenerator)) {
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating")+ " " + pptxTemplate.getFilePath(), false);
			/**
			 * GeneratedFileResourceFactory.
			 * createNewProjectPptXmlFileResource method returns the
			 * ProjectPptxXmlFileResource instance corresponding to
			 * generator, repository and pptxTemplate. if not yet
			 * created it creates it.
			 * 
			 */
				ProjectPptxXmlFileResource res = GeneratedFileResourceFactory.createNewProjectPptXmlFileResource(prep,generator, pptxTemplate);
				resources.add(res);
			
			
		}
	}

	private void buildSlideResourcesAndSetGenerators(String nameGenerator , PresentationRepository prep , Vector<CGRepositoryFileResource> resources){
		PTOCRepository ptocRepository = prep.getPTocRepository();
		int i = 1;
		/**
		 * We don't use the same generator to generate a slide and its rels file.
		 * we must create a generator for every slide. Because the slides are built using the same template
		 * If we use only one generator to generate slides, GeneratedCodeResult<templateName, generatedCode> will only
		 * contain one key (SLIDE_XML which is the only slide template's name) and only one value (the generated code of the last slide).
		 * So all the slides will contain the same generated code.
		 * To know more about this, please analyze the generate method of the DGPptxXMLGenerator class. 
		 */
		for(PSlide slide : ptocRepository.getOrderedSlides()){
			DGPptxXMLGenerator<FlexoProject> generator = getGenerator(nameGenerator+i);
			for (PptxTemplatesEnum pptxTemplate : PptxTemplatesEnum.getOrderedTemplateListGroupedPerGenerator().get(nameGenerator)) {
				refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating")+ " " + pptxTemplate.getFilePath(), false);
									
				ProjectPptxXmlFileResource res= GeneratedFileResourceFactory.createNewProjectSlidePptXmlFileResource(prep,generator, pptxTemplate, slide , i);
				resources.add(res);
			}
			i++;
		}
	}
	

	private DGPptxXMLGenerator<FlexoProject> getGenerator(String nameGenerator) {
		DGPptxXMLGenerator<FlexoProject> returned = generators.get(nameGenerator);
		if (returned == null) {
			generators.put(nameGenerator, returned = new DGPptxXMLGenerator<FlexoProject>(this, getProject(), nameGenerator));
		}
		return returned;
	}

	@Override
	protected String getCopiedResourcesRelativePath(FlexoProjectFile flexoFile) {
		int lastIndexOfSlash = flexoFile.getRelativePath().lastIndexOf('/');
		String relativeDirectoryPath = "";
		if (lastIndexOfSlash != -1) {
			relativeDirectoryPath = flexoFile.getRelativePath().substring(0, lastIndexOfSlash);
		}

		return relativeDirectoryPath;
	}

	private void buildResourcesAndSetGeneratorsForCopyOfPackagedResources(Vector<CGRepositoryFileResource> resources) {
		for (FileResource fileResource : fileResourceToCopy) {
			PackagedResourceToCopyGenerator<DGRepository> generator = getFileResourceGenerator(fileResource);
			generator.buildResourcesAndSetGenerators(getRepository(), resources);
		}
	}

	@Override
	public PackagedResourceToCopyGenerator<DGRepository> getFileResourceGenerator(FileResource r) {
		PackagedResourceToCopyGenerator<DGRepository> returned = packagedResourceToCopyGenerator.get(r);
		if (returned == null) {
	   		//String extension = r.getName().substring(r.getName().lastIndexOf(".")+1);
    		//FileFormat format = FileFormat.getFileFormatByExtension(extension);
        	FileFormat format;
        	if (r.getName().endsWith(".png")) {
				format = FileFormat.PNG;
			} else if (r.getName().endsWith(".jpg")) {
				format = FileFormat.JPG;
			} else if (r.getName().endsWith(".sty")) {
				format = FileFormat.LATEX;
			} else if (r.getName().endsWith(".def")) {
				format = FileFormat.LATEX;
			} else if (r.isDirectory()) {
				format = FileFormat.UNKNOWN_DIRECTORY;
			} else {
				format = FileFormat.UNKNOWN_BINARY_FILE;
			}

			int lastIndexOfSlash = r.getInternalPath().lastIndexOf('/');
			String relativeDirectoryPath = "";
			if (lastIndexOfSlash != -1) {
				relativeDirectoryPath = r.getInternalPath().substring(0, lastIndexOfSlash);
			}
			packagedResourceToCopyGenerator.put(r, returned = new PackagedResourceToCopyGenerator<DGRepository>(this, format,
					ResourceType.COPIED_FILE, r, getRepository().getResourcesSymbolicDirectory(), relativeDirectoryPath));
		}
		return returned;
	}

	@Override
	public void copyAdditionalFiles() throws IOException {
		super.copyAdditionalFiles();
		FileUtils.copyContentDirToDir(getProject().getImportedImagesDir(), new File(getRootOutputDirectory(), "ppt/media/" + getProject()
				.getImportedImagesDir().getName()), CopyStrategy.REPLACE_OLD_ONLY);
		FileUtils.copyContentDirToDir(getProject().getDocxToEmbedDirectory(), getRootOutputDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
	}

	@Override
	public boolean hasBeenInitialized() {
		return hasBeenInitialized;
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.dg.DGGenerator#update(org.openflexo.foundation.FlexoObservable, org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if ((dataModification.propertyName() != null) && dataModification.propertyName().equals("docType")) {
			getTemplateLocator().notifyTemplateModified();
		}
		if (dataModification instanceof TemplateFileNotification) {
			getTemplateLocator().notifyTemplateModified();
		} else if (dataModification instanceof CustomTemplateRepositoryChanged) {
			getTemplateLocator().notifyTemplateModified();
		}

		super.update(observable, dataModification);
	}

	public List<CGRepositoryFileResource> getAllMediaResources() {
		List<CGRepositoryFileResource> mediaResources = new ArrayList<CGRepositoryFileResource>();
		for (CGFile file : getRepository().getFiles()) {
			if ((file.getResource() instanceof FlexoCopiedResource) || ((CGRepositoryFileResource) file.getResource() instanceof FlexoCopyOfFileResource)) {
				mediaResources.add(file.getResource());
			}
		}

		return mediaResources;
	}

	public String getRIdForResource(CGRepositoryFileResource resource) {
		return getRIdForString(getMediaResourceRelativePath(resource));
	}

	public String getRIdForString(String value) {
		return "rId" + value.replaceAll("\\W", "_");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CGTemplate> getVelocityMacroTemplates() {
		List<CGTemplate> result = new ArrayList<CGTemplate>();
		try {
			result.add(templateWithName(PPTX_MACRO_LIBRARY_NAME));
		} catch (TemplateNotFoundException e) {
			logger.warning("Should include velocity macro template for project generator but template is not found '" + PPTX_MACRO_LIBRARY_NAME + "'");
			e.printStackTrace();
		}
		return result;
	}

	public ImageIcon getImageIconForImportedImageNamed(String imagePath) {
		File importedImageDir = getProject().getImportedImagesDir();

		if (!imagePath.startsWith("/") && !imagePath.startsWith("\\")) {
			imagePath = "/" + imagePath;
		}

		File imageFile = new File(importedImageDir.getAbsolutePath() + imagePath);

		if (!imageFile.exists()) {
			// Try to make this image path relative to the imported image directory
			imagePath = imagePath.replace('\\', '/');
			String importedImageDirName = '/' + importedImageDir.getName() + '/';
			int indexOfImportedImageDir = imagePath.indexOf(importedImageDirName);
			if ((indexOfImportedImageDir != -1) && (imagePath.length() > indexOfImportedImageDir + importedImageDirName.length())) {
				imageFile = new File(
						importedImageDir.getAbsolutePath() + '/' + imagePath.substring(indexOfImportedImageDir + importedImageDirName
								.length()));
			}
		}

		if (imageFile.exists()) {
			return new ImageIcon(imageFile.getAbsolutePath());
		}

		return null;
	}

	public PptxVelocityXmlFromHtml convertHTML2DocxVelocityXml(String htmlString, Integer currentUniqueID) {
		return new PptxVelocityXmlFromHtml(htmlString, currentUniqueID, false, this);
	}

	public PptxVelocityXmlFromHtml convertHTML2DocxVelocityXmlWithinP(String htmlString, Integer currentUniqueID) {
		return new PptxVelocityXmlFromHtml(htmlString, currentUniqueID, true, this);
	}

	public String getFlexoDescriptionTag(FlexoModelObject object, String target) {
		return FlexoDescriptionTag.buildFlexoDescriptionTag(String.valueOf(object.getFlexoID()), object.getUserIdentifier(), target);
	}

	public String getFlexoNameTag(FlexoModelObject object) {
		if (object == null) {
			return null;
		}
		return FlexoNameTag.buildFlexoNameTag(String.valueOf(object.getFlexoID()), object.getUserIdentifier());
	}

	public String getTocEntryContentTag(TOCEntry tocEntry) {
		return FlexoContentTag.buildFlexoContentTag(String.valueOf(tocEntry.getFlexoID()), tocEntry.getUserIdentifier());
	}

	public String getTocEntryTitleTag(TOCEntry tocEntry) {
		if (tocEntry == null) {
			return null;
		}
		return FlexoTitleTag.buildFlexoTitleTag(String.valueOf(tocEntry.getFlexoID()), tocEntry.getUserIdentifier());
	}

	public String getMediaResourceRelativePath(CGRepositoryFileResource mediaResource) {
		return getMediaRelativePath(mediaResource.getFile().getPath());
	}

	public String getMediaRelativePath(String filePath) {
		filePath = filePath.replace('\\', '/');
		int index = filePath.lastIndexOf("ppt/");
		if ((index != -1) && (filePath.length() > index + "ppt/".length())) {
			return filePath.substring(index + "ppt/".length());
		}

		return "media/" + filePath;
	}

	public Icon getScreenshotImageIcon(FlexoModelObject o) {
		FlexoCopiedResource r = getScreenshot(o);
		if (r != null) {
			if (r.getFile().exists()) {
				return new ImageIcon(r.getFile().getAbsolutePath());
			}

			return new ImageIcon(r.getResourceToCopy().getFile().getAbsolutePath());
		}

		return new ImageIcon(o.getProject().getScreenshotResource(o, true).getFile().getAbsolutePath());
	}
}
