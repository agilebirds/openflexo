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
package org.openflexo.generator.cg;

import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.rm.cg.PListFileResource;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.generator.file.AbstractCGFile;


public class CGPListFile extends AbstractCGFile {

    public CGPListFile(GeneratedCodeBuilder builder)
    {
        this(builder.generatedCode);
        initializeDeserialization(builder);
     }
    
    public CGPListFile(GeneratedOutput generatedCode)
    {
        super(generatedCode);  
    }

    public CGPListFile(CGRepository repository, PListFileResource resource)
    {
        super(repository,resource);
    }

	@Override
	public PListFileResource getResource() 
	{
		return (PListFileResource)super.getResource();
	}

}
