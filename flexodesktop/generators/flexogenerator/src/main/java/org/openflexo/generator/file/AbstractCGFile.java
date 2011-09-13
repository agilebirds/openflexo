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
package org.openflexo.generator.file;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.rm.GenerationAvailableFileResource;


public abstract class AbstractCGFile extends CGFile {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AbstractCGFile.class.getPackage().getName());

	public AbstractCGFile(GeneratedCodeBuilder builder)
    {
        this(builder.generatedCode);
        initializeDeserialization(builder);
     }
    
    public AbstractCGFile(GeneratedOutput generatedCode)
    {
        super(generatedCode);  
    }

    public AbstractCGFile(GenerationRepository repository, CGRepositoryFileResource resource)
    {
        super(repository,resource);
    }

    public GenerationAvailableFileResource getGenerationAvailableFileResource()
    {
    	if (getResource() instanceof GenerationAvailableFileResource) {
    		return (GenerationAvailableFileResource)getResource();
    	}
    	return null;
    }
    
	public IFlexoResourceGenerator getGenerator()
	{
		if (getGenerationAvailableFileResource() != null) 
			return getGenerationAvailableFileResource().getGenerator();
		return null;
	}

	@Override
	public boolean isCodeGenerationAvailable()
	{
		return (getGenerator() != null);
	}

	public GenerationException getGenerationException()
	{
		if (getGenerationAvailableFileResource() != null) {
			return (GenerationException)getGenerationAvailableFileResource().getGenerationException();
		}
		return null;
	}
	
	@Override
	public boolean hasGenerationErrors()
	{
		if (getMarkedAsDoNotGenerate())
			return false;
		hasGenerationErrors = (getGenerationException() != null);
		return hasGenerationErrors;
	}

	@Override
	public void writeModifiedFile() throws SaveResourceException, FlexoException
	{
		// Before to write the file, ensure generation is up-to-date
/*		if (getGenerator() != null) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Running generator if required");
			getGenerator().generate(false);
		}*/
		
		// GPO: The above code has been commented. Unless we find a very good reason for calling the code above, we should not do this. Why?
		// Well because! No, because when we call generate(false), we may trigger the generator to run again. So far it ain't too bad,
		// except that when the generator is done, it sends a notification CGContentRegenerated and it causes the flag "mark as merged" to
		// go back to false (making it impossible to write it down!). In conclusion, if you decide to uncomment the block above, then you
		// need to do something about the org.openflexo.foundation.cg.CGFile.update(FlexoObservable, DataModification) method that sets
		// the flag markAsMerged back to false
		super.writeModifiedFile();
	}
	
/*	public void dismissWhenUnchanged()
	{
		// Before to write the file, ensure generation is up-to-date
		if (getGenerator() != null) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Running generator if required");
			try {
				getGenerator().generate(false);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		super.dismissWhenUnchanged();
	}
*/	
	@Override
	public boolean needsMemoryGeneration()
	{
		if(getMarkedAsDoNotGenerate())return false;
		//return (getGenerator() != null && getGenerator().needsGeneration());
		return getResource().needsMemoryGeneration();
	}


}
