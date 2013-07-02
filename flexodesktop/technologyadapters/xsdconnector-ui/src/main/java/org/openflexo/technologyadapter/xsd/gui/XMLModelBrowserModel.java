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
package org.openflexo.technologyadapter.xsd.gui;

import java.util.logging.Logger;

import org.openflexo.components.widget.OntologyBrowserModel;
import org.openflexo.technologyadapter.xsd.model.XMLModel;

/**
 * Model supporting browsing through models or metamodels conform to {@link FlexoOntology} API<br>
 * 
 * Developers note: this model is shared by many widgets. Please modify it with caution.
 * 
 * @see FIBOWLClassSelector
 * @see FIBOWLIndividualSelector
 * @see FIBOWLPropertySelector
 * 
 * @author sguerin
 */
public class XMLModelBrowserModel extends OntologyBrowserModel {

	static final Logger logger = Logger.getLogger(XMLModelBrowserModel.class.getPackage().getName());

	public XMLModelBrowserModel(XMLModel model) {
		super(model);
	}

}
