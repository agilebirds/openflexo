/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
 *
 * This file is part of Openflexo.
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
 * along with Openflexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.technologyadapter.excel;

import java.util.logging.Logger;

import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.xml.ViewBuilder;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

public class BasicExcelModelSlotInstance<MS extends BasicExcelModelSlot> extends ModelSlotInstance<BasicExcelModelSlot, ExcelWorkbook> {

	private static final Logger logger = Logger.getLogger(BasicExcelModelSlotInstance.class.getPackage().getName());

	// Serialization/deserialization only, do not use
	private String modelURI;

	/**
	 * Constructor invoked during deserialization
	 * 
	 */
	public BasicExcelModelSlotInstance(ViewBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	/**
	 * Constructor invoked during deserialization
	 * 
	 */
	public BasicExcelModelSlotInstance(VirtualModelInstanceBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	@Override
	public ExcelWorkbook getResourceData() {
		return null;
	}

	// Serialization/deserialization only, do not use
	public String getModelURI() {
		if (getResource() != null) {
			return getResource().getURI();
		}
		return modelURI;
	}

	// Serialization/deserialization only, do not use
	public void setModelURI(String modelURI) {
		this.modelURI = modelURI;
	}

	public ExcelWorkbook getModel() {
		return getResourceData();
	}
}
