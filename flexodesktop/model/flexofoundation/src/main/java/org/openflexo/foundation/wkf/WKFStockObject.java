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
package org.openflexo.foundation.wkf;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;


public class WKFStockObject extends WKFArtefact implements InspectableObject, DeletableObject, LevelledObject {

	// ==========================================================
	// ======================= Constructor ======================
	// ==========================================================

	/**
	 * Constructor used during deserialization
	 */
	public WKFStockObject(FlexoProcessBuilder builder)
	{
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public WKFStockObject(FlexoProcess process)
	{
		super(process);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.STOCK_INSPECTOR;
	}

	@Override
	public String getClassNameKey()
	{
		return "stock";
	}

	@Override
	public String getFullyQualifiedName()
	{
		return "STOCK."+getText();
	}

}
