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
package org.openflexo.technologyadapter.xsd.model;

import java.io.File;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;

public class XSDMetaModel extends XSOntology implements FlexoMetaModel<XSDMetaModel> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSDMetaModel.class.getPackage()
			.getName());

	public XSDMetaModel(String ontologyURI, File xsdFile, XSDTechnologyAdapter adapter) {
		super(ontologyURI, xsdFile, adapter);
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public void setIsReadOnly(boolean b) {
	}

	@Override
	public void save() throws SaveResourceException {
		logger.warning("Imported ontologies are not supposed to be saved !!!");
	}

	@Deprecated
	@Override
	public String getInspectorName() {
		return Inspectors.VE.IMPORTED_ONTOLOGY_INSPECTOR;
	}

	@Deprecated
	@Override
	public String getClassNameKey() {
		return null;
	}

}
