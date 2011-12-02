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
package org.openflexo.components.browser.dm;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.DMRepositoryFolder;
import org.openflexo.foundation.dm.DMTranstyper;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.FlexoExecutionModelRepository;
import org.openflexo.foundation.dm.JDKRepository;
import org.openflexo.foundation.dm.WORepository;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORelationship;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;

public class DMBrowserElementFactory implements BrowserElementFactory {
	@Override
	public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent) {
		if (object instanceof DMModel) {
			return new DMModelElement((DMModel) object, browser, parent);
		} else if (object instanceof DMRepositoryFolder) {
			return new DMRepositoryFolderElement((DMRepositoryFolder) object, browser, parent);
		} else if (object instanceof DMEORepository) {
			if (object instanceof EOPrototypeRepository) {
				return new DMEORepositoryElement((DMEORepository) object, BrowserElementType.DM_EOPROTOTYPES_REPOSITORY, browser, parent);
			} else if (object instanceof FlexoExecutionModelRepository) {
				return new DMEORepositoryElement((DMEORepository) object, BrowserElementType.DM_EXECUTION_MODEL_REPOSITORY, browser, parent);
			} else {
				return new DMEORepositoryElement((DMEORepository) object, browser, parent);
			}
		} else if (object instanceof DMRepository) {
			if (object instanceof JDKRepository) {
				return new DMRepositoryElement((DMRepository) object, BrowserElementType.JDK_REPOSITORY, browser, parent);
			} else if (object instanceof WORepository) {
				return new DMRepositoryElement((DMRepository) object, BrowserElementType.WO_REPOSITORY, browser, parent);
			} else if (object instanceof ExternalRepository) {
				return new DMRepositoryElement((DMRepository) object, BrowserElementType.EXTERNAL_REPOSITORY, browser, parent);
			} else {
				return new DMRepositoryElement((DMRepository) object, browser, parent);
			}
		} else if (object instanceof DMPackage) {
			return new DMPackageElement((DMPackage) object, browser, parent);
		} else if (object instanceof DMEOModel) {
			return new DMEOModelElement((DMEOModel) object, browser, parent);
		} else if (object instanceof DMEOEntity) {
			return new DMEOEntityElement((DMEOEntity) object, browser, parent);
		} else if (object instanceof DMEntity) {
			return new DMEntityElement((DMEntity) object, browser, parent);
		} else if (object instanceof DMEOAttribute) {
			return new DMEOAttributeElement((DMEOAttribute) object, browser, parent);
		} else if (object instanceof DMEORelationship) {
			return new DMEORelationshipElement((DMEORelationship) object, browser, parent);
		} else if (object instanceof DMProperty) {
			return new DMPropertyElement((DMProperty) object, browser, parent);
		} else if (object instanceof DMMethod) {
			return new DMMethodElement((DMMethod) object, browser, parent);
		} else if (object instanceof DMTranstyper) {
			return new DMTranstyperElement((DMTranstyper) object, browser, parent);
		} else if (object instanceof ERDiagram) {
			return new ERDiagramElement((ERDiagram) object, browser, parent);
		}
		return null;
	}

}
