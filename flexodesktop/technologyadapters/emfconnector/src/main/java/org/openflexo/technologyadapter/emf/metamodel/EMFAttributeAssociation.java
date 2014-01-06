/** Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.metamodel;

import org.eclipse.emf.ecore.EAttribute;
import org.openflexo.foundation.ontology.IFlexoOntologyFeature;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;

/**
 * EMF Reference association.
 * 
 * @author gbesancon
 */
public class EMFAttributeAssociation extends AEMFMetaModelObjectImpl<EAttribute> implements IFlexoOntologyFeatureAssociation {

	/**
	 * Constructor.
	 */
	public EMFAttributeAssociation(EMFMetaModel metaModel, EAttribute aAttribute) {
		super(metaModel, aAttribute);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#getName()
	 */
	@Override
	public String getName() {
		return object.getName();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#getDisplayableDescription()
	 */
	@Override
	public String getDisplayableDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation#getDomain()
	 */
	@Override
	public EMFClassClass getDomain() {
		return ontology.getConverter().convertClass(ontology, object.getEContainingClass());
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation#getFeature()
	 */
	@Override
	public IFlexoOntologyFeature getFeature() {
		return ontology.getConverter().convertAttributeProperty(ontology, object);
	}

	@Override
	public IFlexoOntologyObject getRange() {
		if (getFeature() instanceof IFlexoOntologyStructuralProperty) {
			return ((IFlexoOntologyStructuralProperty) getFeature()).getRange();
		}
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation#getLowerBound()
	 */
	@Override
	public Integer getLowerBound() {
		return object.getLowerBound();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation#getUpperBound()
	 */
	@Override
	public Integer getUpperBound() {
		return object.getUpperBound();
	}
}
