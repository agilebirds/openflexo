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
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.openflexo.technologyadapter.emf.rm.EMFModelResourceImpl;

/**
 * EMF Uri Builder from Object.
 * 
 * @author gbesancon
 */
public class EMFModelURIBuilder {


	private static final Logger logger = Logger.getLogger(EMFModelURIBuilder.class.getPackage().getName());

	
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
		// NPE protection
		if (eObject != null){
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
			// If no name use URI Fragment
			if (builder.length() == 0) {
				Resource res = eObject.eResource();
				if (res != null){
					builder.append(eObject.eResource().getURIFragment(eObject));
				}
				else {
					// If no resource use Class SimpleName
					builder.append(eObject.getClass().getSimpleName());
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
		builder.append(EcoreUtil.getURI(eObject));
		return builder.toString();
	}
}
