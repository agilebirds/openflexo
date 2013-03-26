/** Copyright (c) 2013, THALES SYSTEMES AEROPORTES - All Rights Reserved
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
package org.openflexo.technologyadapter.emf.utility;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * 
 * Ecore Utility.
 * 
 * @author gbesancon
 * 
 */
public class EcoreUtility {
	/**
	 * All Contents And CrossReferences.
	 * 
	 * @param object
	 * @return
	 */
	public static EList<EObject> getAllContentsAndCrossReferences(EObject object) {
		EList<EObject> result = new BasicEList<EObject>();
		getAllContentsAndCrossReferences(object, result);
		return result;
	}

	/**
	 * All Contents And CrossReferences.
	 * 
	 * @param object
	 * @param allContentsAndCrossReferences
	 */
	protected static void getAllContentsAndCrossReferences(EObject object, List<EObject> allContentsAndCrossReferences) {
		EList<EObject> contents = object.eContents();
		for (EObject content : contents) {
			if (!allContentsAndCrossReferences.contains(content)) {
				allContentsAndCrossReferences.add(content);
				getAllContentsAndCrossReferences(content, allContentsAndCrossReferences);
			}
		}

		EList<EObject> references = object.eCrossReferences();
		for (EObject reference : references) {
			if (!allContentsAndCrossReferences.contains(reference)) {
				if (reference.eIsProxy()) {
					EcoreUtil.resolveAll(object);
				}
				allContentsAndCrossReferences.add(reference);
				getAllContentsAndCrossReferences(reference, allContentsAndCrossReferences);
			}
		}
	}

	/**
	 * 
	 * All Resources.
	 * 
	 * @param resource
	 * @return
	 */
	public static List<Resource> getAllResources(Resource resource) {
		List<Resource> result = new ArrayList<Resource>();
		result.add(resource);
		getAllResources(resource, result);
		return result;
	}

	/**
	 * All Resources.
	 * 
	 * @param resource
	 * @param resources
	 */
	protected static void getAllResources(Resource resource, List<Resource> resources) {
		for (EObject object : resource.getContents()) {
			getAllResources(object, resources);
		}
	}

	/**
	 * 
	 * All Resources.
	 * 
	 * @param object
	 * @return
	 */
	public static List<Resource> getAllResources(EObject object) {
		List<Resource> result = new ArrayList<Resource>();
		result.add(object.eResource());
		getAllResources(object, result);
		return result;
	}

	/**
	 * 
	 * All Resources.
	 * 
	 * @param object
	 * @param resources
	 */
	protected static void getAllResources(EObject object, List<Resource> resources) {
		for (EObject contentOrReference : getAllContentsAndCrossReferences(object)) {
			Resource resource = contentOrReference.eResource();
			if (resource != null && !resources.contains(resource)) {
				resources.add(resource);
			}
		}
	}

	/**
	 * Get All Contents of type aClass.
	 * 
	 * @param <T>
	 * @param object
	 * @param aClass
	 * @return
	 */
	public static <T extends EObject> List<T> getAllContents(EObject object, Class<T> aClass) {
		List<T> contents = new ArrayList<T>();
		getAllContents(object, aClass, contents);
		return contents;
	}

	/**
	 * Get All Contents of type aClass.
	 * 
	 * @param <T>
	 * @param object
	 * @param aClass
	 * @param contents
	 */
	protected static <T extends EObject> void getAllContents(EObject object, Class<T> aClass, List<T> contents) {
		if (object != null) {
			if (aClass.isInstance(object)) {
				contents.add((T) object);
			}
			for (EObject content : object.eContents()) {
				getAllContents(content, aClass, contents);
			}
		}
	}

	/**
	 * Get All Containers of type aClass.
	 * 
	 * @param <T>
	 * @param object
	 * @param aClass
	 * @return
	 */
	public static <T extends EObject> List<T> getAllContainers(EObject object, Class<T> aClass) {
		List<T> containers = new ArrayList<T>();
		getAllContainers(object, aClass, containers);
		return containers;
	}

	/**
	 * Get All Containers of type aClass.
	 * 
	 * @param <T>
	 * @param object
	 * @param aClass
	 * @param containers
	 */
	protected static <T extends EObject> void getAllContainers(EObject object, Class<T> aClass, List<T> containers) {
		if (object != null) {
			if (aClass.isInstance(object)) {
				containers.add((T) object);
			}
			if (object.eContainer() != null) {
				getAllContainers(object.eContainer(), aClass, containers);
			}
		}
	}

	/**
	 * Get Top Container.
	 * 
	 * @param <T>
	 * @param object
	 * @return
	 */
	public static EObject getTopContainer(EObject object) {
		EObject topContainer = object;
		if (object.eContainer() != null) {
			topContainer = getTopContainer(object.eContainer());
		}
		return topContainer;
	}

	/**
	 * Get Top Container of type aClass.
	 * 
	 * @param <T>
	 * @param object
	 * @param aClass
	 * @return
	 */
	public static <T extends EObject> T getTopContainer(EObject object, Class<T> aClass) {
		T topContainer = null;
		List<T> containers = new ArrayList<T>();
		getAllContainers(object, aClass, containers);
		if (containers.size() != 0) {
			topContainer = containers.get(containers.size() - 1);
		}
		return topContainer;
	}
}
