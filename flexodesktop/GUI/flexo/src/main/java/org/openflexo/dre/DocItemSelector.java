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
package org.openflexo.dre;

import java.io.File;

import org.openflexo.components.widget.FIBFlexoObjectSelector;
import org.openflexo.drm.DocItem;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select a DocItem while browsing the DocResourceCenter
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class DocItemSelector extends FIBFlexoObjectSelector<DocItem> {

	protected static final String EMPTY_STRING = "";

	public static final FileResource FIB_FILE = new FileResource("Fib/DocItemSelector.fib");

	public DocItemSelector(DocItem editedObject) {
		super(editedObject);
	}

	@Override
	public Class<DocItem> getRepresentedType() {
		return DocItem.class;
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

}
