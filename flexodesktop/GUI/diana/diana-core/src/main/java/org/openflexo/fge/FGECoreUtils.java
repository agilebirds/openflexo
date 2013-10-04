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
package org.openflexo.fge;

import java.util.logging.Logger;

import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.toolbox.FileResource;

public class FGECoreUtils {

	static final Logger logger = Logger.getLogger(FGECoreUtils.class.getPackage().getName());

	// Instantiate a new localizer in directory src/dev/resources/FGELocalized
	// Little hack to be removed: linked to parent localizer (which is Openflexo main localizer)
	public static LocalizedDelegateGUIImpl LOCALIZATION = new LocalizedDelegateGUIImpl(new FileResource("FGELocalized"),
			new LocalizedDelegateGUIImpl(new FileResource("Localized"), null, false), true);

	/**
	 * This is the FGE model factory shared by all FGE tools
	 */
	public static FGEModelFactory TOOLS_FACTORY = null;

	static {
		try {
			TOOLS_FACTORY = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

}
