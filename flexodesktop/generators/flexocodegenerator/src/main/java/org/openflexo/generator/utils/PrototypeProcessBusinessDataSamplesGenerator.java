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

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;


public class PrototypeProcessBusinessDataSamplesGenerator extends MetaFileGenerator
{

	private static final Logger logger = FlexoLogger.getLogger(PrototypeProcessBusinessDataSamplesGenerator.class.getPackage().getName());

	private final PrototypeProcessBusinessDataSamplesCreator samplesCreator;
	private final String processBusinessDataKey;

	/**
	 * @param projectGenerator
	 * @param object
	 */
	public PrototypeProcessBusinessDataSamplesGenerator(ProjectGenerator projectGenerator, String processBusinessDataKey, PrototypeProcessBusinessDataSamplesCreator samplesCreator)
	{ 
		super(projectGenerator, FileFormat.TEXT, ResourceType.TEXT_FILE, processBusinessDataKey + ".csv", "PROCESSBUSINESSDATASAMPLE_" + processBusinessDataKey);
		this.samplesCreator = samplesCreator;
		this.processBusinessDataKey = processBusinessDataKey;
	}

	/**
	 * Overrides buildResourcesAndSetGenerators
	 * 
	 * @see org.openflexo.generator.CGGenerator#buildResourcesAndSetGenerators(org.openflexo.foundation.cg.CGRepository,
	 *      Vector)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources)
	{
		textResource = (TextFileResource) resourceForKeyWithCGFile(ResourceType.TEXT_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (textResource == null)
		{
			textResource = GeneratedFileResourceFactory.createNewPrototypeProcessInstanceSamplesResource(repository, this);
			textResource.setGenerator(this);
			logger.info("Created Process sample resource " + textResource.getName());
		}
		else
		{
			textResource.setGenerator(this);
			logger.info("Successfully retrieved Process sample FILE resource " + textResource.getName());
		}
		resources.add(textResource);
	}
	
	public String getProcessBusinessDataKey()
	{
		return processBusinessDataKey;
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
		VelocityContext vc = defaultContext();
		vc.put("processBusinessDataKey", getProcessBusinessDataKey());
		try
		{
			String mergeResult = merge("PrototypeProcessSample.csv.vm", vc);
			generatedCode = new GeneratedTextResource(getFileName(), mergeResult);
			stopGeneration();
		}
		catch (GenerationException e)
		{
			setGenerationException(e);
		}
		catch (Exception e)
		{
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		}
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
	public String getRelativePath()
	{
		return "";
	}

	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository)
	{
		return repository.getResourcesSymbolicDirectory();
	}

	public String getDirectoryPath()
	{
		return "processsamples";
	}
	
	public List<List<String>> getProcessSamples()
	{
		return samplesCreator.getProcessSamples(getProcessBusinessDataKey());
	}
}
