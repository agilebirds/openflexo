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
package org.openflexo.technologyadapter.xml.gui;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.widget.FIBClassSelector;
import org.openflexo.components.widget.FIBIndividualSelector;
import org.openflexo.components.widget.FIBPropertySelector;
import org.openflexo.components.widget.OntologyBrowserModel;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLModel;


/**
 * Model supporting browsing through XML trees 
 *
 * Note: inspired from @see OntologyBrowserModel
 *
 * @see FIBClassSelector
 * @see FIBIndividualSelector
 * @see FIBPropertySelector
 * 
 * @author xtof
 */

public class XMLModelBrowserModel extends OntologyBrowserModel {

	static final Logger logger = Logger.getLogger(XMLModelBrowserModel.class.getPackage().getName());

	public XMLModelBrowserModel(XMLModel model) {
		super(null);

		logger.warning("THERE IS SOME WORK TO DO HERE! recomputeStructure for " + getContext());
	}

	@Override
	public void recomputeStructure() {

		logger.warning("THERE IS SOME WORK TO DO HERE! recomputeStructure for " + getContext());

	}

	
}
