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
package org.openflexo.dg;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.file.DGImageFile;
import org.openflexo.dg.latex.ScreenshotsGenerator;
import org.openflexo.dg.rm.GeneratedFileResourceFactory;
import org.openflexo.dg.rm.LatexFileResource;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoWebServerFileResource;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.CopiedResourceGenerator;
import org.openflexo.generator.Generator;
import org.openflexo.generator.PackagedResourceToCopyGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FileResource;

public abstract class ProjectDocGenerator extends AbstractProjectGenerator<DGRepository> {

	private static final Logger logger = FlexoLogger.getLogger(ProjectDocGenerator.class.getPackage().getName());

    protected ScreenshotsGenerator screenshotsGenerator;
    protected Hashtable<FileResource, PackagedResourceToCopyGenerator<DGRepository>> packagedResourceToCopyGenerator;
    private Hashtable<FlexoCopiedResource, CopiedResourceGenerator> copiedResourceGenerators = new Hashtable<FlexoCopiedResource, CopiedResourceGenerator>();
    private final Hashtable<TOCEntry, TextFileResource> entryResAssociation;

	public ProjectDocGenerator(FlexoProject project, DGRepository repository) throws GenerationException {
		super(project, repository);
		entryResAssociation = new Hashtable<TOCEntry, TextFileResource>();
        screenshotsGenerator = new ScreenshotsGenerator(this, project);
        copiedResourceGenerators = new Hashtable<FlexoCopiedResource, CopiedResourceGenerator>();
        packagedResourceToCopyGenerator = new Hashtable<FileResource, PackagedResourceToCopyGenerator<DGRepository>>();
	}

	/**
	 * @param resources
	 */
	protected void buildResourcesAndSetGeneratorsForCopiedResources(Vector<CGRepositoryFileResource> resources) {
		Vector<FlexoWebServerFileResource> webResources = getProject().getResourcesOfClass(FlexoWebServerFileResource.class);
        for (FlexoWebServerFileResource res : webResources) {
			if ((res.getResourceFormat() == FileFormat.PNG) || (res.getResourceFormat() == FileFormat.JPG)|| (res.getResourceFormat() == FileFormat.GIF)) {
				FlexoCopiedResource copy = (FlexoCopiedResource) getProject().resourceForKey(ResourceType.COPIED_FILE,FlexoCopiedResource.nameForCopiedResource(projectGenerator.getRepository(), res));
				if (copy==null) {
					DGImageFile file = new DGImageFile(projectGenerator.getRepository().getGeneratedDoc());
					copy = GeneratedFileResourceFactory.createNewCopiedFileResource(projectGenerator.getRepository(), file, projectGenerator
                        .getRepository().getResourcesSymbolicDirectory(), res, getCopiedResourcesRelativePath(res.getResourceFile()));
				}
				copy.setGenerator(new CopiedResourceGenerator<DGRepository>(copy, this, this));
				copiedResourceGenerators.put(copy, (CopiedResourceGenerator) copy.getGenerator());
				resources.add(copy);
			}
		}
	}

    /**
     * @param o
     */
    public FlexoCopiedResource getScreenshot(FlexoModelObject o)
    {
        return screenshotsGenerator.getScreenshot(o);
    }

    public PackagedResourceToCopyGenerator<DGRepository> getFileResourceGenerator(FileResource r)
    {
    	PackagedResourceToCopyGenerator<DGRepository> returned = packagedResourceToCopyGenerator.get(r);
        if (returned==null) {
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
        	if (format == FileFormat.LATEX) {
				packagedResourceToCopyGenerator.put(r,returned = new PackagedResourceToCopyGenerator<DGRepository>(this,format,ResourceType.COPIED_FILE,r,getRepository().getLatexSymbolicDirectory(),""));
			} else {
				packagedResourceToCopyGenerator.put(r,returned = new PackagedResourceToCopyGenerator<DGRepository>(this,format,ResourceType.COPIED_FILE,r,getRepository().getResourcesSymbolicDirectory(),""));
			}
        }
        return returned;
    }

	public CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> getResourceForObject(FlexoModelObject object) {
		for (CGFile file:getRepository().getFiles()) {
			if ((file.getResource()!=null) && (file.getResource() instanceof LatexFileResource)) {
				if ((file.getResource().getGenerator()!=null) && (((Generator<? extends FlexoModelObject, DGRepository>)file.getResource().getGenerator()).getObject() == object)) {
					return file.getResource();
				}
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Resource not found for object "+object.getFullyQualifiedName());
		}
		return null;
	}

	public void associateEntryWithResource(TOCEntry entry, TextFileResource res) {
		if(res==null) {
			entryResAssociation.remove(entry);
		} else {
			entryResAssociation.put(entry, res);
		}
	}

	public String getLatexFileResourceNameForEntry(TOCEntry entry){
		if (entry == null) {
			return null;
		}
		TextFileResource res = entryResAssociation.get(entry);
		if (res != null) {
			if (res.getFileName() != null) {
				if (res.getFileName().toLowerCase().endsWith(getFileExtension())) {
					return res.getFileName().substring(0, res.getFileName().length() - getFileExtension().length());
				}
			}
		}
		return null;
	}

	/**
	 * Returns the default file extension with the dot included
	 * @return the default file extension with the dot included
	 */
	public abstract String getFileExtension();

    protected String getCopiedResourcesRelativePath(FlexoProjectFile flexoFile)
    {
    	return "";
    }

}
