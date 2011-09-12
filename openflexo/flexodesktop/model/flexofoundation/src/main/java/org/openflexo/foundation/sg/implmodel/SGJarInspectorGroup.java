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
package org.openflexo.foundation.sg.implmodel;

import java.io.InputStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.openflexo.foundation.InspectorGroup;
import org.openflexo.toolbox.JavaResourceUtil;


/**
 * @author Nicolas Daniels
 *
 */
public class SGJarInspectorGroup implements InspectorGroup {

	private class JarInspectorDTO {
		public String resourceName;
		public Class<?> classFromJar;

		public JarInspectorDTO(String resourceName, Class<?> classFromJar) {
			this.resourceName = resourceName;
			this.classFromJar = classFromJar;
		}
	}

	public static final SGJarInspectorGroup INSTANCE = new SGJarInspectorGroup();

	private Hashtable<String, JarInspectorDTO> allInspectors = new Hashtable<String, JarInspectorDTO>(); // <Inspector Name, JarInspectorDTO>

	private SGJarInspectorGroup() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "SGJar";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsInspector(String inspectorName) {
		return allInspectors.contains(inspectorName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getAllInspectorNames() {
		return Collections.list(allInspectors.keys());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getInspectorStream(String inspectorName) {
		JarInspectorDTO jarInspectorDTO = allInspectors.get(inspectorName);
		if (jarInspectorDTO == null)
			return null;
		return jarInspectorDTO.classFromJar.getResourceAsStream(jarInspectorDTO.resourceName);
	}

	/**
	 * Retrieve and record all inspectors available in resources of the jar/project folder where the specified class belongs. <br>
	 * The inspectors must ends with .inspector.
	 * 
	 * @param clazz : a class which belongs to the jar/project folder
	 */
	public void recordAllInspectors(Class<?> clazz) {
		
		for (String resourceName : JavaResourceUtil.getMatchingResources(clazz, ".inspector")) {
			JarInspectorDTO jarInspectorDTO = new JarInspectorDTO(resourceName, clazz);
			String inspectorName = resourceName.substring(resourceName.lastIndexOf('/') + 1);
			allInspectors.put(inspectorName, jarInspectorDTO);
		}
	}
}
