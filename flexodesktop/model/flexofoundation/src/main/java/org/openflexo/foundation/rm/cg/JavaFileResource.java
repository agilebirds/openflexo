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
package org.openflexo.foundation.rm.cg;

import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.generator.GeneratedCodeResult;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.logging.FlexoLogger;

/**
 * @author sylvain
 * 
 */
public class JavaFileResource<G extends IFlexoResourceGenerator, F extends CGFile> extends ASCIIFileResource<G,F>
{
    static final Logger logger = FlexoLogger.getLogger(JavaFileResource.class.getPackage().getName());

    /**
     * @param builder
     */
    public JavaFileResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }

    /**
     * @param aProject
     */
    public JavaFileResource(FlexoProject aProject)
    {
        super(aProject);
    }

    /**
     * Overrides getResourceType
     * 
     * @see org.openflexo.foundation.rm.FlexoResource#getResourceType()
     */
    @Override
	public ResourceType getResourceType()
    {
        return ResourceType.JAVA_FILE;
    }


    @Override
	protected JavaFile createGeneratedResourceData()
    {
        return new JavaFile(getFile());
    }
    
     public JavaFile getJavaFile()
    {
        return (JavaFile) getGeneratedResourceData();
    }
     
     private Date _lastModelReinjectingDate;
     
 	public Date getLastModelReinjectingDate()
	{
		if ((_lastModelReinjectingDate == null) || (getLastGenerationDate().getTime() > _lastModelReinjectingDate.getTime())) {
			_lastModelReinjectingDate = getLastGenerationDate();
		}
		return _lastModelReinjectingDate;
	}

	public void setLastModelReinjectingDate(Date aDate)
	{
		_lastModelReinjectingDate = aDate;
	}
	
	private File _javaModelFile;
	
	/**
	 * This file stores all known but ignored properties and methods
	 * @return
	 */
	public File getJavaModelFile()
	{
		if (_javaModelFile == null) {
			_javaModelFile = new File(
					getCGFile().getRepository().getCodeGenerationWorkingDirectory(),
					getResourceFile().getRelativePath()+".JAVA_MODEL");
			if (logger.isLoggable(Level.FINE))
				logger.fine("_javaModelFile"+_javaModelFile.getAbsolutePath());
		}
		return _javaModelFile;
	}
	
	@Override
	public String getGenerationResultKey() {
		return GeneratedCodeResult.DEFAULT_KEY;
	}
	
	public void registerObserverWhenRequired(){
		//this method after each generation of the javacode and may be implemented in subclass
	}
}
