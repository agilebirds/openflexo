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

import java.util.logging.Logger;

import org.openflexo.drm.DocResourceCenter;
import org.openflexo.fib.view.widget.DefaultFIBCustomComponent;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;

/**
 * Browser for DocResourceCenter elements
 * 
 * @author sylvain
 * 
 */
// TODO
public class DREBrowser extends DefaultFIBCustomComponent<DocResourceCenter> {

	protected static final Logger logger = Logger.getLogger(DREBrowser.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/DREBrowser.fib");

	// ================================================
	// ================ Constructor ===================
	// ================================================

	public DREBrowser(DocResourceCenter drc) {
		super(FIB_FILE, drc, FlexoLocalization.getMainLocalizer());
	}

	@Override
	public Class<DocResourceCenter> getRepresentedType() {
		return DocResourceCenter.class;
	}

	@Override
	public void delete() {
	}

}
