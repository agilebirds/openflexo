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
package org.openflexo.technologyadapter.emf.model;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * EMF Uri Builder from Object.
 * 
 * @author gbesancon
 */
public class EMFModelURIBuilder {

	/**
	 * Name of resource.
	 * 
	 * @param eObject
	 * @return
	 */
	public static String getName(Resource resource) {
		return resource.getURI().toString().substring(resource.getURI().toString().lastIndexOf("/") + 1);
	}

	/**
	 * Name of object.
	 * 
	 * @param eObject
	 * @return
	 */
	public static String getName(EObject eObject) {
		StringBuilder builder = new StringBuilder();
		List<EAttribute> eAttributes = eObject.eClass().getEAllAttributes();
		if (eAttributes.size() != 0) {
			for (EAttribute eAttribute : eAttributes) {
				if (builder.length() == 0) {
					Object value = eObject.eGet(eAttribute);
					if (value != null) {
						builder.append(value);
					}
				}
			}
		}
		return builder.toString();
	}

	/**
	 * URI of resource.
	 * 
	 * @param resource
	 * @return
	 */
	public static String getUri(Resource resource) {
		return resource.getURI().toString();
	}

	/**
	 * URI of object.
	 * 
	 * @param eObject
	 * @return
	 */
	public static String getUri(EObject eObject) {
		StringBuilder builder = new StringBuilder();
		if (eObject.eContainer() == null) {
			if (eObject.eResource() != null) {
				builder.append(eObject.eResource().getURI().toString());
			}
		} else {
			builder.append(getUri(eObject.eContainer()));
		}
		builder.append("/");
		builder.append(eObject.eClass().getName());
		builder.append("/");
		builder.append(getName(eObject));
		return builder.toString();
	}

}
