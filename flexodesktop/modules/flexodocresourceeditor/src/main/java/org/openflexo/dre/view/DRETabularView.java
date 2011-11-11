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
package org.openflexo.dre.view;

import org.openflexo.components.tabular.TabularView;
import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.dre.controller.DREController;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.foundation.FlexoModelObject;

public class DRETabularView extends TabularView {

	public DRETabularView(DREController controller, AbstractModel model, int visibleRowCount) {
		super(controller, model, visibleRowCount);
	}

	public DRETabularView(DREController controller, AbstractModel model) {
		super(controller, model);
	}

	@Override
	protected FlexoModelObject getParentObject(FlexoModelObject object) {
		if (object instanceof DocItem) {
			return ((DocItem) object).getFolder();
		} else if (object instanceof DocItemFolder) {
			return ((DocItemFolder) object).getParentFolder();
		}
		return null;
	}

}
