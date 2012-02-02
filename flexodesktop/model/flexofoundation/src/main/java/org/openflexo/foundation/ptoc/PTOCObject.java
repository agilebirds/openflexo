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
package org.openflexo.foundation.ptoc;

/**
 * MOS
 * @author MOSTAFA
 * 
 * TODO_MOS
 * 
 */


import java.util.logging.Logger;

import org.openflexo.xmlcode.XMLMapping;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.logging.FlexoLogger;

public abstract class PTOCObject extends FlexoModelObject {

	protected static final Logger logger = FlexoLogger.getLogger(PTOCObject.class
			.getPackage().getName());
	
	private PTOCData data = null;
	
	public PTOCObject(PTOCData data) {
		super(data!=null?data.getProject():null);
		this.data = data;
	}
	
	public PTOCObject(FlexoProject project) {
		super(project);
	}
	
	public PTOCData getData() {
		return data;
	}
	
	@Override
	public FlexoProject getProject() {
		return getData()==null?null:getData().getProject();
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getTOCMapping();
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return getData();
	}
}
