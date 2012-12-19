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
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.metamodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;

/**
 * EMF Annotation annotation.
 * 
 * @author gbesancon
 */
public class EMFAnnotationAnnotation extends AEMFMetaModelObjectImpl<EAnnotation> implements IFlexoOntologyAnnotation {

	/**
	 * Constructor.
	 */
	public EMFAnnotationAnnotation(EMFMetaModel metaModel, EAnnotation annotation) {
		super(metaModel, annotation);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyAnnotation#getName()
	 */
	@Override
	public String getName() {
		return object.getSource();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.FlexoObject#getFullyQualifiedName()
	 */
	@Override
	@Deprecated
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyAnnotation#getDetails()
	 */
	@Override
	public Map<String, String> getDetails() {
		Map<String, String> details = new HashMap<String, String>();
		for (Entry<String, String> entry : object.getDetails().entrySet()) {
			details.put(entry.getKey(), entry.getValue());
		}
		return Collections.unmodifiableMap(details);
	}

}
