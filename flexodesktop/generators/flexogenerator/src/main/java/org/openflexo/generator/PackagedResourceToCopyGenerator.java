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
package org.openflexo.generator;

import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.generator.GeneratedCopiedFile;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.rm.FlexoCopyOfFileResource;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;


public class PackagedResourceToCopyGenerator<R extends GenerationRepository> extends Generator<FlexoModelObject, R> implements IFlexoResourceGenerator {

    private static final Logger logger = Logger.getLogger(PackagedResourceToCopyGenerator.class.getPackage().getName());

    private final FileResource _source;
    private final String _relativePath;
    private final CGSymbolicDirectory _symbolicDir;
    private final FileFormat _format;
    private final ResourceType _type;
    
    public PackagedResourceToCopyGenerator(AbstractProjectGenerator<R> projectGenerator,FileFormat format, ResourceType type,
    		FileResource source, CGSymbolicDirectory symbolicDir, String relativePath) {
    	super(projectGenerator,null);
    	_relativePath = relativePath;
    	_symbolicDir = symbolicDir;
    	_format = format;
    	_type = type;
    	_source = source;
    	generatedCode = new GeneratedCopiedFile(_source);
    }
    
	@Override
	public void generate(boolean forceRegenerate) {
	}
	
	@Override
	public boolean needsGeneration() {
		return !isCodeAlreadyGenerated();
	}
	
	@Override
	public boolean needsRegenerationBecauseOfTemplateUpdated() {
		return false;
	}
	
	@Override
	public boolean needsRegenerationBecauseOfTemplateUpdated(Date diskLastGenerationDate) {
		return FileUtils.getDiskLastModifiedDate(_source).after(diskLastGenerationDate);
	}
	
	public FileResource getSource(){
		return _source;
	}
	
	public ResourceType getFileType(){
		return _type;
	}
	
	public FileFormat getFileFormat(){
		return _format;
	}

	public String getRelativePath() {
		return _relativePath;
	}

	public CGSymbolicDirectory getSymbolicDirectory(R repository) {
		return _symbolicDir;
	}
	
	@Override
	public void buildResourcesAndSetGenerators(R repository, Vector<CGRepositoryFileResource> resources) {
		FlexoCopyOfFileResource copiedFile = GeneratedResourceFileFactory.createNewFlexoCopyOfFileResource(repository, (PackagedResourceToCopyGenerator<GenerationRepository>) this);
        resources.add(copiedFile);
	}
	
	@Override
	public String getIdentifier() {
		return "COPY_OF_"+_source.getName();
	}
	
	@Override
	public TemplateLocator getTemplateLocator() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasFormattingException() {
		return false;
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}
}
