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
package org.openflexo.foundation.cg.generator;

import java.io.File;

/**
 * Encode the result of a component generation
 * 
 * @author bmangez
 */
public class GeneratedComponent extends GeneratedCodeResult {

	public GeneratedComponent(String name, String java, String api, String html, String wod, String woo) {
		super(name);
		setDefaultCode(java);
		addCode(COMPONENT_CODE_TYPE.API.toString(), api);
		addCode(COMPONENT_CODE_TYPE.HTML.toString(), html);
		addCode(COMPONENT_CODE_TYPE.WOD.toString(), wod);
		addCode(COMPONENT_CODE_TYPE.WOO.toString(), woo);
	}

	public void writeWOComponentFiles(File woComponentDirectory, File javaSrcDirectory) {
		GeneratorUtils.writeWOComponentFiles(woComponentDirectory, javaSrcDirectory, name(), java(), api(), html(), wod(), woo());
	}

	public String java() {
		return defaultCode();
	}

	public String api() {
		return get(COMPONENT_CODE_TYPE.API.toString());
	}

	public String wod() {
		return get(COMPONENT_CODE_TYPE.WOD.toString());
	}

	public String woo() {
		return get(COMPONENT_CODE_TYPE.WOO.toString());
	}

	public String html() {
		return get(COMPONENT_CODE_TYPE.HTML.toString());
	}
}
