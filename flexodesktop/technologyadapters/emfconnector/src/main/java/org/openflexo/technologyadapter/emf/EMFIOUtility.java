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
package org.openflexo.technologyadapter.emf;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * EMF IO.
 * 
 * @author gbesancon
 */
public class EMFIOUtility {

	/**
	 * Load file.
	 * 
	 * @param filename
	 * @param ePackage
	 * @param eResourceFactory
	 * @return
	 */
	public static Resource load(String filename, EPackage ePackage, Resource.Factory eResourceFactory) {
		return load(URI.createFileURI(filename), ePackage, eResourceFactory);
	}

	/**
	 * Load URI.
	 * 
	 * @param uri
	 * @param ePackage
	 * @param eResourceFactory
	 * @return
	 */
	public static Resource load(URI uri, EPackage ePackage, Resource.Factory eResourceFactory) {
		Resource resource = null;
		try {
			ResourceSet resourceSet = new ResourceSetImpl();

			Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
			registry.getExtensionToFactoryMap().put(ePackage.getName(), eResourceFactory);
			if (resourceSet.getPackageRegistry() != null) {
				resourceSet.getPackageRegistry().put(ePackage.getName(), ePackage);
			}

			resource = resourceSet.createResource(uri);
			resource.load(null);
		} catch (IOException e) {
			resource = null;
			e.printStackTrace();
		}
		return resource;
	}

	/**
	 * Save file.
	 * 
	 * @param objects
	 * @param filename
	 * @param ePackage
	 * @param eResourceFactory
	 */
	public static void save(Collection<EObject> objects, String filename, EPackage ePackage, Resource.Factory eResourceFactory) {
		save(objects, URI.createFileURI(filename), ePackage, eResourceFactory);
	}

	/**
	 * Save URI.
	 * 
	 * @param objects
	 * @param filename
	 */
	public static void save(Collection<EObject> objects, URI uri, EPackage ePackage, Resource.Factory eResourceFactory) {
		try {
			ResourceSet resourceSet = new ResourceSetImpl();

			Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
			registry.getExtensionToFactoryMap().put(ePackage.getName(), eResourceFactory);
			if (resourceSet.getPackageRegistry() != null) {
				resourceSet.getPackageRegistry().put(ePackage.getName(), ePackage);
			}

			Resource resource = resourceSet.createResource(uri);
			resource.getContents().addAll(objects);

			resource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
